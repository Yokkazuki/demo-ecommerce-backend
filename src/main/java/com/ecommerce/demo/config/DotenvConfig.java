package com.ecommerce.demo.config;

import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.MapPropertySource;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class DotenvConfig {

    @Bean
    public Dotenv dotenv(ConfigurableEnvironment environment) {
        Dotenv dotenv = Dotenv.configure().ignoreIfMissing().load();

        Map<String, Object> envMap = new HashMap<>();
        for (var entry : dotenv.entries()) {
            envMap.put(entry.getKey(), entry.getValue());
        }

        environment.getPropertySources().addFirst(new MapPropertySource("dotenv", envMap));
        return dotenv;
    }
}