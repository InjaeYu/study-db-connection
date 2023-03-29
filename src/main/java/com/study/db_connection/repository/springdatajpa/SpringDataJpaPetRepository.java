package com.study.db_connection.repository.springdatajpa;

import com.study.db_connection.entity.Pet;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface SpringDataJpaPetRepository extends JpaRepository<Pet, Long> {

    @Query("select p from Pet p join fetch p.member where p.id = :id")
    Optional<Pet> findByIdWithMember(Long id);

    @Query("select p from Pet p left join fetch p.member")
    List<Pet> findAllWithMember();
}
