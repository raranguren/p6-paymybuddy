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
    @JoinTable(name="connections",
            joinColumns = @JoinColumn(name = "wallet_id"),
            inverseJoinColumns = @JoinColumn(name = "connection_wallet_id"))
    List<Wallet> connections = new ArrayList<>();

    @OneToMany(mappedBy = "sender")
    private List<Transfer> sentTransfers = new ArrayList<>();

    public double getBalanceInEuros() {
        return getBalanceInCents() / 100.0;
    }

}
