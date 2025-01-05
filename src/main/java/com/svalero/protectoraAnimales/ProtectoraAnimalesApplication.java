package com.svalero.protectoraAnimales;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class ProtectoraAnimalesApplication {

	public static void main(String[] args) {
		try {
			SpringApplication.run(ProtectoraAnimalesApplication.class, args);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
