/* Licensed under MIT 2026. */
package org.fuchss.restredis.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Request payload for the hget endpoint.
 *
 * @param key Redis hash key
 * @param field hash field to read
 */
public record HGetRequest(
        @JsonProperty String key, @JsonProperty String field) {}
