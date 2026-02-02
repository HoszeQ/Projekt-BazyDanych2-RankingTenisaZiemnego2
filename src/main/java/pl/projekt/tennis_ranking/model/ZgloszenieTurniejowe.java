package pl.projekt.tennis_ranking.model;

import jakarta.persistence.*;

@Entity
@Table(name = "zgloszenie_turniejowe")
public class ZgloszenieTurniejowe {

    @Id
    @Column(name = "id_zgloszenia", length = 20)
    private String idZgloszenia;

    @Column(name = "id_turnieju", length = 20, nullable = false)
    private String idTurnieju;

    @Column(name = "id_zawodnika", length = 20, nullable = false)
    private String idZawodnika;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", length = 30, nullable = false)
    private ZgloszenieStatus status;

    public String getIdZgloszenia() { return idZgloszenia; }
    public void setIdZgloszenia(String idZgloszenia) { this.idZgloszenia = idZgloszenia; }

    public String getIdTurnieju() { return idTurnieju; }
    public void setIdTurnieju(String idTurnieju) { this.idTurnieju = idTurnieju; }

    public String getIdZawodnika() { return idZawodnika; }
    public void setIdZawodnika(String idZawodnika) { this.idZawodnika = idZawodnika; }

    public ZgloszenieStatus getStatus() { return status; }
    public void setStatus(ZgloszenieStatus status) { this.status = status; }
}
