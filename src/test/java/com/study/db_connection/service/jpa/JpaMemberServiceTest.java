package com.study.db_connection.service.jpa;

import static org.assertj.core.api.Assertions.assertThat;

import com.study.db_connection.entity.Address;
import com.study.db_connection.entity.Member;
import com.study.db_connection.repository.jpa.JpaMemberRepository;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.context.annotation.Import;


@DataJpaTest
@Import(JpaMemberRepository.class)
class JpaMemberServiceTest {

    @Autowired
    TestEntityManager em;

    @Autowired
    JpaMemberRepository repository;

    @Test
    void save() {
        //given
        Member member = getMember();

        //when
        Member savedMember = repository.save(member);

        //then
        assertThat(savedMember.getId()).isEqualTo(member.getId());
        assertThat(savedMember.getName()).isEqualTo(member.getName());
        assertThat(savedMember.getAge()).isEqualTo(member.getAge());
        assertThat(savedMember.getAddress().getCity()).isEqualTo(member.getAddress().getCity());
        assertThat(savedMember.getAddress().getStreet()).isEqualTo(member.getAddress().getStreet());
        assertThat(savedMember.getAddress().getZipCode()).isEqualTo(
            member.getAddress().getZipCode());
    }

    @Test
    void findAll_데이터_없는_경우() {
        //given

        //when
        List<Member> result = repository.findAll();

        //then
        assertThat(result.isEmpty()).isTrue();
    }

    @Test
    void findAll_데이터_존재() {
        //given
        Member member = getMember();
        em.persist(member);
        em.flush();
        em.clear();

        //when
        List<Member> result = repository.findAll();

        //then
        assertThat(result.size()).isEqualTo(1);
        assertThat(result.get(0).getId()).isEqualTo(member.getId());
    }

    @Test
    void findById_데이터_없는_경우() {
        //given
        Long memberId = 100L;

        //when
        Member findMember = repository.findById(memberId);

        //then
        assertThat(findMember).isNull();
    }

    @Test
    void findById_데이터_존재() {
        //given
        Member member = getMember();
        em.persist(member);
        em.flush();
        em.clear();
        Long memberId = member.getId();

        //when
        Member findMember = repository.findById(memberId);

        //then
        assertThat(findMember).isNotNull();
        assertThat(findMember.getId()).isEqualTo(memberId);
    }

    @Test
    void deleteById() {
        //given
        Member member = getMember();
        em.persist(member);
        em.flush();
        em.clear();
        Long memberId = member.getId();

        //when
        repository.deleteById(memberId);
        em.flush();
        em.clear();

        //then
        assertThat(repository.findById(memberId)).isNull();
    }

    @Test
    void delete() {
        //given
        Member member = getMember();
        em.persist(member);
        em.flush();
        em.clear();
        Long memberId = member.getId();
        Member findMember = repository.findById(memberId);

        //when
        repository.delete(findMember);
        em.flush();
        em.clear();

        //then
        assertThat(repository.findById(memberId)).isNull();
    }

    @Test
    void deleteAll() {
        //given
        for (int i = 1; i <= 10; i++) {
            Member member = getMember(i);
            em.persist(member);
        }
        em.flush();
        em.clear();

        //when
        int size = repository.findAll().size();
        repository.deleteAll();
        em.flush();
        em.clear();

        //then
        List<Member> result = repository.findAll();
        assertThat(result.size()).isNotEqualTo(size);
        assertThat(result).isEmpty();
    }

    @Test
    void update_모든_데이터_수정() {
        //given
        Member member = getMember();
        em.persist(member);
        em.flush();
        em.clear();
        Long memberId = member.getId();
        String city = "changeCity";
        String street = "changeStreet";
        String zipCode = "1234-5678";
        Address address = new Address(city, street, zipCode);
        String name = "changeName";
        int age = 50;
        Member changeData = new Member(name, age, address);

        //when
        Member updatedMember = repository.update(memberId, changeData);

        //then
        assertThat(updatedMember.getName()).isEqualTo(name)
            .isNotEqualTo(member.getName());
        assertThat(updatedMember.getAge()).isEqualTo(age)
            .isNotEqualTo(member.getAge());
        assertThat(updatedMember.getAddress().getCity()).isEqualTo(city)
            .isNotEqualTo(member.getAddress().getCity());
        assertThat(updatedMember.getAddress().getStreet()).isEqualTo(street)
            .isNotEqualTo(member.getAddress().getStreet());
        assertThat(updatedMember.getAddress().getZipCode()).isEqualTo(zipCode)
            .isNotEqualTo(member.getAddress().getZipCode());
    }

    @Test
    void update_이름만_수정() {
        //given
        Member member = getMember();
        em.persist(member);
        em.flush();
        em.clear();
        Long memberId = member.getId();
        String name = "changeName";
        Member changeData = new Member(name, 0, null);

        //when
        Member updatedMember = repository.update(memberId, changeData);

        //then
        assertThat(updatedMember.getName()).isEqualTo(name)
            .isNotEqualTo(member.getName());
        assertThat(updatedMember.getAge()).isEqualTo(member.getAge());
        assertThat(updatedMember.getAddress().getCity()).isEqualTo(member.getAddress().getCity());
        assertThat(updatedMember.getAddress().getStreet()).isEqualTo(
            member.getAddress().getStreet());
        assertThat(updatedMember.getAddress().getZipCode()).isEqualTo(
            member.getAddress().getZipCode());
    }

    @Test
    void update_나이만_수정() {
        //given
        Member member = getMember();
        em.persist(member);
        em.flush();
        em.clear();
        Long memberId = member.getId();
        int age = 50;
        Member changeData = new Member(null, age, null);

        //when
        Member updatedMember = repository.update(memberId, changeData);

        //then
        assertThat(updatedMember.getName()).isEqualTo(member.getName());
        assertThat(updatedMember.getAge()).isEqualTo(age)
            .isNotEqualTo(member.getAge());
        assertThat(updatedMember.getAddress().getCity()).isEqualTo(member.getAddress().getCity());
        assertThat(updatedMember.getAddress().getStreet()).isEqualTo(
            member.getAddress().getStreet());
        assertThat(updatedMember.getAddress().getZipCode()).isEqualTo(
            member.getAddress().getZipCode());
    }

    @Test
    void update_도시만_수정() {
        //given
        Member member = getMember();
        em.persist(member);
        em.flush();
        em.clear();
        Long memberId = member.getId();
        String city = "changeCity";
        Address address = new Address(city, null, null);
        Member changeData = new Member(null, 0, address);

        //when
        Member updatedMember = repository.update(memberId, changeData);

        //then
        assertThat(updatedMember.getName()).isEqualTo(member.getName());
        assertThat(updatedMember.getAge()).isEqualTo(member.getAge());
        assertThat(updatedMember.getAddress().getCity()).isEqualTo(city)
            .isNotEqualTo(member.getAddress().getCity());
        assertThat(updatedMember.getAddress().getStreet()).isEqualTo(
            member.getAddress().getStreet());
        assertThat(updatedMember.getAddress().getZipCode()).isEqualTo(
            member.getAddress().getZipCode());
    }

    @Test
    void update_도로만_수정() {
        //given
        Member member = getMember();
        em.persist(member);
        em.flush();
        em.clear();
        Long memberId = member.getId();
        String street = "changeStreet";
        Address address = new Address(null, street, null);
        Member changeData = new Member(null, 0, address);

        //when
        Member updatedMember = repository.update(memberId, changeData);

        //then
        assertThat(updatedMember.getName()).isEqualTo(member.getName());
        assertThat(updatedMember.getAge()).isEqualTo(member.getAge());
        assertThat(updatedMember.getAddress().getCity()).isEqualTo(member.getAddress().getCity());
        assertThat(updatedMember.getAddress().getStreet()).isEqualTo(street)
            .isNotEqualTo(member.getAddress().getStreet());
        assertThat(updatedMember.getAddress().getZipCode()).isEqualTo(
            member.getAddress().getZipCode());
    }

    @Test
    void update_우편번호만_수정() {
        //given
        Member member = getMember();
        em.persist(member);
        em.flush();
        em.clear();
        Long memberId = member.getId();
        String zipCode = "1234-5678";
        Address address = new Address(null, null, zipCode);
        Member changeData = new Member(null, 0, address);

        //when
        Member updatedMember = repository.update(memberId, changeData);

        //then
        assertThat(updatedMember.getName()).isEqualTo(member.getName());
        assertThat(updatedMember.getAge()).isEqualTo(member.getAge());
        assertThat(updatedMember.getAddress().getCity()).isEqualTo(member.getAddress().getCity());
        assertThat(updatedMember.getAddress().getStreet()).isEqualTo(
            member.getAddress().getStreet());
        assertThat(updatedMember.getAddress().getZipCode()).isEqualTo(zipCode)
            .isNotEqualTo(member.getAddress().getZipCode());
    }

    private static Member getMember() {
        Address address = new Address("city", "street", "0000-0000");
        return new Member("userA", 10, address);
    }

    private static Member getMember(int i) {
        Address address = new Address("city" + i, "street" + i, "0000-0000" + i);
        return new Member("userA" + i, i, address);
    }
}