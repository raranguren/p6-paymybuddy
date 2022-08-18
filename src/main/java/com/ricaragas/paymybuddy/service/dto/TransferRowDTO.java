package com.ricaragas.paymybuddy.service.dto;

import lombok.AllArgsConstructor;

import java.sql.Timestamp;

@AllArgsConstructor
public class TransferRowDTO {
    public String name;
    public String description;
    public double euros;
    public Timestamp timestamp;

    public int compareNewerFirst(TransferRowDTO other) {
        return other.timestamp.compareTo(this.timestamp);
    }
}
