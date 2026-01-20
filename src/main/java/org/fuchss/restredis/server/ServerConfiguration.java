/* Licensed under MIT 2026. */
package org.fuchss.restredis.server;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.File;

public record ServerConfiguration(
        @JsonProperty("redis_host") String redisHost,
        @JsonProperty("redis_port") int redisPort,
        @JsonProperty("http_port") int httpPort) {
    public static ServerConfiguration loadFromFile(File configFile) {
        ObjectMapper mapper = new ObjectMapper();
        try {
            return mapper.readValue(configFile, ServerConfiguration.class);
        } catch (Exception e) {
            throw new IllegalStateException(
                    "Could not load server configuration from file: " + configFile.getAbsolutePath(), e);
        }
    }
}
