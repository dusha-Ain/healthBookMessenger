package com.rent.healthbookmessenger.web;

import com.rent.healthbookmessenger.web.exception.EntityNotFoundException;
import com.rent.healthbookmessenger.web.exception.EntityNotFoundInternalException;
import com.rent.healthbookmessenger.web.exception.EntityStateException;
import com.rent.healthbookmessenger.web.exception.ValidationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.TypeMismatchException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.time.LocalDateTime;


@ControllerAdvice
public class GlobalExceptionHandler {

    private final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    //Отлавливает все ошибки, если они отдельно не описываются
    @ExceptionHandler(exception = {
            Exception.class,
            EntityNotFoundInternalException.class
    })
    public ResponseEntity<ErrorResponseDto> handleGenericException(Exception e) {
        logger.info("Handle exception");
        var error = new ErrorResponseDto(
                "Internal server error",
                e.getMessage(),
                LocalDateTime.now()
        );
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
    }


    //Отлавливает ошибки если проблемы с передаваемыми аргументами
    @ExceptionHandler(exception = {
            IllegalStateException.class,
            IllegalArgumentException.class,
            MethodArgumentNotValidException.class,
            ValidationException.class,
            DataIntegrityViolationException.class,
            EntityStateException.class,
            MissingServletRequestParameterException.class,
            TypeMismatchException.class
    })
    public ResponseEntity<ErrorResponseDto> handleBadRequest(Exception e) {
        logger.info("Handle Illegal argument");
        var error = new ErrorResponseDto(
                "Bad request",
                e.getMessage(),
                LocalDateTime.now()
        );
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }


    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErrorResponseDto> handleJsonParseException(Exception e) {
        logger.info("Handle json parse exception");
        var error = new ErrorResponseDto(
                "Invalid json",
                e.getMessage(),
                LocalDateTime.now()
        );
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }


    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<ErrorResponseDto> handleNotFoundException(Exception e) {
        logger.info("Handle not found");
        var error = new ErrorResponseDto(
                "Not found entity",
                e.getMessage(),
                LocalDateTime.now()
        );
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
    }
}
