package me.arzcbnh.boardcamp.controllers;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import me.arzcbnh.boardcamp.dtos.RentalDTO;
import me.arzcbnh.boardcamp.exceptions.NotFoundException;
import me.arzcbnh.boardcamp.exceptions.RentalNotReturnedException;
import me.arzcbnh.boardcamp.exceptions.UnprocessableEntityException;
import me.arzcbnh.boardcamp.models.RentalModel;
import me.arzcbnh.boardcamp.services.RentalService;

@RestController
@RequestMapping("/rentals")
public class RentalController {
    private final RentalService rentalService;

    RentalController(RentalService rs) {
        rentalService = rs;
    }

    @GetMapping
    public ResponseEntity<List<RentalModel>> getAllRentals() {
        List<RentalModel> rentals = rentalService.getAllRentals();
        return ResponseEntity.status(HttpStatus.OK).body(rentals);
    }

    @PostMapping
    public ResponseEntity<RentalModel> postRental(@RequestBody @Valid RentalDTO body)
        throws NotFoundException, UnprocessableEntityException
        {
        RentalModel rental = rentalService.postRental(body);
        return ResponseEntity.status(HttpStatus.CREATED).body(rental);
    }

    @PostMapping("/{id}/return")
    public ResponseEntity<RentalModel> endRental(@PathVariable("id") Long id)
        throws NotFoundException, UnprocessableEntityException
        {
        RentalModel rental = rentalService.endRental(id);
        return ResponseEntity.status(HttpStatus.OK).body(rental);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Object> deleteRental(@PathVariable("id") Long id)
        throws NotFoundException, RentalNotReturnedException
        {
        rentalService.deleteRental(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}
