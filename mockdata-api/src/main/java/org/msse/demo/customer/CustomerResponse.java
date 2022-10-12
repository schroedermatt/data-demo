package org.msse.demo.customer;

import org.msse.demo.mockdata.address.Address;
import org.msse.demo.mockdata.customer.Customer;
import org.msse.demo.mockdata.email.Email;
import org.msse.demo.mockdata.phone.Phone;

public record CustomerResponse(
        Customer customer,
        Address address,
        Email email,
        Phone phone
) {
}
