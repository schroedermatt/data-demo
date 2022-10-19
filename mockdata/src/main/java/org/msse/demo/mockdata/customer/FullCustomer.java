package org.msse.demo.mockdata.customer;

import org.msse.demo.mockdata.customer.address.Address;
import org.msse.demo.mockdata.customer.email.Email;
import org.msse.demo.mockdata.customer.phone.Phone;
import org.msse.demo.mockdata.customer.profile.Customer;

public record FullCustomer(
        Customer customer,
        Address address,
        Email email,
        Phone phone
) {
}
