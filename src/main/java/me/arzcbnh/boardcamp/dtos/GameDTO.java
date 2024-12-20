package me.arzcbnh.boardcamp.dtos;

import org.hibernate.validator.constraints.URL;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
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
