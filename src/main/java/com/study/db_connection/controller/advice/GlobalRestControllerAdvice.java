package com.study.db_connection.controller.advice;

import com.study.db_connection.controller.dto.ErrorDto;
import com.study.db_connection.controller.dto.ResponseDto;
import jakarta.servlet.http.HttpServletRequest;
import java.util.NoSuchElementException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class GlobalRestControllerAdvice {

    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler(NoSuchElementException.class)
    public ResponseDto noSuchElementExceptionHandler(HttpServletRequest request, NoSuchElementException e) {
        log.info(e.getMessage());
        return new ResponseDto(getErrorDto(HttpStatus.NOT_FOUND, e, request));
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler({IllegalArgumentException.class, IllegalStateException.class})
    public ResponseDto badRequestExceptionHandler(HttpServletRequest request, RuntimeException e) {
        log.info(e.getMessage());
        return new ResponseDto(getErrorDto(HttpStatus.BAD_REQUEST, e, request));
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseDto httpMessageNotReadableExceptionHandler(HttpServletRequest request, HttpMessageNotReadableException e) {
        log.info(e.getMessage());
        return new ResponseDto(getErrorDto(HttpStatus.BAD_REQUEST, "Unknown request body", request));
    }

    private ErrorDto getErrorDto(HttpStatus status, Exception e, HttpServletRequest req) {
        return new ErrorDto(status.toString(), e.getMessage(), req.getRequestURI());
    }

    private ErrorDto getErrorDto(HttpStatus status, String message, HttpServletRequest req) {
        return new ErrorDto(status.toString(), message, req.getRequestURI());
    }
}
