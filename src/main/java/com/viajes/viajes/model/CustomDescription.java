package com.viajes.viajes.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "custom_descriptions")
@Getter
@Setter
public class CustomDescription {

    @Id
    private String id; // e.g. "about-p1-1"

    @Column(columnDefinition = "TEXT", nullable = false)
    private String descripcionEs;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String descripcionEn;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String descripcionNo;

    @Column(nullable = false)
    private String seccion; // e.g. "Sobre Nosotros", "Bitácora", "Inicio", "Donaciones"

    @Column(nullable = false)
    private String label; // e.g. "¿Qué es el 'Loco David'? - Párrafo 1"
}
