package com.study.db_connection.entity;

import com.study.db_connection.audit.TimeEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Pet extends TimeEntity {

    @Id
    @GeneratedValue
    @Setter
    private Long id;
    private String name;
    private String species;
    private int age;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    @Setter
    private Member member;

    public Pet(String name, String species, int age) {
        this.name = name;
        this.species = species;
        this.age = age;
    }
    @Builder
    private Pet(Long id, String name, String species, int age, Member member) {
        this.id = id;
        this.name = name;
        this.species = species;
        this.age = age;
        this.member = member;
    }
}
