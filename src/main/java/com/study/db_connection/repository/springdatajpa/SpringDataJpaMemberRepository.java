package com.study.db_connection.repository.springdatajpa;

import com.study.db_connection.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SpringDataJpaMemberRepository extends JpaRepository<Member, Long> {

}
