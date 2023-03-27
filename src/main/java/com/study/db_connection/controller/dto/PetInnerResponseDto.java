package com.study.db_connection.controller.dto;

import lombok.Data;

@Data
public class PetInnerResponseDto {
    private Long id;
    private String name;
    private String species;
    private int age;

    public PetInnerResponseDto(Long id, String name, String species, int age) {
        this.id = id;
        this.name = name;
        this.species = species;
        this.age = age;
    }
}
