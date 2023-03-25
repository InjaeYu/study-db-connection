package com.study.db_connection.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.springframework.util.StringUtils;

@Embeddable
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@ToString(of = {"city", "street", "zipCode"})
public class Address {

    private String city;
    private String street;
    @Column(length = 18)
    private String zipCode;

    public Address(String city, String street, String zipCode) {
        this.city = city;
        this.street = street;
        this.zipCode = zipCode;
    }

    public void update(Address address) {
        if (StringUtils.hasText(address.getCity())) {
            this.city = address.getCity();
        }
        if (StringUtils.hasText(address.getStreet())) {
            this.street = address.getStreet();
        }
        if (StringUtils.hasText(address.getZipCode())) {
            this.zipCode = address.getZipCode();
        }
    }
}
