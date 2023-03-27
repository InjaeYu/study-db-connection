package com.study.db_connection.controller.mapper;

import com.study.db_connection.controller.dto.MemberResponseDto;
import com.study.db_connection.controller.dto.MemberSaveDto;
import com.study.db_connection.controller.dto.MemberUpdateDto;
import com.study.db_connection.entity.Address;
import com.study.db_connection.entity.Member;

public class MemberMapper {

    public static Member getMember(MemberSaveDto requestBody) {
        Address address = new Address(requestBody.getCity(), requestBody.getStreet(),
            requestBody.getZipCode());
        return new Member(requestBody.getName(), requestBody.getAge(), address);
    }

    public static Member getMember(MemberUpdateDto requestBody) {
        Address address = new Address(requestBody.getCity(), requestBody.getStreet(),
            requestBody.getZipCode());
        return new Member(requestBody.getName(), requestBody.getAge(), address);
    }

    public static Member getMember(Long id, MemberSaveDto requestBody) {
        Address address = new Address(requestBody.getCity(), requestBody.getStreet(),
            requestBody.getZipCode());
        return new Member(id, requestBody.getName(), requestBody.getAge(), address);
    }

    public static MemberResponseDto getResponseDto(Member member) {
        MemberResponseDto responseDto = new MemberResponseDto(member.getId(), member.getName(),
            member.getAge(), member.getAddress());
        member.getPets().forEach(p -> responseDto.getPets().add(PetMapper.getResponseDto(p)));
        return responseDto;
    }
}
