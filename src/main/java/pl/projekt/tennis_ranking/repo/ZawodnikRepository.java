package pl.projekt.tennis_ranking.repo;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import pl.projekt.tennis_ranking.model.Zawodnik;

public interface ZawodnikRepository extends JpaRepository<Zawodnik, String> {
    Optional<Zawodnik> findByIdKonta(String idKonta);

    // top8 do ATP Finals
    List<Zawodnik> findByOrderByPunktyDesc(Pageable pageable);
}
