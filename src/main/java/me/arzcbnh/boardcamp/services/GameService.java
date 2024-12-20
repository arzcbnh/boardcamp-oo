package me.arzcbnh.boardcamp.services;

import java.util.List;

import org.springframework.stereotype.Service;

import me.arzcbnh.boardcamp.dtos.GameDTO;
import me.arzcbnh.boardcamp.exceptions.GameAlreadyExistsException;
import me.arzcbnh.boardcamp.models.GameModel;
import me.arzcbnh.boardcamp.repositories.GameRepository;

@Service
public class GameService {
    private final GameRepository gameRepository;

    GameService(GameRepository gr) {
        gameRepository = gr;
    }

    public List<GameModel> getAllGames() {
        List<GameModel> games = gameRepository.findAll();
        return games;
    }

    public GameModel postGame(GameDTO dto) throws GameAlreadyExistsException {
        if (gameRepository.existsByName(dto.getName())) {
            throw new GameAlreadyExistsException(dto.getName());
        }

        var game = new GameModel(dto);
        return gameRepository.save(game);
    }
}
