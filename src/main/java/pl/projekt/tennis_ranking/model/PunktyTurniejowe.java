package pl.projekt.tennis_ranking.model;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;

@Entity
@Table(name = "punkty_turniejowe")
public class PunktyTurniejowe {

    @Id
    @Column(name = "id", length = 20)
    private String id;

    @Column(name = "id_turnieju", length = 20, nullable = false)
    private String idTurnieju;

    @Column(name = "id_zawodnika", length = 20, nullable = false)
    private String idZawodnika;

    @Column(name = "punkty", nullable = false)
    private int punkty;

    // opis etapu / za co punkty (np. "Udział (R1)", "Awans -> R2", "Mistrz")
    @Column(name = "opis", length = 120)
    private String opis;

    // do sortowania historii na profilu zawodnika
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    public void prePersist() {
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    public void preUpdate() {
        updatedAt = LocalDateTime.now();
    }

    // ===== get/set =====
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getIdTurnieju() { return idTurnieju; }
    public void setIdTurnieju(String idTurnieju) { this.idTurnieju = idTurnieju; }

    public String getIdZawodnika() { return idZawodnika; }
    public void setIdZawodnika(String idZawodnika) { this.idZawodnika = idZawodnika; }

    public int getPunkty() { return punkty; }
    public void setPunkty(int punkty) { this.punkty = punkty; }

    public String getOpis() { return opis; }
    public void setOpis(String opis) { this.opis = opis; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}
