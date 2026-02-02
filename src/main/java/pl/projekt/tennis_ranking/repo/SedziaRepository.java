package pl.projekt.tennis_ranking.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import pl.projekt.tennis_ranking.model.Sedzia;

import java.util.Optional;

public interface SedziaRepository extends JpaRepository<Sedzia, String> {
    Optional<Sedzia> findByIdKonta(String idKonta);
}
