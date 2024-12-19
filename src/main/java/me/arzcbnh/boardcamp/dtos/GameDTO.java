package me.arzcbnh.boardcamp.dtos;

import org.hibernate.validator.constraints.URL;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

@Data
public class GameDTO {
    @NotBlank
    private String name;

    @URL
    @NotBlank
    private String image;

    @NotNull
    @Positive
    private Integer stockTotal;

    @NotNull
    @Positive
    private Integer pricePerDay;
}
