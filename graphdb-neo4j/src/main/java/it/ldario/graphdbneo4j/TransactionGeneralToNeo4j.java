package it.ldario.graphdbneo4j;

import it.ldario.basegraphdb.dbentity.Transaction;
import it.ldario.basegraphdb.dbentity.TransactionBuilder;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
final class TransactionGeneralToNeo4j implements Converter<Transaction,TransactionNeo4j> {

    @Override
    public TransactionNeo4j convert(Transaction transaction) {
        TransactionNeo4j transactionNeo4j = new TransactionNeo4j();
        transactionNeo4j.setDateOfSubmissionx(transaction.getDateOfSubmissionx());
        transactionNeo4j.setLegalEntId(transaction.getLegalEntId());
        transactionNeo4j.setChannelId(transaction.getChannelId());
        transactionNeo4j.setProductId(transaction.getProductId());
        transactionNeo4j.setAmount(transaction.getAmount());
        transactionNeo4j.setCurrency(transaction.getCurrency());
        transactionNeo4j.setIpAddress(transaction.getIpAddress());
        transactionNeo4j.setCity(transaction.getCity());
        transactionNeo4j.setCountry(transaction.getCountry());
        return transactionNeo4j;
    }
}
