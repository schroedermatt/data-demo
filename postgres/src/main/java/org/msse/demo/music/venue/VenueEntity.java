package org.msse.demo.music.venue;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.msse.demo.customer.address.AddressEntity;

import javax.persistence.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "venue")
public class VenueEntity {
    @Id
    private String id;

    private String name;
    private String street;
    private String city;
    private String state;
    private String zip;
    private Double latitude;
    private Double longitude;
    private Integer maxcapacity;
}
