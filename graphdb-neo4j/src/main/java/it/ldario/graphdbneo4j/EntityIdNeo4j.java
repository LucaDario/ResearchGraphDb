package it.ldario.graphdbneo4j;

import org.neo4j.ogm.annotation.GraphId;
import org.neo4j.ogm.annotation.Index;
import org.neo4j.ogm.annotation.NodeEntity;
import org.neo4j.ogm.annotation.Relationship;

import java.util.Vector;

@NodeEntity(label = "EntityId")
public class EntityIdNeo4j implements BaseAccount {

    @GraphId
    private Long id;

    @Index(unique = true) private String entityId;
    @Relationship(type = "OWN")
    private Vector<AccountIdNeo4j> owns = new Vector<>();

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @Override
    public String getUniqueId() {
        return entityId;
    }

    public void setEntityId(String entityId) {
        this.entityId = entityId;
    }

    public EntityIdNeo4j(String entityId) {
        this.entityId = entityId;
    }
    public EntityIdNeo4j(){}
}
