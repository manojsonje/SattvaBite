package com.sattvabite.order.health;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.stereotype.Component;

@Component
public class MongoHealthIndicator implements HealthIndicator {

    private final MongoClient mongoClient;
    private static final String DATABASE_NAME = "orderdb";

    public MongoHealthIndicator(MongoClient mongoClient) {
        this.mongoClient = mongoClient;
    }

    @Override
    public Health health() {
        try {
            MongoDatabase db = mongoClient.getDatabase(DATABASE_NAME);
            Document stats = db.runCommand(new Document("serverStatus", 1));
            return Health.up()
                    .withDetail("version", stats.getString("version"))
                    .withDetail("uptime", stats.get("uptime"))
                    .withDetail("ok", stats.getDouble("ok"))
                    .build();
        } catch (Exception e) {
            return Health.down()
                    .withDetail("error", e.getMessage())
                    .build();
        }
    }
}
