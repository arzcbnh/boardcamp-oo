package me.arzcbnh.boardcamp.exceptions;

import lombok.experimental.StandardException;

@StandardException
public class RentalNotReturnedException extends Exception {
    public RentalNotReturnedException(Long id) {
        super("Rental with id '" + id + "'has to be returned before deleting");
    }
}
