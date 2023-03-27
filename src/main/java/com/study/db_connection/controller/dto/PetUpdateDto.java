package com.study.db_connection.controller.dto;

import lombok.Data;

@Data
public class PetUpdateDto {

    //TODO : Validation 추가 필요
    private String name;
    private String species;
    private Integer age;
    private Long memberId;
}
