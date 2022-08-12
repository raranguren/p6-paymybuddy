package com.ricaragas.paymybuddy.model;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.sql.Timestamp;

@Entity
@Getter
@Setter
public class Transfer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(cascade = CascadeType.PERSIST)
    @JoinColumn(name = "sender_wallet_id")
    private Wallet sender;

    @ManyToOne
    @JoinColumn(name = "receiver_wallet_id")
    private Wallet receiver;

    @Column(name = "description")
    private String description;

    @Column(name = "amount")
    private int amountInCents;

    @Column(name = "time_completed")
    private Timestamp timeCompleted;

    public void setSender(Wallet sender) {
        this.sender = sender;
        sender.getSentTransfers().add(this);
    }

    public double getAmountInEuros() {
        return getAmountInCents() / 100.0;
    }
}
