package it.ldario.graphdbinsert;

import it.ldario.basegraphdb.ReputationController;
import it.ldario.basegraphdb.dbentity.Transaction;
import it.ldario.basegraphdb.dbentity.TransactionBuilder;

import java.util.Date;
import java.util.Random;
import java.util.Vector;

public class GraphDbInsertImpl implements GraphdbInsert{

    private final String[] channelId = {"ROOT","RETAIL","ECOMMERCE"};
    private final String[] city = {"Padova","Borgoricco","Campodarsego","Camposampiero","Sambruson","Castelfranco"};
    private final String[] country = {"Italiy","UK","Spain"};
    private final String[] legalEntityID = {"ROOT","NOROOT"};

    private static String prefixAccountId = "IT";

    private int amountEntityId;
    private int amountAccountId;
    private int amountTransaction;
    private ReputationController reputationController;


    public GraphDbInsertImpl(int amountEntityId, int amountAccountId, int amountTransaction,
                             ReputationController reputationController) {
        this.amountEntityId = amountEntityId;
        this.amountAccountId = amountAccountId;
        this.amountTransaction = amountTransaction;
        this.reputationController = reputationController;
    }

    @Override
    public void start() {
        insertEntityId();
        insertAccountIdWithRandomEntityId();
        insertRandomTransaction();

    }

    //gli entittyId saranno del tipo "0,1,2..amountEntityId-1"
    private void insertEntityId(){
        for(int i = 0; i < this.amountEntityId; i++){
            reputationController.addEntityId(String.valueOf(i));
        }
    }

    //gli account id saranno del tipo "IT+indice del ciclo"
    private void insertAccountIdWithRandomEntityId(){
        Random random = new Random();
        for(int i = 0; i < this.amountAccountId; i++){
            int randomEntityId = random.nextInt(amountEntityId-1);
            reputationController.addAccountIdWithEntityId(String.valueOf(randomEntityId),prefixAccountId+i);

        }
    }

    private void insertRandomTransaction(){
        //equivalgono a 10 giorni, sottrarro un valore random [0,maxRangeDate] al currentMillis
        //per ottenere una data casuale definita tra [currentmilliss-10giorni, currentmilliss]
        int maxRangeRandomDate = 864000000;
        Random random = new Random();

        for(int i = 0; i < this.amountTransaction; i++){
            TransactionBuilder transactionBuilder = new TransactionBuilder();
            transactionBuilder.setDateOfSubmission(new Date(System.currentTimeMillis()-random.nextInt(maxRangeRandomDate)))
                    .setChannelId(this.channelId[random.nextInt(2)]).setAmount(random.nextInt(5000)).setCurrency("EUR")
                    .setProductId("TRANSACTION").setCity(this.city[random.nextInt(5)]).setCountry(this.country[random.nextInt(2)]);
            String payer = prefixAccountId + random.nextInt(amountAccountId-1);
            String payee = prefixAccountId + random.nextInt(amountAccountId-1);
            reputationController.addTransaction(payer,payee,transactionBuilder.build());

        }
    }


}

