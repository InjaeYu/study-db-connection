package com.study.db_connection.controller.jdbc;

import com.study.db_connection.controller.dto.PetResponseDto;
import com.study.db_connection.controller.dto.PetSaveDto;
import com.study.db_connection.controller.dto.ResponseDto;
import com.study.db_connection.entity.Pet;
import com.study.db_connection.service.jdbc.JdbcPetService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/jdbc/pets")
public class JdbcPetController {

    private final JdbcPetService petService;

    @PostMapping
    public ResponseEntity<ResponseDto> save(@RequestBody PetSaveDto requestBody) {
        Pet pet = new Pet(requestBody.getName(), requestBody.getSpecies(), requestBody.getAge());
        Pet savedPet = petService.save(requestBody.getMemberId(), pet);
        PetResponseDto response = new PetResponseDto(savedPet.getId(), savedPet.getName(),
            savedPet.getSpecies(),
            savedPet.getAge(), savedPet.getMember().getName());
        return new ResponseEntity<>(new ResponseDto(response), HttpStatus.CREATED);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity delete(@PathVariable("id") Long id) {
        petService.delete(id);
        return new ResponseEntity(HttpStatus.NO_CONTENT);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ResponseDto> findById(@PathVariable("id") Long id) {
        Pet findPet = petService.findById(id);
        PetResponseDto response = new PetResponseDto(findPet.getId(), findPet.getName(),
            findPet.getSpecies(),
            findPet.getAge(), findPet.getMember().getName());
        return new ResponseEntity<>(new ResponseDto(response), HttpStatus.OK);
    }

    @GetMapping
    public ResponseEntity<ResponseDto> findAll() {
        List<Pet> pets = petService.findAll();
        List<PetResponseDto> result = pets.stream()
            .map(p -> new PetResponseDto(p.getId(), p.getName(), p.getSpecies(),
                p.getAge(), p.getMember().getName())).toList();
        return new ResponseEntity<>(new ResponseDto(result), HttpStatus.OK);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<ResponseDto> update(@PathVariable("id") Long id,
        @RequestBody PetSaveDto requestBody) {
        Pet pet = Pet.builder()
            .id(id)
            .name(requestBody.getName())
            .species(requestBody.getSpecies())
            .age(requestBody.getAge())
            .build();

        Pet updatedPet = petService.update(requestBody.getMemberId(), pet);
        PetResponseDto response = new PetResponseDto(updatedPet.getId(), updatedPet.getName(),
            updatedPet.getSpecies(),
            updatedPet.getAge(), updatedPet.getMember().getName());
        return new ResponseEntity<>(new ResponseDto(response), HttpStatus.OK);
    }
}
