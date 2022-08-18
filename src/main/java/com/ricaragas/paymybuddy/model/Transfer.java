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

    @ManyToOne
    @JoinColumn(name = "connection_id")
    private Connection connection;

    @Column(name = "description")
    private String description;

    @Column(name = "amount")
    private int amountInCents;

    @Column(name = "time_completed")
    private Timestamp timeCompleted;

    public void setConnection(Connection connection) {
        this.connection = connection;
        connection.getTransfers().add(this);
    }

    public double getAmountInEuros() {
        return getAmountInCents() / 100.0;
    }
}
