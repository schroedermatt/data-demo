package org.msse.demo.phone;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.msse.demo.customer.CustomerEntity;

import javax.persistence.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "phone")
public class PhoneEntity {
    @Id
    private String id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customerid")
    private CustomerEntity customer;

    private String phonetypecd;
    private String primaryind;
    private String timezone;
    private String extnbr;
    private String number;
}
