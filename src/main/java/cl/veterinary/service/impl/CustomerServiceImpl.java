package cl.veterinary.service.impl;

import cl.veterinary.CustomerFunction;
import cl.veterinary.model.Customer;
import cl.veterinary.repository.CustomerRepository;
import cl.veterinary.service.CustomerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CustomerServiceImpl implements CustomerService {

    @Autowired
    private CustomerRepository customerRepository;

    @Override
    public List<Customer> findAll() {
        return customerRepository.findAll();
    }

    @Override
    public Customer saveCustomer(Customer customer) {
        return customerRepository.save(customer);
    }
}
