package org.msse.demo.music.venue;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "venue")
public class VenueEntity {
    @Id
    private String id;
    private String addressid;
    private String name;
    private Integer maxcapacity;
}
