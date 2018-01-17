package it.ldario.graphdbneo4j;


import org.junit.After;
import org.junit.Before;
import org.junit.runner.RunWith;
import org.neo4j.driver.v1.AuthTokens;
import org.neo4j.driver.v1.Driver;
import org.neo4j.driver.v1.GraphDatabase;
import org.neo4j.driver.v1.Session;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
public abstract class BaseRepositoryTests {


    @Before

    public void eraseDatabase(){
        Driver driver = GraphDatabase.driver( "bolt://localhost:7676", AuthTokens.basic( "neo4j", "password" ) );
        Session session = driver.session();
        session.run("MATCH(a:AccountId) detach delete a");
        session.run("MATCH(a:EntityId) detach delete a");
        session.close();
        driver.close();

    }
}
