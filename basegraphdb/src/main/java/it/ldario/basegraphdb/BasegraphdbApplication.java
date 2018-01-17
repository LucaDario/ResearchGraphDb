package it.ldario.basegraphdb;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class BasegraphdbApplication implements CommandLineRunner {

	public static void main(String[] args) {
		SpringApplication.run(BasegraphdbApplication.class, args);
	}

	@Override
	public void run(String... strings) throws Exception {
	}
}
