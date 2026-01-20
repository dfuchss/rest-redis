/* Licensed under MIT 2026. */
package org.fuchss.restredis.client;

import kong.unirest.core.Unirest;
import org.fuchss.restredis.dto.ExistsRequest;
import org.fuchss.restredis.dto.HGetRequest;
import org.fuchss.restredis.dto.HSetRequest;

public class Client {

    public Client(ClientConfiguration clientConfiguration) {
        Unirest.config().defaultBaseUrl(clientConfiguration.restUri());
        if (clientConfiguration.username() != null && clientConfiguration.password() != null) {
            Unirest.config().setDefaultBasicAuth(clientConfiguration.username(), clientConfiguration.password());
        }
        if (!ping()) {
            throw new IllegalStateException(
                    "Could not connect to REST-Redis server at " + clientConfiguration.restUri());
        }
    }

    public boolean isBridgeAvailable() {
        return Unirest.get("/").asString().isSuccess();
    }

    public boolean ping() {
        return Unirest.get("/ping").asBytes().isSuccess();
    }

    public boolean exists(String key) {
        var request = Unirest.post("/exists").body(new ExistsRequest(key)).asString();
        if (!request.isSuccess()) {
            throw new IllegalStateException("Request failed with code: " + request.getStatus());
        }
        return Boolean.parseBoolean(request.getBody());
    }

    public String hget(String key, String field) {
        var request = Unirest.post("/hget").body(new HGetRequest(key, field)).asString();
        if (!request.isSuccess()) {
            throw new IllegalStateException("Request failed with code: " + request.getStatus());
        }
        return request.getBody();
    }

    public long hset(String key, String field, String value) {
        var request =
                Unirest.post("/hset").body(new HSetRequest(key, field, value)).asString();
        if (!request.isSuccess()) {
            throw new IllegalStateException("Request failed with code: " + request.getStatus());
        }
        return Long.parseLong(request.getBody());
    }

    public void close() {
        Unirest.shutDown();
    }
}
