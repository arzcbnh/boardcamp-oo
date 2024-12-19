package me.arzcbnh.boardcamp.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import me.arzcbnh.boardcamp.models.RentalModel;

@Repository
public interface RentalRepository extends JpaRepository<RentalModel, Long> {
    @Query(
        nativeQuery = true,
        value = "SELECT * FROM rentals WHERE returnDate IS NOT NULL;"
    )
    List<RentalModel> findAllNotReturnedByGameId(Long gameId);
}
