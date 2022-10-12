package org.msse.demo.mockdata.customer;

public interface CustomerService {
    FullCustomer createCustomer();
    FullCustomer createCustomer(String customerId);
}
