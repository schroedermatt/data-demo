package org.msse.demo.mockdata.domain;

public record Customer(
        String id,
        String type,
        String gender,
        String fname,
        String mname,
        String lname,
        String fullname,
        String suffix,
        String title,
        String birthDate,
        String joinDate) {}