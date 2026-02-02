package pl.projekt.tennis_ranking.service;

import org.springframework.stereotype.Service;

import pl.projekt.tennis_ranking.security.Rola;

@Service
public class RegistrationService {

    private final UserAdminService userAdminService;

    public RegistrationService(UserAdminService userAdminService) {
        this.userAdminService = userAdminService;
    }

    public void registerPlayer(String login, String password, String imie, String nazwisko, String kraj) {
        userAdminService.createUser(
                Rola.ZAWODNIK, login, password,
                imie, nazwisko,
                kraj, 0,
                null,
                null
        );
    }

    public void registerFan(String login, String password, String pseudonim) {
        userAdminService.createUser(
                Rola.KIBIC, login, password,
                null, null,
                null, null,
                null,
                pseudonim
        );
    }
}
