package pl.projekt.tennis_ranking.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import pl.projekt.tennis_ranking.model.Mecz;

import java.util.List;
import java.util.Optional;

public interface MeczRepository extends JpaRepository<Mecz, String> {

    List<Mecz> findByIdTurniejuOrderByRundaAscSlotWRundzieAsc(String idTurnieju);

    Optional<Mecz> findByIdTurniejuAndRundaAndSlotWRundzie(String idTurnieju, int runda, int slotWRundzie);

    List<Mecz> findByIdTurniejuAndRundaOrderBySlotWRundzieAsc(String idTurnieju, int runda);

    List<Mecz> findByIdSedziaOrderByIdTurniejuAscRundaAscSlotWRundzieAsc(String idSedzia);
}
