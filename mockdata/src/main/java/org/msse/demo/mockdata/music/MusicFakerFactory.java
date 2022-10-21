package org.msse.demo.mockdata.music;

import org.msse.demo.mockdata.music.artist.ArtistFaker;
import org.msse.demo.mockdata.music.event.EventFaker;
import org.msse.demo.mockdata.music.stream.StreamFaker;
import org.msse.demo.mockdata.music.ticket.TicketFaker;
import org.msse.demo.mockdata.music.venue.VenueFaker;
import org.springframework.stereotype.Service;

@Service
// aggregate the music fakers so you only need to inject a single faker
public record MusicFakerFactory(ArtistFaker artistFaker,
                                EventFaker eventFaker,
                                StreamFaker streamFaker,
                                TicketFaker ticketFaker,
                                VenueFaker venueFaker) {}
