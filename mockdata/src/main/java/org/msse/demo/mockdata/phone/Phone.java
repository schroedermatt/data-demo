package org.msse.demo.mockdata.phone;

public record Phone(
        String id,
        String customerId,
        String phoneTypeCD,
        String primaryInd,
        String timezone,
        String extNbr,
        String number) {}
