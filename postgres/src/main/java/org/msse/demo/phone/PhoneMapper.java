package org.msse.demo.phone;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.msse.demo.customer.CustomerMapper;
import org.msse.demo.mockdata.customer.Customer;
import org.msse.demo.mockdata.phone.Phone;

@Mapper(componentModel = "spring", uses = CustomerMapper.class)
public interface PhoneMapper {
    @Mapping(source = "phone.id", target = "id")
    PhoneEntity mapToEntity(Phone phone, Customer customer);
}