package com.study.db_connection.controller.jpa;

import static com.study.db_connection.controller.mapper.PetMapper.getPet;
import static com.study.db_connection.controller.mapper.PetMapper.getResponseDto;

import com.study.db_connection.controller.dto.PetResponseDto;
import com.study.db_connection.controller.dto.PetSaveDto;
import com.study.db_connection.controller.dto.PetUpdateDto;
import com.study.db_connection.controller.dto.ResponseDto;
import com.study.db_connection.controller.mapper.PetMapper;
import com.study.db_connection.entity.Pet;
import com.study.db_connection.service.jpa.JpaPetService;
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
@RequestMapping("/jpa/pets")
public class JpaPetController {

    private final JpaPetService petService;

    @PostMapping
    public ResponseEntity<ResponseDto> save(@RequestBody PetSaveDto requestBody) {
        Pet savedPet = petService.save(requestBody.getMemberId(), getPet(requestBody));
        return new ResponseEntity<>(new ResponseDto(getResponseDto(savedPet)), HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ResponseDto> findById(@PathVariable("id") Long id) {
        Pet findPet = petService.findById(id);
        return new ResponseEntity<>(new ResponseDto(getResponseDto(findPet)), HttpStatus.OK);
    }

    @GetMapping
    public ResponseEntity<ResponseDto> findAll() {
        List<Pet> pets = petService.findAll();
        List<PetResponseDto> result = pets.stream()
            .map(PetMapper::getResponseDto)
            .toList();
        return new ResponseEntity<>(new ResponseDto(result), HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity delete(@PathVariable("id") Long id) {
        petService.deleteById(id);
        return new ResponseEntity(HttpStatus.NO_CONTENT);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<ResponseDto> update(@PathVariable("id") Long id, @RequestBody PetUpdateDto requestBody) {
        Pet updatePet = petService.update(id, getPet(requestBody));
        return new ResponseEntity<>(new ResponseDto(getResponseDto(updatePet)), HttpStatus.OK);
    }

}
