package org.msse.demo.mockdata.customer.profile;

import java.io.Serializable;

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
        String joindt) implements Serializable {}