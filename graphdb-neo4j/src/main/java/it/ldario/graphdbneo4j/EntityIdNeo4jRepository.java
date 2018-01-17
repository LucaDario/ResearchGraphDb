package it.ldario.graphdbneo4j;

import org.springframework.data.neo4j.annotation.Depth;
import org.springframework.data.neo4j.annotation.Query;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.stereotype.Component;

@Component
interface EntityIdNeo4jRepository extends Neo4jRepository<EntityIdNeo4j,Long> {



    @Query("MATCH(e:EntityId),(a:AccountId)" +
            "where e.entityId={0} and a.accountId={1}" +
            "create (e)-[r:OWN]->(a) return (count(e)+count(e))")
    int addRelationOwn(String entityId, String accountId);


    @Depth(0)
    @Query("MATCH(a:EntityId{entityId:{0}}) return a")
    Iterable<EntityIdNeo4j> getEntityId(String accountId);

    @Query("MATCH (n:AccountId {accountId:{1}})<-[t:TRANSACTION]-(EntityId{entityId:{0}}) RETURN count(t)")
    int getRelativeReputation(String payerId, String payeeId);

    @Query("MATCH(e:EntityId{entityId:{0}}) DETACH DELETE e")
    void deleteEntity(String entityId);

}
