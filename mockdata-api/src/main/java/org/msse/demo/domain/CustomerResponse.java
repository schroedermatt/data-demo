package org.msse.demo.domain;

import org.msse.demo.mockdata.domain.Address;
import org.msse.demo.mockdata.domain.Customer;
import org.msse.demo.mockdata.domain.Email;
import org.msse.demo.mockdata.domain.Phone;

public record CustomerResponse(
        Customer customer,
        Address address,
        Email email,
        Phone phone
) {
}
