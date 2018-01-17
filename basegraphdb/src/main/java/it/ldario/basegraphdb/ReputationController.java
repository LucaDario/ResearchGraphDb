package it.ldario.basegraphdb;

import it.ldario.basegraphdb.dbentity.Transaction;
import org.springframework.stereotype.Component;

import java.util.Vector;


@Component
public interface ReputationController {

    /**
     * Aggiunge nel DB un nodo di classe EntityId con quel determinato valore, entityId è unico e non può essere duplicato nel DB
     * @param entityId:String aggiunge un entityId nel DB con quella determinata chiave
     * @return true se viene aggiunto, false se è già presente
     */
    boolean addEntityId(String entityId);

    /**
     * Aggiunge nel DB un nodo di classe AccountId con quel determinato valore, accountId è unico e non può essere duplicato nel DB
     * @param accountId:String aggiunge un entityId nel DB con quella determinata chiave
     * @return true se viene aggiunto, false se è già presente
     */
    boolean addAccountId(String accountId);

    /**
     * Aggiunge una relazione di OWN (con un arco) da entityId a accountId, se se non esistono queste entità vengono create e aggiunge al db
     *  [entityId]-----OWN---->[accountId])
     * @param entityId:String proprietario del AccountId
     * @param accountId:String AccountId da aggiungere
     *
     */
    void addAccountIdWithEntityId(String entityId, String accountId);

    /**
     *Elimina l'entity con quel determinato id e con esso tutte le relazioni di OWN ad esso associate
     * @param entityId:String entity da eliminare
     *
     */
    void deleteEntityId(String entityId);

    /**
     * Elimina l'accountId con quel determinato id e con esso tutte le relazioni di TRANSACTION ad esso associate
     * @param accountId:String accountId da eliminare
     *
     */
    void deleteAccountId(String accountId);

    /**
     * Se sono già presenti sia payer che payee viene aggiunta una transizione tra di loro, altrimenti non fa nulla
     * @param payerAccountId:String payer della transizione
     * @param payeeAccountId:String payee della transizione
     * @param transaction:Transaction transizione da aggiungere
     * @return Int: 0 se il payee non è presente, 1 payer non presente, 2 entrambi non presenti, 3 se sono entrambi presenti e
     * aggiunto correttamente.
     */
    int addTransaction(String payerAccountId, String payeeAccountId, Transaction transaction);

    /**
     * Ritorna la reputazione totale di un payee
     * @param payeeId:String payee da calcolare la reputazione
     * @return Int -1 se il payee non è presente, n>0 se è presente e rappresenta la sua reputazione
     */
    int getTotalReputation(String payeeId);

    /**
     * Ritorna la reputazione relativa di un payee, cioè la somma di tutte le transazioni che riceve un payee da un determinato payer
     * @param payeeId:String payee da calcolare la reputazioen relativa
     * @param payerId:String payer
     * @return Int Int -1 se il payee non è presente, n>0 se è presente e rappresenta la sua reputazione relativa
     */
    int getRelativeReputation(String payeeId, String payerId);


    /**
     * Dato un payee ritorna la somma di tutte le reputazioni totali degli accountId che sono associati allo stesso entityId, se presente.
     * es. dato   (accountId:IT56...)<----[OWW]----(entityId:Luca)----[OWN]-->(accountId:IT43...) la chiamata getGlobalReputation("Luca")
     * ritorna la somma della reputazione tra tra IT56.. e IT43.., stessa cosa con la chiamata getGlobalReputation(IT56..)
     * @param payeeId:String
     * @return -1 il payee non esiste, n>0 la somma tra le reputazioni di tutte le entità coinvolte.
     */
    int getGlobalReputation(String payeeId);

    /**
     * Dato un payee e un payer ritorna la somma di tutte le reputazioni relative(a questi 2 dati) degli accountId che sono associati allo stesso
     * entityId, se presente. Es (accountId:IT56...)<----[OWW]----(entityId:Luca)----[OWN]-->(accountId:IT43...) la chiamata getGlobalReputation("Luca","IT..")
     * ritorna la somma della reputazione Relativa tra IT56.. e IT43.., stessa cosa con la chiamata getGlobalReputation(IT56..,"IT..")
     * @param payeeId:Sting
     * @param payerId:String
     * @return -1 se il payeeId non esiste, n>0 se è presente e questo intero rappresenta la reputazione globale relativa
     */
    int getGlobalRelativeReputation(String payeeId, String payerId);


    /**
     * Calcola la somma delle amount delle transazioni svolte da un particolare accountId in un lasso di tempo.
     * Se Days è un intero positivo calcolerà la somma delle transazioni a partire dal [dataAttuale - days, dataAttuale],
     * invece se Days è negativo terrà l'intervallo di [-infinito, dataAttuale - days]
     * @param accountId:String
     * @param days:String
     * @return ritorna la somma delle transazioni nell' intervallo scelto svolte da quel accountiD
     */
    double getAmountAccountIdInATimeRange(String accountId, int days);

    /**
     * Ritorna il nome del database implementato
     * @return String nome dell'implementazione
     */
    String getNameDatabase();










}
