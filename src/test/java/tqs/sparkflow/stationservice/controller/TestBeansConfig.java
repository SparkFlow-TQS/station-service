package tqs.sparkflow.stationservice.controller;

import org.mockito.Mockito;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.web.client.RestTemplate;

@TestConfiguration
public class TestBeansConfig {
    
    @Bean
    @Primary
    public RestTemplate restTemplate() {
        return Mockito.mock(RestTemplate.class);
    }

    @Bean
    @Primary
    public String userServiceUrl() {
        return "http://mock-user-service";
    }
} 