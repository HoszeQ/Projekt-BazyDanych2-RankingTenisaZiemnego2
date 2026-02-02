package pl.projekt.tennis_ranking.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import pl.projekt.tennis_ranking.model.Organizator;

import java.util.Optional;

public interface OrganizatorRepository extends JpaRepository<Organizator, String> {
    Optional<Organizator> findByIdKonta(String idKonta);
}
