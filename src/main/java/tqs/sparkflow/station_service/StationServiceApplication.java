package tqs.sparkflow.station_service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class StationServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(StationServiceApplication.class, args);
	}

	// This is a very long line that should trigger a style violation because it exceeds 100 characters and has operators that should be on new lines
	private String veryLongString = "This is a very long string" + " that should be split into multiple lines" + " because it exceeds the maximum line length" + " and has operators that should be on new lines";

}
