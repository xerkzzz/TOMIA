package tomia.utils.textRedactor.repository;

import org.springframework.stereotype.Component;
import tomia.utils.textRedactor.dao.Text;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Component("ListDb")
public class TextDbList implements TextRedactorListRepository {

    private  List<Text> historyBuffer = null;


    @Override
    public List<Text> getHistoryBuffer() {
        return historyBuffer;
    }

    @Override
    public void saveText(Text text) {

        historyBuffer.add(text);
    }

    @Override
    public void setHistoryBuffer(List<Text> historyBuffer) {
        this.historyBuffer = historyBuffer;
    }

    @Override
    public void deleteAll() {
        this.historyBuffer = new ArrayList<>(Arrays.asList(Text.builder().build()));
    }

    public TextDbList() {
        this.historyBuffer = new ArrayList<>(Arrays.asList(Text.builder().text("").styleList(new ArrayList<>()).build()));
    }
}
