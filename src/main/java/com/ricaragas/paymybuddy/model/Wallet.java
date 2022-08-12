package com.ricaragas.paymybuddy.model;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

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

    @ManyToMany
    @JoinTable(name="contacts",
            joinColumns = @JoinColumn(name = "wallet_id"),
            inverseJoinColumns = @JoinColumn(name = "contact_wallet_id"))
    List<Wallet> contacts = new ArrayList<>();

    @OneToMany(mappedBy = "sender")
    private List<Transfer> sentTransfers = new ArrayList<>();

}
