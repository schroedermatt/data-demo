package org.msse.demo.mockdata.customer.address;

public record Address(
        String id,
        String customerid,
        String formatcode,
        String type,
        String line1,
        String line2,
        String citynm,
        String state,
        String zip5,
        String zip4,
        String countrycd) {}
