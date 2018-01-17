package it.ldario.graphdbcsv;

import it.ldario.basegraphdb.dbentity.Transaction;
import it.ldario.basegraphdb.dbentity.TransactionBuilder;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;

public class GeneratorCsvRandom implements GeneratorCsv{
    private final String[] channelId = {"ROOT","RETAIL","ECOMMERCE"};
    private final String[] city = {"Padova","Borgoricco","Campodarsego","Camposampiero","Sambruson","Castelfranco"};
    private final String[] country = {"Italy","UK","Spain"};
    private final String[] legalEntityID = {"ROOT","NOROOT"};
    private final Object [] FILE_HEADER_ENTITY = {"entityId"};
    private final Object [] FILE_HEADER_ACCOUNT = {"accountId"};
    private final Object [] FILE_HEADER_ENTITY_ACCOUNT = {"entityId","accountId"};
    private final Object [] FILE_HEADER_TRANSACTION = {"payer","payee","dateOfSubmission","amount","currency","channelId","productId",
                                                       "city","country"};

    private final String prefixAccountId = "IT";

    private final String NEW_LINE_SEPARATOR = "\n";
    private FileWriter fileWriterEntity;
    private FileWriter fileWriterEntityAccountId;
    private FileWriter fileWriterTransaction;
    private FileWriter fileWriterAccount;


    private int amountEntityId;
    private int amountAccountId;
    private int amountTransaction;

    private CSVFormat csvFileFormat;

    public GeneratorCsvRandom(int amountEntityId, int amountAccountId, int amountTransaction){
        this.amountEntityId = amountEntityId;
        this.amountAccountId = amountAccountId;
        this.amountTransaction = amountTransaction;
        this.csvFileFormat = CSVFormat.DEFAULT.withRecordSeparator(NEW_LINE_SEPARATOR);
    }






    @Override
    public void start() throws IOException {

        generatorEntityId();
        generatorAccountId();
        generatorRelationOwn();
        generatorTransaction();

    }

    private void generatorEntityId() throws IOException {
        this.fileWriterEntity = new FileWriter("entity.csv");
        CSVPrinter csvFilePrinter = new CSVPrinter(fileWriterEntity, csvFileFormat);
        csvFilePrinter.printRecord(FILE_HEADER_ENTITY);
        for(int i = 0; i < amountEntityId; i++){
            csvFilePrinter.printRecord(i);
        }
        fileWriterEntity.flush();
        fileWriterEntity.close();
        csvFilePrinter.close();
    }

    private void generatorAccountId() throws IOException {
        this.fileWriterAccount = new FileWriter("account.csv");
        CSVPrinter csvFilePrinter = new CSVPrinter(fileWriterAccount, csvFileFormat);
        csvFilePrinter.printRecord(FILE_HEADER_ACCOUNT);
        for(int i = 0; i < amountAccountId; i++){
            csvFilePrinter.printRecord(prefixAccountId+i);
        }
        fileWriterAccount.flush();
        fileWriterAccount.close();
    }

    private void generatorRelationOwn() throws IOException {
        Random random = new Random();
        this.fileWriterEntityAccountId = new FileWriter("entity_account.csv");
        CSVPrinter csvFilePrinter = new CSVPrinter(fileWriterEntityAccountId, csvFileFormat);
        csvFilePrinter.printRecord(FILE_HEADER_ENTITY_ACCOUNT);

        for(int i = 0; i < amountAccountId; i++){
            csvFilePrinter.printRecord(random.nextInt(this.amountEntityId-1),prefixAccountId+i);
        }
        fileWriterEntityAccountId.flush();
        fileWriterEntityAccountId.close();
        csvFilePrinter.close();
    }

    private  void generatorTransaction() throws IOException {
        //equivalgono a 10 giorni, sottrarro un valore random [0,maxRangeDate] al currentMillis
        //per ottenere una data casuale definita tra [currentmilliss-10giorni, currentmilliss]
        int maxRangeRandomDate = 864000000;
        Random random = new Random();
        this.fileWriterTransaction = new FileWriter("transaction.csv");
        CSVPrinter csvFilePrinter = new CSVPrinter(fileWriterTransaction, csvFileFormat);
        csvFilePrinter.printRecord(FILE_HEADER_TRANSACTION);

        for(int i = 0; i < amountTransaction; i++){
            TransactionBuilder transactionBuilder = new TransactionBuilder();
            transactionBuilder.setDateOfSubmission(new Date(System.currentTimeMillis()-random.nextInt(maxRangeRandomDate)))
                    .setChannelId(this.channelId[random.nextInt(2)]).setAmount(random.nextInt(5000)).setCurrency("EUR")
                    .setProductId("TRANSACTION").setCity(this.city[random.nextInt(5)]).setCountry(this.country[random.nextInt(2)]);
            String payer = prefixAccountId + random.nextInt(amountAccountId-1);
            String payee = prefixAccountId + random.nextInt(amountAccountId-1);
            Transaction transaction = transactionBuilder.build();



            csvFilePrinter.printRecord(payer,payee,transaction.getDateOfSubmissionx().getTime(),transaction.getAmount(),
                    transaction.getCurrency(),transaction.getChannelId(),transaction.getProductId(),transaction.getCity(),
                    transaction.getCountry());
        }
        fileWriterTransaction.flush();
        fileWriterTransaction.close();
        csvFilePrinter.close();
    }
}
