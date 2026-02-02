package pl.projekt.tennis_ranking.repo;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import pl.projekt.tennis_ranking.model.PunktyTurniejowe;

public interface PunktyTurniejoweRepository extends JpaRepository<PunktyTurniejowe, String> {

    Optional<PunktyTurniejowe> findByIdTurniejuAndIdZawodnika(String idTurnieju, String idZawodnika);

    List<PunktyTurniejowe> findByIdZawodnikaOrderByUpdatedAtDesc(String idZawodnika);
}
