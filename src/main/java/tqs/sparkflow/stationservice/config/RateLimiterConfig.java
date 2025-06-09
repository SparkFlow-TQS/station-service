package tqs.sparkflow.stationservice.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import com.google.common.util.concurrent.RateLimiter;

@Configuration
public class RateLimiterConfig {

  @Bean
  public RateLimiter routePlanningRateLimiter(RoutePlanningConfig config) {
    return RateLimiter.create(config.getRequestsPerSecond());
  }
}
