package tqs.sparkflow.stationservice.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.config.annotation.PathMatchConfigurer;
import org.springframework.lang.NonNull;

/**
 * Web configuration that adds the /api/v1 prefix to all controllers.
 * This centralizes the API versioning and makes it easier to maintain.
 */
@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void configurePathMatch(@NonNull PathMatchConfigurer configurer) {
        configurer.addPathPrefix("/api/v1", c -> true);
    }
} 