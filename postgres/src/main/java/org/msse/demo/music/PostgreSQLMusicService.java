package org.msse.demo.music;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.msse.demo.customer.address.AddressEntity;
import org.msse.demo.customer.address.AddressMapper;
import org.msse.demo.customer.address.AddressRepository;
import org.msse.demo.customer.profile.CustomerEntity;
import org.msse.demo.customer.profile.CustomerRepository;
import org.msse.demo.mockdata.customer.address.Address;
import org.msse.demo.mockdata.music.MusicFakerFactory;
import org.msse.demo.mockdata.music.MusicService;
import org.msse.demo.mockdata.music.artist.Artist;
import org.msse.demo.mockdata.music.event.Event;
import org.msse.demo.mockdata.music.stream.Stream;
import org.msse.demo.mockdata.music.ticket.Ticket;
import org.msse.demo.mockdata.music.venue.Venue;
import org.msse.demo.music.artist.ArtistEntity;
import org.msse.demo.music.artist.ArtistMapper;
import org.msse.demo.music.artist.ArtistRepository;
import org.msse.demo.music.event.EventEntity;
import org.msse.demo.music.event.EventMapper;
import org.msse.demo.music.event.EventRepository;
import org.msse.demo.music.stream.StreamMapper;
import org.msse.demo.music.stream.StreamRepository;
import org.msse.demo.music.ticket.TicketMapper;
import org.msse.demo.music.ticket.TicketRepository;
import org.msse.demo.music.venue.VenueEntity;
import org.msse.demo.music.venue.VenueMapper;
import org.msse.demo.music.venue.VenueRepository;
import org.springframework.context.annotation.Profile;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.Optional;

@SuppressWarnings("OptionalGetWithoutIsPresent")
@Slf4j
@Service
@Profile("postgres")
@RequiredArgsConstructor
public class PostgreSQLMusicService implements MusicService {
    private final AddressRepository addressRepository;
    private final CustomerRepository customerRepository;

    private final AddressMapper addressMapper;
    private final MusicFakerFactory musicFaker;
    private final ArtistMapper artistMapper;
    private final ArtistRepository artistRepository;
    private final EventMapper eventMapper;
    private final EventRepository eventRepository;
    private final StreamMapper streamMapper;
    private final StreamRepository streamRepository;
    private final TicketMapper ticketMapper;
    private final TicketRepository ticketRepository;
    private final VenueMapper venueMapper;
    private final VenueRepository venueRepository;

    @Override
    public Artist createArtist() {
        Artist artist = musicFaker.artistFaker().generate();

        log.info("Saving Artist ({}) to PostgreSQL", artist.id());

        artistRepository.save(artistMapper.mapToEntity(artist));

        return artist;
    }

    @Override
    public Artist createArtist(String artistId) {
        Artist artist = musicFaker.artistFaker().generate(artistId);

        log.info("Saving Artist ({}) to PostgreSQL", artist.id());

        artistRepository.save(artistMapper.mapToEntity(artist));

        return artist;
    }

    @Override
    public long artistCount() {
        return artistRepository.count();
    }

    @Override
    public Optional<Venue> createVenue(Venue incoming, Address address) {
        // save address
        log.info("Saving Address ({}) to PostgreSQL", address.id());
        addressRepository.save(addressMapper.mapToEntity(address, null));

        // save venue
        Venue venue = incoming;
        if (venue.id() == null) {
          venue = new Venue(
                  musicFaker.streamFaker().randomId(),
                  incoming.addressid(),
                  incoming.name(),
                  incoming.maxcapacity()
          );
        }
        log.info("Saving Venue ({}) at Address ({}) to PostgreSQL", venue.id(), address.id());

        venueRepository.save(venueMapper.mapToEntity(venue));

        return Optional.empty();
    }

    @Override
    public long venueCount() {
        return venueRepository.count();
    }

    @Override
    public Optional<Event> createEvent() {
        return createEvent(
                findRandomArtistId().get(),
                findRandomVenueId().get()
        );
    }

    @Override
    public Optional<Event> createEvent(String artistId, String venueId) {
        Optional<ArtistEntity> existingArtist = artistRepository.findById(artistId);
        Optional<VenueEntity> existingVenue = venueRepository.findById(venueId);

        if (existingVenue.isPresent()) {
            VenueEntity venue = existingVenue.get();

            if (existingArtist.isPresent()) {
                Event event = musicFaker.eventFaker().generate(artistId, venueId, venue.getMaxcapacity());

                log.info("Saving Event ({}) at Venue ({}) for Artist ({}) to PostgreSQL", event.id(), venue.getId(), artistId);

                eventRepository.save(eventMapper.mapToEntity(event, existingArtist.get(), venue));

                return Optional.of(event);
            } else {
                log.info("Artist ({}) does not exist. Event creation cancelled.", artistId);
            }
        } else {
            log.info("Venue ({}) does not exist. Event creation cancelled.", venueId);
        }

        return Optional.empty();
    }

    @Override
    public long eventCount() {
        return eventRepository.count();
    }

    @Override
    public Optional<Ticket> bookTicket() {
        return bookTicket(findRandomEventId().get(), findRandomCustomerId().get());
    }

    @Override
    public Optional<Ticket> bookTicket(String eventId, String customerId) {
        Optional<CustomerEntity> existingCustomer = customerRepository.findById(customerId);

        if (existingCustomer.isPresent()) {
            Optional<EventEntity> existingEvent = eventRepository.findById(eventId);

            if (existingEvent.isPresent()) {
                Ticket ticket = musicFaker.ticketFaker().generate(customerId, eventId);

                log.info("Saving Ticket ({}) for Customer ({}) to Event ({}) to PostgreSQL", ticket.id(), customerId, eventId);

                ticketRepository.save(ticketMapper.mapToEntity(ticket, existingCustomer.get(), existingEvent.get()));

                return Optional.of(ticket);
            } else {
                log.info("Event ({}) does not exist. Ticket creation cancelled.", eventId);
            }
        } else {
            log.info("Customer ({}) does not exist. Ticket creation cancelled.", customerId);
        }

        return Optional.empty();
    }

    @Override
    public long ticketCount() {
        return ticketRepository.count();
    }

    @Override
    public Optional<Stream> streamArtist() {
        return streamArtist(findRandomArtistId().get(), findRandomCustomerId().get());
    }

    @Override
    public Optional<Stream> streamArtist(String artistId, String customerId) {
        Optional<CustomerEntity> existingCustomer = customerRepository.findById(customerId);

        if (existingCustomer.isPresent()) {
            Optional<ArtistEntity> existingArtist = artistRepository.findById(artistId);

            if (existingArtist.isPresent()) {
                Stream stream = musicFaker.streamFaker().generate(customerId, artistId);

                log.info("Saving Stream ({}) for Artist ({}) from Customer ({}) to PostgreSQL", stream.id(), artistId, customerId);

                streamRepository.save(streamMapper.mapToEntity(stream, existingCustomer.get(), existingArtist.get()));

                return Optional.of(stream);
            } else {
                log.info("Artist ({}) does not exist. Stream creation cancelled.", artistId);
            }
        } else {
            log.info("Customer ({}) does not exist. Stream creation cancelled.", customerId);
        }

        return Optional.empty();
    }

    @Override
    public long streamCount() {
        return streamRepository.count();
    }

    // Utilities to find a random entity in PostgreSQL

    // todo - move to com.msse.demo.customer service
    public Optional<String> findRandomAddressId() {
        int randomPage = musicFaker.artistFaker().randomNumberBetween(0, (int) addressRepository.count() - 1);
        Page<AddressEntity> randomAddress = addressRepository.findAll(PageRequest.of(randomPage, 1));

        if (randomAddress.hasContent()) {
            return Optional.of(randomAddress.getContent().get(0).getId());
        }

        return Optional.empty();
    }

    // todo - move to com.msse.demo.customer service
    public Optional<String> findRandomCustomerId() {
        int randomPage = musicFaker.artistFaker().randomNumberBetween(0, (int) customerRepository.count() - 1);
        Page<CustomerEntity> randomCustomer = customerRepository.findAll(PageRequest.of(randomPage, 1));

        if (randomCustomer.hasContent()) {
            return Optional.of(randomCustomer.getContent().get(0).getId());
        }

        return Optional.empty();
    }

    private Optional<String> findRandomVenueId() {
        int randomPage = musicFaker.venueFaker().randomNumberBetween(0, (int) venueCount() - 1);
        Page<VenueEntity> randomVenue = venueRepository.findAll(PageRequest.of(randomPage, 1));

        if (randomVenue.hasContent()) {
            return Optional.of(randomVenue.getContent().get(0).getId());
        }

        return Optional.empty();
    }

    public Optional<String> findRandomEventId() {
        int randomPage = musicFaker.eventFaker().randomNumberBetween(0, (int) eventRepository.count() - 1);
        Page<EventEntity> randomEvent = eventRepository.findAll(PageRequest.of(randomPage, 1));

        if (randomEvent.hasContent()) {
            return Optional.of(randomEvent.getContent().get(0).getId());
        }

        return Optional.empty();
    }

    public Optional<String> findRandomArtistId() {
        int randomPage = musicFaker.artistFaker().randomNumberBetween(0, (int) artistRepository.count() - 1);
        Page<ArtistEntity> randomArtist = artistRepository.findAll(PageRequest.of(randomPage, 1));

        if (randomArtist.hasContent()) {
            return Optional.of(randomArtist.getContent().get(0).getId());
        }

        return Optional.empty();
    }
}
