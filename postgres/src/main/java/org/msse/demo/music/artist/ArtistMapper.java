package org.msse.demo.music.artist;

import org.mapstruct.Mapper;
import org.msse.demo.mockdata.music.artist.Artist;

@Mapper(componentModel = "spring")
public interface ArtistMapper {
    ArtistEntity mapToEntity(Artist artist);
}