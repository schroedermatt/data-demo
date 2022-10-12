package org.msse.demo.mockdata.customer;

import org.msse.demo.mockdata.address.Address;
import org.msse.demo.mockdata.email.Email;
import org.msse.demo.mockdata.phone.Phone;

public record FullCustomer(
        Customer customer,
        Address address,
        Email email,
        Phone phone
) {
}
