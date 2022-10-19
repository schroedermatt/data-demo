package org.msse.demo.music.event;

import org.mapstruct.InheritInverseConfiguration;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.msse.demo.customer.profile.CustomerMapper;
import org.msse.demo.mockdata.music.artist.Artist;
import org.msse.demo.mockdata.music.event.Event;
import org.msse.demo.music.artist.ArtistEntity;
import org.msse.demo.music.artist.ArtistMapper;

@Mapper(componentModel = "spring", uses = ArtistMapper.class)
public interface EventMapper {
    @Mapping(target = "id", source = "event.id")
    EventEntity mapToEntity(Event event, ArtistEntity artist);
}