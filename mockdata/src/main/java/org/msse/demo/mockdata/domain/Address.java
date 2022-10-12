package org.msse.demo.mockdata.domain;

public record Address(
        String id,
        String customerId,
        String formatCode,
        String type,
        String line1,
        String line2,
        String citynm,
        String state,
        String zip5,
        String zip4,
        String countryCd) {}
