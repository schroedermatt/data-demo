package org.msse.demo.customer;

import org.mapstruct.Mapper;
import org.msse.demo.mockdata.customer.Customer;

@Mapper(componentModel = "spring")
public interface CustomerMapper {
    CustomerEntity mapToEntity(Customer customer);
}