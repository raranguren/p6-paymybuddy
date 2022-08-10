package com.ricaragas.paymybuddy.model;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Getter
@Setter
public class Wallet {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    private User user;

    @Column(name="profile_name", nullable = false)
    private String profileName;

    @Column(name="balance")
    private int balanceInCents;

}
