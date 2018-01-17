package it.ldario;

import it.ldario.graphdbcsv.GeneratorCsv;
import it.ldario.graphdbcsv.GeneratorCsvRandom;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class GraphdbCsvApplication implements CommandLineRunner {

	public static void main(String[] args) {
		SpringApplication.run(GraphdbCsvApplication.class, args);
	}

	@Override
	public void run(String... strings) throws Exception {
		//GeneratorCsv generatorCsv = new GeneratorCsvRandom(200000,280000,80000000);
		//generatorCsv.start();

	}
}
