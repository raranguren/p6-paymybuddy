package com.ricaragas.paymybuddy.model;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
public class Connection {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(cascade = CascadeType.PERSIST)
    @JoinColumn(name = "creator_wallet_id")
    private Wallet creator;

    @ManyToOne
    @JoinColumn(name = "target_wallet_id")
    private Wallet target;

    @Column(name = "name_given")
    private String name;

    @OneToMany(mappedBy = "connection")
    private List<Transfer> transfers = new ArrayList<>();

    public void setCreator(Wallet creator) {
        this.creator = creator;
        creator.getConnections().add(this);
    }

}
