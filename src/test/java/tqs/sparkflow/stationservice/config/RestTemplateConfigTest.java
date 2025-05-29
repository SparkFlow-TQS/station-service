package tqs.sparkflow.stationservice.config;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.client.RestTemplate;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(classes = RestTemplateConfig.class)
class RestTemplateConfigTest {
    @Autowired
    private RestTemplateConfig restTemplateConfig;

    @Test
    void contextLoads() {
        assertThat(restTemplateConfig).isNotNull();
        RestTemplate restTemplate = restTemplateConfig.restTemplate();
        assertThat(restTemplate).isNotNull();
    }
} 