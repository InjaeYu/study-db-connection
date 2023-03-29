package com.study.db_connection.service.springdatajpa;

import com.study.db_connection.entity.Pet;
import com.study.db_connection.repository.springdatajpa.SpringDataJpaPetRepository;
import java.util.List;
import java.util.NoSuchElementException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class SpringDataJpaPetService {

    private final SpringDataJpaPetRepository repository;

    @Transactional
    public Pet save(Pet pet) {
        validatePet(pet);
        return repository.save(pet);
    }

    public Pet findById(Long id) {
        validateId(id);
        return repository.findByIdWithMember(id)
            .orElseThrow(() -> new NoSuchElementException("Pet not found(id : " + id + ")"));
    }

    public List<Pet> findAll() {
        return repository.findAllWithMember();
    }

    @Transactional
    public void delete(Long id) {
        validateId(id);
        repository.deleteById(id);
    }

    @Transactional
    public void delete(Pet pet) {
        validatePet(pet);
        repository.delete(pet);
    }

    @Transactional
    public Pet update(Long id, Pet pet) {
        Pet findPet = findById(id);
        validatePet(pet);
        findPet.update(pet);
        return findPet;
    }

    private void validateId(Long id) {
        if (id == null) {
            throw new IllegalArgumentException("Id not nullable");
        }
    }

    private void validatePet(Pet pet) {
        if (pet == null) {
            throw new IllegalArgumentException("Pet not nullable");
        }
    }
}
