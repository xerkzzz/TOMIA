package tomia.utils.textRedactor.service;

public interface TextService {


    void addText(String string);

    void addTextToPosition(String string, int position);

    void remove(int fromPosition, int toPosition);

    void italic(int fromPosition, int toPosition);

    void bold(int fromPosition, int toPosition);

    void underline(int fromPosition, int toPosition);

    void redo();

    void undo();

    void printText();
}
