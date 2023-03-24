package com.study.db_connection.controller.dto;

import lombok.Data;

@Data
public class ErrorDto {
    private String code;
    private String message;
    private String url;

    public ErrorDto(String code, String message, String url) {
        this.code = code;
        this.message = message;
        this.url = url;
    }
}
