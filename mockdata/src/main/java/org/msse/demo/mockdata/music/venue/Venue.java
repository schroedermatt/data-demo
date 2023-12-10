package org.msse.demo.mockdata.music.venue;

import java.io.Serializable;

public record Venue(
        String id,
        String name,
        String street,
        String city,
        String state,
        String zip,
        Double latitude,
        Double longitude,
        Integer maxcapacity
) implements Serializable {}