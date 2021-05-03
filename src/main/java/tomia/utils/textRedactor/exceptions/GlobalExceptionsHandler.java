package tomia.utils.textRedactor.exceptions;


import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;


@Slf4j
@RestControllerAdvice
public class GlobalExceptionsHandler {


    @ExceptionHandler(RuntimeException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    String processingOtherExceptions(RuntimeException e) {
        return processingException(e);
    }

    private String processingException(Exception e) {
        log.error(e.getMessage());
        return e.getMessage();
    }
}
