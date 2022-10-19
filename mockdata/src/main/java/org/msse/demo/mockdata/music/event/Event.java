package org.msse.demo.mockdata.music.event;

public record Event(
        String id,
        String artistid,
        String venue,
        String capacity,
        String eventdate,
        String eventtime) {}
