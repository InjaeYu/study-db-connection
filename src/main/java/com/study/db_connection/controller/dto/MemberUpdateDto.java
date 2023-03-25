package com.study.db_connection.controller.dto;

import lombok.Data;

@Data
public class MemberUpdateDto {

    //TODO : Validation 추가 필요
    private String name;
    private Integer age;
    private String city;
    private String street;
    private String zipCode;
}
