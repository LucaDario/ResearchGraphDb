package it.ldario.graphdbneo4j;

import org.neo4j.ogm.annotation.GraphId;
import org.neo4j.ogm.annotation.Index;
import org.neo4j.ogm.annotation.NodeEntity;
import org.neo4j.ogm.annotation.Relationship;

import java.util.Vector;


@NodeEntity(label = "AccountId")
public class AccountIdNeo4j implements BaseAccount {

    @GraphId
    private Long id;

    private String accountId;


   @Relationship(type = "TRANSACTION")
    private Vector<AccountIdNeo4j> transactions = new Vector<>();


    public AccountIdNeo4j(String accountId) {
        this.accountId = accountId;
    }
    public AccountIdNeo4j(){}



    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @Override
    public String getUniqueId() {
        return accountId;
    }

    public void setAccountId(String accountId) {
        this.accountId = accountId;
    }


}
