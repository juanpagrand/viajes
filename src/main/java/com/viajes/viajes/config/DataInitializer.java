package com.viajes.viajes.config;

import com.viajes.viajes.model.Role;
import com.viajes.viajes.model.User;
import com.viajes.viajes.model.CustomDescription;
import com.viajes.viajes.repository.UserRepository;
import com.viajes.viajes.repository.CustomDescriptionRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final CustomDescriptionRepository customDescriptionRepository;

    public DataInitializer(UserRepository userRepository, PasswordEncoder passwordEncoder, CustomDescriptionRepository customDescriptionRepository) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.customDescriptionRepository = customDescriptionRepository;
    }

    @Override
    public void run(String... args) throws Exception {

        String adminEmail = "admin@gmail.com";
        Optional<User> adminOptional = userRepository.findByEmail(adminEmail);

        if (adminOptional.isEmpty()) {
            User admin = new User();
            admin.setNombre("David (Capitán)");
            admin.setEmail(adminEmail);
            admin.setPassword(passwordEncoder.encode("123456789"));
            admin.setRole(Role.ROLE_ADMIN);
            admin.setDescripcion(
                    "Máster Mariner & Líder de Expediciones. Más de 30 años navegando las aguas más traicioneras del planeta.");
            admin.setFoto("/images/2969933.png");

            userRepository.save(admin);
            System.out.println("Admin user created automatically.");
        }

        String userEmail = "user@gmail.com";
        Optional<User> userOptional = userRepository.findByEmail(userEmail);

        if (userOptional.isEmpty()) {
            User user = new User();
            user.setNombre("pg");
            user.setEmail(userEmail);
            user.setPassword(passwordEncoder.encode("123456789"));
            user.setRole(Role.ROLE_USER);
            user.setDescripcion(
                    "Tripulante experto en la expedición El Loco David.");
            user.setFoto("/images/2969933.png");

            userRepository.save(user);
            System.out.println("User user created automatically.");
        }

        String secondAdminEmail = "jesusadmin@gmail.com";
        Optional<User> secondAdminOptional = userRepository.findByEmail(secondAdminEmail);

        if (secondAdminOptional.isEmpty()) {
            User admin2 = new User();
            admin2.setNombre("Jesus (Admin)");
            admin2.setEmail(secondAdminEmail);
            admin2.setPassword(passwordEncoder.encode("123456789"));
            admin2.setRole(Role.ROLE_ADMIN);
            admin2.setDescripcion("Administrador y co-capitán de la plataforma de expedición El Loco David.");
            admin2.setFoto("/images/2969933.png");

            userRepository.save(admin2);
            System.out.println("Secondary admin user created automatically.");
        }

        initializeCustomDescriptions();
    }

    private void initializeCustomDescriptions() {
        if (customDescriptionRepository.count() == 0) {
            // Seccion: Sobre Nosotros
            createDesc("about-p1-1", "Sobre Nosotros", "¿Qué es el 'Loco David'? - Párrafo 1",
                "El Loco David no es una expedición.",
                "El Loco David is not just an expedition.",
                "El Loco David er ikke bare en ekspedisjon.");
            createDesc("about-p1-2", "Sobre Nosotros", "¿Qué es el 'Loco David'? - Párrafo 2",
                "Es el proceso real de transformar un velero de acero en una plataforma para futuras travesías oceánicas.",
                "It is the real process of transforming a steel sailboat into a platform for future ocean voyages.",
                "Det er den virkelige prosessen med å forvandle en stålseilbåt til en plattform for fremtidige havseilaser.");
            createDesc("about-p1-3", "Sobre Nosotros", "¿Qué es el 'Loco David'? - Párrafo 3",
                "Aquí compartimos avances, errores, reparaciones, aprendizajes y desafíos.",
                "Here we share progress, mistakes, repairs, learning, and challenges.",
                "Her deler vi fremgang, feil, reparasjoner, læring og utfordringer.");
            createDesc("about-p1-4", "Sobre Nosotros", "¿Qué es el 'Loco David'? - Párrafo 4",
                "Porque los grandes viajes comienzan mucho antes de abandonar el puerto.",
                "Because great journeys begin long before leaving port.",
                "Fordi store reiser begynner lenge før man forlater havnen.");

            createDesc("about-p2-1", "Sobre Nosotros", "Cómo empezó esta Historia - Párrafo 1",
                "Después de una vida ligada al mar entre Colombia y Noruega, decidí reconstruir un velero de acero para volver a cruzar océanos.",
                "After a life connected to the sea between Colombia and Norway, I decided to rebuild a steel sailboat to cross oceans again.",
                "Etter et liv knyttet til havet mellom Colombia og Norge, bestemte jeg meg for å gjenoppbygge en stålseilbåt for å krysse hav igjen.");
            createDesc("about-p2-2", "Sobre Nosotros", "Cómo empezó esta Historia - Párrafo 2",
                "No soy una gran organización.",
                "I am not a big organization.",
                "Jeg er ikke en stor organisasjon.");
            createDesc("about-p2-3", "Sobre Nosotros", "Cómo empezó esta Historia - Párrafo 3",
                "No tengo patrocinadores multimillonarios.",
                "I do not have multi-million dollar sponsors.",
                "Jeg har ikke sponsorer med mange millioner.");
            createDesc("about-p2-4", "Sobre Nosotros", "Cómo empezó esta Historia - Párrafo 4",
                "Solo tengo un barco, un objetivo y la determinación de seguir adelante.",
                "I only have a boat, a goal, and the determination to move forward.",
                "Jeg har bare en båt, et mål og viljen til å gå videre.");

            createDesc("about-p3-1", "Sobre Nosotros", "Una visión que nos impulsa - Párrafo 1",
                "No sabemos exactamente qué puertos visitaremos dentro de cinco años.",
                "We don't know exactly which ports we will visit in five years.",
                "Vi vet ikke nøyaktig hvilke havner vi vil besøke om fem år.");
            createDesc("about-p3-2", "Sobre Nosotros", "Una visión que nos impulsa - Párrafo 2",
                "Pero sí sabemos una cosa:",
                "But we do know one thing:",
                "Men vi vet én ting:");
            createDesc("about-p3-3", "Sobre Nosotros", "Una visión que nos impulsa - Párrafo 3",
                "Seguiremos avanzando.",
                "We will keep moving forward.",
                "Vi vil fortsette å gå fremover.");
            createDesc("about-p3-4", "Sobre Nosotros", "Una visión que nos impulsa - Párrafo 4",
                "Cada milla comienza mucho antes de levantar el ancla.",
                "Every mile begins long before raising the anchor.",
                "Hver mil begynner lenge før ankeret heves.");
            createDesc("about-p3-5", "Sobre Nosotros", "Una visión que nos impulsa - Párrafo 5",
                "El Loco David existe para demostrar que nunca es tarde para construir algo extraordinario.",
                "El Loco David exists to prove that it is never too late to build something extraordinary.",
                "El Loco David eksisterer for å bevise at det aldri er for sent å bygge noe ekstraordinært.");

            // Seccion: Bitacora
            createDesc("logbook-subtitle", "Bitácora", "Subtítulo de Bitácora",
                "Reportes y memorias directas desde el Círculo Polar Ártico.",
                "Direct reports and memories from the Arctic Circle.",
                "Direkte rapporter og minner fra polarsirkelen.");

            // Seccion: Inicio
            createDesc("hero-subtitle", "Inicio", "Subtítulo de la sección Hero",
                "Un sueño no tiene fecha de vencimiento.",
                "A dream has no expiration date.",
                "En drøm har ingen utløpsdato.");
            createDesc("story-h3", "Inicio", "Nombre del Navío",
                "EL LOCO DAVID",
                "EL LOCO DAVID",
                "EL LOCO DAVID");
            createDesc("story-specs", "Inicio", "Especificaciones del Navío",
                "Motiva 49 · Velero oceánico de acero · Construido en Dinamarca · 1996 · Bandera noruega",
                "Motiva 49 · Steel Ocean Sailboat · Built in Denmark · 1996 · Norwegian Flag",
                "Motiva 49 · Stålseilbåt for havseilas · Bygget i Danmark · 1996 · Norsk flagg");
            createDesc("story-p1", "Inicio", "Historia del navío - Párrafo 1",
                "El Loco David es un velero oceánico de acero construido en Dinamarca en 1996, concebido para navegación de larga distancia. Con sus 49 pies y más de 30 toneladas de desplazamiento, ofrece robustez, autonomía y capacidad para afrontar largas travesías oceánicas.",
                "El Loco David is a 1996 Danish-built steel ocean sailboat designed for long-distance navigation. At 49 feet and boasting over 30 tons of displacement, it offers robustness, autonomy, and the capacity to tackle long oceanic voyages.",
                "El Loco David er en stålseilbåt for havseilas bygget i Danmark i 1996, utformet for langdistansenavigasjon. Med sine 49 fot og over 30 tonns deplasement tilbyr den robusthet, autonomi og kapasitet til å håndtere lange havseilaser.");
            createDesc("cap-title", "Inicio", "Nombre del Capitán",
                "ALBERTO SIERRA",
                "ALBERTO SIERRA",
                "ALBERTO SIERRA");
            createDesc("cap-role", "Inicio", "Rol del Capitán",
                "Capitán y líder de la expedición",
                "Captain and expedition leader",
                "Kaptein og ekspedisjonsleder");
            createDesc("cap-p1", "Inicio", "Capitán - Párrafo 1",
                "Colombiano y noruego, Alberto Sierra ha construido gran parte de su vida alrededor del mar.",
                "Colombian and Norwegian, Alberto Sierra has built much of his life around the sea.",
                "Kolombiansk og norsk har Alberto Sierra bygget store deler av sitt liv rundt havet.");

            // Seccion: Donaciones
            createDesc("donar-desc", "Donaciones", "Descripción de Donaciones",
                "Cada aporte ayuda a mantener el barco, mejorar los sistemas, documentar el proceso y preparar futuras travesías.<br/><br/>No estás financiando una aventura turística.<br/><br/>Estás apoyando la construcción de un proyecto real.",
                "Every contribution helps maintain the boat, improve systems, document the process, and prepare future voyages.<br/><br/>You are not funding a tourist adventure.<br/><br/>You are helping to build a real project.",
                "Hvert bidrag hjelper til med å vedlikeholde båten, forbedre systemer, dokumentere prosessen og forberede fremtidige reiser.<br/><br/>Du finansierer ikke et turisteventyr.<br/><br/>Du hjelper til med å bygge et reelt prosjekt.");

            System.out.println("Default custom descriptions initialized.");
        }
    }

    private void createDesc(String id, String seccion, String label, String es, String en, String no) {
        CustomDescription desc = new CustomDescription();
        desc.setId(id);
        desc.setSeccion(seccion);
        desc.setLabel(label);
        desc.setDescripcionEs(es);
        desc.setDescripcionEn(en);
        desc.setDescripcionNo(no);
        customDescriptionRepository.save(desc);
    }
}
