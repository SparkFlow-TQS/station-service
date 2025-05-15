package tqs.sparkflow.station_service;

import org.springframework.boot.SpringApplication;

public class TestStationServiceApplication {

	public static void main(String[] args) {
		SpringApplication.from(StationServiceApplication::main).with(TestcontainersConfiguration.class).run(args);
	}

}
