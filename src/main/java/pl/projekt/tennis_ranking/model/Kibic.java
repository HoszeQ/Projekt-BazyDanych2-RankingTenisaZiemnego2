package pl.projekt.tennis_ranking.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "kibic")
public class Kibic {
    @Id
    @Column(name = "id_kibica", length = 20)
    private String idKibica;

    @Column(name = "id_konta", length = 20, nullable = false, unique = true)
    private String idKonta;

    @Column(name = "pseudonim", length = 20, nullable = false, unique = true)
    private String pseudonim;

    // get/set...
    public String getIdKibica() { return idKibica; }
    public void setIdKibica(String idKibica) { this.idKibica = idKibica; }
    public String getIdKonta() { return idKonta; }
    public void setIdKonta(String idKonta) { this.idKonta = idKonta; }
    public String getPseudonim() { return pseudonim; }
    public void setPseudonim(String pseudonim) { this.pseudonim = pseudonim; }
}
