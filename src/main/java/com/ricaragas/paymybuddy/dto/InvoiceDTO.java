package com.ricaragas.paymybuddy.dto;

public class InvoiceDTO {
    private int transfer;
    private int fee;
    private int vat;

    public void setTransferInCents(int transfer) {
        this.transfer = transfer;
    }

    public double getTransferInEuros() {
        return this.transfer / 100.0;
    }

    public void setFeeInCents(int fee) {
        this.fee = fee;
    }

    public double getFeeInEuros() {
        return this.fee / 100.0;
    }

    public void setVatInCents(int vat) {
        this.vat = vat;
    }

    public double getVatInEuros() {
        return this.vat / 100.0;
    }

    public int getTotalInCents() {
        return transfer + fee + vat;
    }

    public double getTotalInEuros() {
        return getTotalInCents() / 100.0;
    }

    public int getFeeInCents() {
        return this.fee;
    }

    public int getTransferInCents() {
        return this.transfer;
    }
}
