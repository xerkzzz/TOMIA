package tomia.utils.textRedactor.service.impl;

import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import tomia.utils.textRedactor.api.TextEditorControllerConstants;
import tomia.utils.textRedactor.dao.Text;
import tomia.utils.textRedactor.pojo.StyleElement;
import tomia.utils.textRedactor.repository.TextRedactorListRepository;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.notNullValue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@AutoConfigureMockMvc
class TextServiceImplTest {

    @Autowired
    private MockMvc mvc;


    @Qualifier("ListDb")
    @Autowired
    private TextRedactorListRepository db;

    @Autowired
    private TextServiceImpl service;

    @Test
    @Order(1)
    void addText() throws Exception {


        String request = "text";

        int bufferSizeBeforeAdding = db.getHistoryBuffer().size();

        mvc.perform(post(TextEditorControllerConstants.ADD_TEXT)
                .content(request)
                .contentType(MediaType.APPLICATION_JSON));

        mvc.perform(post(TextEditorControllerConstants.ADD_TEXT)
                .content(request)
                .contentType(MediaType.APPLICATION_JSON));

        int bufferSizeAfterAdding = db.getHistoryBuffer().size();

        Text text1 = db.getHistoryBuffer().get(0);
        Text text2 = db.getHistoryBuffer().get(1);
        Text text3 = db.getHistoryBuffer().get(2);

        assertThat(text1, notNullValue());
        assertThat(text2, notNullValue());
        assertThat(text3, notNullValue());
        assertThat(text1.getText(), is(""));
        assertThat(request, is(text2.getText()));
        assertThat(text3.getText(), is("texttext"));
        assertThat(bufferSizeBeforeAdding, is(bufferSizeAfterAdding - 2));

    }

    @Test
    @Order(2)
    void addTextToPosition() throws Exception {

        String initRequest = "[InsertByPositionText]";

        int initIndexRequest = 3;

        String textFromBufferBeforeChanging = db.getHistoryBuffer()
                .get(db.getHistoryBuffer().size() - 1).getText();

        int bufferSizeBeforeAdding = db.getHistoryBuffer().size();

        mvc.perform(post(TextEditorControllerConstants.ADD_TEXT_ON_POSITION)
                .content(initRequest)
                .param("position", String.valueOf(initIndexRequest))
                .contentType(MediaType.APPLICATION_JSON));

        int bufferSizeAfterAdding = db.getHistoryBuffer().size();

        String textFromBufferAfterChanging = db.getHistoryBuffer()
                .get(db.getHistoryBuffer().size() - 1).getText();

        assertThat(textFromBufferAfterChanging, notNullValue());
        assertThat(textFromBufferBeforeChanging.contains("[InsertByPositionText]"), is(false));
        assertThat(textFromBufferAfterChanging.contains("[InsertByPositionText]"), is(true));
        assertThat(bufferSizeBeforeAdding, is(bufferSizeAfterAdding - 1));
        assertThat(textFromBufferAfterChanging.length(), is(initRequest.length() + textFromBufferBeforeChanging.length()));
    }

    @Test
    @Order(3)
    void remove() throws Exception {

        String deleteCandidate = "[InsertByPositionText]";

        int fromPosition = 3;

        int toPosition = fromPosition + deleteCandidate.length();

        String textFromBufferBeforeChanging = db.getHistoryBuffer()
                .get(db.getHistoryBuffer().size() - 1).getText();

        int bufferSizeBeforeAdding = db.getHistoryBuffer().size();

        mvc.perform(delete(TextEditorControllerConstants.REMOVE_TEXT)
                .param("fromPosition", String.valueOf(fromPosition))
                .param("toPosition", String.valueOf(toPosition))
                .contentType(MediaType.APPLICATION_JSON));

        int bufferSizeAfterDeleting = db.getHistoryBuffer().size();

        String textFromBufferAfterChanging = db.getHistoryBuffer()
                .get(db.getHistoryBuffer().size() - 1).getText();

        assertThat(textFromBufferAfterChanging, notNullValue());
        assertThat(textFromBufferBeforeChanging.contains("[InsertByPositionText]"), is(true));
        assertThat(textFromBufferAfterChanging.contains("[InsertByPositionText]"), is(false));
        assertThat(bufferSizeBeforeAdding, is(bufferSizeAfterDeleting - 1));
        assertThat(textFromBufferAfterChanging.length(), is(textFromBufferBeforeChanging.length() - deleteCandidate.length()));
    }

    @Test
    @Order(4)
    void italic() throws Exception {

        int fromPosition = 2;

        int toPosition = 5;

        List<StyleElement> patternList = new ArrayList<>(Arrays.asList(null, null,
                StyleElement.builder().italic(true).build(),
                StyleElement.builder().italic(true).build(),
                StyleElement.builder().italic(true).build(),
                StyleElement.builder().italic(true).build(),
                null, null
        ));

        String textFromBufferBeforeChanging = db.getHistoryBuffer()
                .get(db.getHistoryBuffer().size() - 1).getText();

        List<StyleElement> styleListBeforeAdding = db.getHistoryBuffer()
                .get(db.getHistoryBuffer().size() - 1).getStyleList();

        int bufferSizeBeforeAdding = db.getHistoryBuffer().size();

        mvc.perform(post(TextEditorControllerConstants.MAKE_TEXT_ITALIC)
                .param("fromPosition", String.valueOf(fromPosition))
                .param("toPosition", String.valueOf(toPosition))
                .contentType(MediaType.APPLICATION_JSON));

        List<StyleElement> styleListAfterAdding = db.getHistoryBuffer()
                .get(db.getHistoryBuffer().size() - 1).getStyleList();

        int bufferSizeAfterAdding = db.getHistoryBuffer().size();

        List<StyleElement> stylesFromBuffer = db.getHistoryBuffer().get(db.getHistoryBuffer().size() - 1).getStyleList();

        String textFromBufferAfterChanging = db.getHistoryBuffer()
                .get(db.getHistoryBuffer().size() - 1).getText();

        assertThat(textFromBufferAfterChanging, notNullValue());
        assertThat(styleListBeforeAdding, not(styleListAfterAdding));
        assertThat(bufferSizeBeforeAdding, is(bufferSizeAfterAdding - 1));
        assertThat(stylesFromBuffer.equals(patternList), is(true));

    }

    @Test
    @Order(5)
    void bold() throws Exception {
        int fromPosition = 1;

        int toPosition = 4;

        List<StyleElement> patternList = new ArrayList<>(Arrays.asList(null,
                StyleElement.builder().bold(true).build(),
                StyleElement.builder().italic(true).bold(true).build(),
                StyleElement.builder().italic(true).bold(true).build(),
                StyleElement.builder().italic(true).bold(true).build(),
                StyleElement.builder().italic(true).build(),
                null,null
        ));

        String textFromBufferBeforeChanging = db.getHistoryBuffer()
                .get(db.getHistoryBuffer().size() - 1).getText();

        List<StyleElement> styleListBeforeAdding = db.getHistoryBuffer()
                .get(db.getHistoryBuffer().size() - 1).getStyleList();

        int bufferSizeBeforeAdding = db.getHistoryBuffer().size();

        mvc.perform(post(TextEditorControllerConstants.MAKE_TEXT_BOLD)
                .param("fromPosition", String.valueOf(fromPosition))
                .param("toPosition", String.valueOf(toPosition))
                .contentType(MediaType.APPLICATION_JSON));

        List<StyleElement> styleListAfterAdding = db.getHistoryBuffer()
                .get(db.getHistoryBuffer().size() - 1).getStyleList();

        int bufferSizeAfterAdding = db.getHistoryBuffer().size();

        List<StyleElement> stylesFromBuffer = db.getHistoryBuffer().get(db.getHistoryBuffer().size() - 1).getStyleList();

        String textFromBufferAfterChanging = db.getHistoryBuffer()
                .get(db.getHistoryBuffer().size() - 1).getText();

        assertThat(textFromBufferAfterChanging, notNullValue());
        assertThat(styleListBeforeAdding, not(styleListAfterAdding));
        assertThat(bufferSizeBeforeAdding, is(bufferSizeAfterAdding - 1));
        assertThat(stylesFromBuffer.equals(patternList), is(true));
    }

    @Test
    @Order(6)
    void underline() throws Exception {
        int fromPosition = 0;

        int toPosition = 7;

        List<StyleElement> patternList = new ArrayList<>(Arrays.asList(StyleElement.builder().underline(true).build(),
                StyleElement.builder().underline(true).bold(true).build(),
                StyleElement.builder().underline(true).italic(true).bold(true).build(),
                StyleElement.builder().underline(true).italic(true).bold(true).build(),
                StyleElement.builder().underline(true).italic(true).bold(true).build(),
                StyleElement.builder().underline(true).italic(true).build(),
                StyleElement.builder().underline(true).build(),
                StyleElement.builder().underline(true).build()
        ));

        String textFromBufferBeforeChanging = db.getHistoryBuffer()
                .get(db.getHistoryBuffer().size() - 1).getText();

        List<StyleElement> styleListBeforeAdding = db.getHistoryBuffer()
                .get(db.getHistoryBuffer().size() - 1).getStyleList();

        int bufferSizeBeforeAdding = db.getHistoryBuffer().size();

        mvc.perform(post(TextEditorControllerConstants.MAKE_TEXT_UNDERLINE)
                .param("fromPosition", String.valueOf(fromPosition))
                .param("toPosition", String.valueOf(toPosition))
                .contentType(MediaType.APPLICATION_JSON));

        List<StyleElement> styleListAfterAdding = db.getHistoryBuffer()
                .get(db.getHistoryBuffer().size() - 1).getStyleList();

        int bufferSizeAfterAdding = db.getHistoryBuffer().size();

        List<StyleElement> stylesFromBuffer = db.getHistoryBuffer().get(db.getHistoryBuffer().size() - 1).getStyleList();

        String textFromBufferAfterChanging = db.getHistoryBuffer()
                .get(db.getHistoryBuffer().size() - 1).getText();

        assertThat(textFromBufferAfterChanging, notNullValue());
        assertThat(styleListBeforeAdding, not(styleListAfterAdding));
        assertThat(bufferSizeBeforeAdding, is(bufferSizeAfterAdding - 1));
        assertThat(stylesFromBuffer.equals(patternList), is(true));
    }

    @Test
    @Order(7)
    void undo() throws Exception {

        Text currentTextVersionBeforeChanging = service.getCurrentTextVersion();

        List<StyleElement> styleListBeforeUndo = currentTextVersionBeforeChanging.getStyleList();

        int bufferSizeBeforeChangingTextCurrentVersion = db.getHistoryBuffer().size();

        mvc.perform(put(TextEditorControllerConstants.UNDO)
                .contentType(MediaType.APPLICATION_JSON));

        Text currentTextVersionAfterChanging = service.getCurrentTextVersion();

        List<StyleElement> styleListAfterUndo = currentTextVersionAfterChanging.getStyleList();

        Text prevTextFromBufferAfterChanging = db.getHistoryBuffer()
                .get(db.getHistoryBuffer().size() - 2);

        int bufferSizeAfterChangingTextCurrentVersion = db.getHistoryBuffer().size();

        assertThat(bufferSizeBeforeChangingTextCurrentVersion, notNullValue());
        assertThat(bufferSizeAfterChangingTextCurrentVersion, notNullValue());
        assertThat(bufferSizeBeforeChangingTextCurrentVersion, is(bufferSizeAfterChangingTextCurrentVersion));
        assertThat(currentTextVersionBeforeChanging.equals(currentTextVersionAfterChanging), is(false));
        assertThat(styleListBeforeUndo.equals(styleListAfterUndo), is(false));
        assertThat(prevTextFromBufferAfterChanging.equals(currentTextVersionAfterChanging), is(true));
    }

    @Test
    @Order(8)
    void redo() throws Exception {

        Text currentVersionBeforeRedo = service.getCurrentTextVersion();

        int bufferSizeBeforeChangingTextCurrentVersion = db.getHistoryBuffer().size();

        mvc.perform(put(TextEditorControllerConstants.REDO)
                .contentType(MediaType.APPLICATION_JSON));

        Text prevTextFromBufferAfterChanging = db.getHistoryBuffer()
                .get(db.getHistoryBuffer().size() - 2);

        Text textFromServiceAfterChanging = service.getCurrentTextVersion();

        int bufferSizeAfterChangingTextCurrentVersion = db.getHistoryBuffer().size();

        assertThat(bufferSizeBeforeChangingTextCurrentVersion, notNullValue());
        assertThat(bufferSizeAfterChangingTextCurrentVersion, notNullValue());
        assertThat(bufferSizeBeforeChangingTextCurrentVersion, is(bufferSizeAfterChangingTextCurrentVersion));
        assertThat(currentVersionBeforeRedo.equals(db.getHistoryBuffer().get(db.getHistoryBuffer().size() - 2)), is(true));
        assertThat(textFromServiceAfterChanging.equals(currentVersionBeforeRedo), is(false));
    }


}
