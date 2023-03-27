package com.study.db_connection.service.springdatajpa;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchException;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

import com.study.db_connection.entity.Address;
import com.study.db_connection.entity.Member;
import com.study.db_connection.repository.springdatajpa.SpringDataJpaMemberRepository;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

@ExtendWith(MockitoExtension.class)
class SpringDataJpaMemberServiceTest {

    @Mock
    SpringDataJpaMemberRepository repository;

    @InjectMocks
    SpringDataJpaMemberService service;

    @Test
    void save() {
        //given
        Long memberId = 1L;
        Member member = getMember();
        given(repository.save(any(Member.class))).willAnswer(invocation -> {
            Member result = invocation.getArgument(0);
            ReflectionTestUtils.setField(result, "id", memberId);
            return result;
        });

        //when
        Member savedMember = service.save(member);

        //then
        then(repository).should().save(member);
        assertThat(savedMember.getId()).isEqualTo(memberId);
        assertThat(savedMember.getName()).isEqualTo(member.getName());
        assertThat(savedMember.getAge()).isEqualTo(member.getAge());
        assertThat(savedMember.getAddress().getCity()).isEqualTo(member.getAddress().getCity());
        assertThat(savedMember.getAddress().getStreet()).isEqualTo(member.getAddress().getStreet());
        assertThat(savedMember.getAddress().getZipCode()).isEqualTo(
            member.getAddress().getZipCode());
    }

    @Test
    void save_입력값_null() {
        //given
        Member member = null;

        //when
        Exception exception = catchException((() -> service.save(member)));

        //then
        assertThat(exception).isNotNull()
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("Member not nullable");
    }

    @Test
    void findById() {
        //given
        given(repository.findById(anyLong())).willAnswer(invocation -> {
            Member member = getMember();
            ReflectionTestUtils.setField(member, "id", invocation.getArgument(0));
            return Optional.of(member);
        });
        Long memberId = 10L;

        //when
        Member findMember = service.findById(memberId);

        //then
        assertThat(findMember).isNotNull();
        assertThat(findMember.getId()).isEqualTo(memberId);
    }

    @Test
    void findById_존재하지_않는_데이터_조회() {
        //given
        given(repository.findById(anyLong())).willReturn(Optional.empty());
        Long memberId = 10L;

        //when
        Exception exception = catchException(() -> service.findById(memberId));

        //then
        assertThat(exception).isNotNull()
            .isInstanceOf(NoSuchElementException.class)
            .hasMessage("Member not found(id : " + memberId + ")");
    }

    @Test
    void findById_입력값_null() {
        //given
        Long memberId = null;

        //when
        Exception exception = catchException(() -> service.findById(memberId));

        //then
        assertThat(exception).isNotNull()
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("Id not nullable");
    }

    @Test
    void findAll() {
        //given
        int size = 10;
        List<Member> memberList = new ArrayList<>();
        for (int i = 0; i < size; i++) {
            Member member = getMember(i);
            ReflectionTestUtils.setField(member, "id", i + 1L);
            memberList.add(member);
        }
        given(repository.findAll()).willReturn(memberList);

        //when
        List<Member> members = service.findAll();

        //then
        assertThat(members.size()).isEqualTo(size);
    }

    @Test
    void deleteById() {
        //given
        Long memberId = 10L;

        //when
        Exception exception = catchException(() -> service.delete(memberId));

        //then
        assertThat(exception).isNull();
    }

    @Test
    void deleteById_입력값_null() {
        //given
        Long memberId = null;

        //when
        Exception exception = catchException(() -> service.delete(memberId));

        //then
        assertThat(exception).isNotNull()
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("Id not nullable");
    }

    @Test
    void testDelete() {
        //given
        Member member = getMember();

        //when
        Exception exception = catchException(() -> service.delete(member));

        //then
        assertThat(exception).isNull();
    }

    @Test
    void testDelete_입력값_null() {
        //given
        Member member = null;

        //when
        Exception exception = catchException(() -> service.delete(member));

        //then
        assertThat(exception).isNotNull()
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("Member not nullable");
    }

    @Test
    void update() {
        //given
        Long memberId = 10L;
        String city = "changeCity";
        String street = "changeStreet";
        String zipCode = "1234-5678";
        String name = "changeName";
        int age = 50;
        Address address = new Address(city, street, zipCode);
        Member updateData = new Member(name, age, address);
        given(repository.findById(anyLong())).willAnswer(invocation -> {
            Member member = getMember();
            ReflectionTestUtils.setField(member, "id", invocation.getArgument(0));
            return Optional.of(member);
        });

        //when
        Member oldMember = service.findById(memberId);
        Member updatedMember = service.update(memberId, updateData);

        //then
        assertThat(updatedMember.getId()).isEqualTo(memberId);
        assertThat(updatedMember.getName()).isNotEqualTo(oldMember.getName())
            .isEqualTo(name);
        assertThat(updatedMember.getAge()).isNotEqualTo(oldMember.getAge())
            .isEqualTo(age);
        assertThat(updatedMember.getAddress().getCity())
            .isNotEqualTo(oldMember.getAddress().getCity())
            .isEqualTo(city);
        assertThat(updatedMember.getAddress().getStreet())
            .isNotEqualTo(oldMember.getAddress().getStreet())
            .isEqualTo(street);
        assertThat(updatedMember.getAddress().getZipCode())
            .isNotEqualTo(oldMember.getAddress().getZipCode())
            .isEqualTo(zipCode);
    }

    @Test
    void update_이름만_변경() {
        //given
        Long memberId = 10L;
        String city = null;
        String street = null;
        String zipCode = null;
        String name = "changeName";
        int age = 0;
        Address address = new Address(city, street, zipCode);
        Member updateData = new Member(name, age, address);
        given(repository.findById(anyLong())).willAnswer(invocation -> {
            Member member = getMember();
            ReflectionTestUtils.setField(member, "id", invocation.getArgument(0));
            return Optional.of(member);
        });

        //when
        Member oldMember = service.findById(memberId);
        Member updatedMember = service.update(memberId, updateData);

        //then
        assertThat(updatedMember.getId()).isEqualTo(memberId);
        assertThat(updatedMember.getName()).isNotEqualTo(oldMember.getName())
            .isEqualTo(name);
        assertThat(updatedMember.getAge()).isEqualTo(oldMember.getAge());
        assertThat(updatedMember.getAddress().getCity())
            .isEqualTo(oldMember.getAddress().getCity());
        assertThat(updatedMember.getAddress().getStreet())
            .isEqualTo(oldMember.getAddress().getStreet());
        assertThat(updatedMember.getAddress().getZipCode())
            .isEqualTo(oldMember.getAddress().getZipCode());
    }

    @Test
    void update_나이만_변경() {
        //given
        Long memberId = 10L;
        String city = null;
        String street = null;
        String zipCode = null;
        String name = null;
        int age = 50;
        Address address = new Address(city, street, zipCode);
        Member updateData = new Member(name, age, address);
        given(repository.findById(anyLong())).willAnswer(invocation -> {
            Member member = getMember();
            ReflectionTestUtils.setField(member, "id", invocation.getArgument(0));
            return Optional.of(member);
        });

        //when
        Member oldMember = service.findById(memberId);
        Member updatedMember = service.update(memberId, updateData);

        //then
        assertThat(updatedMember.getId()).isEqualTo(memberId);
        assertThat(updatedMember.getName()).isEqualTo(oldMember.getName());
        assertThat(updatedMember.getAge()).isNotEqualTo(oldMember.getAge())
            .isEqualTo(age);
        assertThat(updatedMember.getAddress().getCity())
            .isEqualTo(oldMember.getAddress().getCity());
        assertThat(updatedMember.getAddress().getStreet())
            .isEqualTo(oldMember.getAddress().getStreet());
        assertThat(updatedMember.getAddress().getZipCode())
            .isEqualTo(oldMember.getAddress().getZipCode());
    }

    @Test
    void update_도시만_변경() {
        //given
        Long memberId = 10L;
        String city = "newCity";
        String street = null;
        String zipCode = null;
        String name = null;
        int age = 0;
        Address address = new Address(city, street, zipCode);
        Member updateData = new Member(name, age, address);
        given(repository.findById(anyLong())).willAnswer(invocation -> {
            Member member = getMember();
            ReflectionTestUtils.setField(member, "id", invocation.getArgument(0));
            return Optional.of(member);
        });

        //when
        Member oldMember = service.findById(memberId);
        Member updatedMember = service.update(memberId, updateData);

        //then
        assertThat(updatedMember.getId()).isEqualTo(memberId);
        assertThat(updatedMember.getName()).isEqualTo(oldMember.getName());
        assertThat(updatedMember.getAge()).isEqualTo(oldMember.getAge());
        assertThat(updatedMember.getAddress().getCity())
            .isNotEqualTo(oldMember.getAddress().getCity())
            .isEqualTo(city);
        assertThat(updatedMember.getAddress().getStreet())
            .isEqualTo(oldMember.getAddress().getStreet());
        assertThat(updatedMember.getAddress().getZipCode())
            .isEqualTo(oldMember.getAddress().getZipCode());
    }

    @Test
    void update_도로만_변경() {
        //given
        Long memberId = 10L;
        String city = null;
        String street = "newCity";
        String zipCode = null;
        String name = null;
        int age = 0;
        Address address = new Address(city, street, zipCode);
        Member updateData = new Member(name, age, address);
        given(repository.findById(anyLong())).willAnswer(invocation -> {
            Member member = getMember();
            ReflectionTestUtils.setField(member, "id", invocation.getArgument(0));
            return Optional.of(member);
        });

        //when
        Member oldMember = service.findById(memberId);
        Member updatedMember = service.update(memberId, updateData);

        //then
        assertThat(updatedMember.getId()).isEqualTo(memberId);
        assertThat(updatedMember.getName()).isEqualTo(oldMember.getName());
        assertThat(updatedMember.getAge()).isEqualTo(oldMember.getAge());
        assertThat(updatedMember.getAddress().getCity())
            .isEqualTo(oldMember.getAddress().getCity());
        assertThat(updatedMember.getAddress().getStreet())
            .isNotEqualTo(oldMember.getAddress().getStreet())
            .isEqualTo(street);
        assertThat(updatedMember.getAddress().getZipCode())
            .isEqualTo(oldMember.getAddress().getZipCode());
    }

    @Test
    void update_우편번호만_변경() {
        //given
        Long memberId = 10L;
        String city = null;
        String street = null;
        String zipCode = "1234-5678";
        String name = null;
        int age = 0;
        Address address = new Address(city, street, zipCode);
        Member updateData = new Member(name, age, address);
        given(repository.findById(anyLong())).willAnswer(invocation -> {
            Member member = getMember();
            ReflectionTestUtils.setField(member, "id", invocation.getArgument(0));
            return Optional.of(member);
        });

        //when
        Member oldMember = service.findById(memberId);
        Member updatedMember = service.update(memberId, updateData);

        //then
        assertThat(updatedMember.getId()).isEqualTo(memberId);
        assertThat(updatedMember.getName()).isEqualTo(oldMember.getName());
        assertThat(updatedMember.getAge()).isEqualTo(oldMember.getAge());
        assertThat(updatedMember.getAddress().getCity())
            .isEqualTo(oldMember.getAddress().getCity());
        assertThat(updatedMember.getAddress().getStreet())
            .isEqualTo(oldMember.getAddress().getStreet());
        assertThat(updatedMember.getAddress().getZipCode())
            .isNotEqualTo(oldMember.getAddress().getZipCode())
            .isEqualTo(zipCode);
    }

    @Test
    void update_주소_null() {
        //given
        Long memberId = 10L;
        String name = "newName";
        int age = 30;
        Address address = null;
        Member updateData = new Member(name, age, address);
        given(repository.findById(anyLong())).willAnswer(invocation -> {
            Member member = getMember();
            ReflectionTestUtils.setField(member, "id", invocation.getArgument(0));
            return Optional.of(member);
        });

        //when
        Member oldMember = service.findById(memberId);
        Member updatedMember = service.update(memberId, updateData);

        //then
        assertThat(updatedMember.getId()).isEqualTo(memberId);
        assertThat(updatedMember.getName()).isNotEqualTo(oldMember.getName())
            .isEqualTo(name);
        assertThat(updatedMember.getAge()).isNotEqualTo(oldMember.getAge())
            .isEqualTo(age);
        assertThat(updatedMember.getAddress().getCity())
            .isEqualTo(oldMember.getAddress().getCity());
        assertThat(updatedMember.getAddress().getStreet())
            .isEqualTo(oldMember.getAddress().getStreet());
        assertThat(updatedMember.getAddress().getZipCode())
            .isEqualTo(oldMember.getAddress().getZipCode());
    }

    @Test
    void update_존재하지_않는_멤버() {
        //given
        Long memberId = 100L;
        String name = null;
        int age = 0;
        Address address = null;
        Member updateData = new Member(name, age, address);

        //when
        Exception exception = catchException(() -> service.update(memberId, updateData));

        //then
        assertThat(exception).isNotNull()
            .isInstanceOf(NoSuchElementException.class)
            .hasMessage("Member not found(id : " + memberId + ")");
    }

    @Test
    void update_아이디_null() {
        //given
        Long memberId = null;
        String name = null;
        int age = 0;
        Address address = null;
        Member updateData = new Member(name, age, address);

        //when
        Exception exception = catchException(() -> service.update(memberId, updateData));

        //then
        assertThat(exception).isNotNull()
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("Id not nullable");
    }

    @Test
    void update_수정_데이터_null() {
        //given
        Long memberId = 10L;
        Member updateData = null;
        given(repository.findById(anyLong())).willAnswer(invocation -> {
            Member member = getMember();
            ReflectionTestUtils.setField(member, "id", invocation.getArgument(0));
            return Optional.of(member);
        });

        //when
        Exception exception = catchException(() -> service.update(memberId, updateData));

        //then
        assertThat(exception).isNotNull()
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("Member not nullable");
    }

    private Member getMember() {
        Address address = new Address("city", "street", "0000-0000");
        return new Member("userA", 10, address);
    }

    private Member getMember(int i) {
        Address address = new Address("city" + i, "street" + i, String.format("0000-%04d", i));
        return new Member("user" + i, i, address);
    }
}