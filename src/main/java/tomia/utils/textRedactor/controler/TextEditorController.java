package tomia.utils.textRedactor.controler;


import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import tomia.utils.textRedactor.service.TextService;

import static tomia.utils.textRedactor.api.TextEditorControllerConstants.*;

@Slf4j
@RestController
public class TextEditorController {

    private final TextService service;

    public TextEditorController(TextService service) {
        this.service = service;
    }

    @PostMapping(ADD_TEXT)
    void add(@RequestBody String string) {
        service.addText(string);
    }

    @PostMapping(ADD_TEXT_ON_POSITION)
    void addToPosition(@RequestBody String string,@RequestParam (name = "position") int position) {
        service.addTextToPosition(string ,position);
    }

    @DeleteMapping(REMOVE_TEXT)
    void remove(@RequestParam (name = "fromPosition")int fromPosition,@RequestParam (name = "toPosition") int toPosition) {
        service.remove(fromPosition, toPosition);
    }

    @PostMapping(MAKE_TEXT_ITALIC)
    void italic(@RequestParam (name = "fromPosition")int fromPosition,@RequestParam (name = "toPosition") int toPosition) {
        service.italic(fromPosition, toPosition);
    }

    @PostMapping(MAKE_TEXT_BOLD)
    void bold(@RequestParam (name = "fromPosition")int fromPosition,@RequestParam (name = "toPosition") int toPosition) {
        service.bold(fromPosition, toPosition);
    }

    @PostMapping(MAKE_TEXT_UNDERLINE)
    void underline(@RequestParam (name = "fromPosition")int fromPosition,@RequestParam (name = "toPosition") int toPosition) {
        service.underline(fromPosition, toPosition);
    }

    @PutMapping(REDO)
    void redo() {
        service.redo();
    }

    @PutMapping(UNDO)
    void undo() {
        service.undo();
    }

    @GetMapping(PRINT_TEXT)
    void print() {
        service.printText();
    }

}