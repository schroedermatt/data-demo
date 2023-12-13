package org.msse.demo.music.venue;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.msse.demo.customer.address.AddressEntity;
import org.msse.demo.customer.address.AddressMapper;
import org.msse.demo.customer.profile.CustomerMapper;
import org.msse.demo.mockdata.music.venue.Venue;

@Mapper(componentModel = "spring", uses = { AddressMapper.class, CustomerMapper.class })
public interface VenueMapper {
    @Mapping(target = "id", source = "venue.id")
    VenueEntity mapToEntity(Venue venue);
}