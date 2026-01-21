/* Licensed under MIT 2026. */
package org.fuchss.restredis.client;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.jspecify.annotations.Nullable;

/**
 * Configuration for the REST client.
 *
 * @param restUri base URI of the REST-Redis server
 * @param username optional basic auth username
 * @param password optional basic auth password
 */
public record ClientConfiguration(
        @JsonProperty("rest_uri") String restUri,
        @JsonProperty @Nullable String username,
        @JsonProperty @Nullable String password) {}
