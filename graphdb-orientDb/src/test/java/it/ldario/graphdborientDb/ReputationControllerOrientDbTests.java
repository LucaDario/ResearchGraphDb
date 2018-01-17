package it.ldario.graphdborientDb;

import com.orientechnologies.orient.core.record.impl.ODocument;
import com.orientechnologies.orient.core.sql.OCommandSQL;
import com.orientechnologies.orient.core.sql.query.OSQLSynchQuery;
import com.tinkerpop.blueprints.impls.orient.OrientGraphFactory;
import it.ldario.basegraphdb.dbentity.TransactionBuilder;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;


@RunWith(SpringRunner.class)
@SpringBootTest(classes = ReputationControllerOrientDb.class)
@Import(OrientDbConfiguration.class)

public class ReputationControllerOrientDbTests {

    @Autowired
    ReputationControllerOrientDb reputationControllerOrientDb;


    @Before
    public void eraseGraph() {
        OrientGraphFactory factory = new OrientGraphFactory("remote:localhost:2425/xtn_graph", "admin", "admin");
        String deleteAccount = "DELETE VERTEX AccountId where 1=1";
        String deleteEntity = "DELETE VERTEX EntityId where 1=1";

        factory.getTx().command(
                new OCommandSQL(deleteAccount)
        ).execute();
        factory.getTx().command(
                new OCommandSQL(deleteEntity)
        ).execute();

        factory.close();
    }


    @Test
    public void addEntity() {
        reputationControllerOrientDb.addEntityId("Luca");
        String getEntity = "SELECT FROM EntityId Where entityId = \"Luca\"";
        OrientGraphFactory factory = new OrientGraphFactory("remote:localhost:2425/xtn_graph", "admin", "admin");
        List<ODocument> result = factory.getTx().getRawGraph().query(
                new OSQLSynchQuery(getEntity));

        assertThat(result.get(0).toMap().get("entityId").toString()).isEqualTo("Luca");
        factory.close();
    }

    @Test
    public void addAccount() {
        reputationControllerOrientDb.addAccountId("IT123");
        String getEntity = "SELECT FROM AccountId Where accountId = \"IT123\"";
        OrientGraphFactory factory = new OrientGraphFactory("remote:localhost:2425/xtn_graph", "admin", "admin");
        List<ODocument> result = factory.getTx().getRawGraph().query(
                new OSQLSynchQuery(getEntity));

        assertThat(result.get(0).toMap().get("accountId").toString()).isEqualTo("IT123");
        factory.close();
    }

    @Test
    public void addEdgeOwnWithAccountAndEntityNotInDb() {
        reputationControllerOrientDb.addAccountIdWithEntityId("Luca", "IT123");

        String existRelationship = "SELECT inE() from AccountId where accountId=\"IT123\"";
        OrientGraphFactory factory = new OrientGraphFactory("remote:localhost:2425/xtn_graph", "admin", "admin");
        List<ODocument> result = factory.getTx().getRawGraph().query(
                new OSQLSynchQuery(existRelationship));

        assertThat(result.get(0).toMap().get("inE").toString()).isNotNull();
        factory.close();

    }

    @Test
    public void addEdgeOwnWithAccountNotIdDb() {
        reputationControllerOrientDb.addEntityId("Luca");
        reputationControllerOrientDb.addAccountIdWithEntityId("Luca", "IT123");
        String existRelationship = "SELECT inE() from AccountId where accountId=\"IT123\"";
        OrientGraphFactory factory = new OrientGraphFactory("remote:localhost:2425/xtn_graph", "admin", "admin");
        List<ODocument> result = factory.getTx().getRawGraph().query(
                new OSQLSynchQuery(existRelationship));

        assertThat(result.get(0).toMap().get("inE").toString()).isNotNull();
        factory.close();
    }

    @Test
    public void addEdgeOwnWithEntityNotInDb() {
        reputationControllerOrientDb.addAccountId("IT123");
        reputationControllerOrientDb.addAccountIdWithEntityId("Luca", "IT123");
        String existRelationship = "SELECT inE() from AccountId where accountId=\"IT123\"";
        OrientGraphFactory factory = new OrientGraphFactory("remote:localhost:2425/xtn_graph", "admin", "admin");
        List<ODocument> result = factory.getTx().getRawGraph().query(
                new OSQLSynchQuery(existRelationship));

        assertThat(result.get(0).toMap().get("inE").toString()).isNotNull();
        factory.close();
    }

    @Test
    public void addEdgeOwnWithAccountAndEntityInDb() {
        reputationControllerOrientDb.addAccountId("IT123");
        reputationControllerOrientDb.addEntityId("Luca");
        reputationControllerOrientDb.addAccountIdWithEntityId("Luca", "IT123");
        String existRelationship = "SELECT inE() from AccountId where accountId=\"IT123\"";
        OrientGraphFactory factory = new OrientGraphFactory("remote:localhost:2425/xtn_graph", "admin", "admin");
        List<ODocument> result = factory.getTx().getRawGraph().query(
                new OSQLSynchQuery(existRelationship));

        assertThat(result.get(0).toMap().get("inE").toString()).isNotNull();
        factory.close();

    }

    @Test
    public void deleteEntityId() {
        OrientGraphFactory factory = new OrientGraphFactory("remote:localhost:2425/xtn_graph", "admin", "admin");
        String addEntity = "Create vertex EntityId set entityId=\"Luca\"";
        factory.getTx().command(
                new OCommandSQL(addEntity)
        ).execute();

        reputationControllerOrientDb.deleteEntityId("Luca");

        String getEntity = "Select from EntityId where entityId=\"Luca\"";
        List<ODocument> result = factory.getTx().getRawGraph().query(
                new OSQLSynchQuery(getEntity));

        assertThat(result.size()).isEqualTo(0);


    }

    @Test
    public void deleteAccountId() {
        OrientGraphFactory factory = new OrientGraphFactory("remote:localhost:2425/xtn_graph", "admin", "admin");
        String addEntity = "Create vertex AccountId set accountId=\"Luca\"";
        factory.getTx().command(
                new OCommandSQL(addEntity)
        ).execute();

        reputationControllerOrientDb.deleteAccountId("IT123");

        String getEntity = "Select from AccountId where accountId=\"IT123\"";
        List<ODocument> result = factory.getTx().getRawGraph().query(
                new OSQLSynchQuery(getEntity));

        assertThat(result.size()).isEqualTo(0);


    }

    @Test
    public void addTransactionWithPayeeAndPayerNotInDb() {

        TransactionBuilder transactionBuilder = new TransactionBuilder();
        assertThat(reputationControllerOrientDb.addTransaction("Luca", "IT123", transactionBuilder.build())).isEqualTo(2);
    }

    @Test
    public void addTransactionWithPayerNotInDb() {
        OrientGraphFactory factory = new OrientGraphFactory("remote:localhost:2425/xtn_graph", "admin", "admin");
        String addAccount = "Create vertex AccountId set accountId=\"IT123\"";
        factory.getTx().command(
                new OCommandSQL(addAccount)
        ).execute();
        TransactionBuilder transactionBuilder = new TransactionBuilder();
        assertThat(reputationControllerOrientDb.addTransaction("Luca", "IT123", transactionBuilder.build())).isEqualTo(1);

    }

    @Test
    public void addTransactionWithPayeeNotInDb() {
        OrientGraphFactory factory = new OrientGraphFactory("remote:localhost:2425/xtn_graph", "admin", "admin");
        String addEntity = "Create vertex EntityId set entityId=\"Luca\"";
        factory.getTx().command(
                new OCommandSQL(addEntity)
        ).execute();
        TransactionBuilder transactionBuilder = new TransactionBuilder();
        assertThat(reputationControllerOrientDb.addTransaction("Luca", "IT123", transactionBuilder.build())).isEqualTo(0);
    }

    @Test
    public void addTransactionWithPayeeAndPayerInDb() {
        OrientGraphFactory factory = new OrientGraphFactory("remote:localhost:2425/xtn_graph", "admin", "admin");
        String addAccount = "Create vertex AccountId set accountId=\"IT123\"";
        factory.getTx().command(
                new OCommandSQL(addAccount)
        ).execute();
        String addEntity = "Create vertex EntityId set entityId=\"Luca\"";
        factory.getTx().command(
                new OCommandSQL(addEntity)
        ).execute();
        TransactionBuilder transactionBuilder = new TransactionBuilder();
        assertThat(reputationControllerOrientDb.addTransaction("Luca", "IT123", transactionBuilder.build())).isEqualTo(3);


    }

    @Test
    public void getTotalReputationWithPayeeNotInDb() {
        assertThat(reputationControllerOrientDb.getTotalReputation("IT123")).isEqualTo(-1);
    }

    @Test
    public void getTotalReputationwithPayeeInDb() {
        OrientGraphFactory factory = new OrientGraphFactory("remote:localhost:2425/xtn_graph", "admin", "admin");
        String addAccount = "Create vertex AccountId set accountId=\"IT123\"";
        factory.getTx().command(
                new OCommandSQL(addAccount)
        ).execute();
        assertThat(reputationControllerOrientDb.getTotalReputation("IT123")).isEqualTo(0);

    }

    @Test
    public void getRelativeReputationWithPayeeNotInDb() {
        assertThat(reputationControllerOrientDb.getRelativeReputation("IT123", "Luca")).isEqualTo(-1);
    }

    @Test
    public void getRelativeReputationWithPayeeInDbAndPayerIsAccountId() {
        OrientGraphFactory factory = new OrientGraphFactory("remote:localhost:2425/xtn_graph", "admin", "admin");
        String addAccount = "Create vertex AccountId set accountId=\"IT123\"";
        factory.getTx().command(
                new OCommandSQL(addAccount)
        ).execute();
        addAccount = "Create vertex AccountId set accountId=\"IT321\"";
        factory.getTx().command(
                new OCommandSQL(addAccount)
        ).execute();
        assertThat(reputationControllerOrientDb.getRelativeReputation("IT123", "IT321")).isEqualTo(0);

    }

    @Test
    public void getRelativeReputationWithPayeeInDbAndPayerIsEntityID() {
        OrientGraphFactory factory = new OrientGraphFactory("remote:localhost:2425/xtn_graph", "admin", "admin");
        String addAccount = "Create vertex AccountId set accountId=\"IT123\"";
        factory.getTx().command(
                new OCommandSQL(addAccount)
        ).execute();
        String addEntity = "Create vertex EntityId set entityId=\"Luca\"";
        factory.getTx().command(
                new OCommandSQL(addEntity)
        ).execute();
        assertThat(reputationControllerOrientDb.getRelativeReputation("IT123", "Luca")).isEqualTo(0);

    }

    @Test
    public void getGlobalReputationAndPayeeIsAccountId() {
        OrientGraphFactory factory = new OrientGraphFactory("remote:localhost:2425/xtn_graph", "admin", "admin");
        String addAccount = "Create vertex AccountId set accountId=\"IT123\"";
        factory.getTx().command(
                new OCommandSQL(addAccount)
        ).execute();
        addAccount = "Create vertex AccountId set accountId=\"IT321\"";
        factory.getTx().command(
                new OCommandSQL(addAccount)
        ).execute();
        String addEntity = "Create Vertex EntityId set entityId=\"Luca\"";

        factory.getTx().command(
                new OCommandSQL(addEntity)
        ).execute();
        String queryEdge = "CREATE EDGE OWN FROM (SELECT FROM EntityId where entityId=\"Luca\")" +
                " TO(SELECT FROM AccountId WHERE accountId=\"IT123\")";
        factory.getTx().command(
                new OCommandSQL(queryEdge)
        ).execute();
        queryEdge = "CREATE EDGE OWN FROM (SELECT FROM EntityId where entityId=\"Luca\")" +
                " TO(SELECT FROM AccountId WHERE accountId=\"IT321\")";
        factory.getTx().command(
                new OCommandSQL(queryEdge)
        ).execute();

        String addPayer = "Create vertex AccountId set accountId=\"IT456\"";
        factory.getTx().command(
                new OCommandSQL(addPayer)
        ).execute();

        queryEdge = "CREATE EDGE TRANSACTION FROM (SELECT FROM AccountId where accountId=\"IT456\")" +
                " TO(SELECT FROM AccountId WHERE accountId=\"IT321\")";
        factory.getTx().command(
                new OCommandSQL(queryEdge)
        ).execute();
        queryEdge = "CREATE EDGE TRANSACTION FROM (SELECT FROM AccountId where accountId=\"IT456\")" +
                " TO(SELECT FROM AccountId WHERE accountId=\"IT321\")";
        factory.getTx().command(
                new OCommandSQL(queryEdge)
        ).execute();

        assertThat(reputationControllerOrientDb.getGlobalReputation("IT123")).isEqualTo(2);

    }

    @Test
    public void getGlobalReputationAndPayeeIsEntityId() {
        OrientGraphFactory factory = new OrientGraphFactory("remote:localhost:2425/xtn_graph", "admin", "admin");
        String addAccount = "Create vertex AccountId set accountId=\"IT123\"";
        factory.getTx().command(
                new OCommandSQL(addAccount)
        ).execute();
        addAccount = "Create vertex AccountId set accountId=\"IT321\"";
        factory.getTx().command(
                new OCommandSQL(addAccount)
        ).execute();
        String addEntity = "Create Vertex EntityId set entityId=\"Luca\"";

        factory.getTx().command(
                new OCommandSQL(addEntity)
        ).execute();
        String queryEdge = "CREATE EDGE OWN FROM (SELECT FROM EntityId where entityId=\"Luca\")" +
                " TO(SELECT FROM AccountId WHERE accountId=\"IT123\")";
        factory.getTx().command(
                new OCommandSQL(queryEdge)
        ).execute();
        queryEdge = "CREATE EDGE OWN FROM (SELECT FROM EntityId where entityId=\"Luca\")" +
                " TO(SELECT FROM AccountId WHERE accountId=\"IT321\")";
        factory.getTx().command(
                new OCommandSQL(queryEdge)
        ).execute();

        String addPayer = "Create vertex AccountId set accountId=\"IT456\"";
        factory.getTx().command(
                new OCommandSQL(addPayer)
        ).execute();

        queryEdge = "CREATE EDGE TRANSACTION FROM (SELECT FROM AccountId where accountId=\"IT456\")" +
                " TO(SELECT FROM AccountId WHERE accountId=\"IT321\")";
        factory.getTx().command(
                new OCommandSQL(queryEdge)
        ).execute();
        queryEdge = "CREATE EDGE TRANSACTION FROM (SELECT FROM AccountId where accountId=\"IT456\")" +
                " TO(SELECT FROM AccountId WHERE accountId=\"IT321\")";
        factory.getTx().command(
                new OCommandSQL(queryEdge)
        ).execute();

        assertThat(reputationControllerOrientDb.getGlobalReputation("Luca")).isEqualTo(2);

    }

    @Test
    public void getGlobalRelativeReputationWithPayeeIsAccountId() {
        OrientGraphFactory factory = new OrientGraphFactory("remote:localhost:2425/xtn_graph", "admin", "admin");
        String addAccount = "Create vertex AccountId set accountId=\"IT123\"";
        factory.getTx().command(
                new OCommandSQL(addAccount)
        ).execute();
        addAccount = "Create vertex AccountId set accountId=\"IT321\"";
        factory.getTx().command(
                new OCommandSQL(addAccount)
        ).execute();
        String addEntity = "Create Vertex EntityId set entityId=\"Luca\"";

        factory.getTx().command(
                new OCommandSQL(addEntity)
        ).execute();
        String queryEdge = "CREATE EDGE OWN FROM (SELECT FROM EntityId where entityId=\"Luca\")" +
                " TO(SELECT FROM AccountId WHERE accountId=\"IT123\")";
        factory.getTx().command(
                new OCommandSQL(queryEdge)
        ).execute();
        queryEdge = "CREATE EDGE OWN FROM (SELECT FROM EntityId where entityId=\"Luca\")" +
                " TO(SELECT FROM AccountId WHERE accountId=\"IT321\")";
        factory.getTx().command(
                new OCommandSQL(queryEdge)
        ).execute();

        String addPayer = "Create vertex AccountId set accountId=\"IT456\"";
        factory.getTx().command(
                new OCommandSQL(addPayer)
        ).execute();

        queryEdge = "CREATE EDGE TRANSACTION FROM (SELECT FROM AccountId where accountId=\"IT456\")" +
                " TO(SELECT FROM AccountId WHERE accountId=\"IT123\")";
        factory.getTx().command(
                new OCommandSQL(queryEdge)
        ).execute();
        queryEdge = "CREATE EDGE TRANSACTION FROM (SELECT FROM AccountId where accountId=\"IT456\")" +
                " TO(SELECT FROM AccountId WHERE accountId=\"IT321\")";
        factory.getTx().command(
                new OCommandSQL(queryEdge)
        ).execute();

        assertThat(reputationControllerOrientDb.getGlobalRelativeReputation("IT123", "IT456")).isEqualTo(2);

    }

    @Test
    public void getAmountTransactionInLastPeriod() {
        OrientGraphFactory factory = new OrientGraphFactory("remote:localhost:2425/xtn_graph", "admin", "admin");
        String addAccount = "Create vertex AccountId set accountId=\"IT123\"";
        factory.getTx().command(
                new OCommandSQL(addAccount)
        ).execute();
        addAccount = "Create vertex AccountId set accountId=\"IT321\"";
        factory.getTx().command(
                new OCommandSQL(addAccount)
        ).execute();
        String addPayer = "Create vertex AccountId set accountId=\"IT456\"";
        factory.getTx().command(
                new OCommandSQL(addPayer)
        ).execute();
        String queryEdge = "CREATE EDGE TRANSACTION FROM (SELECT FROM AccountId where accountId=\"IT456\")" +
                " TO(SELECT FROM AccountId WHERE accountId=\"IT123\") set dateOfSubmission=\"" + System.currentTimeMillis() + "\"," +
                "amount=30";
        factory.getTx().command(
                new OCommandSQL(queryEdge)
        ).execute();
        factory.getTx().command(
                new OCommandSQL(queryEdge)
        ).execute();
        queryEdge = "CREATE EDGE TRANSACTION FROM (SELECT FROM AccountId where accountId=\"IT456\")" +
                " TO(SELECT FROM AccountId WHERE accountId=\"IT123\") set dateOfSubmission=\"" + (System.currentTimeMillis() - 432000000L) + "\"," +
                "amount=300";

        factory.getTx().command(
                new OCommandSQL(queryEdge)
        ).execute();


        assertThat(reputationControllerOrientDb.getAmountAccountIdInATimeRange("IT456", 3)).isEqualTo(60);
    }

    @Test
    public void getAmountTransactionWithoutLastPeriod() {
        OrientGraphFactory factory = new OrientGraphFactory("remote:localhost:2425/xtn_graph", "admin", "admin");
        String addAccount = "Create vertex AccountId set accountId=\"IT123\"";
        factory.getTx().command(
                new OCommandSQL(addAccount)
        ).execute();
        addAccount = "Create vertex AccountId set accountId=\"IT321\"";
        factory.getTx().command(
                new OCommandSQL(addAccount)
        ).execute();
        String addPayer = "Create vertex AccountId set accountId=\"IT456\"";
        factory.getTx().command(
                new OCommandSQL(addPayer)
        ).execute();
        String queryEdge = "CREATE EDGE TRANSACTION FROM (SELECT FROM AccountId where accountId=\"IT456\")" +
                " TO(SELECT FROM AccountId WHERE accountId=\"IT123\") set dateOfSubmission=\"" + System.currentTimeMillis() + "\"," +
                "amount=30";
        factory.getTx().command(
                new OCommandSQL(queryEdge)
        ).execute();
        factory.getTx().command(
                new OCommandSQL(queryEdge)
        ).execute();
        queryEdge = "CREATE EDGE TRANSACTION FROM (SELECT FROM AccountId where accountId=\"IT456\")" +
                " TO(SELECT FROM AccountId WHERE accountId=\"IT123\") set dateOfSubmission=\"" + (System.currentTimeMillis() - 432000000L) + "\"," +
                "amount=300";

        factory.getTx().command(
                new OCommandSQL(queryEdge)
        ).execute();


        assertThat(reputationControllerOrientDb.getAmountAccountIdInATimeRange("IT456", -3)).isEqualTo(300);
    }


}


