package it.ldario.graphdbneo4j;



import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.neo4j.driver.v1.AuthTokens;
import org.neo4j.driver.v1.Driver;
import org.neo4j.driver.v1.GraphDatabase;
import org.neo4j.driver.v1.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Iterator;
import java.util.Objects;

import static org.assertj.core.api.Assertions.assertThat;
import static org.neo4j.driver.v1.Values.parameters;

@RunWith(SpringRunner.class)
@SpringBootTest
public class AccountIdNeo4jRepositoryTests extends BaseRepositoryTests{

    @Autowired
    private AccountIdNeo4jRepository accountIdNeo4jRepository;



    @Test
    public void contextLoads() {
    }

    @Test
    public void getAccountTest(){

        Driver driver = GraphDatabase.driver( "bolt://localhost:7676", AuthTokens.basic( "neo4j", "password" ) );
        Session session = driver.session();
        session.run("CREATE (:AccountId{accountId:{name}})",parameters("name","IT123"));
        session.close();
        driver.close();
        String result = accountIdNeo4jRepository.getAccountId("IT123").iterator().next().getUniqueId();
        assertThat(result).isEqualTo("IT123");

    }

    @Test
    public void getTotalReputation(){
        Driver driver = GraphDatabase.driver( "bolt://localhost:7676", AuthTokens.basic( "neo4j", "password" ) );
        Session session = driver.session();
        session.run("CREATE (:AccountId{accountId:{name}})",parameters("name","IT123"));
        session.close();
        driver.close();
        assertThat(accountIdNeo4jRepository.getTotalReputation("IT123")).isEqualTo(0);


    }

    @Test
    public void getRelativeReputation(){
        Driver driver = GraphDatabase.driver( "bolt://localhost:7676", AuthTokens.basic( "neo4j", "password" ) );
        Session session = driver.session();
        session.run("CREATE (:AccountId{accountId:{name}})",parameters("name","IT123"));
        session.run("CREATE (:AccountId{accountId:{name}})",parameters("name","IT321"));
        session.run("MATCH(a:AccountId{accountId:{name}}),(b:AccountId{accountId:{name1}})" +
                        "CREATE(a)-[:TRANSACTION]->(b)",parameters("name","IT123","name1","IT321"));
        session.close();
        driver.close();
        assertThat(accountIdNeo4jRepository.getRelativeReputation("IT123","IT321")).isEqualTo(1);


    }

    @Test
    public void getAccountIdOwnAnEntity(){
        Driver driver = GraphDatabase.driver( "bolt://localhost:7676", AuthTokens.basic( "neo4j", "password" ) );
        Session session = driver.session();
        session.run("CREATE (:AccountId{accountId:{name}})",parameters("name","IT123"));
        session.run("CREATE (:EntityId{entityId:{name}})",parameters("name","Luca"));
        session.run("MATCH(a:AccountId{accountId:{name}}),(b:EntityId{entityId:{name1}})" +
                "CREATE(a)<-[:OWN]-(b)",parameters("name","IT123","name1","Luca"));
        session.close();
        driver.close();

        Iterator<BaseAccount> it = accountIdNeo4jRepository.getAccountIdOwnAnEntity("Luca").iterator();
        assertThat(it.next().getUniqueId()).isEqualTo("IT123");
        assertThat(it.hasNext()).isEqualTo(false);


    }

    @Test
    public void getAccountIdConnectedAnAccountId(){
        Driver driver = GraphDatabase.driver( "bolt://localhost:7676", AuthTokens.basic( "neo4j", "password" ) );
        Session session = driver.session();
        session.run("CREATE (:AccountId{accountId:{name}})",parameters("name","IT123"));
        session.run("CREATE (:AccountId{accountId:{name}})",parameters("name","IT321"));
        session.run("CREATE (:EntityId{entityId:{name}})",parameters("name","Luca"));
        session.run("MATCH(a:AccountId{accountId:{name}}),(b:EntityId{entityId:{name1}})" +
                "CREATE(a)<-[:OWN]-(b)",parameters("name","IT123","name1","Luca"));
        session.run("MATCH(a:AccountId{accountId:{name}}),(b:EntityId{entityId:{name1}})" +
                "CREATE(a)<-[:OWN]-(b)",parameters("name","IT321","name1","Luca"));

        session.close();
        driver.close();
        Iterator<BaseAccount> it = accountIdNeo4jRepository.getAccountIdConnectedAnAccountId("IT123").iterator();
        String i = it.next().getUniqueId();
        String j = it.next().getUniqueId();
        //dovuto far cosi perchè per uno strano motivo l'ordine degli accountId è deciso a runtime

        if(Objects.equals(i, "IT123")){
            assertThat(i).isEqualTo("IT123");
            assertThat(j).isEqualTo("IT321");
        }
        else{
            assertThat(j).isEqualTo("IT123");
            assertThat(i).isEqualTo("IT321");
        }
        assertThat(it.hasNext()).isEqualTo(false);


    }

    @Test
    public void deleteAccountIdAndHisTransaction(){
        Driver driver = GraphDatabase.driver( "bolt://localhost:7676", AuthTokens.basic( "neo4j", "password" ) );
        Session session = driver.session();
        session.run("CREATE (:AccountId{accountId:{name}})",parameters("name","IT123"));
        session.run("CREATE (:AccountId{accountId:{name}})",parameters("name","IT321"));
        session.run("MATCH(a:AccountId{accountId:{name}}),(b:AccountId{accountId:{name1}})" +
                "CREATE(a)-[:TRANSACTION]->(b)",parameters("name","IT123","name1","IT321"));
        session.close();
        driver.close();

        accountIdNeo4jRepository.deleteAccountIdAndHisTransaction("IT321");
        accountIdNeo4jRepository.deleteAccountIdAndHisTransaction("IT321");
        assertThat(accountIdNeo4jRepository.getAccountId("IT321").iterator().hasNext()).isEqualTo(false);



    }

    @Test
    public void getGlobalReputationOfAccount(){
        Driver driver = GraphDatabase.driver( "bolt://localhost:7676", AuthTokens.basic( "neo4j", "password" ) );
        Session session = driver.session();
        session.run("CREATE (:AccountId{accountId:{name}})",parameters("name","IT123"));
        session.run("CREATE (:AccountId{accountId:{name}})",parameters("name","IT456"));
        session.run("CREATE (:AccountId{accountId:{name}})",parameters("name","IT321"));
        session.run("CREATE (:EntityId{entityId:{name}})",parameters("name","Luca"));
        session.run("MATCH(a:AccountId{accountId:{name}}),(b:EntityId{entityId:{name1}})" +
                "CREATE(a)<-[:OWN]-(b)",parameters("name","IT123","name1","Luca"));
        session.run("MATCH(a:AccountId{accountId:{name}}),(b:EntityId{entityId:{name1}})" +
                "CREATE(a)<-[:OWN]-(b)",parameters("name","IT321","name1","Luca"));
        session.run("MATCH(a:AccountId{accountId:{name}}),(b:AccountId{accountId:{name1}})" +
                "CREATE(a)-[:TRANSACTION]->(b)",parameters("name","IT456","name1","IT123"));
        session.run("MATCH(a:AccountId{accountId:{name}}),(b:AccountId{accountId:{name1}})" +
                "CREATE(a)-[:TRANSACTION]->(b)",parameters("name","IT456","name1","IT321"));

        session.close();
        driver.close();

        assertThat(accountIdNeo4jRepository.getGlobalReputationForAccountId("IT321")).isEqualTo(2);


    }

    @Test
    public void getGlobalReputationOfEntityId(){
        Driver driver = GraphDatabase.driver( "bolt://localhost:7676", AuthTokens.basic( "neo4j", "password" ) );
        Session session = driver.session();
        session.run("CREATE (:AccountId{accountId:{name}})",parameters("name","IT123"));
        session.run("CREATE (:AccountId{accountId:{name}})",parameters("name","IT456"));
        session.run("CREATE (:AccountId{accountId:{name}})",parameters("name","IT321"));
        session.run("CREATE (:EntityId{entityId:{name}})",parameters("name","Luca"));
        session.run("MATCH(a:AccountId{accountId:{name}}),(b:EntityId{entityId:{name1}})" +
                "CREATE(a)<-[:OWN]-(b)",parameters("name","IT123","name1","Luca"));
        session.run("MATCH(a:AccountId{accountId:{name}}),(b:EntityId{entityId:{name1}})" +
                "CREATE(a)<-[:OWN]-(b)",parameters("name","IT321","name1","Luca"));
        session.run("MATCH(a:AccountId{accountId:{name}}),(b:AccountId{accountId:{name1}})" +
                "CREATE(a)-[:TRANSACTION]->(b)",parameters("name","IT456","name1","IT123"));
        session.run("MATCH(a:AccountId{accountId:{name}}),(b:AccountId{accountId:{name1}})" +
                "CREATE(a)-[:TRANSACTION]->(b)",parameters("name","IT456","name1","IT321"));
        session.close();
        driver.close();

        assertThat(accountIdNeo4jRepository.getGlobalReputationForEntityId("Luca")).isEqualTo(2);


    }

    @Test
    public void getAmountLastPeriod(){
        Driver driver = GraphDatabase.driver( "bolt://localhost:7676", AuthTokens.basic( "neo4j", "password" ) );
        Session session = driver.session();
        session.run("CREATE (:AccountId{accountId:{name}})",parameters("name","IT123"));
        session.run("CREATE (:AccountId{accountId:{name}})",parameters("name","IT456"));
        session.run("MATCH(a:AccountId{accountId:{name}}),(b:AccountId{accountId:{name1}})" +
                "CREATE(a)-[:TRANSACTION{amount:{amount},dateOfSubmission:{date}}]->(b)",parameters(
                        "name","IT456","name1","IT123","amount","50","date","20"));
        session.run("MATCH(a:AccountId{accountId:{name}}),(b:AccountId{accountId:{name1}})" +
                "CREATE(a)-[:TRANSACTION{amount:{amount},dateOfSubmission:{date}}]->(b)",parameters(
                        "name","IT456","name1","IT123","amount","50","date","2"));
        session.close();
        driver.close();
        assertThat(accountIdNeo4jRepository.getAmountAccountInLastPeriod("IT456",12L)).isEqualTo(50);
        assertThat(accountIdNeo4jRepository.getAmountAccountInLastPeriod("IT456",22L)).isEqualTo(0);

    }

    @Test
    public void getAmountWithoutLastPeriod(){
        Driver driver = GraphDatabase.driver( "bolt://localhost:7676", AuthTokens.basic( "neo4j", "password" ) );
        Session session = driver.session();
        session.run("CREATE (:AccountId{accountId:{name}})",parameters("name","IT123"));
        session.run("CREATE (:AccountId{accountId:{name}})",parameters("name","IT456"));
        session.run("MATCH(a:AccountId{accountId:{name}}),(b:AccountId{accountId:{name1}})" +
                "CREATE(a)-[:TRANSACTION{amount:{amount},dateOfSubmission:{date}}]->(b)",parameters(
                "name","IT456","name1","IT123","amount","50","date","20"));
        session.run("MATCH(a:AccountId{accountId:{name}}),(b:AccountId{accountId:{name1}})" +
                "CREATE(a)-[:TRANSACTION{amount:{amount},dateOfSubmission:{date}}]->(b)",parameters(
                "name","IT456","name1","IT123","amount","30","date","2"));
        session.close();
        driver.close();
        assertThat(accountIdNeo4jRepository.getAmountAccountWithoutLastPeriod("IT456",12L)).isEqualTo(30);
        assertThat(accountIdNeo4jRepository.getAmountAccountWithoutLastPeriod("IT456",1L)).isEqualTo(0);
    }



}
