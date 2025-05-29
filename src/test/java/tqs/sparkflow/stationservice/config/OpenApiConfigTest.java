package tqs.sparkflow.stationservice.config;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.beans.factory.annotation.Autowired;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(classes = OpenApiConfig.class)
class OpenApiConfigTest {
    @Autowired
    private OpenApiConfig openApiConfig;

    @Test
    void contextLoads() {
        assertThat(openApiConfig).isNotNull();
    }
} 