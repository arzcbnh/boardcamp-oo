package me.arzcbnh.boardcamp.exceptions;

import lombok.experimental.StandardException;

@StandardException
public class CustomerAlreadyExists extends ConflictException {
    public CustomerAlreadyExists(String cpf) {
        super("A customer with cpf '" + cpf + "' already exists");
    }
}
