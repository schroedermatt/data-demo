package org.msse.demo.mockdata.load;

import java.io.Serializable;

public record CityPopulation(
        String city,
        String stateAbbr,
        Integer population
        ) implements Serializable {
}
