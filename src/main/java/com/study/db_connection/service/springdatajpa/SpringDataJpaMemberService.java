package com.study.db_connection.service.springdatajpa;

import com.study.db_connection.entity.Member;
import com.study.db_connection.repository.springdatajpa.SpringDataJpaMemberRepository;
import java.util.List;
import java.util.NoSuchElementException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class SpringDataJpaMemberService {

    private final SpringDataJpaMemberRepository repository;

    @Transactional
    public Member save(Member member) {
        validateMember(member);
        return repository.save(member);
    }

    public Member findById(Long id) {
        validateId(id);
        return repository.findById(id)
            .orElseThrow(() -> new NoSuchElementException("Member not found(id : " + id + ")"));
    }

    public List<Member> findAll() {
        return repository.findAll();
    }

    @Transactional
    public void delete(Long id) {
        validateId(id);
        repository.deleteById(id);
    }

    @Transactional
    public void delete(Member member) {
        validateMember(member);
        repository.delete(member);
    }

    @Transactional
    public Member update(Long id, Member updateData) {
        Member findMember = findById(id);
        validateMember(updateData);
        findMember.update(updateData.getName(), updateData.getAge(), updateData.getAddress());
        return findMember;
    }

    private void validateId(Long id) {
        if (id == null) {
            throw new IllegalArgumentException("Id not nullable");
        }
    }

    private void validateMember(Member member) {
        if (member == null) {
            throw new IllegalArgumentException("Member not nullable");
        }
    }
}
