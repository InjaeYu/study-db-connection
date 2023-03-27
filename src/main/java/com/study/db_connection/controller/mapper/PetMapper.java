package com.study.db_connection.controller.mapper;

import com.study.db_connection.controller.dto.PetInnerResponseDto;
import com.study.db_connection.controller.dto.PetResponseDto;
import com.study.db_connection.controller.dto.PetSaveDto;
import com.study.db_connection.controller.dto.PetUpdateDto;
import com.study.db_connection.entity.Pet;

public class PetMapper {

    public static Pet getPet(PetSaveDto requestBody) {
        return new Pet(requestBody.getName(), requestBody.getSpecies(), requestBody.getAge());
    }

    public static Pet getPet(PetUpdateDto requestBody) {
        return new Pet(requestBody.getName(), requestBody.getSpecies(), requestBody.getAge());
    }

    public static PetResponseDto getResponseDto(Pet pet) {
        return new PetResponseDto(pet.getId(), pet.getName(), pet.getSpecies(), pet.getAge(), pet.getMember().getName());
    }

    public static PetInnerResponseDto getInnerResponseDto(Pet pet) {
        return new PetInnerResponseDto(pet.getId(), pet.getName(), pet.getSpecies(), pet.getAge());
    }

}
