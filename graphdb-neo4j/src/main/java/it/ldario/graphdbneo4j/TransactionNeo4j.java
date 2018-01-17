package it.ldario.graphdbneo4j;

import org.neo4j.ogm.annotation.*;
import org.neo4j.ogm.annotation.typeconversion.Convert;
import org.neo4j.ogm.annotation.typeconversion.DateLong;
import org.neo4j.ogm.annotation.typeconversion.DateString;

import java.util.Date;

@RelationshipEntity(type = "TRANSACTION")
public class TransactionNeo4j {

    @GraphId
    private Long id;
    @StartNode
    private BaseAccount payer;
    @EndNode
    private AccountIdNeo4j payee;

    private String transactionId;
    @DateLong
    private Date dateOfSubmissionx = new Date();
    private double amount;
    private String currency="N/A";
    private String channelId;
    private String productId;
    private String ipAddress;
    private String city;
    private String country;

    public String getLegalEntId() {
        return legalEntId;
    }

    public void setLegalEntId(String legalEntId) {
        this.legalEntId = legalEntId;
    }

    private String legalEntId;

    public TransactionNeo4j(String transactionId, Date dateOfSubmissionx, double amount,
                            String currency, String channelId, String productId, String
                                    ipAddress, String city, String country) {
        this.transactionId = transactionId;
        this.dateOfSubmissionx = dateOfSubmissionx;

        this.amount = amount;
        this.currency = currency;
        this.channelId = channelId;
        this.productId = productId;
        this.ipAddress = ipAddress;
        this.city = city;
        this.country = country;
    }
    public TransactionNeo4j(){}

    public String getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(String transactionId) {
        this.transactionId = transactionId;
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

    public void setAmount(String amount) {
        this.amount = Double.valueOf(amount);
    }
    public void setAmount(double amount){
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

    public void setStartNode(BaseAccount baseAccount){
        this.payer = baseAccount;
    }

    public void setEndNode(AccountIdNeo4j accountIdNeo4j){
        this.payee = accountIdNeo4j;
    }

}
