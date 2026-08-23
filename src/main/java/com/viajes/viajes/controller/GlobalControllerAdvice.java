package com.viajes.viajes.controller;

import com.viajes.viajes.service.CustomDescriptionService;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Map;

@ControllerAdvice
public class GlobalControllerAdvice {

    private final CustomDescriptionService customDescriptionService;
    private final ObjectMapper objectMapper;

    public GlobalControllerAdvice(CustomDescriptionService customDescriptionService, ObjectMapper objectMapper) {
        this.customDescriptionService = customDescriptionService;
        this.objectMapper = objectMapper;
    }

    @ModelAttribute("customTranslationsJson")
    public String getCustomTranslationsJson() {
        try {
            Map<String, Map<String, String>> translations = customDescriptionService.getAllTranslationsMap();
            return objectMapper.writeValueAsString(translations);
        } catch (Exception e) {
            return "{}";
        }
    }
}
