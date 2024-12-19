package me.arzcbnh.boardcamp.services;

import java.time.Duration;
import java.time.LocalDate;
import java.util.List;

import org.springframework.stereotype.Service;

import me.arzcbnh.boardcamp.dtos.RentalDTO;
import me.arzcbnh.boardcamp.exceptions.*;
import me.arzcbnh.boardcamp.models.*;
import me.arzcbnh.boardcamp.repositories.*;

@Service
public class RentalService {
    private final RentalRepository rentalRepository;
    private final GameRepository gameRepository;
    private final CustomerRepository customerRepository;

    RentalService(RentalRepository rr, GameRepository gr, CustomerRepository cr) {
        rentalRepository = rr;
        gameRepository = gr;
        customerRepository = cr;
    }

    public List<RentalModel> getAllRentals() {
        List<RentalModel> rentals = rentalRepository.findAll();
        return rentals;
    }

    public RentalModel postRental(RentalDTO dto) throws NotFoundException, UnprocessableEntityException {
        GameModel game = gameRepository
            .findById(dto.getGameId())
            .orElseThrow(() -> new GameNotFoundException(dto.getGameId()));

        CustomerModel customer = customerRepository
            .findById(dto.getCustomerId())
            .orElseThrow(() -> new CustomerNotFoundException(dto.getCustomerId()));
        
        var isInStock = game.getStockTotal() <= rentalRepository.findAllNotReturnedByGameId(game.getId()).size();
        if (isInStock) {
            throw new OutOfStockException(game.getId());
        }

        RentalModel rental = new RentalModel(dto, game, customer);
        return rentalRepository.save(rental);
    }

    public RentalModel endRental(Long id) throws NotFoundException, UnprocessableEntityException {
        RentalModel rental = rentalRepository
            .findById(id)
            .orElseThrow(() -> new RentalNotFoundException(id));
        
        var isReturned = rental.getReturnDate() != null;
        if (isReturned) {
            throw new RentalAlreadyReturnedException(id);
        }
    
        var returnDate = LocalDate.now();
        var overdueDays = Duration.between(rental.getRentDate(), returnDate).toDays() - rental.getDaysRented();
        var delayFee = (int) (overdueDays > 0 ? overdueDays * rental.getGame().getPricePerDay() : 0);

        rental.setReturnDate(returnDate);
        rental.setDelayFee(delayFee);

        return rentalRepository.save(rental);
    }

    public void deleteRental(Long id) throws NotFoundException, RentalNotReturnedException {
        RentalModel rental = rentalRepository
            .findById(id)
            .orElseThrow(() -> new RentalNotFoundException(id));
    
        if (rental.getReturnDate() == null) {
            throw new RentalNotReturnedException(id);
        }

        rentalRepository.deleteById(id);
    }
}
