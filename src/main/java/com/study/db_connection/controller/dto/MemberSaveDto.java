package com.study.db_connection.controller.dto;

import lombok.Data;

@Data
public class MemberSaveDto {

    private String name;
    private int age;
    private String city;
    private String street;
    private String zipCode;

}
