package com.springboot.auftragsmanagement;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class SpringbootAuftragsmanagementApplication {

	public static void main(String[] args) {
		long firstOrderNumber = OrderNumberGenerator.getInstance().generateNextOrderNumber();
		System.out.println("Erste generierte Auftragsnummer (Singleton): " + firstOrderNumber);
		SpringApplication.run(SpringbootAuftragsmanagementApplication.class, args);
	}

}

