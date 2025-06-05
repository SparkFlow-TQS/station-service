package tqs.sparkflow.stationservice.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.lang.NonNull;
import org.springframework.web.servlet.config.annotation.PathMatchConfigurer;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import tqs.sparkflow.stationservice.controller.RootController;

/**
 * Web configuration that adds the /api/v1 prefix to controllers except RootController and SpringDoc.
 * This centralizes the API versioning and makes it easier to maintain.
 */
@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void configurePathMatch(@NonNull PathMatchConfigurer configurer) {
        configurer.addPathPrefix("/api/v1", c -> {
            // Exclude RootController from the prefix
            if (c.equals(RootController.class)) {
                return false;
            }
            
            // Exclude SpringDoc/Swagger classes from the prefix
            String packageName = c.getPackage().getName();
            if (packageName.startsWith("org.springdoc") || packageName.startsWith("org.springframework.boot.actuate")) {
                return false;
            }
            
            return true;
        });
    }
} 