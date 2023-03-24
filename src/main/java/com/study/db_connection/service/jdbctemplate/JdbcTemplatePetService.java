package com.study.db_connection.service.jdbctemplate;

import com.study.db_connection.entity.Pet;
import com.study.db_connection.repository.jdbctemplate.JdbcTemplatePetRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class JdbcTemplatePetService {

    private final JdbcTemplatePetRepository repository;

    public Pet save(Long memberId, Pet pet) {
        return repository.save(memberId, pet);
    }

    public Pet findById(Long id) {
        return repository.findById(id);
    }

    public List<Pet> findAll() {
        return repository.findAll();
    }

    public void delete(Long id) {
        repository.deleteById(id);
    }

    public Pet update(Long memberId, Pet pet) {
        return repository.update(memberId, pet);
    }

}
