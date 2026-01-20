/* Licensed under MIT 2026. */
package org.fuchss.restredis.client;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.jspecify.annotations.Nullable;

public record ClientConfiguration(
        @JsonProperty("rest_uri") String restUri,
        @JsonProperty @Nullable String username,
        @JsonProperty @Nullable String password) {}
