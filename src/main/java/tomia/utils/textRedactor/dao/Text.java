package tomia.utils.textRedactor.dao;

import lombok.*;
import org.springframework.stereotype.Component;
import tomia.utils.textRedactor.pojo.StyleElement;

import java.util.ArrayList;
import java.util.List;

import static java.util.Objects.nonNull;

@Component
@Builder
@AllArgsConstructor
@Getter
@Setter
public class Text {

    String text;

    List<StyleElement> styleList;

    public Text() {
        this.text = "";
        this.styleList = new ArrayList<>();
    }

    public static Text getInstance(Text anotherText) {
        return Text.builder()
                .text(anotherText.getText())
                .styleList(new ArrayList<>(anotherText.getStyleList()))
                .build();
    }

    @Override
    public String toString() {
        int size= 0;
        if(nonNull(styleList)){
            size =styleList.size();
        }
        return "Text{" +
                "text='" + text + '\'' +
                ", styleListSize=" + size +
                '}';
    }
}
