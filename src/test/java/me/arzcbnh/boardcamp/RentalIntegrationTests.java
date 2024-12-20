package me.arzcbnh.boardcamp;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;
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

import me.arzcbnh.boardcamp.dtos.*;
import me.arzcbnh.boardcamp.models.*;
import me.arzcbnh.boardcamp.repositories.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
public class RentalIntegrationTests {
    @Autowired
    private TestRestTemplate template;

    @Autowired
    private GameRepository gameRepository;

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private RentalRepository rentalRepository;

    private List<GameModel> games;
    private List<CustomerModel> customers;

    @BeforeEach
    public void setupDatabase() {
        rentalRepository.deleteAll();

        gameRepository.deleteAll();
        games = new ArrayList<>(2);
        games.add(gameRepository.save(new GameModel(new GameDTO("Sonic", "https://www.sega.com", 10, 1500))));
        games.add(gameRepository.save(new GameModel(new GameDTO("Minecraft", "https://www.minecraft.net", 1, 6000))));
        

        customerRepository.deleteAll();
        customers = new ArrayList<>(2);
        customers.add(customerRepository.save(new CustomerModel(new CustomerDTO("John", "4811111111", "12345678900"))));
        customers.add(customerRepository.save(new CustomerModel(new CustomerDTO("Mary", "4899999999", "12345678901"))));
    }

    @AfterEach
    public void clearDatabase() {
        rentalRepository.deleteAll();
        customerRepository.deleteAll();
        gameRepository.deleteAll();
    }

    @Test
    void givenNoRentals_whenGettingAllRentals_thenReturnEmptyList() {
        ResponseEntity<List<RentalModel>> response = template.exchange(
            "/rentals",
            HttpMethod.GET,
            HttpEntity.EMPTY,
            new ParameterizedTypeReference<List<RentalModel>>() {});

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(0, response.getBody().size());
    }

    @Test
    void givenSomeRentals_whenGettingAllRentals_thenReturnAllRentals() {
        List<RentalModel> rentals = new ArrayList<>();
        rentals.add(new RentalModel(new RentalDTO(games.get(0).getId(), customers.get(0).getId(), 5), games.get(0), customers.get(0)));
        rentals.add(new RentalModel(new RentalDTO(games.get(1).getId(), customers.get(1).getId(), 5), games.get(1), customers.get(1)));

        rentalRepository.saveAll(rentals);

        ResponseEntity<List<RentalModel>> response = template.exchange(
            "/rentals",
            HttpMethod.GET,
            HttpEntity.EMPTY,
            new ParameterizedTypeReference<List<RentalModel>>() {});

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(rentals.size(), response.getBody().size());
        assertTrue(response.getBody().containsAll(rentals));
    }

    @Test
    void givenInvalidFields_whenPostingRental_thenReturnBadRequest() {
        List<ResponseEntity<String>> responses = new ArrayList<>(6);
        RentalDTO dto;

        // Null gameId
        dto = new RentalDTO(null, customers.get(0).getId(), 5);
        responses.add(postInvalidDTO(dto));

        // Null customerId
        dto = new RentalDTO(games.get(0).getId(), null, 5);
        responses.add(postInvalidDTO(dto));

        // Non-positive daysRented
        dto = new RentalDTO(games.get(0).getId(), customers.get(0).getId(), 0);
        responses.add(postInvalidDTO(dto));

        responses.forEach(response -> assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode()));
    }

    private ResponseEntity<String> postInvalidDTO(RentalDTO dto) {
        return template.exchange(
            "/customers",
            HttpMethod.POST,
            new HttpEntity<>(dto),
            String.class);
    }

    @Test
    void givenNonExistingGame_whenPostingRental_thenReturnNotFound() {
        var dto = new RentalDTO(games.get(0).getId() + games.get(1).getId(), customers.get(0).getId(), 5);

        ResponseEntity<String> response = template.exchange(
            "/rentals",
            HttpMethod.POST,
            new HttpEntity<>(dto),
            String.class);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    void givenNonExistingCustomer_whenPostingRental_thenReturnNotFound() {
        var dto = new RentalDTO(games.get(0).getId(), customers.get(0).getId() + customers.get(1).getId(), 5);

        ResponseEntity<String> response = template.exchange(
            "/rentals",
            HttpMethod.POST,
            new HttpEntity<>(dto),
            String.class);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    void givenNoGameInStock_whenPostingRental_thenReturnUnprocessableEntity() {
        // Game with ID 1 has only 1 in stock
        var dto = new RentalDTO(games.get(1).getId(), customers.get(0).getId(), 5);
        rentalRepository.save(new RentalModel(dto, games.get(1), customers.get(0)));

        ResponseEntity<String> response = template.exchange(
            "/rentals", 
            HttpMethod.POST,
            new HttpEntity<>(dto),
            String.class);
        
        assertEquals(HttpStatus.UNPROCESSABLE_ENTITY, response.getStatusCode());
    }

    @Test
    void givenNonExistingRental_whenReturningRental_thenReturnNotFound() {
        ResponseEntity<String> response = template.exchange(
            "/rentals/1/return",
            HttpMethod.POST,
            HttpEntity.EMPTY,
            String.class);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    void givenRentalAlreadyReturned_whenReturningRental_thenReturnUnprocessableEntity() {
        var rental = new RentalModel(new RentalDTO(games.get(0).getId(), customers.get(0).getId(), 5), games.get(0), customers.get(0));
        rental.setReturnDate(LocalDate.now());
        rental = rentalRepository.save(rental);

        ResponseEntity<String> response = template.exchange(
            "/rentals/" + rental.getId() + "/return", 
            HttpMethod.POST,
            HttpEntity.EMPTY,
            String.class);
        
        assertEquals(HttpStatus.UNPROCESSABLE_ENTITY, response.getStatusCode());
    }

    @Test
    void givenExistingRental_whenReturningRental_thenReturnOk() {
        var rental = new RentalModel(new RentalDTO(games.get(0).getId(), customers.get(0).getId(), 5), games.get(0), customers.get(0));
        rental = rentalRepository.save(rental);

        ResponseEntity<RentalModel> response = template.exchange(
            "/rentals/" + rental.getId() + "/return", 
            HttpMethod.POST,
            HttpEntity.EMPTY,
            RentalModel.class);
        
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    void givenNonExistingRental_whenDeletingRental_thenReturnNotFound() {
        ResponseEntity<String> response = template.exchange(
            "/rentals/1", 
            HttpMethod.DELETE,
            HttpEntity.EMPTY,
            String.class);
        
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    void givenExistingRental_whenDeletingRental_thenReturnNoContent() {
        var rental = new RentalModel(new RentalDTO(games.get(0).getId(), customers.get(0).getId(), 5), games.get(0), customers.get(0));
        rental.setReturnDate(LocalDate.now());
        rental = rentalRepository.save(rental);

        ResponseEntity<Void> response = template.exchange(
            "/rentals/" + rental.getId(),
            HttpMethod.DELETE,
            HttpEntity.EMPTY,
            Void.class);
        
        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
    }
}

