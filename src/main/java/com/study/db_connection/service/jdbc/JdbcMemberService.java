package com.study.db_connection.service.jdbc;

import com.study.db_connection.entity.Member;
import com.study.db_connection.repository.jdbc.JdbcMemberRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class JdbcMemberService {

    private final JdbcMemberRepository repository;

    public Member save(Member member) {
        return repository.save(member);
    }

    public Member findById(Long id) {
        return repository.findById(id);
    }

    public List<Member> findAll() {
        return repository.findAll();
    }

    public void delete(Long id) {
        repository.deleteById(id);
    }

    public Member update(Member member) {
        return repository.update(member);
    }

}
