package pl.projekt.tennis_ranking.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import pl.projekt.tennis_ranking.security.Rola;

@Entity
@Table(name = "konto")
public class Konto {

    @Id
    @Column(name = "id_konta", length = 20)
    private String idKonta;

    @Column(name = "login", length = 30, nullable = false, unique = true)
    private String login;

    @Column(name = "password_hash", length = 255, nullable = false)
    private String passwordHash;

    @Enumerated(EnumType.STRING)
    @Column(name = "rola", nullable = false)
    private Rola rola;

    @Column(name = "enabled", nullable = false)
    private boolean enabled = true;

    public String getIdKonta() { return idKonta; }
    public void setIdKonta(String idKonta) { this.idKonta = idKonta; }

    public String getLogin() { return login; }
    public void setLogin(String login) { this.login = login; }

    public String getPasswordHash() { return passwordHash; }
    public void setPasswordHash(String passwordHash) { this.passwordHash = passwordHash; }

    public Rola getRola() { return rola; }
    public void setRola(Rola rola) { this.rola = rola; }

    public boolean isEnabled() { return enabled; }
    public void setEnabled(boolean enabled) { this.enabled = enabled; }
}
