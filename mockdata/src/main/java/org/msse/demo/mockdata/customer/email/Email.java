package org.msse.demo.mockdata.customer.email;

import java.io.Serializable;

public record Email(
        String id,
        String customerid,
        String email) implements Serializable {}
