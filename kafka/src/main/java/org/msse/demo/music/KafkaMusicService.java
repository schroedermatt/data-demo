package org.msse.demo.music;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.msse.demo.KafkaConfig;
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

    private final MusicCache musicCache;
    private final KafkaConfig.Topics topics;
    private final Producer<String, Object> kafkaProducer;

    @Override
    @SneakyThrows
    public Artist createArtist() {
        Artist artist = musicCache.createArtist();

        log.info("Producing Artist ({}) to Kafka", artist.id());
        send(topics.artists(), artist.id(), artist);

        return artist;
    }

    @Override
    public Artist createArtist(String artistId) {
        Artist artist = musicCache.createArtist(artistId);

        log.info("Producing Artist ({}) to Kafka", artist.id());
        send(topics.artists(), artist.id(), artist);

        return artist;
    }

    @Override
    public long artistCount() {
        return musicCache.artistCount();
    }

    @Override
    public Optional<Venue> createVenue(Venue incoming) {

        Optional<Venue> venue = musicCache.createVenue(incoming);

        venue.ifPresent(value -> {
            log.info("Producing Venue ({}) to Kafka", value.id());
            send(topics.venues(), value.id(), value);
        });

        return venue;
    }


    @Override
    public long venueCount() {
        return musicCache.venueCount();
    }

    @Override
    public Optional<Event> createEvent() {
        Optional<Event> event = musicCache.createEvent();

        event.ifPresent(value -> {
            log.info("Producing Event ({}) at Venue ({}) for Artist ({}) to Kafka", value.id(), value.venueid(), value.artistid());
            send(topics.events(), value.id(), value);
        });

        return event;
    }

    @Override
    public Optional<Event> createEvent(String artistId, String venueId) {
        Optional<Event> event = musicCache.createEvent(artistId, venueId);

        event.ifPresent(value -> {
            log.info("Producing Event ({}) at Venue ({}) for Artist ({}) to Kafka", value.id(), value.venueid(), value.artistid());
            send(topics.events(), value.id(), value);
        });

        return event;
    }

    @Override
    public long eventCount() {
        return musicCache.eventCount();
    }

    @Override
    public Optional<Ticket> bookTicket() {
        Optional<Ticket> ticket = musicCache.bookTicket();

        ticket.ifPresent(value -> {
            log.info("Producing Ticket ({}) for Customer ({}) to Event ({}) to Kafka", value.id(), value.customerid(), value.eventid());
            send(topics.tickets(), value.id(), value);
        });

        return ticket;
    }

    @Override
    public Optional<Ticket> bookTicket(String eventId, String customerId) {
        Optional<Ticket> ticket = musicCache.bookTicket(eventId, customerId);

        ticket.ifPresent(value -> {
            log.info("Producing Ticket ({}) for Customer ({}) to Event ({}) to Kafka", value.id(), value.customerid(), value.eventid());
            send(topics.tickets(), value.id(), value);
        });

        return ticket;
    }

    @Override
    public long ticketCount() {
        return musicCache.ticketCount();
    }

    @Override
    public Optional<Stream> streamArtist() {
        Optional<Stream> stream = musicCache.streamArtist();

        stream.ifPresent(value -> {
            log.info("Producing Stream ({}) for Artist ({}) from Customer ({}) to Kafka", value.id(), value.artistid(), value.customerid());
            send(topics.streams(), value.id(), value);
        });

        return stream;
    }

    @Override
    public Optional<Stream> streamArtist(String artistId, String customerId) {
        Optional<Stream> stream = musicCache.streamArtist(customerId, artistId);

        stream.ifPresent(value -> {
            log.info("Producing Stream ({}) for Artist ({}) from Customer ({}) to Kafka", value.id(), value.artistid(), value.customerid());
            send(topics.streams(), value.id(), value);
        });

        return stream;
    }

    @Override
    public long streamCount() {
        return musicCache.streamCount();
    }

    @SneakyThrows
    private void send(String topic, String key, Object value) {
        kafkaProducer.send(new ProducerRecord<>(topic, key, value)).get();
    }
}
