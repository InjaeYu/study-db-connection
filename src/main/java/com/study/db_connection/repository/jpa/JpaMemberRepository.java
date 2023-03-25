package com.study.db_connection.repository.jpa;

import com.study.db_connection.entity.Member;
import jakarta.persistence.EntityManager;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class JpaMemberRepository {

    private final EntityManager em;

    @Transactional
    public Member save(Member member) {
        em.persist(member);
        return member;
    }

    public Member findById(Long id) {
        return em.find(Member.class, id);
    }

    public List<Member> findAll() {
        return em.createQuery("select m from Member m", Member.class).getResultList();
    }

    @Transactional
    public void delete(Member member) {
        em.remove(member);
    }

    @Transactional
    public void deleteById(Long id) {
        em.createQuery("delete from Member m where m.id = :id")
            .setParameter("id", id)
            .executeUpdate();
    }

    @Transactional
    public void deleteAll() {
        em.createQuery("delete from Member").executeUpdate();
    }

    @Transactional
    public Member update(Long id, Member updateData) {
        Member findMember = em.find(Member.class, id);
        findMember.update(updateData.getName(), updateData.getAge(), updateData.getAddress());
        return findMember;
    }
}
