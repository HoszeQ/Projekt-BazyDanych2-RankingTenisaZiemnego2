package pl.projekt.tennis_ranking.repo;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import pl.projekt.tennis_ranking.model.Konto;

public interface KontoRepository extends JpaRepository<Konto, String> {
    Optional<Konto> findByLogin(String login);
    boolean existsByLogin(String login);
}
