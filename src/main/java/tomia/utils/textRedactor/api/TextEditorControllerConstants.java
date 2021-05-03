package tomia.utils.textRedactor.api;

import org.springframework.web.bind.annotation.*;

public interface TextEditorControllerConstants {

    String ADD_TEXT ="/addText";
    String ADD_TEXT_ON_POSITION ="/addTextOnPosition";
    String REMOVE_TEXT ="/removeText";
    String MAKE_TEXT_ITALIC ="/makeTextItalic";
    String MAKE_TEXT_BOLD ="/makeTextBold";
    String MAKE_TEXT_UNDERLINE ="/makeTextUnderline";
    String REDO ="/redo";
    String UNDO ="/undo";
    String PRINT_TEXT ="/printText";
}
