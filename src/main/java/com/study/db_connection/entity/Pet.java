package com.study.db_connection.entity;

import com.study.db_connection.audit.TimeEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.util.StringUtils;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Pet extends TimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Setter
    private Long id;
    private String name;
    private String species;
    private Integer age;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    @Setter
    private Member member;

    public Pet(String name, String species, Integer age) {
        this.name = name;
        this.species = species;
        this.age = age;
    }
    @Builder
    private Pet(Long id, String name, String species, Integer age, Member member) {
        this.id = id;
        this.name = name;
        this.species = species;
        this.age = age;
        this.member = member;
    }

    public void update(Pet updateData) {
        if (StringUtils.hasText(updateData.getName())) {
            this.name = updateData.getName();
        }
        if (StringUtils.hasText(updateData.getSpecies())) {
            this.species = updateData.getSpecies();
        }
        if (updateData.getAge() != null) {
            this.age = updateData.getAge();
        }
    }
}
