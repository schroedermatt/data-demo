package org.msse.demo.mockdata.music.ticket;

public record Ticket(
        String id,
        String customerid,
        String eventid,
        Double price) {}
