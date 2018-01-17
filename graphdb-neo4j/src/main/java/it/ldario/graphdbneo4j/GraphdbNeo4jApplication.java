package it.ldario.graphdbneo4j;

import it.ldario.basegraphdb.dbentity.Transaction;
import it.ldario.basegraphdb.dbentity.TransactionBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class GraphdbNeo4jApplication implements CommandLineRunner {

	public static void main(String[] args) {
		SpringApplication.run(GraphdbNeo4jApplication.class, args);
	}

	@Autowired
	ReputationControllerNeo4j reputationControllerNeo4j;

	@Autowired
	AccountIdNeo4jRepository accountIdNeo4jRepository;


	@Override
	public void run(String... strings) throws Exception {



	}
}
