package com.study.db_connection.repository.jpa;


import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;

import com.study.db_connection.entity.Address;
import com.study.db_connection.entity.Member;
import com.study.db_connection.entity.Pet;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.context.annotation.Import;

@DataJpaTest
@Import(JpaPetRepository.class)
class JpaPetRepositoryTest {

    @Autowired
    TestEntityManager em;

    @Autowired
    JpaPetRepository repository;

    @Test
    void save() {
        //given
        Member member = getMember();
        Pet pet = Pet.builder()
            .name("newPet")
            .species("newSpecies")
            .age(10)
            .member(member)
            .build();

        //when
        Pet savedPet = repository.save(pet);

        //then
        assertThat(savedPet.getId()).isEqualTo(pet.getId());
        assertThat(savedPet.getName()).isEqualTo(pet.getName());
        assertThat(savedPet.getSpecies()).isEqualTo(pet.getSpecies());
        assertThat(savedPet.getAge()).isEqualTo(pet.getAge());
    }

    @Test
    void save_멤버_없이_생성() {
        //given
        Pet pet = new Pet("newPet", "newSpecies", 10);

        //when
        Throwable thrown = catchThrowable(() -> repository.save(pet));

        //then
        assertThat(thrown)
            .isInstanceOf(IllegalStateException.class)
            .hasMessage("Member not nullable");
    }

    @Test
    void findById_데이터_존재() {
        //given
        Pet pet = addPet();
        Long petId = pet.getId();

        //when
        Pet findPet = repository.findById(petId);

        //then
        assertThat(findPet.getId()).isEqualTo(petId);
        assertThat(findPet.getName()).isEqualTo(pet.getName());
        assertThat(findPet.getSpecies()).isEqualTo(pet.getSpecies());
        assertThat(findPet.getAge()).isEqualTo(pet.getAge());
        assertThat(findPet.getCreatedDate()).isEqualTo(pet.getCreatedDate());
        assertThat(findPet.getLastModifiedDate()).isEqualTo(pet.getLastModifiedDate());
    }

    @Test
    void findById_데이터_없는_경우() {
        //given
        Long petId = 100L;

        //when
        Pet findPet = repository.findById(petId);

        //then
        assertThat(findPet).isNull();
    }

    @Test
    void findById_입력값_null() {
        //given
        Long petId = null;

        //when
        Throwable exception = catchThrowable(() -> repository.findById(petId));

        //then
        assertThat(exception)
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("Id not nullable");
    }

    @Test
    void findAll_데이터_없는_경우() {
        //given

        //when
        List<Pet> result = repository.findAll();

        //then
        assertThat(result).isEmpty();
    }

    @Test
    void deleteById_데이터_존재() {
        //given
        Long petId = addPet().getId();

        //when
        repository.deleteById(petId);
        em.clear();

        //then
        assertThat(repository.findById(petId)).isNull();
    }

    @Test
    void deleteById_데이터_없는_경우() {
        //given
        Long petId = 100L;

        //when
        Pet findPet = repository.findById(petId);

        //then
        assertThat(findPet).isNull();
    }

    @Test
    void deleteById_입력값_null() {
        //given
        Long petId = null;

        //when
        Throwable thrown = catchThrowable(() -> repository.deleteById(petId));

        //then
        assertThat(thrown)
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("Id not nullable");
    }

    @Test
    void delete_데이터_존재() {
        //given
        Long petId = addPet().getId();
        Pet findPet = repository.findById(petId);

        //when
        repository.delete(findPet);
        em.flush();
        em.clear();

        //then
        assertThat(repository.findById(petId)).isNull();
    }

    @Test
    void delete_데이터_없는_경우() {
        //given
        Pet pet = new Pet("notSavedPet", "none", 0);

        //when
        Throwable thrown = catchThrowable(() -> repository.delete(pet));

        //then
        assertThat(thrown).doesNotThrowAnyException();
    }

    @Test
    void delete_입력값_null() {
        //given
        Pet pet = null;

        //when
        Throwable thrown = catchThrowable(() -> repository.delete(pet));

        //then
        assertThat(thrown)
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("Pet not nullable");
    }

    @Test
    void deleteAll() {
        //given
        List<Member> memberList = new ArrayList<>();
        for (int i = 1; i <= 3; i++) {
            memberList.add(getMember());
        }
        for (int i = 1; i <= 10; i++) {
            addPet(i, memberList.get(i % 3));
        }
        int size = repository.findAll().size();
        em.clear();

        //when
        repository.deleteAll();
        List<Pet> result = repository.findAll();

        //then
        assertThat(result.size())
            .isNotEqualTo(size)
            .isEqualTo(0);
    }

    @Test
    void update_모든_데이터_수정() {
        //given
        Pet pet = addPet();
        Long petId = pet.getId();
        String name = "changeName";
        String species = "changeSpecies";
        int age = 5;
        Pet updateData = new Pet(name, species, age);

        //when
        Pet updatedPet = repository.update(petId, updateData);

        //then
        assertThat(updatedPet.getId()).isEqualTo(petId);
        assertThat(updatedPet.getName())
            .isNotEqualTo(pet.getName())
            .isEqualTo(name);
        assertThat(updatedPet.getSpecies())
            .isNotEqualTo(pet.getSpecies())
            .isEqualTo(species);
        assertThat(updatedPet.getAge())
            .isNotEqualTo(pet.getAge())
            .isEqualTo(age);
    }

    @Test
    void update_이름만_수정() {
        //given
        Pet pet = addPet();
        Long petId = pet.getId();
        String name = "changeName";
        String species = null;
        Integer age = null;
        Pet updateData = new Pet(name, species, age);

        //when
        Pet updatedPet = repository.update(petId, updateData);

        //then
        assertThat(updatedPet.getId()).isEqualTo(petId);
        assertThat(updatedPet.getName())
            .isNotEqualTo(pet.getName())
            .isEqualTo(name);
        assertThat(updatedPet.getSpecies())
            .isEqualTo(pet.getSpecies());
        assertThat(updatedPet.getAge())
            .isEqualTo(pet.getAge());
    }

    @Test
    void update_종만_수정() {
        //given
        Pet pet = addPet();
        Long petId = pet.getId();
        String name = null;
        String species = "changeSpecies";
        Integer age = null;
        Pet updateData = new Pet(name, species, age);

        //when
        Pet updatedPet = repository.update(petId, updateData);

        //then
        assertThat(updatedPet.getId()).isEqualTo(petId);
        assertThat(updatedPet.getName())
            .isEqualTo(pet.getName());
        assertThat(updatedPet.getSpecies())
            .isNotEqualTo(pet.getSpecies())
            .isEqualTo(species);
        assertThat(updatedPet.getAge())
            .isEqualTo(pet.getAge());
    }

    @Test
    void update_나이만_수정() {
        //given
        Pet pet = addPet();
        Long petId = pet.getId();
        String name = null;
        String species = null;
        Integer age = 5;
        Pet updateData = new Pet(name, species, age);

        //when
        Pet updatedPet = repository.update(petId, updateData);

        //then
        assertThat(updatedPet.getId()).isEqualTo(petId);
        assertThat(updatedPet.getName())
            .isEqualTo(pet.getName());
        assertThat(updatedPet.getSpecies())
            .isEqualTo(pet.getSpecies());
        assertThat(updatedPet.getAge())
            .isNotEqualTo(pet.getAge())
            .isEqualTo(age);
    }

    @Test
    void update_비어있는_데이터로_수정() {
        //given
        Pet pet = addPet();
        Long petId = pet.getId();
        String name = null;
        String species = null;
        Integer age = null;
        Pet updateData = new Pet(name, species, age);

        //when
        Pet updatedPet = repository.update(petId, updateData);

        //then
        assertThat(updatedPet.getId()).isEqualTo(petId);
        assertThat(updatedPet.getName())
            .isEqualTo(pet.getName());
        assertThat(updatedPet.getSpecies())
            .isEqualTo(pet.getSpecies());
        assertThat(updatedPet.getAge())
            .isEqualTo(pet.getAge());
    }

    @Test
    void update_존재하지_않는_pet_수정() {
        //given
        Long petId = 1000L;
        String name = "name";
        String species = "species";
        Integer age = 10;
        Pet updateData = new Pet(name, species, age);

        //when
        Throwable thrown = catchThrowable(() -> repository.update(petId, updateData));

        //then
        assertThat(thrown)
            .isInstanceOf(NoSuchElementException.class)
            .hasMessageContaining("Not found pet");
    }

    @Test
    void update_petId_입력값_null() {
        //given
        Long petId = null;
        Pet updateData = new Pet(null, null, null);

        //when
        Throwable thrown = catchThrowable(() -> repository.update(petId, updateData));

        //then
        assertThat(thrown)
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("Id not nullable");
    }

    @Test
    void update_업데이트_데이터_입력값_null() {
        //given
        Long petId = 100L;
        Pet updateData = null;

        //when
        Throwable thrown = catchThrowable(() -> repository.update(petId, updateData));

        //then
        assertThat(thrown)
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("Pet not nullable");
    }

    Pet addPet() {
        Member member = getMember();
        Pet pet = Pet.builder()
            .name("newPet")
            .species("newSpecies")
            .age(10)
            .member(member)
            .build();

        em.persist(pet);
        em.flush();
        em.clear();
        return pet;
    }

    Pet addPet(int i) {
        Member member = getMember(i);
        Pet pet = Pet.builder()
            .name("newPet" + i)
            .species("newSpecies" + i)
            .age(i)
            .member(member)
            .build();

        em.persist(pet);
        em.flush();
        em.clear();
        return pet;
    }

    Pet addPet(int i, Member member) {
        Pet pet = Pet.builder()
            .name("newPet" + i)
            .species("newSpecies" + i)
            .age(i)
            .member(member)
            .build();

        em.persist(pet);
        em.flush();
        em.clear();
        return pet;
    }

    Member getMember() {
        Address address = new Address("city", "street", "000-000");
        Member member = new Member("memberA", 10, address);
        em.persist(member);
        em.flush();
        em.clear();
        return member;
    }

    Member getMember(int i) {
        Address address = new Address("city" + i, "street" + i, String.format("000-%03d", i));
        Member member = new Member("member" + i, i, address);
        em.persist(member);
        em.flush();
        em.clear();
        return member;
    }
}