package it.ldario.graphdborientDb;

import com.orientechnologies.orient.core.record.impl.ODocument;
import com.orientechnologies.orient.core.sql.OCommandSQL;
import com.orientechnologies.orient.core.sql.query.OSQLSynchQuery;
import com.tinkerpop.blueprints.Edge;
import com.tinkerpop.blueprints.Vertex;
import com.tinkerpop.blueprints.impls.orient.OrientGraph;
import it.ldario.basegraphdb.ReputationController;
import it.ldario.basegraphdb.dbentity.Transaction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import java.util.List;
import java.util.Objects;
import java.util.Vector;

import static java.lang.StrictMath.abs;

@Component
public class ReputationControllerOrientDb implements ReputationController {



    private final OrientGraph graph;

    @Autowired
    public ReputationControllerOrientDb(OrientGraph graph) {
        this.graph = graph;
    }

    @Override
    public boolean addAccountId(String accountId) {
        if(!existAccountId(accountId)) {
            String queryAddAccountId = "CREATE VERTEX AccountId CONTENT { \"accountId\" : \"" + accountId + "\"}";

            return this.graph.command(
                    new OCommandSQL(queryAddAccountId)
            ).execute() != null;
        }
        return false;
    }

    @Override
    public boolean addEntityId(String entityId) {
        if(!existEntityId(entityId)) {
            String queryAddentity = "CREATE VERTEX EntityId CONTENT { \"entityId\" : \"" + entityId + "\"}";

            return this.graph.command(
                    new OCommandSQL(queryAddentity)
            ).execute() != null;
        }
        return false;
    }

    @Override
    public void addAccountIdWithEntityId(String entityId, String accountId) {
        String queryEdge = "CREATE EDGE OWN FROM (SELECT FROM EntityId where entityId=\""+entityId+"\")" +
                            " TO(SELECT FROM AccountId WHERE accountId=\""+accountId+"\")";

        if(!existAccountId(accountId)){

            addAccountId(accountId);
        }
        if(!existEntityId(entityId)){
            addEntityId(entityId);
        }


        this.graph.command(
                new OCommandSQL(queryEdge)
        ).execute();


    }

    @Override
    public void deleteEntityId(String entityId) {

        String queryDeleteEntity ="DELETE VERTEX EntityId WHERE entityId=\""+entityId+"\"";



        if(existEntityId(entityId)){
            this.graph.command(
                    new OCommandSQL(queryDeleteEntity)
            ).execute();


        }


    }

    @Override
    public void deleteAccountId(String accountId) {

        String queryDeleteAccount ="DELETE VERTEX AccountId WHERE accountId=\""+accountId+"\"";

        String vId = getInternalIdAccountId(accountId);

        if(vId != null){
            this.graph.command(
                    new OCommandSQL(queryDeleteAccount)
            ).execute();


        }


    }

    //no thread safe
    @Override
    public int addTransaction(String payerAccountId, String payeeAccountId, Transaction transaction) {
        int count = -1;
        String payerInternalIdAc = getInternalIdAccountId(payerAccountId);
        String payerInternalIdEnt = getInternalIdEntityId(payerAccountId);
        String payeeInternalId = getInternalIdAccountId(payeeAccountId);




        if(payeeInternalId != null && (payerInternalIdEnt != null || payerInternalIdAc != null)){

            count = 3;
            Vertex vPayee = graph.getVertex(payeeInternalId);
            Vertex vPayer;
            if(payerInternalIdAc != null){
                vPayer = graph.getVertex(payerInternalIdAc);
            }
            else{
                vPayer = graph.getVertex(payerInternalIdEnt);
            }

            Edge edge = graph.addEdge(null,vPayer,vPayee,"TRANSACTION");


            edge.setProperty("timeStamp",transaction.getDateOfSubmissionx().getTime());

            if(transaction.getLegalEntId() != null){
                edge.setProperty("legalEntId",transaction.getLegalEntId());
            }
            if(transaction.getChannelId() != null){
                edge.setProperty("channelId",transaction.getChannelId());
            }
            if(transaction.getProductId() != null){
                edge.setProperty("channelId",transaction.getProductId());
            }
            edge.setProperty("amount",transaction.getAmount());

            if(transaction.getCurrency() != null){
                edge.setProperty("currency",transaction.getCurrency());
            }

            if(transaction.getIpAddress() != null){
                edge.setProperty("ipAddress",transaction.getIpAddress());
            }
            if(transaction.getCity() != null){
                edge.setProperty("city",transaction.getCity());
            }
            if(transaction.getCountry() != null){
                edge.setProperty("country",transaction.getCountry());
            }

            graph.commit();


        }

        else{
            //se manca solo il payee
            if(payeeInternalId == null && (payerInternalIdEnt != null || payerInternalIdAc != null)){
                count = 0;
            }

            //se manca solo il payer
            else if(payeeInternalId != null && (payerInternalIdEnt == null || payerInternalIdAc == null)){
                count = 1;
            }
            //se mancano tutte e 2
            else{
                count = 2;
            }
        }


        return count;
    }

    @Override
    public int getTotalReputation(String payeeId) {
        int count = -1;
        if(existAccountId(payeeId)) {
            String query = "SELECT in(\"TRANSACTION\").size() from AccountId where accountId=\"" + payeeId + "\"";

            try {
                List<ODocument> result = this.graph.getRawGraph().query(
                        new OSQLSynchQuery(query));
                count = Integer.valueOf(result.get(0).toMap().get("in").toString());
            }
            catch (Exception e){

            }
        }
        return count;
    }

    //-1 non esiste il payee, -2 non esiste il payer, >= 0 esistono
    @Override
    public int getRelativeReputation(String payeeId, String payerId) {
        int count = -1;
        final boolean existPayee = existAccountId(payeeId);
        final boolean existPayerAc = existAccountId(payerId);
        if(existPayee){
            if(existPayerAc){
                count = 0;


                String query = "SELECT count(*) FROM \n" +
                        "(TRAVERSE inE(\"TRANSACTION\")  FROM (SELECT FROM AccountId  WHERE accountId=\""+payeeId+"\")) \n" +
                        "where @class=\"TRANSACTION\" and out=\""+getInternalIdAccountId(payerId)+"\"";
                List<ODocument> result = this.graph.getRawGraph().query(
                        new OSQLSynchQuery(query));

                count = Integer.valueOf(result.get(0).toMap().get("count").toString());

            }
            else{
                count = 0;
                final boolean existPayerEnt = existEntityId(payerId);
                if(existPayerEnt){
                    String query = "SELECT count(*) FROM \n" +
                            "(TRAVERSE inE(\"TRANSACTION\")  FROM (SELECT FROM AccountId  WHERE accountId=\""+payeeId+"\")) \n" +
                            "where @class=\"TRANSACTION\" and out=\""+getInternalIdEntityId(payerId)+"\"";
                    List<ODocument> result = this.graph.getRawGraph().query(
                            new OSQLSynchQuery(query));
                    count = Integer.valueOf(result.get(0).toMap().get("count").toString());

                }

            }

        }
        return count;
    }



    private Vector<String> getAccountIdOwnAnEntity(String entityId){
        Vector<String> returned = new Vector<>();

        String query = "select from (traverse out(\"OWN\") from " +
                "(select from EntityId where entityId=\""+entityId+"\")) where @class=\"AccountId\";";
        List<ODocument> result = this.graph.getRawGraph().query(
                new OSQLSynchQuery<>(query)
        );

        for(ODocument document : result){
            if(document.toMap().get("accountId") != null){
                returned.add(document.toMap().get("accountId").toString());
            }
        }


        return returned;
    }

    private Vector<String> getAccountIdConnectedAnAccountId(String accountId){
        Vector<String> returned = new Vector<>();
        String query = "select from (traverse out(\"OWN\") from (select from (traverse in(\"OWN\") " +
                "from(select from AccountId where accountId = \""+accountId+"\"))))";
        List<ODocument> result= this.graph.getRawGraph().query(
                new OSQLSynchQuery<>(query)
        );
        for(ODocument document : result){
            if(document.toMap().get("accountId") != null){
                returned.add(document.toMap().get("accountId").toString());
            }
        }
        return returned;
    }


    private boolean existAccountId(String accountId){
        String query = "select count(*) from AccountId where accountId=\""+accountId+"\"";
        int count = 0;
        try {
            graph.commit();
            List<ODocument> result = this.graph.getRawGraph().query(
                    new OSQLSynchQuery(query));
            count = Integer.valueOf(result.get(0).toMap().get("count").toString());
        }
        catch (Exception e){
            //TODO capire le accezzioni
        }
        return count > 0;


    }

    private boolean existEntityId(String entityId){
        String query = "select count(*) from EntityId where entityId=\""+entityId+"\"";
        int count = 0;
        try {
            graph.commit();
            List<ODocument> result = this.graph.getRawGraph().query(
                    new OSQLSynchQuery(query));
            count = Integer.valueOf(result.get(0).toMap().get("count").toString());


        } catch (Exception e){

           throw e;
        }

        return count > 0;

    }


    private String getInternalIdAccountId(String accountId){
        String queryGetId = "select @rid from AccountId where accountId=\""+accountId+"\"";

        this.graph.commit();

        List<ODocument> result = this.graph.getRawGraph().query(
                new OSQLSynchQuery(queryGetId));
        //pazzia di oggetti
        if(result.size() > 0){
            return ((ODocument) result.get(0).toMap().get("rid")).toMap().get("@rid").toString();
        }
        else{
            return null;
        }


    }

    private String getInternalIdEntityId(String entityId){
        String queryGetId = "select @rid from EntityId where entityId=\""+entityId+"\"";


        List<ODocument> result = this.graph.getRawGraph().query(
                new OSQLSynchQuery(queryGetId));
        //pazzia di oggetti
        if(result.size() > 0){
            return ((ODocument) result.get(0).toMap().get("rid")).toMap().get("@rid").toString();
        }
        else{
            return null;
        }
    }

    @Override
    public int getGlobalReputation(String payeeId) {
        int count = -1;

        if(existAccountId(payeeId)){
            count = 0;

            Vector<String> vectorAccountId = getAccountIdConnectedAnAccountId(payeeId);
            for(String id : vectorAccountId){
                count = count + getTotalReputation(id);
            }

        }

        else if(existEntityId(payeeId)){
            count = 0;
            Vector<String> vectorAccountId = getAccountIdOwnAnEntity(payeeId);
            for(String id : vectorAccountId){
                count = count + getTotalReputation(id);

            }
        }
        return count;
    }

    @Override
    public int getGlobalRelativeReputation(String payeeId, String payerId) {

        int count = -1;
        Vector<String> payees = new Vector<>();
        boolean payerIsEntity= false;

        if(existEntityId(payeeId)){
            payees = getAccountIdOwnAnEntity(payeeId);
        }
        else if(existAccountId(payeeId)){
            payees = getAccountIdConnectedAnAccountId(payeeId);
        }


        Vector<String> payers = null;

        if(existEntityId(payerId)){
            payers = getAccountIdOwnAnEntity(payerId);
            payerIsEntity = true;
        }

        else if(existAccountId(payerId)){
            payers = getAccountIdConnectedAnAccountId(payerId);
        }

        if(payees != null){
            count = 0;

            for(String payee : payees){

                for(String payer : payers){
                    int c = getRelativeReputation(payee,payer);
                    if(c != -1 && !Objects.equals(payee,payer)){
                        count = c + count;

                    }
                }
            }
            //se il payer è un EnityId devo calcolare e sommare anche la reputazione tra il payee e questo entity vistoc che quello
            //calcolato sopra non comprendeva quella relativa tra tutti i payee e il payer passato nel caso fosseEntity perche
            //getAccountIdOwnAnEntity getAccountIdConnectedAnAccountId ritorna solo AccountId
            if(payerIsEntity){
                for(String payee : payees){
                    int c = getRelativeReputation(payee,payerId);
                    if(c != -1){
                        count = c + count;
                    }
                }
            }
        }


        return count;
    }

    @Override
    public double getAmountAccountIdInATimeRange(String accountId, int days) {
        Long millis = abs(days)*86400000L;
        Long range = System.currentTimeMillis() - (millis);
        String query = "";
        if(days >= 0){
            query = "select sum(amount) from (traverse outE(\"TRANSACTION\") from (select from AccountId " +
                    "where accountId = \""+accountId+"\")) where dateOfSubmission >" + range;
        }
        else{

            query = "select sum(amount) from (traverse outE(\"TRANSACTION\") from (select from AccountId " +
                    "where accountId = \""+accountId+"\")) where dateOfSubmission < " + range;
        }

        graph.commit();
        List<ODocument> result= this.graph.getRawGraph().query(
                new OSQLSynchQuery<>(query)
        );

        return Double.valueOf(result.get(0).toMap().get("sum").toString());
    }

    @Override
    public String getNameDatabase() {
        return "OrientDB";
    }
}
