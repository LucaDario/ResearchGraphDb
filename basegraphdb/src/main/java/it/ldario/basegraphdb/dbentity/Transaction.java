package it.ldario.basegraphdb.dbentity;

import java.util.Date;
import java.util.UUID;

public class Transaction {
    private final UUID id = UUID.randomUUID();
    private Date dateOfSubmissionx = new Date();
    private double amount;
    private String currency="N/A";
    private String channelId;
    private String productId;
    private String ipAddress;
    private String city;
    private String country;
    private String legalEntId;

    public String getLegalEntId() {
        return legalEntId;
    }

    public void setLegalEntId(String legalEntId) {
        this.legalEntId = legalEntId;
    }

    Transaction(){}

    public UUID getId() {
        return id;
    }

    public Date getDateOfSubmissionx() {

        return dateOfSubmissionx;
    }

    public void setDateOfSubmissionx(Date dateOfSubmissionx) {
        this.dateOfSubmissionx = dateOfSubmissionx;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public String getChannelId() {
        return channelId;
    }

    public void setChannelId(String channelId) {
        this.channelId = channelId;
    }

    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }

    public String getIpAddress() {
        return ipAddress;
    }

    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }
}
