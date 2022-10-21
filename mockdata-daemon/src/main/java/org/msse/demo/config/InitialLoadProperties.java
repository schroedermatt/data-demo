package org.msse.demo.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "initial-load")
public record InitialLoadProperties(
        long artists,
        long customers,
        long venues,
        long events,
        long tickets,
        long streams
) {}
