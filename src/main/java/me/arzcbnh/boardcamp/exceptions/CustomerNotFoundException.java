package me.arzcbnh.boardcamp.exceptions;

import lombok.experimental.StandardException;

@StandardException
public class CustomerNotFoundException extends NotFoundException {
    public CustomerNotFoundException(Long id) {
        super("Customer with id '" + id + "' not found");
    }
}
