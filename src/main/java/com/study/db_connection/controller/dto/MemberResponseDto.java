package com.study.db_connection.controller.dto;

import com.study.db_connection.entity.Address;
import java.util.ArrayList;
import java.util.List;
import lombok.Data;

@Data
public class MemberResponseDto {

    private Long id;
    private String name;

    private int age;
    private String city;
    private String street;
    private String zipCode;

    private List<PetInnerResponseDto> pets = new ArrayList<>();

    public MemberResponseDto(Long id, String name, int age, Address address) {
        this.id = id;
        this.name = name;
        this.age = age;
        this.city = address.getCity();
        this.street = address.getStreet();
        this.zipCode = address.getZipCode();
    }
}
