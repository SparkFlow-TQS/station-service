package tqs.sparkflow.stationservice;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

@TestConfiguration
@Testcontainers
public class TestcontainersConfiguration {

  @SuppressWarnings("resource")
  @Container
  static final MySQLContainer<?> mysql = new MySQLContainer<>("mysql:8.0")
      .withDatabaseName("testdb")
      .withUsername("test")
      .withPassword("test");

  @DynamicPropertySource
  static void configureProperties(DynamicPropertyRegistry registry) {
    registry.add("spring.datasource.url", mysql::getJdbcUrl);
    registry.add("spring.datasource.username", mysql::getUsername);
    registry.add("spring.datasource.password", mysql::getPassword);
    registry.add("spring.datasource.driver-class-name", () -> "com.mysql.cj.jdbc.Driver");

    registry.add("spring.jpa.properties.hibernate.dialect", () -> "org.hibernate.dialect.MySQLDialect");
    registry.add("spring.jpa.hibernate.ddl-auto", () -> "create-drop");
    registry.add("spring.jpa.show-sql", () -> "true");
    registry.add("spring.jpa.properties.hibernate.format_sql", () -> "true");
    registry.add("spring.jpa.properties.hibernate.jdbc.time_zone", () -> "UTC");
    registry.add("spring.jpa.properties.hibernate.query.fail_on_pagination_over_collection_fetch", () -> "false");
    registry.add("spring.jpa.properties.hibernate.id.new_generator_mappings", () -> "false");

    registry.add("spring.flyway.enabled", () -> "false");
    registry.add("spring.flyway.baseline-on-migrate", () -> "false");
  }
}
