package it.ldario.graphdbneo4j;

import it.ldario.basegraphdb.ReputationController;
import it.ldario.basegraphdb.dbentity.Transaction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Component;
import java.util.Objects;
import java.util.Vector;

import static java.lang.StrictMath.abs;

@Component
public class ReputationControllerNeo4j implements ReputationController {

    private final EntityIdNeo4jRepository entityIdNeo4jRepository;
    private final AccountIdNeo4jRepository accountIdNeo4jRepository;
    private final TransactionNeo4jRepository transactionNeo4jRepository;

    @Autowired
    public ReputationControllerNeo4j(EntityIdNeo4jRepository entityIdNeo4jRepository,
                                     AccountIdNeo4jRepository accountIdNeo4jRepository,
                                     TransactionNeo4jRepository transactionNeo4jRepository) {
        this.entityIdNeo4jRepository = entityIdNeo4jRepository;
        this.accountIdNeo4jRepository = accountIdNeo4jRepository;
        this.transactionNeo4jRepository = transactionNeo4jRepository;
    }


    @Override
    public void deleteEntityId(String entityId) {
       entityIdNeo4jRepository.deleteEntity(entityId);
    }

    @Override
    public void deleteAccountId(String accountId) {
        accountIdNeo4jRepository.deleteAccountIdAndHisTransaction(accountId);
    }

    @Override
    public boolean addAccountId(String accountId) {
        try {
            AccountIdNeo4j accountIdNeo4j = new AccountIdNeo4j(accountId);
            accountIdNeo4jRepository.save(accountIdNeo4j);
            return true;
        }
        catch (DataIntegrityViolationException e){
            System.out.println("Integrità violata, accountId già presente");
        }
        return false;
    }

    @Override
    public boolean addEntityId(String entityId) {
        try {
            EntityIdNeo4j entityIdNeo4j = new EntityIdNeo4j(entityId);
            entityIdNeo4jRepository.save(entityIdNeo4j);
            return true;
        }
        catch (DataIntegrityViolationException e){
            System.out.println("Integrità violata, entityId già presente");
        }
        return false;

    }

    @Override
    public void addAccountIdWithEntityId(String entityId, String accountId) {

        if(getEntityId(entityId) == null){
            EntityIdNeo4j entityIdNeo4j = new EntityIdNeo4j(entityId);
            entityIdNeo4jRepository.save(entityIdNeo4j);
        }
        if(getAccountId(accountId) == null){
            AccountIdNeo4j accountIdNeo4j = new AccountIdNeo4j(accountId);
            accountIdNeo4jRepository.save(accountIdNeo4j);
        }

        entityIdNeo4jRepository.addRelationOwn(entityId,accountId);
    }

    @Override
    public int addTransaction(String payerAccountId, String payeeAccountId, Transaction transaction) {
        boolean existPayer = false;
        boolean existPayee = false;

        AccountIdNeo4j payerAccountIdObject = getAccountId(payerAccountId);
        EntityIdNeo4j payerEntityIdObject = getEntityId(payerAccountId);

        AccountIdNeo4j payeeAccountIdObject = getAccountId(payeeAccountId);


        int result = 0;
        existPayer = payerAccountIdObject != null;
        if(!existPayer){
            existPayer = payerEntityIdObject != null;
        }

        existPayee = payeeAccountIdObject != null;

        if(!existPayee && existPayer){
            result = 0;
        }

        if(existPayee && !existPayer){
            result = 1;
        }

        if(!existPayer && (!existPayee)){
            result = 2;
        }




        if(existPayee  && existPayer){
            TransactionNeo4j transactionNeo4j = new TransactionGeneralToNeo4j().convert(transaction);
            if(payerEntityIdObject != null){
                transactionNeo4j.setStartNode(payerEntityIdObject);
            }
            else{
                transactionNeo4j.setStartNode(payerAccountIdObject);
            }

            transactionNeo4j.setEndNode(payeeAccountIdObject);
            transactionNeo4jRepository.save(transactionNeo4j);
            result = 3;
        }
        return result;


    }

    @Override
    public int getTotalReputation(String payeeId) {
        int count = -1;
        if(getAccountId(payeeId) != null){
            count = accountIdNeo4jRepository.getTotalReputation(payeeId);
        }
        return count;
    }

    //ritorna la somma di una reputazione relativa ad un payer di tipo accountId sommata a quella relativa
    //ad una reputazione di tipo entityId cosi me ne frego del tipo che mi passa
    @Override
    public int getRelativeReputation(String payeeId, String payerId) {
        boolean isAccountId = getAccountId(payerId) != null;
        boolean isEntityId =  getEntityId(payerId) != null;
        boolean existPayee = getAccountId(payeeId) != null;

        int count = -1;
        if(existPayee && isAccountId){
            count = accountIdNeo4jRepository.getRelativeReputation(payerId,payeeId);
        }

        else if(existPayee && isEntityId){
            count = entityIdNeo4jRepository.getRelativeReputation(payerId,payeeId);
        }

        if(existPayee && !isAccountId && !isEntityId){
            count = 0;
        }
        return count;
    }

    private AccountIdNeo4j getAccountId(String accountId){
        Iterable<AccountIdNeo4j> iterable = accountIdNeo4jRepository.getAccountId(accountId);
        if(iterable.iterator().hasNext() ){
            return iterable.iterator().next();
        }
        else{
            return null;
        }
    }
    private EntityIdNeo4j getEntityId(String entityId){
        Long c = System.nanoTime();

        Iterable<EntityIdNeo4j> iterable = entityIdNeo4jRepository.getEntityId(entityId);
        if(iterable.iterator().hasNext()  ){
            return iterable.iterator().next();
        }
        else{
            return null;
        }
    }



    @Override
    public int getGlobalReputation(String payeeId) {

        /*
        VERSIONE SENZA USARE LA POTENZA DEL SUO LINGUAGGIO DI QUERYNG
        int count = -1;

        if(getEntityId(payeeId) != null){
            count = 0;
            Iterable<BaseAccount> accountsId = accountIdNeo4jRepository.getAccountIdOwnAnEntity(payeeId);
            for(BaseAccount baseAccount : accountsId){
               int c = getTotalReputation(baseAccount.getUniqueId());
               if(c != -1){
                   count = c + count;
               }


            }
        }
        else if(getAccountId(payeeId) != null){
            count = 0;
            Iterable<BaseAccount> accountsId = accountIdNeo4jRepository.getAccountIdConnectedAnAccountId(payeeId);
            for(BaseAccount baseAccount : accountsId){
                int c = getTotalReputation(baseAccount.getUniqueId());
                if(c != -1){
                    count = c + count;
                }

            }

        }
        return count;
         */

        //VERSIONE SFRUTATNDO A PIENO CHYPER
        int count = -1;

        if(getEntityId(payeeId) != null){
            count = 0;
            count = accountIdNeo4jRepository.getGlobalReputationForEntityId(payeeId);


        }
        else if(getAccountId(payeeId) != null){
            count = 0;
            count = accountIdNeo4jRepository.getGlobalReputationForAccountId(payeeId);

        }
        return count;
    }

    @Override
    public int getGlobalRelativeReputation(String payeeId, String payerId) {
        //setto tutti i parametri esatti
        int count = -1;
        Iterable<BaseAccount> payees = null;
        boolean payerIsEntity = false;
        if(getEntityId(payeeId) != null){
            payees =  accountIdNeo4jRepository.getAccountIdOwnAnEntity(payeeId);
        }
        else if(getAccountId(payeeId) != null){
            payees = accountIdNeo4jRepository.getAccountIdConnectedAnAccountId(payeeId);
        }

        Iterable<BaseAccount> payers = null;

        if(getEntityId(payerId) != null){
            payers =  accountIdNeo4jRepository.getAccountIdOwnAnEntity(payerId);
            payerIsEntity = true;
        }
        else if(getAccountId(payerId) != null){
            payers = accountIdNeo4jRepository.getAccountIdConnectedAnAccountId(payerId);
        }
        //per ogni payer legato da una relazione di OWNa quello dato e stessa cosa per ogni payee
        if(payees != null && payers != null){
            count = 0;
            for(BaseAccount payee : payees){
                for(BaseAccount payer : payers){
                    int c = getRelativeReputation(payee.getUniqueId(),payer.getUniqueId());
                    if(c != -1 && !Objects.equals(payee.getUniqueId(),payer.getUniqueId())){
                        count = count + c;
                    }

                }
            }
            //se il payer è un EnityId devo calcolare e sommare anche la reputazione tra il payee e questo entity vistoc che quello
            //calcolato sopra non comprendeva quella relativa tra tutti i payee e il payer passato nel caso fosse Entity perche
            //getAccountIdOwnAnEntity getAccountIdConnectedAnAccountId ritorna solo AccountId
            if(payerIsEntity){
                for(BaseAccount payee : payees){
                    int c = getRelativeReputation(payee.getUniqueId(),payerId);
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
        int amount = 0;
        Long millis = abs(days)*86400000L;

        Long range = System.currentTimeMillis() - (millis);
        if(days >= 0){

            amount = accountIdNeo4jRepository.getAmountAccountInLastPeriod(accountId,range);
        }

        else{
            amount = accountIdNeo4jRepository.getAmountAccountWithoutLastPeriod(accountId,range);
        }

        return amount;

    }

    @Override
    public String getNameDatabase() {
        return "Neo4j";
    }
}
