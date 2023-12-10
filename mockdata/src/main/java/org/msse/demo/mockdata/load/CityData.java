package org.msse.demo.mockdata.load;

import java.io.Serializable;

public record CityData(
        String state,
        String state_abbr,
        String zip,
        String county,
        String city) implements Serializable {
}
