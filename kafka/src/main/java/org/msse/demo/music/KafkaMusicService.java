package org.msse.demo.music;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.msse.demo.KafkaConfig;
import org.msse.demo.mockdata.music.MusicFakerFactory;
import org.msse.demo.mockdata.music.MusicService;
import org.msse.demo.mockdata.music.artist.Artist;
import org.msse.demo.mockdata.music.event.Event;
import org.msse.demo.mockdata.music.stream.Stream;
import org.msse.demo.mockdata.music.ticket.Ticket;
import org.msse.demo.mockdata.music.venue.Venue;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Slf4j
@Service
@Profile("kafka")
@RequiredArgsConstructor
public class KafkaMusicService implements MusicService {

    private final MusicFakerFactory musicFakerFactory;
    private final KafkaConfig kafkaConfig;

    @Override
    public Artist createArtist() {
        return null;
    }

    @Override
    public Artist createArtist(String artistId) {
        return null;
    }

    @Override
    public long artistCount() {
        return 0;
    }

    @Override
    public Optional<Venue> createVenue() {
        return Optional.empty();
    }

    @Override
    public Optional<Venue> createVenue(String addressId) {
        return Optional.empty();
    }

    @Override
    public long venueCount() {
        return 0;
    }

    @Override
    public Optional<Event> createEvent() {
        return Optional.empty();
    }

    @Override
    public Optional<Event> createEvent(String artistId, String venueId) {
        return Optional.empty();
    }

    @Override
    public long eventCount() {
        return 0;
    }

    @Override
    public Optional<Ticket> bookTicket() {
        return Optional.empty();
    }

    @Override
    public Optional<Ticket> bookTicket(String eventId, String customerId) {
        return Optional.empty();
    }

    @Override
    public long ticketCount() {
        return 0;
    }

    @Override
    public Optional<Stream> streamArtist() {
        return Optional.empty();
    }

    @Override
    public Optional<Stream> streamArtist(String artistId, String customerId) {
        return Optional.empty();
    }

    @Override
    public long streamCount() {
        return 0;
    }
}
