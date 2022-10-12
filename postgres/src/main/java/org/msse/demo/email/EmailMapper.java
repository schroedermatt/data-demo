package org.msse.demo.email;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.msse.demo.customer.CustomerMapper;
import org.msse.demo.mockdata.customer.Customer;
import org.msse.demo.mockdata.email.Email;

@Mapper(componentModel = "spring", uses = CustomerMapper.class)
public interface EmailMapper {
    @Mapping(source = "email.id", target = "id")
    EmailEntity mapToEntity(Email email, Customer customer);
}