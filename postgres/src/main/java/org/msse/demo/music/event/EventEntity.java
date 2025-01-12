package org.msse.demo.music.event;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.msse.demo.music.artist.ArtistEntity;
import org.msse.demo.music.venue.VenueEntity;

import jakarta.persistence.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "event")
public class EventEntity {
    @Id
    private String id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "artistid")
    private ArtistEntity artist;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "venueid")
    private VenueEntity venue;

    private Integer capacity;
    private String eventdate;
}
