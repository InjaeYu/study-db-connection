package com.study.db_connection.service.jpa;

import com.study.db_connection.entity.Member;
import com.study.db_connection.repository.jpa.JpaMemberRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class JpaMemberService {

    private final JpaMemberRepository memberRepository;

    public Member save(Member member) {
        return memberRepository.save(member);
    }

    public Member findById(Long id) {
        return memberRepository.findById(id);
    }

    public List<Member> findAll() {
        return memberRepository.findAll();
    }

    public void delete(Member member) {
        memberRepository.delete(member);
    }

    public void deleteById(Long id) {
        memberRepository.deleteById(id);
    }

    public Member update(Long id, Member member) {
        return memberRepository.update(id, member);
    }

}
