package me.arzcbnh.boardcamp.models;

import java.time.LocalDate;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import me.arzcbnh.boardcamp.dtos.RentalDTO;

@Data
@NoArgsConstructor
@Entity
@Table(name = "rentals")
public class RentalModel {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @EqualsAndHashCode.Exclude
    private Long id;

    @Column(nullable = false)
    private LocalDate rentDate;

    @Column
    private LocalDate returnDate;

    @Column(nullable = false)
    private Integer daysRented;

    @Column(nullable = false)
    private Integer originalPrice;

    @Column(nullable = false)
    private Integer delayFee;

    @ManyToOne
    @JoinColumn(name = "gameId")
    private GameModel game;

    @ManyToOne
    @JoinColumn(name = "customerId")
    private CustomerModel customer;

    public RentalModel(RentalDTO dto, GameModel gm, CustomerModel cm) {
        game = gm;
        customer = cm;
        delayFee = 0;
        rentDate = LocalDate.now();
        daysRented = dto.getDaysRented();
        originalPrice = daysRented * gm.getPricePerDay();
    }
}
