package com.urfu.library.controller.advice;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import javax.naming.NameAlreadyBoundException;
import java.util.NoSuchElementException;

/**
 * ControllerAdvice для BookController, отвечает за обработку ошибок и отправку корректных HTTP статусов
 * @author Alexandr Filatov
 */
@ControllerAdvice(annotations = RestController.class)
public class RestControllerAdvice extends ResponseEntityExceptionHandler {

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException exception,
                                                                  HttpHeaders headers,
                                                                  HttpStatusCode status,
                                                                  WebRequest request) {
        return new ResponseEntity<>(HttpStatus.UNPROCESSABLE_ENTITY);
    }

    /**
     * Обработчик исключений NoSuchElementException
     */
    @ExceptionHandler(NoSuchElementException.class)
    public ResponseEntity<Object> handleNoSuchElementException(NoSuchElementException exception) {
        return new ResponseEntity<>(exception.getMessage(), HttpStatus.NOT_FOUND);
    }

    /**
     * Обработчик исключений NameAlreadyBoundException
     */
    @ExceptionHandler(NameAlreadyBoundException.class)
    public ResponseEntity<Object> handleNameAlreadyBoundException(NameAlreadyBoundException exception) {
        return new ResponseEntity<>(exception.getMessage(), HttpStatus.UNPROCESSABLE_ENTITY);
    }
}