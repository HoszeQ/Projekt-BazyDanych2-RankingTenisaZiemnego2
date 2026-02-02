package pl.projekt.tennis_ranking.seed;

import java.util.UUID;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import pl.projekt.tennis_ranking.model.Konto;
import pl.projekt.tennis_ranking.repo.KontoRepository;
import pl.projekt.tennis_ranking.security.Rola;

@Configuration
public class SeedAdmin {

    private static String id20() {
        return UUID.randomUUID().toString().replace("-", "").substring(0, 20);
    }

    @Bean
CommandLineRunner seed(KontoRepository repo, PasswordEncoder encoder) {
    return args -> {
        var konto = repo.findByLogin("admin").orElseGet(() -> {
            Konto k = new Konto();
            k.setIdKonta("ACC_A1"); // możesz zostawić stałe
            k.setLogin("admin");
            k.setRola(Rola.ADMIN);
            k.setEnabled(true);
            return k;
        });

        konto.setPasswordHash(encoder.encode("admin123"));
        repo.save(konto);
    };
}

}
