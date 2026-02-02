package pl.projekt.tennis_ranking.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import pl.projekt.tennis_ranking.model.Kibic;

import java.util.Optional;

public interface KibicRepository extends JpaRepository<Kibic, String> {
    Optional<Kibic> findByIdKonta(String idKonta);
}
