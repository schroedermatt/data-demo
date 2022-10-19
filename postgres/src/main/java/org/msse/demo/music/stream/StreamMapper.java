package org.msse.demo.music.stream;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.msse.demo.customer.profile.CustomerEntity;
import org.msse.demo.customer.profile.CustomerMapper;
import org.msse.demo.mockdata.customer.profile.Customer;
import org.msse.demo.mockdata.music.artist.Artist;
import org.msse.demo.mockdata.music.stream.Stream;
import org.msse.demo.music.artist.ArtistEntity;
import org.msse.demo.music.artist.ArtistMapper;

@Mapper(componentModel = "spring", uses = {CustomerMapper.class, ArtistMapper.class})
public interface StreamMapper {
    @Mapping(target = "id", source = "stream.id")
    StreamEntity mapToEntity(Stream stream, CustomerEntity customer, ArtistEntity artist);
}