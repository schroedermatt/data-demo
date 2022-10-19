package org.msse.demo.music;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.msse.demo.customer.profile.CustomerEntity;
import org.msse.demo.customer.profile.CustomerRepository;
import org.msse.demo.mockdata.music.MusicFakerFactory;
import org.msse.demo.mockdata.music.MusicService;
import org.msse.demo.mockdata.music.artist.Artist;
import org.msse.demo.mockdata.music.event.Event;
import org.msse.demo.mockdata.music.stream.Stream;
import org.msse.demo.mockdata.music.ticket.Ticket;
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
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Slf4j
@Service
@Profile("postgres")
@RequiredArgsConstructor
public class PostgreSQLMusicService implements MusicService {
    private final MusicFakerFactory musicFaker;
    private final ArtistMapper artistMapper;
    private final ArtistRepository artistRepository;
    private final EventMapper eventMapper;
    private final EventRepository eventRepository;
    private final StreamMapper streamMapper;
    private final StreamRepository streamRepository;
    private final TicketMapper ticketMapper;
    private final TicketRepository ticketRepository;
    private final CustomerRepository customerRepository;

    @Override
    public Artist createArtist() {
        Artist artist = musicFaker.artistFaker().generate();

        log.info("Saving Artist to PostgreSQL - ID={}", artist.id());

        artistRepository.save(artistMapper.mapToEntity(artist));

        return artist;
    }

    @Override
    public Artist createArtist(String artistId) {
        Artist artist = musicFaker.artistFaker().generate(artistId);

        log.info("Saving Artist to PostgreSQL - ID={}", artist.id());

        artistRepository.save(artistMapper.mapToEntity(artist));

        return artist;
    }

    @Override
    public Optional<Event> createEvent(String artistId) {
        Optional<ArtistEntity> existingArtist = artistRepository.findById(artistId);

        if (existingArtist.isPresent()) {
            Event event = musicFaker.eventFaker().generate(artistId);

            log.info("Saving Event ({}) for Artist ({}) to PostgreSQL", event.id(), artistId);

            eventRepository.save(eventMapper.mapToEntity(event, existingArtist.get()));

            return Optional.of(event);
        } else {
            log.info("Artist ({}) does not exist. Event creation cancelled.", artistId);
        }

        return Optional.empty();
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
}
