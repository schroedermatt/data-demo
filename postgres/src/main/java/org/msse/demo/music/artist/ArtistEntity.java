package org.msse.demo.music.artist;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "artist")
public class ArtistEntity {
    @Id
    private String id;

    private String name;
    private String genre;
}
