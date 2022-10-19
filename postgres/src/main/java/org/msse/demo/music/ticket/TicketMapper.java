package org.msse.demo.music.ticket;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.msse.demo.customer.profile.CustomerEntity;
import org.msse.demo.customer.profile.CustomerMapper;
import org.msse.demo.mockdata.music.ticket.Ticket;
import org.msse.demo.music.event.EventEntity;
import org.msse.demo.music.event.EventMapper;

@Mapper(componentModel = "spring", uses = {CustomerMapper.class, EventMapper.class})
public interface TicketMapper {
    @Mapping(target = "id", source = "ticket.id")
    @Mapping(target = "event.artist", ignore = true)
    TicketEntity mapToEntity(Ticket ticket, CustomerEntity customer, EventEntity event);
}