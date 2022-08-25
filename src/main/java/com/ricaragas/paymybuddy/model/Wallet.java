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

    @OneToOne
    private BillingDetails billingDetails;

    @Column(name="balance")
    private int balanceInCents;

    @OneToMany(mappedBy = "creator")
    private List<Connection> connections = new ArrayList<>();

    public double getBalanceInEuros() {
        return getBalanceInCents() / 100.0;
    }

}
