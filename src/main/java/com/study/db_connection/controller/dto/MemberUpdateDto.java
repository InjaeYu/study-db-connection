package com.study.db_connection.controller.dto;

import lombok.Data;

@Data
public class MemberUpdateDto {

    //TODO : Validation 추가 필요
    private String name;
    private int age;
    private String city;
    private String street;
    private String zipCode;

    public MemberUpdateDto(String name, int age, String city, String street, String zipCode) {
        this.name = name;
        this.age = age;
        this.city = city;
        this.street = street;
        this.zipCode = zipCode;
    }
}
