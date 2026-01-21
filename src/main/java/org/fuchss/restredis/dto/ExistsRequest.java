/* Licensed under MIT 2026. */
package org.fuchss.restredis.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Request payload for the exists endpoint.
 *
 * @param key key to check
 */
public record ExistsRequest(@JsonProperty String key) {}
