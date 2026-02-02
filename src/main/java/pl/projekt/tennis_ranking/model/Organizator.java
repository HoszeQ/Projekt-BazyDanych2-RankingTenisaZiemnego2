package pl.projekt.tennis_ranking.model;

import jakarta.persistence.*;

@Entity
@Table(name = "organizator")
public class Organizator {
    @Id
    @Column(name = "id_organizatora", length = 20)
    private String idOrganizatora;

    @Column(name = "id_konta", length = 20, nullable = false, unique = true)
    private String idKonta;

    @Column(name = "imie", length = 25, nullable = false)
    private String imie;

    @Column(name = "nazwisko", length = 25, nullable = false)
    private String nazwisko;

    // get/set...
    public String getIdOrganizatora() { return idOrganizatora; }
    public void setIdOrganizatora(String idOrganizatora) { this.idOrganizatora = idOrganizatora; }
    public String getIdKonta() { return idKonta; }
    public void setIdKonta(String idKonta) { this.idKonta = idKonta; }
    public String getImie() { return imie; }
    public void setImie(String imie) { this.imie = imie; }
    public String getNazwisko() { return nazwisko; }
    public void setNazwisko(String nazwisko) { this.nazwisko = nazwisko; }
}
