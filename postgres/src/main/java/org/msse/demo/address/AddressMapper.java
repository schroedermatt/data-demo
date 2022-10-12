package org.msse.demo.address;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.msse.demo.customer.CustomerMapper;
import org.msse.demo.mockdata.address.Address;
import org.msse.demo.mockdata.customer.Customer;

@Mapper(componentModel = "spring", uses = CustomerMapper.class)
public interface AddressMapper {
    @Mapping(source = "address.id", target = "id")
    @Mapping(source = "address.type", target = "type")
    AddressEntity mapToEntity(Address address, Customer customer);
}