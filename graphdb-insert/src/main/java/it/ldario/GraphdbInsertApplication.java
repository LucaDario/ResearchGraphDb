package it.ldario;

import it.ldario.graphdbinsert.GraphDbInsertImpl;
import it.ldario.graphdbinsert.GraphdbInsert;
import it.ldario.graphdbneo4j.ReputationControllerNeo4j;
import it.ldario.graphdborientDb.ReputationControllerOrientDb;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.FileReader;
import java.io.FileWriter;
import java.util.concurrent.TimeUnit;

@SpringBootApplication
public class GraphdbInsertApplication implements CommandLineRunner{

	public static void main(String[] args) {
		SpringApplication.run(GraphdbInsertApplication.class, args);
	}


	@Autowired
	private ReputationControllerNeo4j reputationControllerNeo4j;

	@Autowired
	private ReputationControllerOrientDb reputationControllerOrientDb;
	@Override
	public void run(String... strings) throws Exception {

		//grazie al polimorfisco passo il reputation controller del tipo che voglio popolare con il mio csv


		//GraphDbInsertImpl graphDbInsert = new GraphDbInsertImpl(10,10,10,reputationControllerOrientDb);






	}
}
