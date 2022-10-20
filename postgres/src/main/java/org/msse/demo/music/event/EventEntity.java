package org.msse.demo.music.event;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.msse.demo.music.artist.ArtistEntity;

import javax.persistence.*;

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

    private String venue;
    private String capacity;
    private String eventdate;
    private String eventtime;
}
