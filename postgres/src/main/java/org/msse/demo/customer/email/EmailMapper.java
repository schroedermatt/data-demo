package org.msse.demo.customer.email;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.msse.demo.customer.profile.CustomerMapper;
import org.msse.demo.mockdata.customer.profile.Customer;
import org.msse.demo.mockdata.customer.email.Email;

@Mapper(componentModel = "spring", uses = CustomerMapper.class)
public interface EmailMapper {
    @Mapping(target = "id", source = "email.id")
    EmailEntity mapToEntity(Email email, Customer customer);
}