package pl.projekt.tennis_ranking.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "turniej")
public class Turniej {

    @Id
    @Column(name = "id_turnieju", length = 20)
    private String idTurnieju;

    @Column(name = "ranga", nullable = false)
    private String ranga;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 30)
    private TurniejStatus status;

    @Column(name = "id_organizatora", length = 20, nullable = false)
    private String idOrganizatora;

    @Column(name = "nazwa", length = 60, nullable = false)
    private String nazwa;

    @Column(name = "sezon", nullable = false)
    private int sezon;

    @Column(name = "max_zawodnikow", nullable = false)
    private int maxZawodnikow = 32;

    @Column(name = "drabinka_turnieju", columnDefinition = "LONGTEXT")
    private String drabinkaTurnieju;

    public String getIdTurnieju() { return idTurnieju; }
    public void setIdTurnieju(String idTurnieju) { this.idTurnieju = idTurnieju; }

    public String getRanga() { return ranga; }
    public void setRanga(String ranga) { this.ranga = ranga; }

    public TurniejStatus getStatus() { return status; }
    public void setStatus(TurniejStatus status) { this.status = status; }

    public String getIdOrganizatora() { return idOrganizatora; }
    public void setIdOrganizatora(String idOrganizatora) { this.idOrganizatora = idOrganizatora; }

    public String getNazwa() { return nazwa; }
    public void setNazwa(String nazwa) { this.nazwa = nazwa; }

    public int getSezon() { return sezon; }
    public void setSezon(int sezon) { this.sezon = sezon; }

    public int getMaxZawodnikow() { return maxZawodnikow; }
    public void setMaxZawodnikow(int maxZawodnikow) { this.maxZawodnikow = maxZawodnikow; }

    public String getDrabinkaTurnieju() { return drabinkaTurnieju; }
    public void setDrabinkaTurnieju(String drabinkaTurnieju) { this.drabinkaTurnieju = drabinkaTurnieju; }
}
