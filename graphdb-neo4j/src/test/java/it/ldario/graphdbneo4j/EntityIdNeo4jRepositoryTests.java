package it.ldario.graphdbneo4j;


import org.junit.Test;
import org.junit.runner.RunWith;
import org.neo4j.driver.v1.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import static org.assertj.core.api.Assertions.assertThat;
import static org.neo4j.driver.v1.Values.parameters;

@RunWith(SpringRunner.class)
@SpringBootTest


public class EntityIdNeo4jRepositoryTests extends BaseRepositoryTests {

    @Autowired
    private EntityIdNeo4jRepository entityIdNeo4jRepository;

    @Test
    public void addRelationOwn() {
        Driver driver = GraphDatabase.driver("bolt://localhost:7676", AuthTokens.basic("neo4j", "password"));
        Session session = driver.session();
        session.run("CREATE (:AccountId{accountId:{name}})", parameters("name", "IT123"));
        session.run("CREATE (:EntityId{entityId:{name}})", parameters("name", "Luca"));

        session.close();
        driver.close();

        entityIdNeo4jRepository.addRelationOwn("Luca", "IT123");

        driver = GraphDatabase.driver("bolt://localhost:7676", AuthTokens.basic("neo4j", "password"));
        session = driver.session();
        StatementResult result = session.run("MATCH(EntityId{entityId:{entity}})-[r:OWN]->(:AccountId{accountId:{account}}) return count(r)",
                parameters("entity", "Luca", "account", "IT123"));
        session.close();
        driver.close();

        assertThat(Integer.valueOf(result.next().get("count(r)").toString())).isEqualTo(1);


    }

    @Test
    public void getEntity() {
        Driver driver = GraphDatabase.driver("bolt://localhost:7676", AuthTokens.basic("neo4j", "password"));
        Session session = driver.session();
        session.run("CREATE (:EntityId{entityId:{name}})", parameters("name", "Luca"));
        session.close();
        driver.close();

        assertThat(entityIdNeo4jRepository.getEntityId("Luca").iterator().next().getUniqueId()).isEqualTo("Luca");
    }

    @Test
    public void getRelativeReputation() throws InterruptedException {
        Driver driver = GraphDatabase.driver("bolt://localhost:7676", AuthTokens.basic("neo4j", "password"));
        Session session = driver.session();
        session.runAsync("CREATE (:AccountId{accountId:{name}})", parameters("name", "IT123"));
        session.runAsync("CREATE (:EntityId{entityId:{name}})", parameters("name", "Luca"));
        session.runAsync("MATCH(a:EntityId{entityId:{name}}),(b:AccountId{accountId:{name1}})" +
                "CREATE(a)-[:TRANSACTION]->(b)", parameters("name", "Luca", "name1", "IT123"));
        session.close();
        driver.close();


        assertThat(entityIdNeo4jRepository.getRelativeReputation("Luca", "IT123")).isEqualTo(1);


    }

    @Test
    public void deleteEntity() throws InterruptedException {
        Driver driver = GraphDatabase.driver("bolt://localhost:7676", AuthTokens.basic("neo4j", "password"));
        Session session = driver.session();

        session.run("CREATE (:AccountId{accountId:{name}})", parameters("name", "IT123"));
        session.run("CREATE (:EntityId{entityId:{name}})", parameters("name", "Luca"));
        session.run("MATCH(a:AccountId{accountId:{name}}),(b:EntityId{entityId:{name1}})" +
                "CREATE(a)<-[:OWN]-(b)", parameters("name", "IT123", "name1", "Luca"));


        session.close();
        driver.close();



        entityIdNeo4jRepository.deleteEntity("Luca");
        driver = GraphDatabase.driver("bolt://localhost:7676", AuthTokens.basic("neo4j", "password"));
        session = driver.session();

        int countEntity = Integer.valueOf(session.run("MATCH (a:EntityId{entityId:{name}}) return count(a)", parameters("name", "Luca")).next().get("count(a)").toString());
        int countAccount = Integer.valueOf(session.run("MATCH (a:AccountId{accountId:{name}}) return count(a)", parameters("name", "IT123")).next().get("count(a)").toString());
        session.close();
        driver.close();

        assertThat(countEntity).isEqualTo(0);


    }


}
