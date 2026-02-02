package pl.projekt.tennis_ranking.repo;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import pl.projekt.tennis_ranking.model.PunktyLog;

public interface PunktyLogRepository extends JpaRepository<PunktyLog, String> {

    List<PunktyLog> findByIdTurniejuOrderByCreatedAtAsc(String idTurnieju);

    boolean existsByIdMeczuAndIdZawodnikaAndTyp(String idMeczu, String idZawodnika, String typ);

    @Query("select coalesce(sum(p.punkty),0) from PunktyLog p where p.idTurnieju = ?1 and p.idZawodnika = ?2")
    int sumPointsForPlayerInTournament(String idTurnieju, String idZawodnika);
}
