package me.arzcbnh.boardcamp.models;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;
import me.arzcbnh.boardcamp.dtos.GameDTO;

@Data
@Entity
@Table(name = "games")
public class GameModel {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(nullable = false, unique = true)
    private String name;

    @Column(nullable = false)
    private String image;

    @Column(nullable = false)
    private Integer stockTotal;

    @Column(nullable = false)
    private Integer pricePerDay;

    public GameModel(GameDTO dto) {
        name = dto.getName();
        image = dto.getImage();
        stockTotal = dto.getStockTotal();
        pricePerDay = dto.getPricePerDay();
    }
}
