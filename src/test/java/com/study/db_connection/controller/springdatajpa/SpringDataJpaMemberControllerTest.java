package com.study.db_connection.controller.springdatajpa;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.study.db_connection.controller.dto.MemberSaveDto;
import com.study.db_connection.entity.Member;
import com.study.db_connection.service.springdatajpa.SpringDataJpaMemberService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

@WebMvcTest(SpringDataJpaMemberController.class)
class SpringDataJpaMemberControllerTest {

    @Autowired
    MockMvc mockMvc;

    @MockBean
    SpringDataJpaMemberService service;

    ObjectMapper mapper = new ObjectMapper();

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
            .andExpect(jsonPath("$.data.id").value(memberId))
            .andExpect(jsonPath("$.data.name").value(name))
            .andExpect(jsonPath("$.data.age").value(age))
            .andExpect(jsonPath("$.data.city").value(city))
            .andExpect(jsonPath("$.data.street").value(street))
            .andExpect(jsonPath("$.data.zipCode").value(zipCode));
    }

    @Test
    void findAll() {
        //given

        //when

        //then

    }

    @Test
    void findById() {
        //given

        //when

        //then

    }

    @Test
    void delete() {
        //given

        //when

        //then

    }

    @Test
    void update() {
        //given

        //when

        //then

    }

}