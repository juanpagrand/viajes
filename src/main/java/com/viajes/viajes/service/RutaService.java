package com.viajes.viajes.service;

import com.viajes.viajes.model.Ruta;
import com.viajes.viajes.repository.RutaRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Random;

@Service
public class RutaService {

    private static final Map<String, double[]> PUERTOS = new LinkedHashMap<>();
    static {
        PUERTOS.put("Tromsø, Noruega", new double[]{69.6492, 18.9553});
        PUERTOS.put("Longyearbyen, Svalbard", new double[]{78.2232, 15.6267});
        PUERTOS.put("Reykjavik, Islandia", new double[]{64.1466, -21.9426});
        PUERTOS.put("Nuuk, Groenlandia", new double[]{64.1814, -51.6941});
        PUERTOS.put("Ushuaia, Argentina", new double[]{-54.8019, -68.3030});
        PUERTOS.put("Punta Arenas, Chile", new double[]{-53.1638, -70.9171});
        PUERTOS.put("Península Antártica", new double[]{-63.3244, -57.8967});
        PUERTOS.put("Alemania", new double[]{51.165691, 10.451526});
        PUERTOS.put("Argentina", new double[]{-38.416097, -63.616672});
        PUERTOS.put("Australia", new double[]{-25.274398, 133.775136});
        PUERTOS.put("Brasil", new double[]{-14.235004, -51.92528});
        PUERTOS.put("Canadá", new double[]{56.130366, -106.346771});
        PUERTOS.put("Chile", new double[]{-35.675147, -71.542969});
        PUERTOS.put("China", new double[]{35.86166, 104.195397});
        PUERTOS.put("Colombia", new double[]{4.570868, -74.297333});
        PUERTOS.put("Costa Rica", new double[]{9.748917, -83.753428});
        PUERTOS.put("Cuba", new double[]{21.521757, -77.781167});
        PUERTOS.put("Dinamarca", new double[]{56.26392, 9.501785});
        PUERTOS.put("Ecuador", new double[]{-1.831239, -78.183406});
        PUERTOS.put("Egipto", new double[]{26.820553, 30.802498});
        PUERTOS.put("España", new double[]{40.463667, -3.74922});
        PUERTOS.put("Estados Unidos", new double[]{37.09024, -95.712891});
        PUERTOS.put("Francia", new double[]{46.227638, 2.213749});
        PUERTOS.put("Grecia", new double[]{39.074208, 21.824312});
        PUERTOS.put("India", new double[]{20.593684, 78.96288});
        PUERTOS.put("Islandia", new double[]{64.963051, -19.020835});
        PUERTOS.put("Italia", new double[]{41.87194, 12.56738});
        PUERTOS.put("Japón", new double[]{36.204824, 138.252924});
        PUERTOS.put("Marruecos", new double[]{31.791702, -7.09262});
        PUERTOS.put("México", new double[]{23.634501, -102.552784});
        PUERTOS.put("Noruega", new double[]{60.472024, 8.468946});
        PUERTOS.put("Nueva Zelanda", new double[]{-40.900557, 174.885971});
        PUERTOS.put("Países Bajos", new double[]{52.132633, 5.291266});
        PUERTOS.put("Panamá", new double[]{8.537981, -80.782127});
        PUERTOS.put("Perú", new double[]{-9.189967, -75.015152});
        PUERTOS.put("Portugal", new double[]{39.399872, -8.224454});
        PUERTOS.put("Reino Unido", new double[]{55.378051, -3.435973});
        PUERTOS.put("Rusia", new double[]{61.52401, 105.318756});
        PUERTOS.put("Sudáfrica", new double[]{-30.559482, 22.937506});
        PUERTOS.put("Suecia", new double[]{60.128161, 18.643501});
        PUERTOS.put("Suiza", new double[]{46.818188, 8.227512});
        PUERTOS.put("Turquía", new double[]{38.963745, 35.243322});
        PUERTOS.put("Uruguay", new double[]{-32.522779, -55.765835});
        PUERTOS.put("Venezuela", new double[]{6.42375, -66.58973});
    }

    private final RutaRepository rutaRepository;
    private final Random random = new Random();

    public RutaService(RutaRepository rutaRepository) {
        this.rutaRepository = rutaRepository;
    }

    public List<Ruta> findAll() {
        return rutaRepository.findAll();
    }

    public Optional<Ruta> getRutaActiva() {
        return rutaRepository.findByActivaTrue();
    }

    public List<String> getPuertosDisponibles() {
        return new ArrayList<>(PUERTOS.keySet());
    }

    public void iniciarRuta(String origen, String destino) {
        // Desactivar cualquier ruta activa actual
        Optional<Ruta> activa = rutaRepository.findByActivaTrue();
        activa.ifPresent(r -> {
            r.setActiva(false);
            rutaRepository.save(r);
        });

        Ruta nuevaRuta = new Ruta();
        nuevaRuta.setOrigen(origen);
        nuevaRuta.setDestino(destino);
        nuevaRuta.setActiva(true);
        nuevaRuta.setFechaInicio(LocalDateTime.now());
        
        double[] coordOrigen = PUERTOS.getOrDefault(origen, new double[]{68.0, 13.0});
        double[] coordDestino = PUERTOS.getOrDefault(destino, new double[]{70.0, 18.0});

        nuevaRuta.setLatitudOrigen(coordOrigen[0]);
        nuevaRuta.setLongitudOrigen(coordOrigen[1]);
        nuevaRuta.setLatitudDestino(coordDestino[0]);
        nuevaRuta.setLongitudDestino(coordDestino[1]);

        // Calcular distancia simplificada
        double dist = Math.sqrt(Math.pow(coordDestino[0] - coordOrigen[0], 2) + Math.pow(coordDestino[1] - coordOrigen[1], 2));
        double estimatedHours = Math.max(24.0, dist * 5.0); // 5 horas por grado
        nuevaRuta.setTiempoEstimadoHoras((double) Math.round(estimatedHours * 10) / 10.0);

        rutaRepository.save(nuevaRuta);
    }

    public void finalizarRutaActiva() {
        Optional<Ruta> activa = rutaRepository.findByActivaTrue();
        activa.ifPresent(r -> {
            r.setActiva(false);
            rutaRepository.save(r);
        });
    }

    public double calcularProgreso() {
        Optional<Ruta> rutaOpt = getRutaActiva();
        if (rutaOpt.isEmpty()) return 0.0;
        
        Ruta ruta = rutaOpt.get();
        if (ruta.getFechaInicio() == null || ruta.getTiempoEstimadoHoras() == null) return 0.0;

        long minutosTranscurridos = ChronoUnit.MINUTES.between(ruta.getFechaInicio(), LocalDateTime.now());
        double horasTranscurridas = minutosTranscurridos / 60.0;
        
        double progreso = (horasTranscurridas / ruta.getTiempoEstimadoHoras()) * 100.0;
        
        return Math.min(progreso, 100.0);
    }

    public double[] calcularCoordenadasActuales(double progreso) {
        Optional<Ruta> rutaOpt = getRutaActiva();
        if (rutaOpt.isEmpty()) return new double[]{0.0, 0.0};
        
        Ruta ruta = rutaOpt.get();
        double factor = progreso / 100.0;
        
        double latActual = ruta.getLatitudOrigen() + (ruta.getLatitudDestino() - ruta.getLatitudOrigen()) * factor;
        double lngActual = ruta.getLongitudOrigen() + (ruta.getLongitudDestino() - ruta.getLongitudOrigen()) * factor;
        
        // Redondear a 2 decimales
        latActual = Math.round(latActual * 100.0) / 100.0;
        lngActual = Math.round(lngActual * 100.0) / 100.0;
        
        return new double[]{latActual, lngActual};
    }
}
