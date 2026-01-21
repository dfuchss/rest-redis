/* Licensed under MIT 2026. */
package org.fuchss.restredis.client;

import kong.unirest.core.Unirest;
import org.fuchss.restredis.dto.ExistsRequest;
import org.fuchss.restredis.dto.HGetRequest;
import org.fuchss.restredis.dto.HSetRequest;

/**
 * REST client for the REST-Redis server.
 *
 * <p>Provides convenience methods that map to the exposed HTTP endpoints.
 */
public class Client {

    /**
     * Creates a REST-Redis client instance.
     *
     * @param clientConfiguration configuration containing REST base URL and optional credentials
     */
    public Client(ClientConfiguration clientConfiguration) {
        Unirest.config().defaultBaseUrl(clientConfiguration.restUri());
        if (clientConfiguration.username() != null && clientConfiguration.password() != null) {
            Unirest.config().setDefaultBasicAuth(clientConfiguration.username(), clientConfiguration.password());
        }
    }

    /**
     * Checks whether the HTTP bridge endpoint is available.
     *
     * @return {@code true} if the root endpoint responds successfully
     */
    public boolean isBridgeAvailable() {
        return Unirest.get("/").asString().isSuccess();
    }

    /**
     * Pings the underlying Redis connection through the server.
     *
     * @return {@code true} if Redis responds successfully
     */
    public boolean ping() {
        return Unirest.get("/ping").asBytes().isSuccess();
    }

    /**
     * Checks whether a key exists in Redis.
     *
     * @param key key to check
     * @return {@code true} if the key exists
     * @throws IllegalStateException if the request fails
     */
    public boolean exists(String key) {
        var request = Unirest.post("/exists").body(new ExistsRequest(key)).asString();
        if (!request.isSuccess()) {
            throw new IllegalStateException("Request failed with code: " + request.getStatus());
        }
        return Boolean.parseBoolean(request.getBody());
    }

    /**
     * Reads a field value from a Redis hash.
     *
     * @param key Redis hash key
     * @param field hash field to read
     * @return value for the field, or {@code null} if not present
     * @throws IllegalStateException if the request fails
     */
    public String hget(String key, String field) {
        var request = Unirest.post("/hget").body(new HGetRequest(key, field)).asString();
        if (!request.isSuccess()) {
            throw new IllegalStateException("Request failed with code: " + request.getStatus());
        }
        return request.getBody();
    }

    /**
     * Writes a field value to a Redis hash.
     *
     * @param key Redis hash key
     * @param field hash field to write
     * @param value value to store
     * @return number of fields that were added
     * @throws IllegalStateException if the request fails
     */
    public long hset(String key, String field, String value) {
        var request =
                Unirest.post("/hset").body(new HSetRequest(key, field, value)).asString();
        if (!request.isSuccess()) {
            throw new IllegalStateException("Request failed with code: " + request.getStatus());
        }
        return Long.parseLong(request.getBody());
    }

    /**
     * Shuts down the underlying HTTP client resources.
     */
    public void close() {
        Unirest.shutDown();
    }
}
