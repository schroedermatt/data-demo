package org.msse.demo.music.artist;

import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ArtistRepository extends PagingAndSortingRepository<ArtistEntity, String>, CrudRepository<ArtistEntity, String> {}
