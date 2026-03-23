package com.esprit.backendsport.Security;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.io.File;
import java.nio.file.Paths;

/**
 * Configuration for serving static files (images) from the /uploads directory
 * Makes the /uploads folder PUBLIC (no authentication required)
 */
@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    /**
     * Configure resource handlers to serve uploaded images
     * Maps /uploads/** requests to the file:uploads/ directory
     * Images are publicly accessible without authentication
     */
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // Get absolute path to uploads directory
        String uploadDir = new File("uploads").getAbsolutePath();
        
        // Register resource handler for /uploads/** path
        registry.addResourceHandler("/uploads/**")
                .addResourceLocations("file:" + uploadDir + "/")
                .setCachePeriod(3600) // Cache for 1 hour
                .resourceChain(true);
    }
}

