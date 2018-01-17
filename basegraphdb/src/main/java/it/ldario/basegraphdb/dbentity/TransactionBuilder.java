package it.ldario.basegraphdb.dbentity;

import java.util.Date;
import java.util.Objects;

public class TransactionBuilder {
    private Date dateOfSubmission;
    private double amount;
    private String currency;
    private String channelId;
    private String productId;
    private String ipAddress;
    private String city;
    private String country;
    private String legalEntId;

    public TransactionBuilder setLegalEntId(String legalEntId) {
        this.legalEntId = legalEntId;
        return this;
    }

    public TransactionBuilder setDateOfSubmission(Date dateOfSubmission) {
        this.dateOfSubmission = dateOfSubmission;
        return this;
    }

    public TransactionBuilder setAmount(double amount) {
        this.amount = amount;
        return this;
    }

    public TransactionBuilder setCurrency(String currency) {
        this.currency = currency;
        return this;
    }

    public TransactionBuilder setChannelId(String channelId) {
        this.channelId = channelId;
        return this;
    }

    public TransactionBuilder setProductId(String productId) {
        this.productId = productId;
        return this;
    }

    public TransactionBuilder setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
        return this;
    }

    public TransactionBuilder setCity(String city) {
        this.city = city;
        return this;
    }

    public TransactionBuilder setCountry(String country) {
        this.country = country;
        return this;
    }

    public Transaction build(){
        Transaction transaction = new Transaction();
        if(this.dateOfSubmission != null){
            transaction.setDateOfSubmissionx(this.dateOfSubmission);
        }

        transaction.setAmount(this.amount);

        if(!Objects.equals(this.currency, "N/A")){
            transaction.setCurrency(this.currency);
        }

        transaction.setChannelId(this.channelId);
        transaction.setProductId(this.productId);
        transaction.setIpAddress(this.ipAddress);
        transaction.setCity(this.city);
        transaction.setCountry(this.country);
        transaction.setLegalEntId(this.legalEntId);
        return transaction;
    }


}
