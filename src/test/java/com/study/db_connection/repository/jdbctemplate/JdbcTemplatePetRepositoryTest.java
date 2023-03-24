package com.study.db_connection.repository.jdbctemplate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatNoException;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.study.db_connection.entity.Address;
import com.study.db_connection.entity.Member;
import com.study.db_connection.entity.Pet;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.InvalidDataAccessApiUsageException;

@SpringBootTest
@TestInstance(value = Lifecycle.PER_CLASS)
class JdbcTemplatePetRepositoryTest {

    @Autowired
    JdbcTemplateMemberRepository memberRepository;
    @Autowired
    JdbcTemplatePetRepository petRepository;

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
    @DisplayName("존재하지 않는 유저 조회시 예외 발생")
    void findByIdFail_1() {
        assertThatThrownBy(() -> petRepository.findById(100L)).isInstanceOf(
            NoSuchElementException.class);
    }

    @Test
    @DisplayName("null 조회시 예외 발생")
    void findByIdFail_2() {
        assertThatThrownBy(() -> petRepository.findById(null)).isInstanceOf(
            InvalidDataAccessApiUsageException.class);
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

    @Test
    @DisplayName("존재하지 않는 유저 삭제시 예외 발생하지 않음")
    void deleteByIdNotExistsPetId() {
        assertThatNoException().isThrownBy(() -> petRepository.deleteById(100L));
    }

    @Test
    @DisplayName("null 삭제시 예외 발생")
    void deleteByIdFail() {
        assertThatThrownBy(() -> petRepository.deleteById(null)).isInstanceOf(
            InvalidDataAccessApiUsageException.class);
    }

    @Test
    void update() {
        //given
        Member findMember = memberRepository.findById(1L);
        Pet pet = new Pet("Mung", "dog", 1);
        findMember.addPet(pet);
        petRepository.save(findMember.getId(), pet);
        Pet modifiedPet = Pet.builder()
            .id(pet.getId())
            .name("Mew")
            .species("cat")
            .age(2)
            .build();

        //when
        Pet updatedPet = petRepository.update(findMember.getId(), modifiedPet);

        //then
        assertThat(updatedPet.getId()).isEqualTo(modifiedPet.getId());
        assertThat(updatedPet.getName()).isEqualTo(modifiedPet.getName());
        assertThat(updatedPet.getAge()).isEqualTo(modifiedPet.getAge());
        assertThat(updatedPet.getSpecies()).isEqualTo(modifiedPet.getSpecies());
    }
}