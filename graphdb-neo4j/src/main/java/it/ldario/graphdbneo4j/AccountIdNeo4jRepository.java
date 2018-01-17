package it.ldario.graphdbneo4j;

import org.springframework.data.neo4j.annotation.Depth;
import org.springframework.data.neo4j.annotation.Query;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.stereotype.Component;

@Component
interface AccountIdNeo4jRepository extends Neo4jRepository<AccountIdNeo4j,Long>{


    @Depth(0)
    @Query("MATCH(a:AccountId{accountId:{0}}) return a")
    Iterable<AccountIdNeo4j> getAccountId(String accountId);

    @Query("MATCH (n:AccountId {accountId:{0}})<-[t:TRANSACTION]-(AccountId) RETURN count(t)")
    int getTotalReputation(String payeeId);

    @Query("MATCH (n:AccountId {accountId:{1}})<-[t:TRANSACTION]-(AccountId{accountId:{0}}) RETURN count(t)")
    int getRelativeReputation(String payerId, String payeeId);









    @Query("MATCH(e:EntityId{entityId:{0}})-[:OWN]->(g:AccountId) return g")
    Iterable<BaseAccount> getAccountIdOwnAnEntity(String entityId);

    @Query("MATCH(i:AccountId{accountId:{0}})<-[:OWN]-(g:EntityId)-[:OWN]->(f:AccountId) return f,i")
    Iterable<BaseAccount> getAccountIdConnectedAnAccountId(String accountId);

    @Query("MATCH(e:AccountId{accountId:{0}}) DETACH DELETE e")
    void deleteAccountIdAndHisTransaction(String accountId);

    @Query("match(y)<-[:OWN]-(c:EntityId{entityId:{0}})" +
            "OPTIONAL MATCH (y)<-[r:TRANSACTION]-(:AccountId)" +
            "return count(r)")
    int getGlobalReputationForEntityId(String entityId);

    @Query("match(:AccountId{accountId:{0}})<-[:OWN]-(f:EntityId)" +
            "match(f)-[:OWN]->(j:AccountId)" +
            "match(j)<-[r:TRANSACTION]-(:AccountId)" +
            "return count(r)")
    int getGlobalReputationForAccountId(String accountId);

    @Query("MATCH(a:AccountId{accountId:{0}})-[r:TRANSACTION]->(:AccountId)" +
            "WHERE toInteger(r.dateOfSubmission) > {1}" +
            "return sum(toInteger(r.amount))")
    int getAmountAccountInLastPeriod(String accountId, Long period);


    @Query("MATCH(a:AccountId{accountId:{0}})-[r:TRANSACTION]->(:AccountId)" +
            "WHERE toInteger(r.dateOfSubmission) < {1}" +
            "return sum(toInteger(r.amount))")
    int getAmountAccountWithoutLastPeriod(String accountId, Long period);




}

