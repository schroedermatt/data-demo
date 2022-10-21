package org.msse.demo.customer;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.msse.demo.mockdata.customer.FullCustomer;
import org.msse.demo.mockdata.customer.address.Address;
import org.msse.demo.mockdata.customer.email.Email;
import org.msse.demo.mockdata.customer.phone.Phone;
import org.msse.demo.mockdata.customer.profile.Customer;
import org.msse.demo.mockdata.customer.profile.CustomerFaker;
import org.springframework.cache.Cache;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.data.redis.cache.RedisCache;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@EnableCaching
@RequiredArgsConstructor
public class CustomerCache {
    public final static String CACHE_FULL_CUSTOMER = "full_customer";
    public final static String CACHE_ADDRESS = "address";
    public final static String CACHE_EMAIL = "email";
    public final static String CACHE_PHONE = "phone";

    private final CustomerFaker customerFaker;
    private final RedisCacheManager cacheManager;

    public FullCustomer generateCustomer() {
        return getCustomer(customerFaker.randomId());
    }

    @Cacheable(value = CACHE_FULL_CUSTOMER, key = "#id")
    public FullCustomer getCustomer(String id) {
        Customer customer = customerFaker.generate(id);

        return new FullCustomer(
                customer,
                getAddress(customerFaker.randomId(), customer.id()),
                getEmail(customerFaker.randomId(), customer.id()),
                getPhone(customerFaker.randomId(), customer.id())
        );
    }

    @Cacheable(value = CACHE_ADDRESS, key = "#addressId")
    public Address getAddress(String addressId, String customerId) {
        return customerFaker.getAddressFaker().generateCustomerAddress(addressId, customerId);

    }

    @Cacheable(value = CACHE_EMAIL, key = "#emailId")
    public Email getEmail(String emailId, String customerId) {
        return customerFaker.getEmailFaker().generate(emailId, customerId);

    }

    @Cacheable(value = CACHE_PHONE, key = "#phoneId")
    public Phone getPhone(String phoneId, String customerId) {
        return customerFaker.getPhoneFaker().generate(phoneId, customerId);

    }

    /**
     * Count derived from redis cache PUTS (items added) minus DELETES (items removed)
     */
    public int customerCount() {
        Cache cache = cacheManager.getCache(CACHE_FULL_CUSTOMER);

        if (cache == null) {
            log.warn("{} cache does not exist.", CACHE_FULL_CUSTOMER);
            return 0;
        }

        RedisCache redisCache = (RedisCache)cache.getNativeCache();

        return (int)redisCache.getStatistics().getPuts() - (int)redisCache.getStatistics().getDeletes();
    }
}
