package pl.projekt.tennis_ranking.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "mecz")
public class Mecz {

    @Id
    @Column(name = "id_meczu", length = 20)
    private String idMeczu;

    @Column(name = "id_turnieju", length = 20, nullable = false)
    private String idTurnieju;

    @Column(name = "runda", nullable = false)
    private int runda;

    @Column(name = "slot_w_rundzie", nullable = false)
    private int slotWRundzie;

    @Column(name = "id_zawodnik_a", length = 20)
    private String idZawodnikA;

    @Column(name = "id_zawodnik_b", length = 20)
    private String idZawodnikB;

    @Column(name = "seed_a")
    private Integer seedA;

    @Column(name = "seed_b")
    private Integer seedB;

    @Column(name = "id_zwyciezcy", length = 20)
    private String idZwyciezcy;

    @Column(name = "wynik", length = 50)
    private String wynik;

    @Column(name = "id_sedzia", length = 20)
    private String idSedzia;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", length = 30, nullable = false)
    private MeczStatus status;

    // get/set...
    public String getIdMeczu() { return idMeczu; }
    public void setIdMeczu(String idMeczu) { this.idMeczu = idMeczu; }

    public String getIdTurnieju() { return idTurnieju; }
    public void setIdTurnieju(String idTurnieju) { this.idTurnieju = idTurnieju; }

    public int getRunda() { return runda; }
    public void setRunda(int runda) { this.runda = runda; }

    public int getSlotWRundzie() { return slotWRundzie; }
    public void setSlotWRundzie(int slotWRundzie) { this.slotWRundzie = slotWRundzie; }

    public String getIdZawodnikA() { return idZawodnikA; }
    public void setIdZawodnikA(String idZawodnikA) { this.idZawodnikA = idZawodnikA; }

    public String getIdZawodnikB() { return idZawodnikB; }
    public void setIdZawodnikB(String idZawodnikB) { this.idZawodnikB = idZawodnikB; }

    public Integer getSeedA() { return seedA; }
    public void setSeedA(Integer seedA) { this.seedA = seedA; }

    public Integer getSeedB() { return seedB; }
    public void setSeedB(Integer seedB) { this.seedB = seedB; }

    public String getIdZwyciezcy() { return idZwyciezcy; }
    public void setIdZwyciezcy(String idZwyciezcy) { this.idZwyciezcy = idZwyciezcy; }

    public String getWynik() { return wynik; }
    public void setWynik(String wynik) { this.wynik = wynik; }

    public String getIdSedzia() { return idSedzia; }
    public void setIdSedzia(String idSedzia) { this.idSedzia = idSedzia; }

    public MeczStatus getStatus() { return status; }
    public void setStatus(MeczStatus status) { this.status = status; }
}
