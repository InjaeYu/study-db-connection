package com.study.db_connection.service.jpa;

import com.study.db_connection.entity.Member;
import com.study.db_connection.entity.Pet;
import com.study.db_connection.repository.jpa.JpaMemberRepository;
import com.study.db_connection.repository.jpa.JpaPetRepository;
import java.util.List;
import java.util.NoSuchElementException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class JpaPetService {
    private final JpaMemberRepository memberRepository;
    private final JpaPetRepository petRepository;

    public Pet save(Long memberId, Pet pet) {
        if (memberId == null) {
            throw new IllegalArgumentException("MemberId not nullable");
        }
        if (pet == null) {
            throw new IllegalArgumentException("Pet not nullable");
        }
        Member findMember = memberRepository.findById(memberId);
        if (findMember == null) {
            throw new NoSuchElementException("Member not found(id : " + memberId + ")");
        }
        pet.setMember(findMember);
        return petRepository.save(pet);
    }

    public Pet findById(Long id) {
        if (id == null) {
            throw new IllegalArgumentException("Id not nullable");
        }
        return petRepository.findById(id);
    }

    public List<Pet> findAll() {
        return petRepository.findAll();
    }

    public void delete(Pet pet) {
        if (pet == null) {
            throw new IllegalArgumentException("pet not nullable");
        }
        petRepository.delete(pet);
    }

    public void deleteById(Long id) {
        if (id == null) {
            throw new IllegalArgumentException("Id not nullable");
        }
        petRepository.deleteById(id);
    }

    public Pet update(Long id, Pet updateData) {
        if (id == null) {
            throw new IllegalArgumentException("Id not nullable");
        }
        if (updateData == null) {
            throw new IllegalArgumentException("Update data not nullable");
        }
        return petRepository.update(id, updateData);
    }

}
