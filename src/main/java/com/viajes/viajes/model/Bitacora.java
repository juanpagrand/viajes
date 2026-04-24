package com.viajes.viajes.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Entity
@Table(name = "bitacoras")
@Getter
@Setter
public class Bitacora {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String titulo;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String descripcion;

    @Column(nullable = false)
    private LocalDate fecha;

    @Column(nullable = false)
    private String icono; // Nombre del icono de lucide, ej: "anchor", "ship", "map"

    @Column
    private String fotoAdjunta; // Ruta de la foto cargada
}
