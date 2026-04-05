package com.khataflow.entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "parties")
@Data
public class Party extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long storeId;

    @Column(nullable = false)
    private String name;

    private String phone;

    private String email;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PartyType partyType;

    private String externalId;
}