package pl.projekt.tennis_ranking.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import pl.projekt.tennis_ranking.model.Turniej;
import pl.projekt.tennis_ranking.model.TurniejStatus;

import java.util.List;
import java.util.Optional;

public interface TurniejRepository extends JpaRepository<Turniej, String> {

    List<Turniej> findByIdOrganizatoraOrderBySezonDescNazwaAsc(String idOrganizatora);

    boolean existsBySezonAndRanga(int sezon, String ranga);

    // ✅ sezon helpers
    Optional<Turniej> findTopByOrderBySezonDesc();

    List<Turniej> findBySezonOrderByStatusAscNazwaAsc(int sezon);

    List<Turniej> findBySezonLessThanOrderBySezonDescNazwaAsc(int sezon);

    List<Turniej> findBySezonAndStatusOrderByNazwaAsc(int sezon, TurniejStatus status);

    List<Turniej> findBySezonLessThanAndStatusOrderBySezonDescNazwaAsc(int sezon, TurniejStatus status);
}
