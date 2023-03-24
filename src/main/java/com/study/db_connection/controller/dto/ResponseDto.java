package com.study.db_connection.controller.dto;

import lombok.Data;

@Data
public class ResponseDto<T> {
    private T data;

    public ResponseDto(T data) {
        this.data = data;
    }
}
