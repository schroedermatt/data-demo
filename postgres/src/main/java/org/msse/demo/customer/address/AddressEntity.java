package org.msse.demo.customer.address;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.msse.demo.customer.profile.CustomerEntity;

import javax.persistence.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "address")
public class AddressEntity {
    @Id
    private String id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customerid")
    private CustomerEntity customer;

    private String formatcode;
    private String type;
    private String line1;
    private String line2;
    private String citynm;
    private String state;
    private String zip5;
    private String zip4;
    private String countrycd;
}
