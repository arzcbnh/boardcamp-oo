package me.arzcbnh.boardcamp.models;

import java.sql.Date;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Data;

@Data
@Entity
@Table(name = "rentals")
public class RentalModel {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(nullable = false)
    private Date rentDate;

    @Column
    private Date returnDate;

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
}
