package org.msse.demo.mockdata.customer.phone;

public record Phone(
        String id,
        String customerid,
        String phonetypecd,
        String primaryind,
        String timezone,
        String extnbr,
        String number) {}
