package me.arzcbnh.boardcamp.controllers;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import me.arzcbnh.boardcamp.dtos.GameDTO;
import me.arzcbnh.boardcamp.exceptions.ConflictException;
import me.arzcbnh.boardcamp.models.GameModel;
import me.arzcbnh.boardcamp.services.GameService;

@RestController
@RequestMapping("/games")
public class GameController {
    private final GameService gameService;

    GameController(GameService gs) {
        gameService = gs;
    }

    @GetMapping
    public ResponseEntity<List<GameModel>> getAllGames() {
        List<GameModel> games = gameService.getAllGames();
        return ResponseEntity.status(HttpStatus.OK).body(games);
    }

    @PostMapping
    public ResponseEntity<GameModel> postGame(@RequestBody @Valid GameDTO body) throws ConflictException {
        GameModel game = gameService.postGame(body);
        return ResponseEntity.status(HttpStatus.CREATED).body(game);
    }
}
