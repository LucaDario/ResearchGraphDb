package it.ldario.graphdbneo4j;

import org.neo4j.ogm.annotation.EndNode;
import org.neo4j.ogm.annotation.GraphId;
import org.neo4j.ogm.annotation.RelationshipEntity;
import org.neo4j.ogm.annotation.StartNode;

@RelationshipEntity(type = "OWN")
class Own {
    @GraphId
    private Long id;
    @StartNode
    private EntityIdNeo4j payer;
    @EndNode
    private AccountIdNeo4j payee;

}
