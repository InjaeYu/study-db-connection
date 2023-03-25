package com.study.db_connection.entity;

import com.study.db_connection.audit.TimeEntity;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import java.util.ArrayList;
import java.util.List;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.springframework.util.StringUtils;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@ToString(of = {"id", "name", "age", "address"})
public class Member extends TimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Setter
    private Long id;

    private String name;
    private int age;
    @OneToMany(mappedBy = "member")
    private final List<Pet> pets = new ArrayList<>();

    @Embedded
    private Address address;

    public Member(String name, int age, Address address) {
        this.name = name;
        this.age = age;
        this.address = address;
    }

    @Builder
    public Member(Long id, String name, int age, Address address) {
        this.id = id;
        this.name = name;
        this.age = age;
        this.address = address;
    }

    public void addPet(Pet pet) {
        if (pet != null) {
            if (pet.getMember() != null) {
                pet.getMember().getPets().remove(pet);
            }
            if (!this.getPets().contains(pet)) {
                this.getPets().add(pet);
            }
            pet.setMember(this);
        }
    }

    public void update(String name, int age, Address address) {
        if (StringUtils.hasText(name)) {
            this.name = name;
        }
        if (age > 0) {
            this.age = age;
        }
        if (address != null) {
            this.address.update(address);
        }
    }
}
