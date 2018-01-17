package it.ldario;

import it.ldario.basegraphdb.ReputationController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.Collection;
import java.util.Vector;
import java.util.concurrent.TimeUnit;

@SpringBootApplication
public class GraphdbPocApplication implements CommandLineRunner {

    public static void main(String[] args) {
        SpringApplication.run(GraphdbPocApplication.class, args);
    }


    private Collection<ReputationController> reputationControllers;

    @Autowired
    public GraphdbPocApplication(Collection<ReputationController> r) {
        this.reputationControllers = r;
    }

    @Override
    public void run(String... strings) throws Exception {

        StringBuilder res = new StringBuilder("\n");


        for (ReputationController reputationController : reputationControllers) {
            Long i = System.nanoTime();
            int r = reputationController.getGlobalReputation("IT345");
            Long e = System.nanoTime();
            res.append("0 ").append(reputationController.getNameDatabase()).append("Reputazione Totale ").append("[ris: ").append(r).append("] millis: ").append(TimeUnit.MILLISECONDS.convert(e - i, TimeUnit.NANOSECONDS)).append("\n");
        }
        res.append("\n");

        for (ReputationController reputationController : reputationControllers) {
            Long i = System.nanoTime();
            int r = reputationController.getRelativeReputation("IT345", "IT164072");
            Long e = System.nanoTime();
            res.append("1 ").append(reputationController.getNameDatabase()).append("Reputazione relativa ").append("[ris: ").append(r).append("] millis: ").append(TimeUnit.MILLISECONDS.convert(e - i, TimeUnit.NANOSECONDS)).append("\n");
        }
        res.append("\n");

        for (ReputationController reputationController : reputationControllers) {
            Long i = System.nanoTime();
            int r = reputationController.getGlobalReputation("IT345");
            Long e = System.nanoTime();
            res.append("2 ").append(reputationController.getNameDatabase()).append("Reputazione globale ").append("[ris: ").append(r).append("] millis: ").append(TimeUnit.MILLISECONDS.convert(e - i, TimeUnit.NANOSECONDS)).append("\n");
        }
        res.append("\n");

        for (ReputationController reputationController : reputationControllers) {
            Long i = System.nanoTime();
            int r = reputationController.getGlobalRelativeReputation("IT345", "IT164072");
            Long e = System.nanoTime();
            res.append("3 ").append(reputationController.getNameDatabase()).append("Reputazione globale relativa").append("[ris: ").append(r).append("] millis: ").append(TimeUnit.MILLISECONDS.convert(e - i, TimeUnit.NANOSECONDS)).append("\n");
        }
        res.append("\n");




        for (ReputationController reputationController : reputationControllers) {
            Long i = System.nanoTime();
            double r = reputationController.getAmountAccountIdInATimeRange("IT345", 15);
            Long e = System.nanoTime();
            res.append("4 ").append(reputationController.getNameDatabase()).append("Transazione nelle ultimi 15 giorni").append("[ris: ").append(r).append("] millis: ").append(TimeUnit.MILLISECONDS.convert(e - i, TimeUnit.NANOSECONDS)).append("\n");
        }
        res.append("\n");


        for (ReputationController reputationController : reputationControllers) {
            Long i = System.nanoTime();
            double r = reputationController.getAmountAccountIdInATimeRange("IT345", -15);
            Long e = System.nanoTime();
            res.append("5 ").append(reputationController.getNameDatabase()).append("transazione senza gli ultimi 15 giorni").append("[ris: ").append(r).append("] millis: ").append(TimeUnit.MILLISECONDS.convert(e - i, TimeUnit.NANOSECONDS)).append("\n");
        }
        res.append("\n");


        System.out.println(res);
    }


}


