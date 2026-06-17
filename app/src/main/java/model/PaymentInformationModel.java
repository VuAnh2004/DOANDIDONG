package model;

import com.google.gson.annotations.SerializedName;

public class PaymentInformationModel {
    @SerializedName("orderType")
    private String orderType;
    
    @SerializedName("amount")
    private double amount;
    
    @SerializedName("orderDescription")
    private String orderDescription;
    
    @SerializedName("name")
    private String name;

    @SerializedName("platform")
    private String platform;

    public PaymentInformationModel(String orderType, double amount, String orderDescription, String name, String platform) {
        this.orderType = orderType;
        this.amount = amount;
        this.orderDescription = orderDescription;
        this.name = name;
        this.platform = platform;
    }

    // Getters
    public String getOrderType() { return orderType; }
    public double getAmount() { return amount; }
    public String getOrderDescription() { return orderDescription; }
    public String getName() { return name; }
    public String getPlatform() { return platform; }
}