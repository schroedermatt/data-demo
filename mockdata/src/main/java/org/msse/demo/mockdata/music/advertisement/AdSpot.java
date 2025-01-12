package org.msse.demo.mockdata.music.advertisement;

import java.io.Serializable;
import java.time.Instant;

public record AdSpot(
        String id,
        String artistId,
        String city,
        String state,
        Integer duration,
        Instant aired) implements Serializable {}
