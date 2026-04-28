package com.viajes.viajes.repository;

import com.viajes.viajes.model.Donacion;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface DonacionRepository extends JpaRepository<Donacion, Long> {
    Optional<Donacion> findByOrderId(String orderId);
}