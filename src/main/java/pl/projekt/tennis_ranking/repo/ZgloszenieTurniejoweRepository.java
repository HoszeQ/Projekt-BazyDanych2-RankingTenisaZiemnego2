package pl.projekt.tennis_ranking.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import pl.projekt.tennis_ranking.model.ZgloszenieTurniejowe;
import pl.projekt.tennis_ranking.model.ZgloszenieStatus;

import java.util.List;

public interface ZgloszenieTurniejoweRepository extends JpaRepository<ZgloszenieTurniejowe, String> {

    boolean existsByIdTurniejuAndIdZawodnika(String idTurnieju, String idZawodnika);

    long countByIdTurniejuAndStatus(String idTurnieju, ZgloszenieStatus status);

    List<ZgloszenieTurniejowe> findByIdTurniejuAndStatus(String idTurnieju, ZgloszenieStatus status);

    List<ZgloszenieTurniejowe> findByIdTurnieju(String idTurnieju);

    List<ZgloszenieTurniejowe> findByIdZawodnikaOrderByIdTurniejuAsc(String idZawodnika);
}
