package model;

import java.io.Serializable;

public class PaymentResponseModel implements Serializable {
    private String OrderId;
    private String TransactionId;
    private String OrderDescription;
    private Double Amount;
    private String PaymentMethod;
    private String Date;

    public String getOrderId() { return OrderId; }
    public String getTransactionId() { return TransactionId; }
    public String getOrderDescription() { return OrderDescription; }
    public Double getAmount() { return Amount; }
    public String getPaymentMethod() { return PaymentMethod; }
    public String getDate() { return Date; }
}