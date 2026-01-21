/* Licensed under MIT 2026. */
package org.fuchss.restredis.server;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.File;

/**
 * Configuration for the REST-Redis server.
 *
 * @param redisHost Redis hostname
 * @param redisPort Redis port
 * @param httpPort HTTP server port
 */
public record ServerConfiguration(
        @JsonProperty("redis_host") String redisHost,
        @JsonProperty("redis_port") int redisPort,
        @JsonProperty("http_port") int httpPort) {
    /**
     * Loads the server configuration from a JSON file.
     *
     * @param configFile configuration file to read
     * @return parsed {@link ServerConfiguration}
     * @throws IllegalStateException if the file cannot be read or parsed
     */
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
