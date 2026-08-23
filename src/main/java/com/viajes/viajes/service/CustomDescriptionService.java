package com.viajes.viajes.service;

import com.viajes.viajes.model.CustomDescription;
import com.viajes.viajes.repository.CustomDescriptionRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class CustomDescriptionService {

    private final CustomDescriptionRepository repository;

    public CustomDescriptionService(CustomDescriptionRepository repository) {
        this.repository = repository;
    }

    public List<CustomDescription> findAll() {
        return repository.findAll();
    }

    public List<CustomDescription> findBySeccion(String seccion) {
        return repository.findBySeccion(seccion);
    }

    public Optional<CustomDescription> findById(String id) {
        return repository.findById(id);
    }

    @Transactional
    public CustomDescription save(CustomDescription customDescription) {
        return repository.save(customDescription);
    }

    public Map<String, Map<String, String>> getAllTranslationsMap() {
        List<CustomDescription> list = repository.findAll();
        Map<String, Map<String, String>> map = new HashMap<>();
        map.put("es", new HashMap<>());
        map.put("en", new HashMap<>());
        map.put("no", new HashMap<>());

        for (CustomDescription cd : list) {
            map.get("es").put(cd.getId(), cd.getDescripcionEs());
            map.get("en").put(cd.getId(), cd.getDescripcionEn());
            map.get("no").put(cd.getId(), cd.getDescripcionNo());
        }
        return map;
    }
}
