package me.arzcbnh.boardcamp.exceptions;

import lombok.experimental.StandardException;

@StandardException
public class RentalNotFoundException extends NotFoundException {
    public RentalNotFoundException(Long id) {
        super("Rental with id '" + id + "' not found");
    }
}
