package org.msse.demo.api;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.msse.demo.mockdata.customer.CustomerService;
import org.msse.demo.mockdata.customer.FullCustomer;
import org.msse.demo.mockdata.music.MusicService;
import org.msse.demo.mockdata.music.artist.Artist;
import org.msse.demo.mockdata.music.event.Event;
import org.msse.demo.mockdata.music.stream.Stream;
import org.msse.demo.mockdata.music.stream.StreamRequest;
import org.msse.demo.mockdata.music.ticket.Ticket;
import org.msse.demo.mockdata.music.ticket.TicketRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

@Slf4j
@RestController
@RequiredArgsConstructor
public class MockDataApi {

  private final CustomerService customerService;
  private final MusicService musicService;

  @PostMapping(path = "customers")
  public ResponseEntity<FullCustomer> generateCustomer() {
    FullCustomer savedCustomer = customerService.createCustomer();

    return ResponseEntity.status(HttpStatus.CREATED).body(savedCustomer);
  }

  @PostMapping(path = "artists")
  public ResponseEntity<Artist> generateArtist() {
    Artist savedArtist = musicService.createArtist();

    return ResponseEntity.status(HttpStatus.CREATED).body(savedArtist);
  }

  @PostMapping(path = "artists/{artistId}/events")
  public ResponseEntity<Event> generateArtistEvent(@PathVariable String artistId) {
    return musicService.createEvent(artistId)
            .map(savedEvent -> ResponseEntity.status(HttpStatus.CREATED).body(savedEvent))
            .orElseGet(() -> ResponseEntity.badRequest().build());
  }

  @PostMapping(path = "tickets")
  public ResponseEntity<Ticket> bookTicket(@RequestBody TicketRequest ticket) {
    return musicService.bookTicket(ticket.eventid(), ticket.customerid())
            .map(savedTicket -> ResponseEntity.status(HttpStatus.CREATED).body(savedTicket))
            .orElseGet(() -> ResponseEntity.badRequest().build());
  }

  @PostMapping(path = "streams")
  public ResponseEntity<Stream> streamArtist(@RequestBody StreamRequest stream) {
    return musicService.streamArtist(stream.artistid(), stream.customerid())
            .map(savedStream -> ResponseEntity.status(HttpStatus.CREATED).body(savedStream))
            .orElseGet(() -> ResponseEntity.badRequest().build());
  }
}
