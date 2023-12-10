package org.msse.demo.mockdata.music;

import org.msse.demo.mockdata.music.artist.Artist;
import org.msse.demo.mockdata.music.event.Event;
import org.msse.demo.mockdata.music.stream.Stream;
import org.msse.demo.mockdata.music.ticket.Ticket;
import org.msse.demo.mockdata.music.venue.Venue;

import java.util.Optional;

public interface MusicService {
    Artist createArtist();
    Artist createArtist(String artistId);
    long artistCount();
    Optional<Venue> createVenue(Venue venueData);
    long venueCount();
    Optional<Event> createEvent();
    Optional<Event> createEvent(String artistId, String venueId);
    long eventCount();
    Optional<Ticket> bookTicket();
    Optional<Ticket> bookTicket(String eventId, String customerId);
    long ticketCount();
    Optional<Stream> streamArtist();
    Optional<Stream> streamArtist(String artistId, String customerId);
    long streamCount();
}
