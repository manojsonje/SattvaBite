package com.sattvabite.common.health;

import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.util.List;
import java.util.Map;

@Component
public class DatabaseHealthIndicator implements HealthIndicator {

    private final JdbcTemplate jdbcTemplate;

    public DatabaseHealthIndicator(DataSource dataSource) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    @Override
    public Health health() {
        try {
            // Execute a simple query to check database connectivity
            List<Map<String, Object>> result = jdbcTemplate.queryForList(
                "SELECT 1 as status, VERSION() as version, DATABASE() as database, USER() as user");
            
            if (!result.isEmpty()) {
                Map<String, Object> dbInfo = result.get(0);
                return Health.up()
                        .withDetail("status", "Database is up and running")
                        .withDetail("version", dbInfo.get("version"))
                        .withDetail("database", dbInfo.get("database"))
                        .withDetail("user", dbInfo.get("user"))
                        .build();
            }
            return Health.unknown().withDetail("status", "No database information available").build();
        } catch (Exception e) {
            return Health.down()
                    .withDetail("status", "Database connection failed")
                    .withDetail("error", e.getMessage())
                    .build();
        }
    }
}
