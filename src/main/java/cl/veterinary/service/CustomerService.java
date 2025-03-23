package cl.veterinary.service;

import cl.veterinary.model.Customer;

import java.util.List;
import java.util.Optional;

public interface CustomerService {

    List<Customer>findAll();
    Optional<Customer> findCustomerById(Long id);
    Customer saveCustomer(Customer customer);
    Customer updateCustomer(Customer customer);
    void deleteCustomer(Long id);
}
