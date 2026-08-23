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
            createDesc("story-p1", "Inicio", "Historia del navío - Párrafo 1",
                "El corazón del proyecto es un Motiva 49 construido en Dinamarca en 1996. No fue elegido por lujo. Fue elegido por resistencia. Actualmente se encuentra en un proceso continuo de modernización y preparación para futuras travesías oceánicas. Cada sistema instalado y cada mejora completada representan un paso más hacia el próximo capítulo de esta historia. El barco no es el destino. Es la herramienta que hace posible el viaje.",
                "The heart of the project is a Motiva 49 built in Denmark in 1996. It was not chosen for luxury; it was chosen for endurance. It is currently undergoing a continuous process of modernization and preparation for future ocean voyages. Each installed system and completed improvement represents a step closer to the next chapter of this story. The ship is not the destination; it is the tool that makes the journey possible.",
                "Hjertet av prosjektet er en Motiva 49 bygget i Danmark i 1996. Den ble ikke valgt for luksus; den ble valgt for slitestyrke. Den er for tiden i en kontinuerlig prosess med modernisering og forberedelse til fremtidige havseilaser. Hvert installerte system og hver fullførte forbedring representerer et skritt nærmere neste kapittel i denne historien. Skipet er ikke målet; det er verktøyet som gjør reisen mulig.");
            createDesc("story-p2", "Inicio", "Historia del navío - Párrafo 2",
                "Su historia comenzó hace más de dos décadas cruzando el recóndito paso del noroeste. Hoy en día, ha sido completamente remodelado para combinar el romanticismo de la navegación clásica con las comodidades modernas necesarias para exploraciones científicas a largo plazo.",
                "Its history began over two decades ago crossing the remote Northwest Passage. Today, it has been completely remodeled to combine the romance of classic navigation with the modern amenities necessary for long-term scientific explorations.",
                "Dets historie begynte for over to tiår siden ved å krysse den avsidesliggende Nordvestpassasjen. I dag er fullstendig pusset opp for å kobine romantikken ved klassisk navigasjon med moderne fasiliteter som er nødvendige for langsiktige vitenskapelige utforskninger.");

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
