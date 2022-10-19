package org.msse.demo.customer.phone;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.msse.demo.customer.profile.CustomerMapper;
import org.msse.demo.mockdata.customer.profile.Customer;
import org.msse.demo.mockdata.customer.phone.Phone;

@Mapper(componentModel = "spring", uses = CustomerMapper.class)
public interface PhoneMapper {
    @Mapping(target = "id", source = "phone.id")
    PhoneEntity mapToEntity(Phone phone, Customer customer);
}