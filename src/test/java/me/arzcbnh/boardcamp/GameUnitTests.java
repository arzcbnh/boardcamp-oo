package me.arzcbnh.boardcamp;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;

import me.arzcbnh.boardcamp.dtos.GameDTO;
import me.arzcbnh.boardcamp.exceptions.GameAlreadyExistsException;
import me.arzcbnh.boardcamp.models.GameModel;
import me.arzcbnh.boardcamp.repositories.GameRepository;
import me.arzcbnh.boardcamp.services.GameService;

@SpringBootTest
public class GameUnitTests {
    @InjectMocks
    private GameService gameService;
    
    @Mock
    private GameRepository gameRepository;

    @Test
    void givenSomeGames_whenGettingAllGames_thenReturnAllGames() {
        var game = new GameModel(mockGameDTO());
        doReturn(List.of(game)).when(gameRepository).findAll();

        List<GameModel> games = gameService.getAllGames();

        assertInstanceOf(List.class, games);
        assertEquals(1, games.size());
        assertTrue(games.contains(game));
        verify(gameRepository, times(1)).findAll();
    }

    @Test
    void givenRepeatedName_whenPostingGame_thenThrowException() {
        doReturn(true).when(gameRepository).existsByName(any());
        assertThrows(GameAlreadyExistsException.class, () -> gameService.postGame(mockGameDTO()));
        verify(gameRepository, times(0)).save(any());
    }

    @Test
    void givenValidDTO_whenPostingGame_thenReturnGame() {
        var dto = mockGameDTO();
        var expected = new GameModel(dto);
        doReturn(expected).when(gameRepository).save(any());

        GameModel returned = assertDoesNotThrow(() -> gameService.postGame(dto));

        assertEquals(expected, returned);
        verify(gameRepository, times(1)).save(any());
    }

    private static GameDTO mockGameDTO() {
        return new GameDTO("Mario", "https://www.nintendo.com", 4, 5000);
    }
}
