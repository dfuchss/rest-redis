/* Licensed under MIT 2026. */
package org.fuchss.restredis.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Request payload for the hset endpoint.
 *
 * @param key Redis hash key
 * @param field hash field to write
 * @param value value to store
 */
public record HSetRequest(
        @JsonProperty String key,
        @JsonProperty String field,
        @JsonProperty String value) {}
