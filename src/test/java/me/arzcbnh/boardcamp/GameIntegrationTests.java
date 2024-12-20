package me.arzcbnh.boardcamp;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;

import me.arzcbnh.boardcamp.dtos.GameDTO;
import me.arzcbnh.boardcamp.models.GameModel;
import me.arzcbnh.boardcamp.repositories.GameRepository;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
public class GameIntegrationTests {
    @Autowired
    private TestRestTemplate template;

    @Autowired
    private GameRepository gameRepository;

    @AfterEach
    @BeforeEach
    public void clearDatabase() {
        gameRepository.deleteAll();
    }

    @Test
    void givenNoGames_whenGettingAllGames_thenReturnEmptyList() {
        ResponseEntity<List<GameModel>> response = template.exchange(
            "/games",
            HttpMethod.GET,
            HttpEntity.EMPTY,
            new ParameterizedTypeReference<List<GameModel>>() {});

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(0, response.getBody().size());
    }

    @Test
    void givenSomeGames_whenGettingAllGames_thenReturnAllGames() {
        List<GameModel> games = new ArrayList<>();
        games.add(new GameModel(new GameDTO("Sonic", "https://www.sega.com", 3, 1500)));
        games.add(new GameModel(new GameDTO("Minecraft", "https://www.minecraft.net", 10, 6000)));

        gameRepository.saveAll(games);

        ResponseEntity<List<GameModel>> response = template.exchange(
            "/games",
            HttpMethod.GET,
            HttpEntity.EMPTY,
            new ParameterizedTypeReference<List<GameModel>>() {});

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(games.size(), response.getBody().size());
        assertTrue(response.getBody().containsAll(games));
    }

    @Test
    void givenInvalidFields_whenPostingGame_thenReturnBadRequest() {
        List<ResponseEntity<String>> responses = new ArrayList<>(5);
        GameDTO dto;

        // Blank name
        dto = new GameDTO("", "https://google.com", 3, 1500);
        responses.add(postInvalidDTO(dto));

        // Blank image
        dto = new GameDTO("Google Doodle", "", 3, 1500);
        responses.add(postInvalidDTO(dto));

        // Non-URL image
        dto = new GameDTO("Google Doodle", "Not-A-Url", 3, 1500);
        responses.add(postInvalidDTO(dto));

        // Non-positive stockTotal
        dto = new GameDTO("Google Doodle", "https://google.com", 0, 1500);
        responses.add(postInvalidDTO(dto));

        // Non-positive pricePerDay
        dto = new GameDTO("Google Doodle", "https://google.com", 3, 0);
        responses.add(postInvalidDTO(dto));
        
        responses.forEach(response -> assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode()));
    }

    private ResponseEntity<String> postInvalidDTO(GameDTO dto) {
        return template.exchange(
            "/games",
            HttpMethod.POST,
            new HttpEntity<>(dto),
            String.class);
    }

    @Test
    void givenRepeatedName_whenPostingGame_thenReturnConflict() {
        var dto = new GameDTO("Coup", "https://images.google.com", 2, 1000);
        gameRepository.save(new GameModel(dto));

        ResponseEntity<String> response = template.exchange(
            "/games", 
            HttpMethod.POST, 
            new HttpEntity<>(dto),
            String.class);

        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
    }

    @Test
    void givenValidDTO_whenPostingGame_thenReturnCreated() {
        var dto = new GameDTO("Coup", "https://images.google.com", 2, 1000);
        var game = new GameModel(dto);

        ResponseEntity<GameModel> response = template.exchange(
            "/games", 
            HttpMethod.POST,
            new HttpEntity<>(dto),
            GameModel.class);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(game, response.getBody());
    }
}
