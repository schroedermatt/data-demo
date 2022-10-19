package org.msse.demo.customer.profile;

import org.mapstruct.Mapper;
import org.msse.demo.mockdata.customer.profile.Customer;

@Mapper(componentModel = "spring")
public interface CustomerMapper {
    CustomerEntity mapToEntity(Customer customer);
}