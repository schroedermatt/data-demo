package org.msse.demo.music.artist;

import org.mapstruct.InheritInverseConfiguration;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.msse.demo.customer.profile.CustomerMapper;
import org.msse.demo.mockdata.music.artist.Artist;

@Mapper(componentModel = "spring")
public interface ArtistMapper {
    @InheritInverseConfiguration
    ArtistEntity mapToEntity(Artist artist);
}