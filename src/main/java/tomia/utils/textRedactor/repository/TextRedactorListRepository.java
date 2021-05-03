package tomia.utils.textRedactor.repository;

import org.springframework.stereotype.Repository;
import tomia.utils.textRedactor.dao.Text;

import java.util.List;

@Repository
public interface TextRedactorListRepository {

    List<Text> getHistoryBuffer();

    void saveText(Text text);

    void setHistoryBuffer(List<Text> historyBuffer);

    void deleteAll();

}
