package pl.projekt.tennis_ranking.model;

import java.time.Instant;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

@Entity
@Table(name = "punkty_log",
        uniqueConstraints = @UniqueConstraint(name = "uq_log_once", columnNames = {"id_meczu","id_zawodnika","typ"}))
public class PunktyLog {

    @Id
    @Column(name = "id_logu", length = 20)
    private String idLogu;

    @Column(name = "id_turnieju", length = 20, nullable = false)
    private String idTurnieju;

    @Column(name = "id_meczu", length = 20, nullable = false)
    private String idMeczu;

    @Column(name = "id_zawodnika", length = 20, nullable = false)
    private String idZawodnika;

    @Column(name = "typ", length = 40, nullable = false)
    private String typ;

    @Column(name = "punkty", nullable = false)
    private int punkty;

    @Column(name = "created_at", nullable = false, updatable = false, insertable = false)
    private Instant createdAt;

    public String getIdLogu() { return idLogu; }
    public void setIdLogu(String idLogu) { this.idLogu = idLogu; }

    public String getIdTurnieju() { return idTurnieju; }
    public void setIdTurnieju(String idTurnieju) { this.idTurnieju = idTurnieju; }

    public String getIdMeczu() { return idMeczu; }
    public void setIdMeczu(String idMeczu) { this.idMeczu = idMeczu; }

    public String getIdZawodnika() { return idZawodnika; }
    public void setIdZawodnika(String idZawodnika) { this.idZawodnika = idZawodnika; }

    public String getTyp() { return typ; }
    public void setTyp(String typ) { this.typ = typ; }

    public int getPunkty() { return punkty; }
    public void setPunkty(int punkty) { this.punkty = punkty; }

    public Instant getCreatedAt() { return createdAt; }
}
