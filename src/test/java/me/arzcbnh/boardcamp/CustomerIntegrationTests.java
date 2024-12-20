package me.arzcbnh.boardcamp;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;

import me.arzcbnh.boardcamp.dtos.CustomerDTO;
import me.arzcbnh.boardcamp.models.CustomerModel;
import me.arzcbnh.boardcamp.repositories.CustomerRepository;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
public class CustomerIntegrationTests {
    @Autowired
    private TestRestTemplate template;

    @Autowired
    private CustomerRepository customerRepository;

    @AfterEach
    @BeforeEach
    public void clearDatabase() {
        customerRepository.deleteAll();
    }

    @Test
    void givenNoCustomers_whenGettingAllCustomers_thenReturnEmptyList() {
        ResponseEntity<List<CustomerModel>> response = template.exchange(
            "/customers",
            HttpMethod.GET,
            HttpEntity.EMPTY,
            new ParameterizedTypeReference<List<CustomerModel>>() {});

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(0, response.getBody().size());
    }

    @Test
    void givenSomeCustomers_whenGettingAllCustomers_thenReturnAllCustomers() {
        List<CustomerModel> customers = new ArrayList<>();
        customers.add(new CustomerModel(new CustomerDTO("John", "4811111111", "12345678900")));
        customers.add(new CustomerModel(new CustomerDTO("Mary", "4899999999", "12345678901")));

        customerRepository.saveAll(customers);

        ResponseEntity<List<CustomerModel>> response = template.exchange(
            "/customers",
            HttpMethod.GET,
            HttpEntity.EMPTY,
            new ParameterizedTypeReference<List<CustomerModel>>() {});

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(customers.size(), response.getBody().size());
        assertTrue(response.getBody().containsAll(customers));
    }

    @Test
    void givenNonExistingCustomer_whenGettingCustomerById_thenReturnNotFound() {
        ResponseEntity<String> response = template.exchange(
            "/customers/1", 
            HttpMethod.GET,
            HttpEntity.EMPTY,
            String.class);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());;
    }

    @Test
    void givenExistingCustomer_whenGettingCustomerById_thenReturnCustomer() {
        CustomerModel customer = new CustomerModel(new CustomerDTO("John", "489999999", "12345678901"));
        customer = customerRepository.save(customer);

        ResponseEntity<CustomerModel> response = template.exchange(
            "/customers/" + customer.getId(), 
            HttpMethod.GET,
            HttpEntity.EMPTY,
            CustomerModel.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());;
        assertEquals(customer, response.getBody());
    }

    @Test
    void givenInvalidFields_whenPostingCustomer_thenReturnBadRequest() {
        List<ResponseEntity<String>> responses = new ArrayList<>(6);
        CustomerDTO dto;

        // Blank name
        dto = new CustomerDTO("", "4899999999", "12345678901");
        responses.add(postInvalidDTO(dto));

        // Blank phone
        dto = new CustomerDTO("John", "", "12345678901");
        responses.add(postInvalidDTO(dto));

        // Blank CPF
        dto = new CustomerDTO("John", "4899999999", "");
        responses.add(postInvalidDTO(dto));

        // Phone with less than 10 characters
        dto = new CustomerDTO("John", "48", "12345678901");
        responses.add(postInvalidDTO(dto));

        // Phone with more than 11 characters
        dto = new CustomerDTO("John", "481234567890", "12345678901");

        // CPF without 11 characters
        dto = new CustomerDTO("John", "4899999999", "1");
        
        responses.forEach(response -> assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode()));
    }

    private ResponseEntity<String> postInvalidDTO(CustomerDTO dto) {
        return template.exchange(
            "/customers",
            HttpMethod.POST,
            new HttpEntity<CustomerDTO>(dto),
            String.class);
    }

    @Test
    void givenRepeatedCpf_whenPostingCustomer_thenReturnConflict() {
        var dto = new CustomerDTO("John", "4899999999", "12345678901");
        customerRepository.save(new CustomerModel(dto));

        ResponseEntity<String> response = template.exchange(
            "/customers", 
            HttpMethod.POST, 
            new HttpEntity<CustomerDTO>(dto),
            String.class);

        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
    }

    @Test
    void givenValidDTO_whenPostingCustomer_thenReturnCreated() {
        var dto = new CustomerDTO("John", "4899999999", "12345678901");
        var customer = new CustomerModel(dto);

        ResponseEntity<CustomerModel> response = template.exchange(
            "/customers", 
            HttpMethod.POST,
            new HttpEntity<>(dto),
            CustomerModel.class);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(customer, response.getBody());
    }
}

