package org.msse.demo.mockdata.domain;

public record Phone(
        String id,
        String customerId,
        String phoneTypeCD,
        String primaryInd,
        String timezone,
        String extNbr,
        String number) {}
