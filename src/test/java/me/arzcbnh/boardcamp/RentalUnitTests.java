package me.arzcbnh.boardcamp;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;

import me.arzcbnh.boardcamp.dtos.CustomerDTO;
import me.arzcbnh.boardcamp.dtos.GameDTO;
import me.arzcbnh.boardcamp.dtos.RentalDTO;
import me.arzcbnh.boardcamp.exceptions.CustomerNotFoundException;
import me.arzcbnh.boardcamp.exceptions.GameNotFoundException;
import me.arzcbnh.boardcamp.exceptions.OutOfStockException;
import me.arzcbnh.boardcamp.exceptions.RentalAlreadyReturnedException;
import me.arzcbnh.boardcamp.exceptions.RentalNotFoundException;
import me.arzcbnh.boardcamp.exceptions.RentalNotReturnedException;
import me.arzcbnh.boardcamp.models.CustomerModel;
import me.arzcbnh.boardcamp.models.GameModel;
import me.arzcbnh.boardcamp.models.RentalModel;
import me.arzcbnh.boardcamp.repositories.CustomerRepository;
import me.arzcbnh.boardcamp.repositories.GameRepository;
import me.arzcbnh.boardcamp.repositories.RentalRepository;
import me.arzcbnh.boardcamp.services.RentalService;

@SpringBootTest
public class RentalUnitTests {
    @InjectMocks
    private RentalService rentalService;

    @Mock
    private GameRepository gameRepository;

    @Mock
    private CustomerRepository customerRepository;

    @Mock
    private RentalRepository rentalRepository;

    @Test
    void givenSomeRentals_whenGettingAllRentals_thenReturnAllRentals() {
        var rental = mockRental();
        doReturn(List.of(rental)).when(rentalRepository).findAll();

        List<RentalModel> rentals = rentalService.getAllRentals();

        assertInstanceOf(List.class, rentals);
        assertEquals(1, rentals.size());
        assertTrue(rentals.contains(rental));
        verify(rentalRepository, times(1)).findAll();
    }

    @Test
    void givenNonExistingGameId_whenPostingRental_thenThrowException() {
        doReturn(Optional.empty()).when(gameRepository).findById(any());
        assertThrows(GameNotFoundException.class, () -> rentalService.postRental(mockRentalDTO()));
        verify(gameRepository, times(1)).findById(any());
    }

    @Test
    void givenNonExistingCustomerId_whenPostingRental_thenThrowException() {
        doReturn(Optional.of(mockGame())).when(gameRepository).findById(any());
        doReturn(Optional.empty()).when(customerRepository).findById(any());
        assertThrows(CustomerNotFoundException.class, () -> rentalService.postRental(mockRentalDTO()));
        verify(customerRepository, times(1)).findById(any());
    }

    @Test
    void givenNoGameInStock_whenPostingRental_thenThrowException() {
        doReturn(Optional.of(mockGame())).when(gameRepository).findById(any());
        doReturn(Optional.of(mockCustomer())).when(customerRepository).findById(any());
        doReturn(List.of(1, 2, 3, 4)).when(rentalRepository).findAllNotReturnedByGameId(any());

        assertThrows(OutOfStockException.class, () -> rentalService.postRental(mockRentalDTO()));
        verify(gameRepository, times(1)).findById(any());
        verify(customerRepository, times(1)).findById(any());
        verify(rentalRepository, times(1)).findAllNotReturnedByGameId(any());
    }

    @Test
    void givenValidDTO_whenPostingRental_thenReturnRental() {
        doReturn(Optional.of(mockGame())).when(gameRepository).findById(any());
        doReturn(Optional.of(mockCustomer())).when(customerRepository).findById(any());
        doReturn(List.of()).when(rentalRepository).findAllNotReturnedByGameId(any());
        doReturn(mockRental()).when(rentalRepository).save(any());

        RentalModel rental = assertDoesNotThrow(() -> rentalService.postRental(mockRentalDTO()));

        assertInstanceOf(LocalDate.class, rental.getRentDate());
        assertEquals(0, rental.getDelayFee());
        assertNull(rental.getReturnDate());
        verify(rentalRepository, times(1)).save(any());
    }

    @Test
    void givenNonExistingRentalId_whenEndingRental_thenThrowException() {
        doReturn(Optional.empty()).when(rentalRepository).findById(any());
        assertThrows(RentalNotFoundException.class, () -> rentalService.endRental(1L));
        verify(rentalRepository, times(1)).findById(any());
    }

    @Test
    void givenRentalIsReturned_whenEndingRental_thenThrowException() {
        var rental = mockRental();
        rental.setReturnDate(LocalDate.now());
        doReturn(Optional.of(rental)).when(rentalRepository).findById(any());

        assertThrows(RentalAlreadyReturnedException.class, () -> rentalService.endRental(1L));
        verify(rentalRepository, times(1)).findById(any());
    }

    @Test
    void givenRentalReturnedEarly_whenEndingRental_thenReturnRental() {
        var mock = mockRental();
        doReturn(Optional.of(mock)).when(rentalRepository).findById(any());
        doReturn(mock).when(rentalRepository).save(any());

        var rental = assertDoesNotThrow(() -> rentalService.endRental(1L));

        assertNotNull(rental.getReturnDate());
        verify(rentalRepository, times(1)).findById(any());
        verify(rentalRepository, times(1)).save(any());
    }

    @Test
    void givenRentalReturnedLate_whenEndingRental_thenReturnRental() {
        var mock = mockRental();
        mock.setDaysRented(1);
        mock.setRentDate(LocalDate.now().minusDays(2));
        doReturn(Optional.of(mock)).when(rentalRepository).findById(any());
        doReturn(mock).when(rentalRepository).save(any());

        var rental = assertDoesNotThrow(() -> rentalService.endRental(1L));

        assertNotNull(rental.getReturnDate());
        assertNotEquals(0, rental.getDelayFee());
        verify(rentalRepository, times(1)).findById(any());
        verify(rentalRepository, times(1)).save(any());
    }

    @Test
    void givenNonExistingRental_whenDeletingRental_thenThrowException() {
        doReturn(Optional.empty()).when(rentalRepository).findById(any());
        assertThrows(RentalNotFoundException.class, () -> rentalService.deleteRental(1L));
        verify(rentalRepository, times(1)).findById(any());
    }

    @Test
    void givenRentalIsNotReturned_whenDeletingRental_thenThrowException() {
        doReturn(Optional.of(mockRental())).when(rentalRepository).findById(any());
        assertThrows(RentalNotReturnedException.class, () -> rentalService.deleteRental(1L));
        verify(rentalRepository, times(1)).findById(any());
    }

    @Test
    void givenReturnedRental_whenDeletingRental_thenDoNotThrowException() {
        var mock = mockRental();
        mock.setReturnDate(LocalDate.now());
        doReturn(Optional.of(mock)).when(rentalRepository).findById(any());

        assertDoesNotThrow(() -> rentalService.deleteRental(1L));
        verify(rentalRepository, times(1)).deleteById(any());
    }

    private static GameModel mockGame() {
        return new GameModel(new GameDTO("Sonic", "https://www.sega.com", 3, 1500));
    }

    private static CustomerModel mockCustomer() {
        return new CustomerModel(new CustomerDTO("Matt", "48999999999", "12345678901"));
    }

    private static RentalModel mockRental() {
        return new RentalModel(new RentalDTO(1L, 1L, 3), mockGame(), mockCustomer());
    }

    private static RentalDTO mockRentalDTO() {
        return new RentalDTO(1L, 1L, 3);
    }
}
