package ca.qc.bdeb.c5gm.stageplanif;

import java.util.UUID;

public class Entreprise {
    private final String id;
    private final String nom;
    private final String adresse;
    private final String ville;
    private final String province;
    private final String codePostal;


    public Entreprise(String id, String nom, String adresse, String ville, String province, String codePostal) {
        this.id = id;
        this.nom = nom;
        this.adresse = adresse;
        this.ville = ville;
        this.province = province;
        this.codePostal = codePostal;
    }

    public Entreprise(String nom, String adresse, String ville, String province, String codePostal) {
        this.id = UUID.randomUUID().toString();
        this.nom = nom;
        this.adresse = adresse;
        this.ville = ville;
        this.province = province;
        this.codePostal = codePostal;
    }

    public String getId() {
        return id;
    }

    public String getNom() {
        return nom;
    }

    public String getAdresse() {
        return adresse;
    }

    public String getVille() {
        return ville;
    }

    public String getProvince() {
        return province;
    }

    public String getCp() {
        return codePostal;
    }
}
