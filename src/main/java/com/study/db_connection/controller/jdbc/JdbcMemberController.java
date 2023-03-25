package com.study.db_connection.controller.jdbc;

import static com.study.db_connection.controller.mapper.MemberMapper.getMember;
import static com.study.db_connection.controller.mapper.MemberMapper.getResponseDto;

import com.study.db_connection.controller.dto.MemberResponseDto;
import com.study.db_connection.controller.dto.MemberSaveDto;
import com.study.db_connection.controller.dto.ResponseDto;
import com.study.db_connection.controller.mapper.MemberMapper;
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
        Member member = getMember(requestBody);
        Member savedMember = memberService.save(member);
        return new ResponseEntity<>(new ResponseDto(getResponseDto(savedMember)),
            HttpStatus.CREATED);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity delete(@PathVariable("id") Long id) {
        memberService.delete(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }


    @GetMapping("/{id}")
    public ResponseEntity<ResponseDto> findById(@PathVariable("id") Long id) {
        Member findMember = memberService.findById(id);
        return new ResponseEntity<>(new ResponseDto(getResponseDto(findMember)), HttpStatus.OK);
    }


    @GetMapping
    public ResponseEntity<ResponseDto> findAll() {
        List<Member> members = memberService.findAll();
        List<MemberResponseDto> result = members.stream()
            .map(MemberMapper::getResponseDto)
            .toList();
        return new ResponseEntity<>(new ResponseDto(result), HttpStatus.OK);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<ResponseDto> update(@PathVariable("id") Long id,
        @RequestBody MemberSaveDto requestBody) {
        Member member = getMember(id, requestBody);
        Member updatedMember = memberService.update(member);
        return new ResponseEntity<>(new ResponseDto(getResponseDto(updatedMember)), HttpStatus.OK);
    }
}
