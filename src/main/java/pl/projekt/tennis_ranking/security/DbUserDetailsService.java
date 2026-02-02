package pl.projekt.tennis_ranking.security;

import org.springframework.security.authentication.DisabledException;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import pl.projekt.tennis_ranking.repo.KontoRepository;



@Service
public class DbUserDetailsService implements UserDetailsService {

    private final KontoRepository kontoRepository;

    public DbUserDetailsService(KontoRepository kontoRepository) {
        this.kontoRepository = kontoRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        var konto = kontoRepository.findByLogin(username)
                .orElseThrow(() -> new UsernameNotFoundException("Nie znaleziono loginu: " + username));

        if (!konto.isEnabled()) {
            throw new DisabledException("Konto zablokowane");
        }

        return User.withUsername(konto.getLogin())
                .password(konto.getPasswordHash())
                .roles(konto.getRola().name()) // tworzy ROLE_ADMIN, ROLE_ZAWODNIK itd.
                .build();
    }
}
