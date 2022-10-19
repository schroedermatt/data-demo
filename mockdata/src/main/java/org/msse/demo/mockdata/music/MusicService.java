package org.msse.demo.mockdata.music;

import org.msse.demo.mockdata.music.artist.Artist;
import org.msse.demo.mockdata.music.event.Event;
import org.msse.demo.mockdata.music.stream.Stream;
import org.msse.demo.mockdata.music.ticket.Ticket;

import java.util.Optional;

public interface MusicService {
    Artist createArtist();
    Artist createArtist(String artistId);
    Optional<Event> createEvent(String artistId);
    Optional<Ticket> bookTicket(String eventId, String customerId);
    Optional<Stream> streamArtist(String artistId, String customerId);
}
