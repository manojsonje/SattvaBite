package com.sattvabite.order.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthComponent;
import org.springframework.boot.actuate.health.HealthEndpoint;
import org.springframework.boot.actuate.metrics.MetricsEndpoint;
import org.springframework.http.ResponseEntity;

import java.util.*;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/actuator/health")
@Tag(name = "Health Check", description = "API for health check and metrics")
public class HealthController {

    private final HealthEndpoint healthEndpoint;
    private final MetricsEndpoint metricsEndpoint;

    @Autowired
    public HealthController(HealthEndpoint healthEndpoint, MetricsEndpoint metricsEndpoint) {
        this.healthEndpoint = healthEndpoint;
        this.metricsEndpoint = metricsEndpoint;
    }

    @GetMapping("/readiness")
    @Operation(summary = "Check service readiness",
            description = "Check if the service is ready to handle requests")
    @ApiResponse(responseCode = "200", description = "Service is ready",
            content = @Content(schema = @Schema(implementation = Map.class)))
    public ResponseEntity<Map<String, Object>> readiness() {
        Map<String, Object> response = new HashMap<>();
        response.put("status", "UP");
        response.put("service", "order-service");
        return ResponseEntity.ok(response);
    }

    @GetMapping("/liveness")
    @Operation(summary = "Check service liveness",
            description = "Check if the service is running")
    @ApiResponse(responseCode = "200", description = "Service is live",
            content = @Content(schema = @Schema(implementation = Map.class)))
    public ResponseEntity<Map<String, Object>> liveness() {
        Map<String, Object> response = new HashMap<>();
        response.put("status", "ALIVE");
        response.put("service", "order-service");
        return ResponseEntity.ok(response);
    }

    @GetMapping("/info")
    @Operation(summary = "Get service information",
            description = "Get detailed information about the service")
    @ApiResponse(responseCode = "200", description = "Service information retrieved",
            content = @Content(schema = @Schema(implementation = Map.class)))
    public ResponseEntity<Map<String, Object>> info() {
        Map<String, Object> info = new HashMap<>();
        info.put("name", "order-service");
        info.put("version", "1.0.0");
        
        // Get health status
        HealthComponent health = healthEndpoint.health();
        info.put("status", health.getStatus().getCode());
        
        // Add additional health details if available
        if (health instanceof Health) {
            Map<String, Object> details = ((Health) health).getDetails();
            if (details != null && !details.isEmpty()) {
                info.put("details", details.keySet());
            } else {
                info.put("details", Collections.singleton("default"));
            }
        } else {
            info.put("details", Collections.singleton("default"));
        }
        
        return ResponseEntity.ok(info);
    }

    @GetMapping("/metrics/summary")
    @Operation(summary = "Get metrics summary",
            description = "Get a summary of important service metrics")
    @ApiResponse(responseCode = "200", description = "Metrics summary retrieved",
            content = @Content(schema = @Schema(implementation = Map.class)))
    public ResponseEntity<Map<String, Object>> metricsSummary() {
        Map<String, Object> metrics = new HashMap<>();
        
        // JVM metrics
        metrics.put("jvm.memory.used", getMetricValue("jvm.memory.used"));
        metrics.put("jvm.memory.max", getMetricValue("jvm.memory.max"));
        metrics.put("jvm.threads.live", getMetricValue("jvm.threads.live"));
        
        // HTTP server metrics
        metrics.put("http.server.requests.count", getMetricValue("http.server.requests", "count"));
        metrics.put("http.server.requests.max", getMetricValue("http.server.requests", "max"));
        
        // System metrics
        metrics.put("system.cpu.usage", getMetricValue("system.cpu.usage"));
        metrics.put("process.cpu.usage", getMetricValue("process.cpu.usage"));
        
        // Database metrics
        metrics.put("hikaricp.connections.active", getMetricValue("hikaricp.connections.active"));
        metrics.put("hikaricp.connections.idle", getMetricValue("hikaricp.connections.idle"));
        
        return ResponseEntity.ok(metrics);
    }

    private Object getMetricValue(String metricName) {
        return metricsEndpoint.metric(metricName, null)
                .getMeasurements()
                .stream()
                .findFirst()
                .map(MetricsEndpoint.Sample::getValue)
                .orElse(null);
    }

    private Object getMetricValue(String metricName, String statistic) {
        return metricsEndpoint.metric(metricName, null)
                .getMeasurements()
                .stream()
                .filter(m -> statistic.equals(m.getStatistic().name()))
                .findFirst()
                .map(MetricsEndpoint.Sample::getValue)
                .orElse(null);
    }
}
