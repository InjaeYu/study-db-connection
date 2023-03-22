package com.study.db_connection.entity;

import com.study.db_connection.audit.TimeEntity;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import java.util.ArrayList;
import java.util.List;
import lombok.Getter;

@Entity
@Getter
public class Member extends TimeEntity {

    @Id
    @GeneratedValue
    private Long id;

    private String name;
    private int age;
    @OneToMany(mappedBy = "member")
    private List<Pet> pets = new ArrayList<>();

    @Embedded
    private Address address;

}
