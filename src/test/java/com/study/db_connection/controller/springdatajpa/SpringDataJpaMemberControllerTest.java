package com.study.db_connection.controller.springdatajpa;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willThrow;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.study.db_connection.controller.dto.MemberSaveDto;
import com.study.db_connection.controller.dto.MemberUpdateDto;
import com.study.db_connection.entity.Address;
import com.study.db_connection.entity.Member;
import com.study.db_connection.service.springdatajpa.SpringDataJpaMemberService;
import java.util.NoSuchElementException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

@WebMvcTest(SpringDataJpaMemberController.class)
class SpringDataJpaMemberControllerTest {

    @Autowired
    MockMvc mockMvc;
    @Autowired
    ObjectMapper mapper;
    @MockBean
    SpringDataJpaMemberService service;

    @Test
    void save() throws Exception {
        //given
        Long memberId = 10L;
        given(service.save(any(Member.class))).willAnswer(invocation -> {
            Member member = invocation.getArgument(0);
            ReflectionTestUtils.setField(member, "id", memberId);
            return member;
        });
        String name = "memberA";
        int age = 20;
        String city = "city";
        String street = "street";
        String zipCode = "1234-5678";
        MemberSaveDto dto = new MemberSaveDto(name, age, city, street, zipCode);
        String content = mapper.writeValueAsString(dto);

        //when
        ResultActions actions = mockMvc.perform(
            post("/spring-data-jpa/members")
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(content)
        );

        //then
        actions.andExpect(status().isCreated())
            .andExpect(jsonPath("$.data").isNotEmpty())
            .andExpect(jsonPath("$.data.id").value(memberId))
            .andExpect(jsonPath("$.data.name").value(name))
            .andExpect(jsonPath("$.data.age").value(age))
            .andExpect(jsonPath("$.data.city").value(city))
            .andExpect(jsonPath("$.data.street").value(street))
            .andExpect(jsonPath("$.data.zipCode").value(zipCode));
    }

    @Test
    void findAll() throws Exception {
        //given

        //when
        ResultActions actions = mockMvc.perform(
            get("/spring-data-jpa/members")
                .accept(MediaType.APPLICATION_JSON)
        );

        //then
        actions.andExpect(status().isOk())
            .andExpect(jsonPath("$.data").isArray())
            .andExpect(jsonPath("$.data").isEmpty());
    }

    @Test
    void findById() throws Exception {
        //given
        String name = "memberA";
        int age = 10;
        String city = "newCity";
        String street = "newStreet";
        String zipCode = "012-3324";
        given(service.findById(anyLong())).willAnswer(invocation -> {
            Long id = invocation.getArgument(0);
            Member member = getMember(name, age, city, street, zipCode);
            ReflectionTestUtils.setField(member, "id", id);
            return member;
        });
        Long memberId = 10L;

        //when
        ResultActions actions = mockMvc.perform(
            get("/spring-data-jpa/members/{id}", memberId)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
        );

        //then
        actions.andExpect(status().isOk())
            .andExpect(jsonPath("$.data").isNotEmpty())
            .andExpect(jsonPath("$.data.id").value(memberId))
            .andExpect(jsonPath("$.data.name").value(name))
            .andExpect(jsonPath("$.data.age").value(age))
            .andExpect(jsonPath("$.data.city").value(city))
            .andExpect(jsonPath("$.data.street").value(street))
            .andExpect(jsonPath("$.data.zipCode").value(zipCode));
    }

    @Test
    void findById_존재하지_않는_유저() throws Exception {
        //given
        Long memberId = 10L;
        given(service.findById(anyLong())).willThrow(
            new NoSuchElementException("Member not found(id : " + memberId + ")"));

        //when
        ResultActions actions = mockMvc.perform(
            get("/spring-data-jpa/members/{id}", memberId)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
        );

        //then
        actions.andExpect(status().isNotFound())
            .andExpect(jsonPath("$.data").isNotEmpty())
            .andExpect(jsonPath("$.data.code").value(HttpStatus.NOT_FOUND.toString()))
            .andExpect(jsonPath("$.data.message").value("Member not found(id : " + memberId + ")"))
            .andExpect(jsonPath("$.data.url").value("/spring-data-jpa/members/" + memberId));
    }

    @Test
    void delete() throws Exception {
        //given
        Long memberId = 10L;

        //when
        ResultActions actions = mockMvc.perform(
            MockMvcRequestBuilders.delete("/spring-data-jpa/members/{id}", memberId)
        );

        //then
        actions.andExpect(status().isNoContent());
    }

    @Test
    void delete_존재하지_않는_유저() throws Exception {
        //given
        Long memberId = 10L;
        willThrow(new NoSuchElementException("Member not found(id : " + memberId + ")")).given(
            service).delete(anyLong());

        //when
        ResultActions actions = mockMvc.perform(
            MockMvcRequestBuilders.delete("/spring-data-jpa/members/{id}", memberId)
        );

        //then
        actions.andExpect(status().isNotFound())
            .andExpect(jsonPath("$.data").isNotEmpty())
            .andExpect(jsonPath("$.data.code").value(HttpStatus.NOT_FOUND.toString()))
            .andExpect(jsonPath("$.data.message").value("Member not found(id : " + memberId + ")"))
            .andExpect(jsonPath("$.data.url").value("/spring-data-jpa/members/" + memberId));
    }

    @Test
    void update() throws Exception {
        //given
        given(service.update(anyLong(), any(Member.class))).willAnswer(invocation -> {
            Long id = invocation.getArgument(0);
            Member member = invocation.getArgument(1);
            ReflectionTestUtils.setField(member, "id", id);
            return member;
        });
        Long memberId = 10L;
        String name = "name";
        int age = 10;
        String city = "city", street = "street", zipCode = "000-0000";
        MemberUpdateDto dto = new MemberUpdateDto(name, age, city, street, zipCode);
        String content = mapper.writeValueAsString(dto);

        //when
        ResultActions actions = mockMvc.perform(
            patch("/spring-data-jpa/members/{id}", memberId)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(content)
        );

        //then
        actions.andExpect(status().isOk())
            .andExpect(jsonPath("$.data.id").value(memberId))
            .andExpect(jsonPath("$.data.name").value(name))
            .andExpect(jsonPath("$.data.age").value(age))
            .andExpect(jsonPath("$.data.city").value(city))
            .andExpect(jsonPath("$.data.street").value(street))
            .andExpect(jsonPath("$.data.zipCode").value(zipCode));
    }

    @Test
    void update_존재하지_않는_멤버() throws Exception {
        //given
        Long memberId = 10L;
        given(service.update(anyLong(), any(Member.class))).willThrow(
            new NoSuchElementException("Member not found(id : " + memberId + ")"));
        String name = "name";
        int age = 10;
        String city = "city", street = "street", zipCode = "000-0000";
        MemberUpdateDto dto = new MemberUpdateDto(name, age, city, street, zipCode);
        String content = mapper.writeValueAsString(dto);

        //when
        ResultActions actions = mockMvc.perform(
            patch("/spring-data-jpa/members/{id}", memberId)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(content)
        );

        //then
        actions.andExpect(status().isNotFound())
            .andExpect(jsonPath("$.data").isNotEmpty())
            .andExpect(jsonPath("$.data.code").value(HttpStatus.NOT_FOUND.toString()))
            .andExpect(jsonPath("$.data.message").value("Member not found(id : " + memberId + ")"))
            .andExpect(jsonPath("$.data.url").value("/spring-data-jpa/members/" + memberId));
    }

    private Member getMember(String name, int age, String city, String street, String zipCode) {
        Address address = new Address(city, street, zipCode);
        return new Member(name, age, address);
    }

}