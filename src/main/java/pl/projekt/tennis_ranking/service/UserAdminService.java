package pl.projekt.tennis_ranking.service;

import java.util.UUID;

import org.springframework.data.domain.Sort;


import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import pl.projekt.tennis_ranking.model.Kibic;
import pl.projekt.tennis_ranking.model.Konto;
import pl.projekt.tennis_ranking.model.Organizator;
import pl.projekt.tennis_ranking.model.Sedzia;
import pl.projekt.tennis_ranking.model.Zawodnik;
import pl.projekt.tennis_ranking.repo.KibicRepository;
import pl.projekt.tennis_ranking.repo.KontoRepository;
import pl.projekt.tennis_ranking.repo.OrganizatorRepository;
import pl.projekt.tennis_ranking.repo.SedziaRepository;
import pl.projekt.tennis_ranking.repo.ZawodnikRepository;
import pl.projekt.tennis_ranking.security.Rola;

@Service
public class UserAdminService {

    private final KontoRepository kontoRepo;
    private final ZawodnikRepository zawodnikRepo;
    private final OrganizatorRepository organizatorRepo;
    private final SedziaRepository sedziaRepo;
    private final KibicRepository kibicRepo;
    private final PasswordEncoder encoder;

    public UserAdminService(
            KontoRepository kontoRepo,
            ZawodnikRepository zawodnikRepo,
            OrganizatorRepository organizatorRepo,
            SedziaRepository sedziaRepo,
            KibicRepository kibicRepo,
            PasswordEncoder encoder
    ) {
        this.kontoRepo = kontoRepo;
        this.zawodnikRepo = zawodnikRepo;
        this.organizatorRepo = organizatorRepo;
        this.sedziaRepo = sedziaRepo;
        this.kibicRepo = kibicRepo;
        this.encoder = encoder;
    }

    private static String id20(String prefix) {
        String core = UUID.randomUUID().toString().replace("-", "").substring(0, 16);
        return (prefix + core).substring(0, 20);
    }

    @Transactional
    public void createUser(Rola rola, String login, String rawPassword,
                           String imie, String nazwisko,
                           String kraj, Integer punkty,
                           Integer numerLicencji,
                           String pseudonim) {

        if (kontoRepo.existsByLogin(login)) {
            throw new IllegalArgumentException("Login jest już zajęty");
        }

        String idKonta = id20("ACC_");

        Konto konto = new Konto();
        konto.setIdKonta(idKonta);
        konto.setLogin(login);
        konto.setPasswordHash(encoder.encode(rawPassword));
        konto.setRola(rola);
        konto.setEnabled(true);
        kontoRepo.save(konto);

        switch (rola) {
            case ZAWODNIK -> {
                Zawodnik z = new Zawodnik();
                z.setIdZawodnika(id20("ZAW_"));
                z.setIdKonta(idKonta);
                z.setImie(imie);
                z.setNazwisko(nazwisko);
                z.setKraj(kraj);
                z.setPunkty(punkty != null ? punkty : 0);
                zawodnikRepo.save(z);
            }
            case ORGANIZATOR -> {
                Organizator o = new Organizator();
                o.setIdOrganizatora(id20("ORG_"));
                o.setIdKonta(idKonta);
                o.setImie(imie);
                o.setNazwisko(nazwisko);
                organizatorRepo.save(o);
            }
            case SEDZIA -> {
                Sedzia s = new Sedzia();
                s.setIdSedzia(id20("SED_"));
                s.setIdKonta(idKonta);
                s.setImie(imie);
                s.setNazwisko(nazwisko);
                s.setNumerLicencji(numerLicencji != null ? numerLicencji : 0);
                sedziaRepo.save(s);
            }
            case KIBIC -> {
                Kibic k = new Kibic();
                k.setIdKibica(id20("KIB_"));
                k.setIdKonta(idKonta);
                k.setPseudonim(pseudonim);
                kibicRepo.save(k);
            }
            case ADMIN -> {
                // Na razie admin jest seedowany.
            }
        }
    }
    @Transactional(readOnly = true)
public java.util.List<Konto> listKonta(String query) {
    var all = kontoRepo.findAll(Sort.by("login").ascending());
    if (query == null || query.isBlank()) return all;
    String q = query.toLowerCase();
    return all.stream()
            .filter(k -> k.getLogin() != null && k.getLogin().toLowerCase().contains(q))
            .toList();
}

@Transactional
public void toggleEnabled(String idKonta) {
    var k = kontoRepo.findById(idKonta)
            .orElseThrow(() -> new IllegalArgumentException("Nie znaleziono konta"));
    k.setEnabled(!k.isEnabled());
    kontoRepo.save(k);
}

@Transactional
public void deleteKonto(String idKonta) {
    kontoRepo.deleteById(idKonta); // FK w bazie usuwa profil ról przez CASCADE
}
@Transactional
public void updateUser(String idKonta, boolean enabled, String newPassword,
                       String imie, String nazwisko, String kraj, Integer punkty,
                       Integer numerLicencji, String pseudonim) {

    var konto = kontoRepo.findById(idKonta)
            .orElseThrow(() -> new IllegalArgumentException("Nie znaleziono konta"));

    konto.setEnabled(enabled);

    if (newPassword != null && !newPassword.isBlank()) {
        if (newPassword.length() < 6) throw new IllegalArgumentException("Hasło min. 6 znaków.");
        konto.setPasswordHash(encoder.encode(newPassword));
    }

    kontoRepo.save(konto);

    switch (konto.getRola()) {
        case ZAWODNIK -> {
            var z = zawodnikRepo.findByIdKonta(idKonta)
                    .orElseThrow(() -> new IllegalArgumentException("Brak profilu zawodnika"));
            z.setImie(imie);
            z.setNazwisko(nazwisko);
            z.setKraj(kraj);
            if (punkty != null) z.setPunkty(punkty);
            zawodnikRepo.save(z);
        }
        case ORGANIZATOR -> {
            var o = organizatorRepo.findByIdKonta(idKonta)
                    .orElseThrow(() -> new IllegalArgumentException("Brak profilu organizatora"));
            o.setImie(imie);
            o.setNazwisko(nazwisko);
            organizatorRepo.save(o);
        }
        case SEDZIA -> {
            var s = sedziaRepo.findByIdKonta(idKonta)
                    .orElseThrow(() -> new IllegalArgumentException("Brak profilu sędziego"));
            s.setImie(imie);
            s.setNazwisko(nazwisko);
            if (numerLicencji != null) s.setNumerLicencji(numerLicencji);
            sedziaRepo.save(s);
        }
        case KIBIC -> {
            var k = kibicRepo.findByIdKonta(idKonta)
                    .orElseThrow(() -> new IllegalArgumentException("Brak profilu kibica"));
            k.setPseudonim(pseudonim);
            kibicRepo.save(k);
        }
        case ADMIN -> {
            // na razie pomijamy edycję profilu admina, można dodać identycznie jak inne role
        }
    }
}
}
