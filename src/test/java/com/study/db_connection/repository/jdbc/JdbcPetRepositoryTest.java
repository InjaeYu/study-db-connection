package com.study.db_connection.repository.jdbc;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.study.db_connection.entity.Address;
import com.study.db_connection.entity.Member;
import com.study.db_connection.entity.Pet;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
@TestInstance(value = Lifecycle.PER_CLASS)
class JdbcPetRepositoryTest {

    @Autowired
    JdbcMemberRepository memberRepository;
    @Autowired
    JdbcPetRepository petRepository;

    @BeforeAll
    void dataInit() {
        Member member = new Member("member1", 20, new Address("city", "street", "0000-0000"));
        memberRepository.save(member);
    }

    @BeforeEach
    void clear() {
        petRepository.deleteAll();
    }

    @Test
    void save() {
        //given
        Member findMember = memberRepository.findById(1L);
        Pet pet = new Pet("Mung", "dog", 2);
        findMember.addPet(pet);

        //when
        Pet savedPet = petRepository.save(findMember.getId(), pet);

        //then
        assertThat(savedPet.getId()).isEqualTo(pet.getId());
        assertThat(savedPet.getName()).isEqualTo(pet.getName());
        assertThat(savedPet.getAge()).isEqualTo(pet.getAge());
        assertThat(savedPet.getSpecies()).isEqualTo(pet.getSpecies());
        assertThat(savedPet.getMember().getId()).isEqualTo(pet.getMember().getId());
    }

    @Test
    void saveFail() {
        //given
        Member findMember = memberRepository.findById(1L);
        Pet pet = new Pet(null, "dog", 2);
        findMember.addPet(pet);

        //when

        //then
        assertThatThrownBy(() -> petRepository.save(findMember.getId(), pet));
    }

    @Test
    void findById() {
        //given
        Member findMember = memberRepository.findById(1L);
        Pet pet = new Pet("Mung", "dog", 2);
        findMember.addPet(pet);
        petRepository.save(findMember.getId(), pet);

        //when
        Pet findPet = petRepository.findById(pet.getId());

        //then
        assertThat(findPet.getId()).isEqualTo(pet.getId());
        assertThat(findPet.getName()).isEqualTo(pet.getName());
        assertThat(findPet.getAge()).isEqualTo(pet.getAge());
        assertThat(findPet.getSpecies()).isEqualTo(pet.getSpecies());
        assertThat(findPet.getMember().getId()).isEqualTo(pet.getMember().getId());
    }

    @Test
    void findAll() {
        //given
        Member findMember = memberRepository.findById(1L);
        List<Pet> petList = new ArrayList<>();
        for (int i = 1; i < 11; i++) {
            Pet pet = new Pet("petName" + i, "species" + i, i);
            findMember.addPet(pet);
            petList.add(pet);
        }
        petList.forEach(p -> petRepository.save(p.getMember().getId(), p));

        //when
        List<Pet> result = petRepository.findAll();

        List<Long> idList = petList.stream().map(Pet::getId).toList();
        List<String> nameList = petList.stream().map(Pet::getName).toList();
        List<String> speciesList = petList.stream().map(Pet::getSpecies).toList();
        List<Integer> ageList = petList.stream().map(Pet::getAge).toList();
        List<Long> memberIdList = petList.stream().map(p -> p.getMember().getId()).toList();

        //then
        assertThat(result).extracting("id").containsExactlyElementsOf(idList);
        assertThat(result).extracting("name").containsExactlyElementsOf(nameList);
        assertThat(result).extracting("species").containsExactlyElementsOf(speciesList);
        assertThat(result).extracting("age").containsExactlyElementsOf(ageList);
        assertThat(result).extracting("member").extracting("id")
            .containsExactlyElementsOf(memberIdList);
    }

    @Test
    void deleteById() {
        //given
        Member findMember = memberRepository.findById(1L);
        Pet pet = new Pet("Mung", "dog", 2);
        findMember.addPet(pet);
        Pet savedPet = petRepository.save(findMember.getId(), pet);

        //when
        petRepository.deleteById(savedPet.getId());

        //then
        assertThatThrownBy(() -> petRepository.findById(savedPet.getId()))
            .isInstanceOf(NoSuchElementException.class);
    }
}