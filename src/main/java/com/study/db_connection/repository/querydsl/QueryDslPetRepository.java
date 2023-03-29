package com.study.db_connection.repository.querydsl;

import static com.study.db_connection.entity.QMember.member;
import static com.study.db_connection.entity.QPet.pet;

import com.querydsl.jpa.impl.JPAQueryFactory;
import com.study.db_connection.entity.Pet;
import jakarta.persistence.EntityManager;
import java.util.List;
import org.springframework.stereotype.Repository;

@Repository
public class QueryDslPetRepository {

    private final JPAQueryFactory query;

    public QueryDslPetRepository(EntityManager em) {
        this.query = new JPAQueryFactory(em);
    }

    public Pet findByIdWithMember(Long id) {
        return query
            .selectFrom(pet)
            .leftJoin(pet.member, member).fetchJoin()
            .where(pet.id.eq(id))
            .fetchOne();
    }

    public List<Pet> findAllWithMember() {
        return query
            .selectFrom(pet)
            .leftJoin(pet.member, member).fetchJoin()
            .fetch();
    }
}
