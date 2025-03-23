package cl.veterinary.service;

import cl.veterinary.model.Customer;

import java.util.List;

public interface CustomerService {

    List<Customer>findAll();
    Customer saveCustomer(Customer customer);
}
