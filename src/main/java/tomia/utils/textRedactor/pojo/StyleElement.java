package tomia.utils.textRedactor.pojo;

import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class StyleElement {

    private boolean bold;

    private boolean italic;

    private boolean underline;

    public void mergeStyles(StyleElement anotherStyleElement) {

        if (anotherStyleElement.isBold()) {

            this.bold = anotherStyleElement.isBold();
        }
        if (anotherStyleElement.isItalic()) {

            this.italic = anotherStyleElement.isItalic();
        }
        if (anotherStyleElement.isUnderline()) {

            this.underline = anotherStyleElement.isUnderline();
        }
    }

    public static  StyleElement getInstance(StyleElement anotherStyleElement){
        return StyleElement.builder()
                .bold(anotherStyleElement.isBold())
                .italic(anotherStyleElement.isItalic())
                .underline(anotherStyleElement.isUnderline())
                .build();
    }
}
