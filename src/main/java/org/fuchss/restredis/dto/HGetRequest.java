/* Licensed under MIT 2026. */
package org.fuchss.restredis.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public record HGetRequest(
        @JsonProperty String key, @JsonProperty String field) {}
