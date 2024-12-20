package me.arzcbnh.boardcamp.exceptions;

import lombok.experimental.StandardException;

@StandardException
public class GameAlreadyExistsException extends ConflictException {
    public GameAlreadyExistsException(String name) {
        super("A game with name '" + name + "' already exists");
    }
}
