package org.msse.demo.mockdata.music.event;

public record Event(
        String id,
        String artistid,
        String venueid,
        Integer capacity,
        String eventdate) {}
