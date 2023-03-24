package com.study.db_connection.controller.advice;

import com.study.db_connection.controller.dto.ErrorDto;
import com.study.db_connection.controller.dto.ResponseDto;
import jakarta.servlet.http.HttpServletRequest;
import java.util.NoSuchElementException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalRestControllerAdvice {

    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler(NoSuchElementException.class)
    public ResponseDto noSuchElementExceptionHandler(HttpServletRequest request, NoSuchElementException e) {
        ErrorDto errorDto = new ErrorDto(HttpStatus.NOT_FOUND.toString(), e.getMessage(),
            request.getRequestURI());
        return new ResponseDto(errorDto);
    }


}
