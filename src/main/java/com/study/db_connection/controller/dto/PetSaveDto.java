package com.study.db_connection.controller.dto;

import lombok.Data;

@Data
public class PetSaveDto {
    private String name;
    private String species;
    private Integer age;
    private Long memberId;

    public PetSaveDto(String name, String species, Integer age, Long memberId) {
        this.name = name;
        this.species = species;
        this.age = age;
        this.memberId = memberId;
    }
}
