package org.msse.demo.customer;

import lombok.extern.slf4j.Slf4j;
import org.msse.demo.mockdata.customer.FullCustomer;
import org.msse.demo.mockdata.customer.address.Address;
import org.msse.demo.mockdata.customer.email.Email;
import org.msse.demo.mockdata.customer.phone.Phone;
import org.msse.demo.mockdata.customer.profile.Customer;
import org.msse.demo.mockdata.customer.profile.CustomerFaker;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import java.util.Map;
import java.util.Optional;

@Slf4j
@Repository
public class CustomerCache {

    public final static String CACHE_CUSTOMER = "customer";
    public final static String CACHE_ADDRESS = "address";
    public final static String CACHE_EMAIL = "email";
    public final static String CACHE_PHONE = "phone";

    private final CustomerFaker customerFaker;
    private final HashOperations<String, String, Object> redis;

    public CustomerCache(CustomerFaker customerFaker, RedisTemplate<String, Object> redisTemplate) {
        this.customerFaker = customerFaker;
        this.redis = redisTemplate.opsForHash();
    }

    public FullCustomer generateCustomer() {
        return getCustomer(customerFaker.randomId());
    }

    public FullCustomer getCustomer(String id) {
        Customer customer = customerFaker.generate(id);

        redis.put(CACHE_CUSTOMER, customer.id(), customer);

        return new FullCustomer(
                customer,
                getAddress(customerFaker.randomId(), customer.id()),
                getEmail(customerFaker.randomId(), customer.id()),
                getPhone(customerFaker.randomId(), customer.id())
        );
    }

    public Address getAddress(String addressId, String customerId) {
        Address address = customerFaker.getAddressFaker().generateCustomerAddress(addressId, customerId);

        redis.put(CACHE_ADDRESS, address.id(), address);

        return address;
    }

    public Email getEmail(String emailId, String customerId) {
        Email email = customerFaker.getEmailFaker().generate(emailId, customerId);

        redis.put(CACHE_EMAIL, email.id(), email);

        return email;
    }

    public Phone getPhone(String phoneId, String customerId) {
        Phone phone = customerFaker.getPhoneFaker().generate(phoneId, customerId);

        redis.put(CACHE_PHONE, phone.id(), phone);

        return phone;
    }

    public Optional<Customer> randomCustomer() {
        Map.Entry<String, Object> randomEntry = redis.randomEntry(CACHE_CUSTOMER);

        if (randomEntry != null) {
            return Optional.of ((Customer) randomEntry.getValue());
        }

        return Optional.empty();
    }

    public Optional<Address> randomAddress() {
        Map.Entry<String, Object> randomEntry = redis.randomEntry(CACHE_ADDRESS);

        if (randomEntry != null) {
            return Optional.of ((Address) randomEntry.getValue());
        }

        return Optional.empty();
    }

    /**
     * Count derived from redis cache PUTS (items added) minus DELETES (items removed)
     */
    public int customerCount() {
        return redis.size(CACHE_CUSTOMER).intValue();
    }
}
