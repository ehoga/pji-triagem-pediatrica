package com.pji.triagem.base.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.flyway.FlywayMigrationStrategy;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FlyWayMigrationConfig 
{
    @Value("${spring.flyway.enabled}")
    private Boolean isEnabled;

    @Bean
    public FlywayMigrationStrategy flywayMigrationStrategy() 
    {
        return flyway -> {
            if (isEnabled) {
                flyway.migrate();
            }
        };
    }
}

