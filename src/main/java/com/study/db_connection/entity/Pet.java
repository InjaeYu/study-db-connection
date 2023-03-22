package com.study.db_connection.entity;

import com.study.db_connection.audit.TimeEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.Getter;

@Entity
@Getter
public class Pet extends TimeEntity {

    @Id
    @GeneratedValue
    private Long id;
    private String name;
    private String species;
    private int age;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private Member member;
}
