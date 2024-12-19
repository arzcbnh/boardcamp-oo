package me.arzcbnh.boardcamp.exceptions;

import lombok.experimental.StandardException;

@StandardException
public class GameNotFoundException extends NotFoundException {
    public GameNotFoundException(Long id) {
        super("Game with id '" + id + "' not found");
    }
}
