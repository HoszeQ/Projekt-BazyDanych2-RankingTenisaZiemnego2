package pl.projekt.tennis_ranking.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "zawodnik")
public class Zawodnik {
    @Id
    @Column(name = "id_zawodnika", length = 20)
    private String idZawodnika;

    @Column(name = "id_konta", length = 20, nullable = false, unique = true)
    private String idKonta;

    @Column(name = "imie", length = 25, nullable = false)
    private String imie;

    @Column(name = "nazwisko", length = 25, nullable = false)
    private String nazwisko;

    @Column(name = "punkty", nullable = false)
    private int punkty;

    @Column(name = "kraj", length = 10, nullable = false)
    private String kraj;

    // get/set...
    public String getIdZawodnika() { return idZawodnika; }
    public void setIdZawodnika(String idZawodnika) { this.idZawodnika = idZawodnika; }
    public String getIdKonta() { return idKonta; }
    public void setIdKonta(String idKonta) { this.idKonta = idKonta; }
    public String getImie() { return imie; }
    public void setImie(String imie) { this.imie = imie; }
    public String getNazwisko() { return nazwisko; }
    public void setNazwisko(String nazwisko) { this.nazwisko = nazwisko; }
    public int getPunkty() { return punkty; }
    public void setPunkty(int punkty) { this.punkty = punkty; }
    public String getKraj() { return kraj; }
    public void setKraj(String kraj) { this.kraj = kraj; }
}
