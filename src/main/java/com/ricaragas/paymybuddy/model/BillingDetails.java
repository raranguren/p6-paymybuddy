package com.ricaragas.paymybuddy.model;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Getter
@Setter
public class BillingDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(mappedBy = "billingDetails")
    @JoinColumn(name="billing_details_id")
    private Wallet wallet;
}
