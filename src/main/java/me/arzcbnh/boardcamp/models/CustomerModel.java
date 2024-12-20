package me.arzcbnh.boardcamp.models;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import me.arzcbnh.boardcamp.dtos.CustomerDTO;

@Data
@NoArgsConstructor
@Entity
@Table(name = "customers")
public class CustomerModel {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @EqualsAndHashCode.Exclude
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String phone;

    @Column(nullable = false, unique = true)
    private String cpf;

    public CustomerModel(CustomerDTO dto) {
        name = dto.getName();
        phone = dto.getPhone();
        cpf = dto.getCpf();
    }
}
