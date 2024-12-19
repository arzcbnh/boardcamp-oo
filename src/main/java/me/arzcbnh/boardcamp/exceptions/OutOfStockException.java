package me.arzcbnh.boardcamp.exceptions;

import lombok.experimental.StandardException;

@StandardException
public class OutOfStockException extends UnprocessableEntityException {
    public OutOfStockException(Long id) {
        super("Game with id '" + id + "' is out of stock");
    }
}
