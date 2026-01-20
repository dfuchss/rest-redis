/* Licensed under MIT 2026. */
package org.fuchss.restredis.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public record ExistsRequest(@JsonProperty String key) {}
