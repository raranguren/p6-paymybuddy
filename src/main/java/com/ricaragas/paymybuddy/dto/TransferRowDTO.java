package com.ricaragas.paymybuddy.dto;

import lombok.AllArgsConstructor;

import java.sql.Timestamp;

@AllArgsConstructor
public class TransferRowDTO {
    public String name;
    public String description;
    public double euros;
    public Timestamp timestamp;
}
