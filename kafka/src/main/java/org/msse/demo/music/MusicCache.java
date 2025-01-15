package org.msse.demo.music;

import lombok.extern.slf4j.Slf4j;
import org.msse.demo.customer.CustomerCache;
import org.msse.demo.mockdata.customer.address.Address;
import org.msse.demo.mockdata.customer.profile.Customer;
import org.msse.demo.mockdata.music.MusicFakerFactory;
import org.msse.demo.mockdata.music.advertisement.AdSpot;
import org.msse.demo.mockdata.music.artist.Artist;
import org.msse.demo.mockdata.music.event.Event;
import org.msse.demo.mockdata.music.stream.Stream;
import org.msse.demo.mockdata.music.ticket.Ticket;
import org.msse.demo.mockdata.music.venue.Venue;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import java.util.Map;
import java.util.Optional;

@SuppressWarnings("ConstantConditions")
@Slf4j
@Repository
public class MusicCache {

    public final static String CACHE_ADDRESS = "address";
    public final static String CACHE_ARTIST = "artist";
    public final static String CACHE_EVENT = "event";
    public final static String CACHE_VENUE = "venue";
    public final static String CACHE_STREAM = "stream";
    public final static String CACHE_TICKET = "ticket";

    private final CustomerCache customerCache;
    private final MusicFakerFactory musicFaker;
    private final HashOperations<String, String, Object> redis;

    public MusicCache(CustomerCache customerCache, MusicFakerFactory musicFaker, RedisTemplate<String, Object> redisTemplate) {
        this.customerCache = customerCache;
        this.musicFaker = musicFaker;
        this.redis = redisTemplate.opsForHash();
    }

    public Artist createArtist() {
        return createArtist(musicFaker.artistFaker().randomId());
    }

    public Artist createArtist(String artistId) {
        Artist artist = musicFaker.artistFaker().generate(artistId);

        redis.put(CACHE_ARTIST, artist.id(), artist);

        return artist;
    }

    public long artistCount() {
        return redis.size(CACHE_ARTIST).intValue();
    }

    public Address createAddress(Address address) {
        redis.put(CACHE_ADDRESS, address.id(), address);
        return address;
    }

    public Optional<Venue> createVenue(Venue incoming) {
        Venue venue = incoming;

        if (venue.id() == null) {
            venue = new Venue(
                    musicFaker.streamFaker().randomId(),
                    incoming.addressid(),
                    incoming.name(),
                    incoming.maxcapacity()
            );
        }

        redis.put(CACHE_VENUE, venue.id(), venue);

        return Optional.of(venue);
    }

    public long venueCount() {
        return redis.size(CACHE_VENUE).intValue();
    }

    public Optional<Event> createEvent() {
        Optional<Artist> artist = randomArtist();

        if (artist.isPresent()) {
            Optional<Venue> randomVenue = randomVenue();

            if (randomVenue.isPresent()) {
                Venue venue = randomVenue.get();
                return createEvent(artist.get().id(), venue.id());
            }

            log.info("Venue not found. Event creation cancelled.");
        }

        log.info("Artist not found. Event creation cancelled.");

        return Optional.empty();
    }

    public Optional<Event> createEvent(String artistId, String venueId) {
        return createEvent(musicFaker.eventFaker().randomId(), artistId, venueId);
    }

    public Optional<Event> createEvent(String eventId, String artistId, String venueId) {
        Venue selectedVenue = (Venue)redis.get(CACHE_VENUE, venueId);

        Event event = musicFaker.eventFaker().generate(eventId, artistId, venueId, selectedVenue.maxcapacity());

        redis.put(CACHE_EVENT, event.id(), event);

        return Optional.of(event);
    }

    public long eventCount() {
        return redis.size(CACHE_EVENT).intValue();
    }

    public Optional<Ticket> bookTicket() {
        Optional<Customer> customer = customerCache.randomCustomer();

        if (customer.isPresent()) {
            Optional<Event> event = randomEvent();

            if (event.isPresent()) {
                return bookTicket(event.get().id(), customer.get().id());
            }

            log.info("Event not found. Ticket creation cancelled.");
        }

        log.info("Customer not found. Ticket creation cancelled.");

        return Optional.empty();
    }

    public Optional<Ticket> bookTicket(String eventId, String customerId) {
        return bookTicket(musicFaker.eventFaker().randomId(), eventId, customerId);
    }

    public Optional<Ticket> bookTicket(String ticketId, String eventId, String customerId) {
        Ticket ticket = musicFaker.ticketFaker().generate(ticketId, customerId, eventId);

        redis.put(CACHE_TICKET, ticket.id(), ticket);

        return Optional.of(ticket);
    }

    public long ticketCount() {
        return redis.size(CACHE_TICKET).intValue();
    }

    public Optional<Stream> streamArtist() {
        Optional<Customer> customer = customerCache.randomCustomer();

        if (customer.isPresent()) {
            Optional<Artist> artist = randomArtist();

            if (artist.isPresent()) {
                return streamArtist(customer.get().id(), artist.get().id());
            }

            log.info("Artist not found. Stream creation cancelled.");
        }

        log.info("Customer not found. Stream creation cancelled.");

        return Optional.empty();
    }

    public Optional<Stream> streamArtist(String customerId, String artistId) {
        return streamArtist(musicFaker.eventFaker().randomId(), customerId, artistId);
    }

    public Optional<Stream> streamArtist(String streamId, String customerId, String artistId) {
        Stream stream = musicFaker.streamFaker().generate(streamId, customerId, artistId);

        redis.put(CACHE_STREAM, stream.id(), stream);

        return Optional.of(stream);
    }

    public Optional<AdSpot> airAdvertisement() {
        //TODO "pool" if IPs
        return randomArtist().map(artist -> musicFaker.advertisementFaker().generate(artist.id(), "IP"));
    }


    public long streamCount() {
        return redis.size(CACHE_STREAM).intValue();
    }

    public Optional<Artist> randomArtist() {
        Map.Entry<String, Object> randomEntry = redis.randomEntry(CACHE_ARTIST);

        if (randomEntry != null) {
            return Optional.of ((Artist) randomEntry.getValue());
        }

        return Optional.empty();
    }

    public Optional<Venue> randomVenue() {
        Map.Entry<String, Object> randomEntry = redis.randomEntry(CACHE_VENUE);

        if (randomEntry != null) {
            return Optional.of ((Venue) randomEntry.getValue());
        }

        return Optional.empty();
    }

    public Optional<Event> randomEvent() {
        Map.Entry<String, Object> randomEntry = redis.randomEntry(CACHE_EVENT);

        if (randomEntry != null) {
            return Optional.of ((Event) randomEntry.getValue());
        }

        return Optional.empty();
    }
}
