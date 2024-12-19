package me.arzcbnh.boardcamp.exceptions;

import lombok.experimental.StandardException;

@StandardException
public class RentalAlreadyReturnedException extends UnprocessableEntityException {
    public RentalAlreadyReturnedException(Long id) {
        super("Rental with id '" + id + "' has already been returned");
    }
}
