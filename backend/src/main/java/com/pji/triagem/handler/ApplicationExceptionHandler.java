package com.pji.triagem.handler;

import com.pji.triagem.base.dto.ResponseDTO;
import com.pji.triagem.exception.*;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;
import java.util.Collections;
import java.util.List;

@ControllerAdvice
@RequiredArgsConstructor
public class ApplicationExceptionHandler extends ResponseEntityExceptionHandler {

    private final Logger log = LoggerFactory.getLogger(this.getClass());

    @ExceptionHandler({ RuntimeException.class })
    public ResponseEntity<Object> handleRuntimeException(RuntimeException ex, WebRequest request) {
        log.warn(ex.getMessage());
        return handleException(ex, HttpStatus.BAD_REQUEST, request, ex.getMessage());
    }

    @ExceptionHandler({ ResourceNotFoundException.class })
    public ResponseEntity<Object> handleResourceNotFoundException(ResourceNotFoundException ex,
                                                                  WebRequest request) {
        return handleException(ex, HttpStatus.NOT_FOUND, request, ex.getMessage() + " - " + ex.getResource());
    }

    @ExceptionHandler({ ServicesException.class })
    public ResponseEntity<Object> handleServicesException(ValidationException ex,
                                                          WebRequest request) {
        return handleException(ex, HttpStatus.BAD_REQUEST, request, ex.getMessage());
    }
    @ExceptionHandler({ ValidationException.class })
    public ResponseEntity<Object> handleValidationException(ValidationException ex,
                                                            WebRequest request) {
        return handleException(ex, HttpStatus.BAD_REQUEST, request, ex.getMessage());
    }

    @ExceptionHandler({ InvalidLoginException.class })
    public ResponseEntity<Object> handleInvalidLoginException(InvalidLoginException ex,
                                                              WebRequest request) {
        return handleException(ex, HttpStatus.UNAUTHORIZED, request, ex.getMessage());
    }

    protected ResponseEntity<Object> handleException(Exception ex, HttpStatus status, WebRequest req, String message) {
        ResponseDTO<List<String>> response = new ResponseDTO<>();
        response.setErrors(Collections.singletonList((message)));
        return handleExceptionInternal(ex, response, new HttpHeaders(), status, req);
    }

}
