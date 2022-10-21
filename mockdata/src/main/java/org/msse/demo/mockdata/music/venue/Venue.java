package org.msse.demo.mockdata.music.venue;

import java.io.Serializable;

public record Venue(
        String id,
        String addressid,
        String name,
        Integer maxcapacity) implements Serializable {}