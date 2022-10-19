package org.msse.demo.customer.address;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.msse.demo.customer.profile.CustomerMapper;
import org.msse.demo.mockdata.customer.address.Address;
import org.msse.demo.mockdata.customer.profile.Customer;

@Mapper(componentModel = "spring", uses = CustomerMapper.class)
public interface AddressMapper {
    @Mapping(target = "id", source = "address.id")
    @Mapping(target = "type", source = "address.type")
    AddressEntity mapToEntity(Address address, Customer customer);
}