package tomia.utils.textRedactor.service.impl;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import tomia.utils.textRedactor.pojo.StyleConstants;
import tomia.utils.textRedactor.pojo.StyleElement;
import tomia.utils.textRedactor.dao.Text;
import tomia.utils.textRedactor.repository.TextRedactorListRepository;
import tomia.utils.textRedactor.service.TextService;

import javax.annotation.PostConstruct;
import java.util.*;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;


@Slf4j
@Getter
@Service
public class TextServiceImpl implements TextService {

    private final TextRedactorListRepository repository;

    @Autowired
    public TextServiceImpl(@Qualifier("ListDb") TextRedactorListRepository repository) {
        this.repository = repository;
    }

    private Text currentTextVersion;

    private static final int MAX_BUFFER_SIZE = 8;

    @PostConstruct
    void init() {

        log.info("\033[1;35mMAX BUFFER SIZE IS {}\033[0m", MAX_BUFFER_SIZE);

        log.info("\033[1;35mIndexes are absolute values\033[0m");

        currentTextVersion = repository.getHistoryBuffer().get(0);

    }

    @Override
    public void addText(String string) {

        Text newVersionText = Text.getInstance(currentTextVersion);

        newVersionText.setText(mergeStrings(newVersionText.getText(), string, newVersionText.getText().length()));

        addTextToBuffer(newVersionText);

        currentTextVersion = newVersionText;
    }

    @Override
    public void addTextToPosition(String string, int position) {

        Text newVersionText = Text.getInstance(currentTextVersion);

        addStylesByIndex(newVersionText, position, string.length());

        addTextByIndex(newVersionText, position, string);

        addTextToBuffer(newVersionText);

        currentTextVersion = newVersionText;
    }

    @Override
    public void remove(int fromPosition, int toPosition) {

        Text newVersionText = Text.getInstance(currentTextVersion);

        removeText(newVersionText, checkIndexes(fromPosition, toPosition));

        removeStyles(newVersionText, checkIndexes(fromPosition, toPosition));

        addTextToBuffer(newVersionText);

        currentTextVersion = newVersionText;
    }

    @Override
    public void italic(int fromPosition, int toPosition) {

        addStyle(fromPosition, toPosition, StyleElement.builder().italic(true).build());
    }

    @Override
    public void bold(int fromPosition, int toPosition) {

        addStyle(fromPosition, toPosition, StyleElement.builder().bold(true).build());
    }

    @Override
    public void underline(int fromPosition, int toPosition) {

        addStyle(fromPosition, toPosition, StyleElement.builder().underline(true).build());
    }

    @Override
    public void redo() {

        List<Text> historyBuffer = repository.getHistoryBuffer();

        int index = historyBuffer.indexOf(currentTextVersion);

        if (index + 1 <= historyBuffer.size()) {

            currentTextVersion = historyBuffer.get(index + 1);

        } else {

            currentTextVersion = historyBuffer.get(historyBuffer.size() - 1);
        }
    }

    @Override
    public void undo() {

        List<Text> historyBuffer = repository.getHistoryBuffer();

        int index = historyBuffer.indexOf(currentTextVersion);

        if (index - 1 >= 0) {

            currentTextVersion = historyBuffer.get(index - 1);

        } else {

            currentTextVersion = historyBuffer.get(0);
        }
    }

    @Override
    public void printText() {

        /*
         *  Information for logs --->
         */

        List<Text> historyBufferForLog = repository.getHistoryBuffer();

        Text lastText = null;

        Text firstText = null;

        String historyBuffer = "empty";

        if (nonNull(historyBufferForLog)) {

            historyBuffer = historyBufferForLog.toString();

            lastText = historyBufferForLog.get(historyBufferForLog.size() - 1);

            firstText = historyBufferForLog.get(0);

        }
/*
<-- for logs
 */

        String resText = currentTextVersion.getText();

        StringBuilder sb = new StringBuilder();

        if (!resText.equals("")) {

            ArrayList<String> listText = new ArrayList<>(Arrays.asList(resText.split("")));

            String[] textStyleList = getStyleList(currentTextVersion.getStyleList());

            int deltaIndex = 0;

            for (int i = 0; i < textStyleList.length; i++) {

                if (textStyleList[i] != null) {

                    listText.add(i + deltaIndex, textStyleList[i]);

                    deltaIndex++;
                }
            }

            for (String el : listText) {
                sb.append(el);
            }
        }

        System.out.println("*****************************************************************************************");
        System.out.println(sb);
        System.out.println("*****************************************************************************************");

        log.debug("\n\033[1;35mCURRENT VERSION: \033[0m\033[1;32m" + currentTextVersion + "\033[0m");

        log.debug("\n\033[1;35mBUFFER SIZE: \033[0m\033[1;32m" + MAX_BUFFER_SIZE + "\033[0m");

        log.debug("\n\033[1;35mNEWEST VERSION IN BUFFER\033[0m\033[1;32m" + lastText + "\033[0m");

        log.debug("\n\033[1;35mOLDEST VERSION IN BUFFER  \033[0m\033[1;32m" + firstText + "\033[0m");

        log.debug("\n\n\033[1;35mBUFFER : \033[0m\033[1;32m" + historyBuffer + "\033[0m");

    }

    private void addStyle(int fromPosition, int toPosition, StyleElement styleElement) {

        Text newVersionText = addStyleToStyleArray(checkIndexes(fromPosition, toPosition)
                , currentTextVersion, styleElement);

        addTextToBuffer(newVersionText);

        currentTextVersion = newVersionText;
    }

    void addTextToBuffer(Text newVersionText) {

        List<Text> historyBuffer = repository.getHistoryBuffer();

        int index = historyBuffer.indexOf(currentTextVersion);

        if (index < historyBuffer.size() - 1) {

            repository.setHistoryBuffer(historyBuffer.subList(0, index + 1));
        }

        if (historyBuffer.size() > MAX_BUFFER_SIZE) {

            repository.setHistoryBuffer(historyBuffer.subList(1, historyBuffer.size()));
        }

        repository.setHistoryBuffer(historyBuffer);

        repository.saveText(newVersionText);
    }

    private void addStylesByIndex(Text newVersionText, int position, int length) {

        if (position < newVersionText.getText().length()) {
            List<StyleElement> styleElementsList = newVersionText.getStyleList();
            if (styleElementsList.size() < position) {
                fillArray(styleElementsList, position);
            }

            while (length > 0) {
                styleElementsList.add(position, null);
                length--;
            }
        }

    }

    private void addTextByIndex(Text newVersionText, int position, String string) {
        if (position > newVersionText.getText().length()) {
            position = newVersionText.getText().length();
        }
        newVersionText.setText(mergeStrings(currentTextVersion.getText(), string, position));
    }

    private String mergeStrings(String text, String string, int position) {

        String res;

        if (position > text.length()) {

            res = string + text;

        } else {

            res = text.substring(0, position) + string + text.substring(position);
        }

        return res;
    }

    private void removeText(Text currentString, int[] indexes) {

        String resString = currentString.getText().substring(0, indexes[0]) +
                currentString.getText().substring(indexes[1]);

        currentString.setText(resString);
    }

    private void removeStyles(Text currentTextVersion, int[] indexes) {

        List<StyleElement> stylesList = currentTextVersion.getStyleList();

        if (stylesList.size() >= indexes[1]) {

            while (currentTextVersion.getText().length() != stylesList.size()) {
                currentTextVersion.getStyleList().remove(indexes[0]);
            }

        } else if (stylesList.size() > indexes[0]) {

            for (int i = 0; i <= (stylesList.size() - indexes[0]); i++) {

                currentTextVersion.getStyleList().remove(indexes[0]);
            }

        }


    }

    private Text addStyleToStyleArray(int[] indexes, Text text, StyleElement styleElement) {

        Text newVersionText = Text.getInstance(text);

        List<StyleElement> styleList = newVersionText.getStyleList();

        setStyle(styleList, indexes, styleElement);

        newVersionText.setStyleList(styleList);

        return newVersionText;
    }

    private void setStyle(List<StyleElement> styleList, int[] indexes, StyleElement styleElement) {

        int maxStyleIndex = 0;

        if (nonNull(currentTextVersion)) {
            maxStyleIndex = currentTextVersion.getText().length();
        }

        if (maxStyleIndex <= indexes[1]) {
            indexes[1] = maxStyleIndex;
        }

        if (styleList.size() < indexes[1]) {
            fillArray(styleList, indexes[1]);
        }

        for (int i = 0; i < styleList.size(); i++) {

            if (i >= indexes[0] && i <= indexes[1]) {

                if (nonNull(styleList.get(i))) {

                    styleList.get(i).mergeStyles(styleElement);

                } else {

                    styleList.set(i, StyleElement.getInstance(styleElement));
                }
            }
        }


    }

    private String[] getStyleList(List<StyleElement> list) {

        String[] resList = new String[list.size() + 1];

        if (list.size() > 1) {
            String prevEl = keyAnalyzer(list.get(0));

            resList[0] = prevEl;

            for (int i = 1; i < list.size(); i++) {

                String currentEl = keyAnalyzer(list.get(i));

                if (!prevEl.equals(currentEl)) {

                    prevEl = currentEl;

                    resList[i] = currentEl;
                }
            }
            resList[list.size()] = StyleConstants.REGULAR;
        }

        return resList;
    }

    private <T> void fillArray(List<T> list, int anotherArraySize) {

        while (anotherArraySize > list.size()) {
            list.add(null);
        }
    }

    private String keyAnalyzer(StyleElement styleElement) {

        String res = null;

        if (isNull(styleElement)) {
            res = StyleConstants.REGULAR;
        } else if (styleElement.isBold() && !styleElement.isItalic() && !styleElement.isUnderline()) {
            res = StyleConstants.BOLD;
        } else if (!styleElement.isBold() && styleElement.isItalic() && !styleElement.isUnderline()) {
            res = StyleConstants.ITALIC;
        } else if (!styleElement.isBold() && !styleElement.isItalic() && styleElement.isUnderline()) {
            res = StyleConstants.UNDERLINE;
        } else if (styleElement.isBold() && styleElement.isItalic() && !styleElement.isUnderline()) {
            res = StyleConstants.BOLD_ITALIC;
        } else if (styleElement.isBold() && !styleElement.isItalic() && styleElement.isUnderline()) {
            res = StyleConstants.BOLD_UNDERLINE;
        } else if (!styleElement.isBold() && styleElement.isItalic() && styleElement.isUnderline()) {
            res = StyleConstants.ITALIC_UNDERLINE;
        } else if (styleElement.isBold() && styleElement.isItalic() && styleElement.isUnderline()) {
            res = StyleConstants.BOLD_ITALIC_UNDERLINE;
        }

        return res;
    }

    private int[] checkIndexes(int fromIndex, int toIndex) {

        int[] resArr = new int[2];

        resArr[0] = Math.min(Math.abs(fromIndex), Math.abs(toIndex));

        resArr[1] = Math.max(Math.abs(fromIndex), Math.abs(toIndex));

        return resArr;
    }
}

