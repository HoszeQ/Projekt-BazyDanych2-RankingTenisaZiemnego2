package pl.projekt.tennis_ranking.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "sedzia")
public class Sedzia {
    @Id
    @Column(name = "id_sedzia", length = 20)
    private String idSedzia;

    @Column(name = "id_konta", length = 20, nullable = false, unique = true)
    private String idKonta;

    @Column(name = "imie", length = 25, nullable = false)
    private String imie;

    @Column(name = "nazwisko", length = 25, nullable = false)
    private String nazwisko;

    @Column(name = "numer_licencji", nullable = false)
    private int numerLicencji;

    // get/set...
    public String getIdSedzia() { return idSedzia; }
    public void setIdSedzia(String idSedzia) { this.idSedzia = idSedzia; }
    public String getIdKonta() { return idKonta; }
    public void setIdKonta(String idKonta) { this.idKonta = idKonta; }
    public String getImie() { return imie; }
    public void setImie(String imie) { this.imie = imie; }
    public String getNazwisko() { return nazwisko; }
    public void setNazwisko(String nazwisko) { this.nazwisko = nazwisko; }
    public int getNumerLicencji() { return numerLicencji; }
    public void setNumerLicencji(int numerLicencji) { this.numerLicencji = numerLicencji; }
}
