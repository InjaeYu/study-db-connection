package com.study.db_connection.repository.jdbctemplate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.study.db_connection.entity.Address;
import com.study.db_connection.entity.Member;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class JdbcTemplateMemberRepositoryTest {

    @Autowired
    JdbcTemplateMemberRepository repository;

    @BeforeEach
    void clear() {
        repository.deleteAll();
    }

    @Test
    void save() {
        //given
        Member member = new Member("member1", 10, new Address("city1", "street1", "0000-0000"));

        //when
        Member savedMember = repository.save(member);

        //then
        assertThat(savedMember.getId()).isEqualTo(savedMember.getId());
        assertThat(savedMember.getName()).isEqualTo(member.getName());
        assertThat(savedMember.getAge()).isEqualTo(member.getAge());
        assertThat(savedMember.getAddress().getCity()).isEqualTo(member.getAddress().getCity());
        assertThat(savedMember.getAddress().getStreet()).isEqualTo(member.getAddress().getStreet());
        assertThat(savedMember.getAddress().getZipCode()).isEqualTo(
            member.getAddress().getZipCode());
    }

    @Test
    void saveFail() {
        //given
        Member member = new Member(null, 10, new Address("city1", "street1", "0000-0000"));

        //when

        //then
        assertThatThrownBy(() -> repository.save(member));

    }

    @Test
    void findById() {
        //given
        Member member = new Member("member1", 10, new Address("city1", "street1", "0000-0000"));
        Member savedMember = repository.save(member);

        //when
        Member findMember = repository.findById(savedMember.getId());

        //then
        assertThat(findMember.getId()).isEqualTo(savedMember.getId());
        assertThat(findMember.getName()).isEqualTo(member.getName());
        assertThat(findMember.getAge()).isEqualTo(member.getAge());
        assertThat(findMember.getAddress().getCity()).isEqualTo(member.getAddress().getCity());
        assertThat(findMember.getAddress().getStreet()).isEqualTo(member.getAddress().getStreet());
        assertThat(findMember.getAddress().getZipCode()).isEqualTo(
            member.getAddress().getZipCode());
    }

    @Test
    void findAll() {
        //given
        List<Member> memberList = new ArrayList<>();
        for (int i = 1; i < 11; i++) {
            memberList.add(
                new Member("member" + i, i, new Address("city" + i, "street" + i, "000-00" + i)));
        }
        memberList.forEach(repository::save);

        //when
        List<Member> result = repository.findAll();

        List<Long> idList = memberList.stream().map(Member::getId).toList();
        List<String> nameList = memberList.stream().map(Member::getName).toList();
        List<Integer> ageList = memberList.stream().map(Member::getAge).toList();
        List<String> cityList = memberList.stream().map(Member::getAddress).map(Address::getCity)
            .toList();
        List<String> streetList = memberList.stream().map(Member::getAddress)
            .map(Address::getStreet).toList();
        List<String> zipCodeList = memberList.stream().map(Member::getAddress)
            .map(Address::getZipCode).toList();

        //then
        assertThat(result).extracting("id").containsExactlyElementsOf(idList);
        assertThat(result).extracting("name").containsExactlyElementsOf(nameList);
        assertThat(result).extracting("age").containsExactlyElementsOf(ageList);
        assertThat(result).extracting("address").extracting("city")
            .containsExactlyElementsOf(cityList);
        assertThat(result).extracting("address").extracting("street")
            .containsExactlyElementsOf(streetList);
        assertThat(result).extracting("address").extracting("zipCode")
            .containsExactlyElementsOf(zipCodeList);
    }

    @Test
    void deleteById() {
        //given
        Member member = new Member("member1", 10, new Address("city1", "street1", "0000-0000"));
        Member savedMember = repository.save(member);

        //when
        repository.deleteById(savedMember.getId());

        //then
        assertThatThrownBy(() -> repository.findById(savedMember.getId())).isInstanceOf(
            NoSuchElementException.class);
    }

    @Test
    void update() {
        //given
        Member member = new Member("member1", 10, new Address("city1", "street1", "0000-0000"));
        repository.save(member);
        Member modifiedMember = new Member(member.getId(), "modifiedName", 20,
            new Address("modifiedCity", "modifiedStreet", "1111-2222"));

        //when
        Member updatedMember = repository.update(modifiedMember);

        //then
        assertThat(updatedMember.getName()).isEqualTo(modifiedMember.getName());
        assertThat(updatedMember.getAge()).isEqualTo(modifiedMember.getAge());
        assertThat(updatedMember.getAddress().getCity()).isEqualTo(
            modifiedMember.getAddress().getCity());
        assertThat(updatedMember.getAddress().getStreet()).isEqualTo(
            modifiedMember.getAddress().getStreet());
        assertThat(updatedMember.getAddress().getZipCode()).isEqualTo(
            modifiedMember.getAddress().getZipCode());
    }
}