package me.arzcbnh.boardcamp;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;

import me.arzcbnh.boardcamp.dtos.CustomerDTO;
import me.arzcbnh.boardcamp.exceptions.CustomerAlreadyExists;
import me.arzcbnh.boardcamp.exceptions.CustomerNotFoundException;
import me.arzcbnh.boardcamp.models.CustomerModel;
import me.arzcbnh.boardcamp.repositories.CustomerRepository;
import me.arzcbnh.boardcamp.services.CustomerService;

@SpringBootTest
public class CustomerUnitTests {
    @InjectMocks
    private CustomerService customerService;

    @Mock
    private CustomerRepository customerRepository;

    @Test
    void givenSomeCustomers_whenGettingAllCustomers_thenReturnAllCustomers() {
        var customer = new CustomerModel(mockCustomerDTO());
        doReturn(List.of(customer)).when(customerRepository).findAll();

        List<CustomerModel> customers = customerService.getAllCustomers();

        assertInstanceOf(List.class, customers);
        assertEquals(1, customers.size());
        assertTrue(customers.contains(customer));
        verify(customerRepository, times(1)).findAll();
    }

    @Test
    void givenNonExistingCustomer_whenGettingCustomerById_thenThrowException() {
        assertThrows(CustomerNotFoundException.class, () -> customerService.getCustomerById(1l));
        verify(customerRepository, times(1)).findById(any());
    }

    @Test
    void givenExistingCustomer_whenGettingCustomerById_thenReturnCustomer() {
        var expected = new CustomerModel(mockCustomerDTO());
        doReturn(Optional.of(expected)).when(customerRepository).findById(any());

        var obtained = assertDoesNotThrow(() -> customerService.getCustomerById(1L));

        assertEquals(expected, obtained);
        verify(customerRepository, times(1)).findById(any());
    }

    @Test
    void givenRepeatedCpf_whenPostingCustomer_thenThrowException() {
        doReturn(true).when(customerRepository).existsByCpf(any());
        assertThrows(CustomerAlreadyExists.class, () -> customerService.postCustomer(mockCustomerDTO()));
        verify(customerRepository, times(0)).save(any());
    }

    @Test
    void givenValidDTO_whenPostingCustomer_thenReturnCustomer() {
        var dto = mockCustomerDTO();
        var expected = new CustomerModel(dto);
        doReturn(expected).when(customerRepository).save(any());

        CustomerModel returned = assertDoesNotThrow(() -> customerService.postCustomer(dto));

        assertEquals(expected, returned);
        verify(customerRepository, times(1)).save(any());
    }

    static private CustomerDTO mockCustomerDTO() {
        return new CustomerDTO("John", "4899999999", "12345678901");
    }
}
