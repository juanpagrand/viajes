package com.viajes.viajes.service;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.UUID;

@Service
public class FileStorageService {

    private static final List<String> MIME_PERMITIDOS = List.of(
        "image/jpeg", "image/png", "image/webp", "image/gif"
    );

    private static final List<String> EXTENSIONES_PERMITIDAS = List.of(
        ".jpg", ".jpeg", ".png", ".webp", ".gif"
    );

    private final Path rootLocation = Paths.get("uploads");

    public FileStorageService() {
        try {
            Files.createDirectories(rootLocation);
        } catch (IOException e) {
            throw new RuntimeException("No se pudo inicializar el directorio de uploads", e);
        }
    }

    public String storeFile(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            return null;
        }

        // Validar MIME type
        String contentType = file.getContentType();
        if (!MIME_PERMITIDOS.contains(contentType)) {
            throw new RuntimeException("Tipo de archivo no permitido: " + contentType);
        }

        // Validar extensión
        String originalFilename = file.getOriginalFilename();
        String extension = "";
        if (originalFilename != null && originalFilename.contains(".")) {
            extension = originalFilename.substring(originalFilename.lastIndexOf(".")).toLowerCase();
        }
        if (!EXTENSIONES_PERMITIDAS.contains(extension)) {
            throw new RuntimeException("Extensión no permitida: " + extension);
        }

        try {
            String newFilename = UUID.randomUUID().toString() + extension;
            Path destinationFile = this.rootLocation
                .resolve(Paths.get(newFilename))
                .normalize()
                .toAbsolutePath();

            // Protección contra path traversal
            if (!destinationFile.startsWith(this.rootLocation.toAbsolutePath())) {
                throw new RuntimeException("Ruta de destino no permitida");
            }

            try (InputStream inputStream = file.getInputStream()) {
                Files.copy(inputStream, destinationFile, StandardCopyOption.REPLACE_EXISTING);
            }

            return "/uploads/" + newFilename;
        } catch (IOException e) {
            throw new RuntimeException("Error al guardar el archivo", e);
        }
    }
}