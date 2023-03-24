package com.study.db_connection.controller.jdbc;

import com.study.db_connection.controller.dto.MemberResponseDto;
import com.study.db_connection.controller.dto.MemberSaveDto;
import com.study.db_connection.controller.dto.ResponseDto;
import com.study.db_connection.entity.Address;
import com.study.db_connection.entity.Member;
import com.study.db_connection.service.jdbc.JdbcMemberService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/jdbc/members")
public class JdbcMemberController {

    private final JdbcMemberService memberService;

    @PostMapping
    public ResponseEntity<ResponseDto> save(@RequestBody MemberSaveDto requestBody) {
        Member member = new Member(requestBody.getName(), requestBody.getAge(),
            new Address(requestBody.getCity(), requestBody.getStreet(), requestBody.getZipCode()));
        Member save = memberService.save(member);
        MemberResponseDto responseDto = new MemberResponseDto(save.getId(), save.getName(),
            save.getAge(), save.getAddress());
        return new ResponseEntity<>(new ResponseDto(responseDto), HttpStatus.CREATED);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity delete(@PathVariable("id") Long id) {
        memberService.delete(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }


    @GetMapping("/{id}")
    public ResponseEntity<ResponseDto> findById(@PathVariable("id") Long id) {
        Member findMember = memberService.findById(id);
        MemberResponseDto responseDto = new MemberResponseDto(findMember.getId(),
            findMember.getName(),
            findMember.getAge(),
            findMember.getAddress());
        return new ResponseEntity<>(new ResponseDto(responseDto), HttpStatus.OK);
    }


    @GetMapping
    public ResponseEntity<ResponseDto> findAll() {
        List<Member> members = memberService.findAll();
        List<MemberResponseDto> result = members.stream()
            .map(m -> new MemberResponseDto(m.getId(), m.getName(), m.getAge(), m.getAddress()))
            .toList();
        return new ResponseEntity<>(new ResponseDto(result), HttpStatus.OK);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<ResponseDto> update(@PathVariable("id") Long id,
        @RequestBody MemberSaveDto requestBody) {
        Member member = new Member(id, requestBody.getName(), requestBody.getAge(),
            new Address(requestBody.getCity(), requestBody.getStreet(), requestBody.getZipCode()));
        Member updatedMember = memberService.update(member);
        MemberResponseDto responseDto = new MemberResponseDto(updatedMember.getId(),
            updatedMember.getName(), updatedMember.getAge(), updatedMember.getAddress());
        return new ResponseEntity<>(new ResponseDto(responseDto), HttpStatus.OK);
    }

}
