package me.arzcbnh.boardcamp.services;

import java.util.List;

import org.springframework.stereotype.Service;

import me.arzcbnh.boardcamp.dtos.CustomerDTO;
import me.arzcbnh.boardcamp.exceptions.CustomerAlreadyExists;
import me.arzcbnh.boardcamp.exceptions.CustomerNotFoundException;
import me.arzcbnh.boardcamp.exceptions.NotFoundException;
import me.arzcbnh.boardcamp.models.CustomerModel;
import me.arzcbnh.boardcamp.repositories.CustomerRepository;

@Service
public class CustomerService {
    private final CustomerRepository customerRepository;

    CustomerService(CustomerRepository cr) {
        customerRepository = cr;
    }

    public List<CustomerModel> getAllCustomers() {
        List<CustomerModel> customers = customerRepository.findAll();
        return customers;
    }

    public CustomerModel getCustomerById(Long id) throws NotFoundException {
        CustomerModel customer = customerRepository
            .findById(id)
            .orElseThrow(() -> new CustomerNotFoundException(id));
        
        return customer;
    }

    public CustomerModel postCustomer(CustomerDTO dto) throws CustomerAlreadyExists {
        if (customerRepository.existsByCpf(dto.getCpf())) {
            throw new CustomerAlreadyExists(dto.getCpf());
        }

        var customer = new CustomerModel(dto);
        return customerRepository.save(customer);
    }
}
