package com.viajes.viajes.repository;

import com.viajes.viajes.model.CustomDescription;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CustomDescriptionRepository extends JpaRepository<CustomDescription, String> {
    List<CustomDescription> findBySeccion(String seccion);
}
