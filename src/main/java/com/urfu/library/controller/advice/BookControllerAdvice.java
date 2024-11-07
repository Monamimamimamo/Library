package com.urfu.library.controller.advice;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

/**
 * ControllerAdvice для BookController, отвечает за обработку ошибок и отправку корректных HTTP статусов
 * @author Alexandr Filatov
 */
@ControllerAdvice(annotations = RestController.class)
public class BookControllerAdvice extends ResponseEntityExceptionHandler {
    /**
     * Отдает статус 422 Unprocessable Entity в случае невалидных аргументов метода
     * @author Alexandr Filatov
     */
    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException exception,
                                                                  HttpHeaders headers,
                                                                  HttpStatusCode status,
                                                                  WebRequest request) {
        return new ResponseEntity<>(HttpStatus.UNPROCESSABLE_ENTITY);
    }
}
