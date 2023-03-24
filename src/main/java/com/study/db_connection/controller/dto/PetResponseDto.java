package com.study.db_connection.controller.dto;

import lombok.Data;

@Data
public class PetResponseDto {
    private Long id;
    private String name;
    private String species;
    private int age;
    private String userName;

    public PetResponseDto(Long id, String name, String species, int age, String userName) {
        this.id = id;
        this.name = name;
        this.species = species;
        this.age = age;
        this.userName = userName;
    }
}
