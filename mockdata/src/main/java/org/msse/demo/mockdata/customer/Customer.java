package org.msse.demo.mockdata.customer;

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
        String birthdt,
        String joindt) {}