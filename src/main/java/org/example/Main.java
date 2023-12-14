package org.example;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import org.flywaydb.core.Flyway;

public class Main {
    public static void main(String[] args) throws JsonProcessingException {
        Config config = ConfigFactory.load();
        ObjectMapper mapper = new ObjectMapper();

        Flyway flyway =
                Flyway.configure()
                        .locations("classpath:db/migrations")
                        .dataSource(
                                config.getString("app.database.url"),
                                config.getString("app.database.user"),
                                config.getString("app.database.password"))
                        .load();
        flyway.migrate();
    }
}