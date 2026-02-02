package pl.projekt.tennis_ranking.service;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.projekt.tennis_ranking.model.Konto;
import pl.projekt.tennis_ranking.model.Organizator;
import pl.projekt.tennis_ranking.model.Kibic;
import pl.projekt.tennis_ranking.model.Zawodnik;
import pl.projekt.tennis_ranking.repo.KontoRepository;
import pl.projekt.tennis_ranking.repo.OrganizatorRepository;
import pl.projekt.tennis_ranking.repo.KibicRepository;
import pl.projekt.tennis_ranking.repo.ZawodnikRepository;

@Service
public class UserProfileService {

    private final KontoRepository kontoRepo;
    private final ZawodnikRepository zawodnikRepo;
    private final OrganizatorRepository organizatorRepo;
    private final KibicRepository kibicRepo;

    public UserProfileService(KontoRepository kontoRepo,
                              ZawodnikRepository zawodnikRepo,
                              OrganizatorRepository organizatorRepo,
                              KibicRepository kibicRepo) {
        this.kontoRepo = kontoRepo;
        this.zawodnikRepo = zawodnikRepo;
        this.organizatorRepo = organizatorRepo;
        this.kibicRepo = kibicRepo;
    }

    public String getLogin() {
        Authentication a = SecurityContextHolder.getContext().getAuthentication();
        return a != null ? a.getName() : null;
    }

    @Transactional(readOnly = true)
    public Konto getMyKonto() {
        String login = getLogin();
        if (login == null) return null;
        return kontoRepo.findByLogin(login).orElse(null);
    }

    @Transactional(readOnly = true)
    public ProfileDto getMyProfile() {
        Konto k = getMyKonto();
        if (k == null) return null;

        String rola = k.getRola() != null ? k.getRola().name() : "-";

        // email u Ciebie jest w tabeli administrator, więc dla reszty dajemy null
        String email = null;

        return switch (k.getRola()) {
            case ZAWODNIK -> {
                Zawodnik z = zawodnikRepo.findByIdKonta(k.getIdKonta()).orElse(null);
                yield new ProfileDto(
                        rola,
                        k.getLogin(),
                        z != null ? (z.getImie() + " " + z.getNazwisko()) : k.getLogin(),
                        email,
                        z != null ? z.getKraj() : null,
                        z != null ? z.getPunkty() : null
                );
            }
            case ORGANIZATOR -> {
                Organizator o = organizatorRepo.findByIdKonta(k.getIdKonta()).orElse(null);
                yield new ProfileDto(
                        rola,
                        k.getLogin(),
                        o != null ? (o.getImie() + " " + o.getNazwisko()) : k.getLogin(),
                        email,
                        null,
                        null
                );
            }
            case KIBIC -> {
                Kibic kb = kibicRepo.findByIdKonta(k.getIdKonta()).orElse(null);
                yield new ProfileDto(
                        rola,
                        k.getLogin(),
                        kb != null ? kb.getPseudonim() : k.getLogin(),
                        email,
                        null,
                        null
                );
            }
            default -> new ProfileDto(rola, k.getLogin(), k.getLogin(), email, null, null);
        };
    }

    public record ProfileDto(String rola, String login, String displayName,
                             String email, String kraj, Integer punkty) {}
}
