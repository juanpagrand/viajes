package com.viajes.viajes.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDateTime;

@Entity
@Table(name = "donaciones")
@Getter
@Setter
public class Donacion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String orderId;

    @Column(nullable = false)
    private double monto;

    @Column(nullable = false)
    private LocalDateTime fecha;

    @Column(nullable = false)
    private String estado; // PENDIENTE | COMPLETADA | FALLIDA

    @PrePersist
    public void prePersist() {
        this.fecha = LocalDateTime.now();
    }
}