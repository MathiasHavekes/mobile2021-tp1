package ca.qc.bdeb.c5gm.stageplanif;

import android.graphics.Bitmap;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.time.LocalTime;

import ca.qc.bdeb.c5gm.stageplanif.Utils;
import ca.qc.bdeb.c5gm.stageplanif.data.Compte;
import ca.qc.bdeb.c5gm.stageplanif.data.Entreprise;
import ca.qc.bdeb.c5gm.stageplanif.data.Priorite;
import ca.qc.bdeb.c5gm.stageplanif.data.Stage;

/**
 * ViewModel permettant de faire communiquer les fragments et l'activitee
 */
public class InfoStageViewModel extends ViewModel {
    /**
     * Donnee live contenant la priorite du stage
     */
    private final MutableLiveData<Priorite> priorite = new MutableLiveData<>();
    /**
     * Donnee live contenant la photo de l'eleve
     */
    private final MutableLiveData<byte[]> photo = new MutableLiveData<>();
    /**
     * Donnee live contenant le compte de l'eleve
     */
    private final MutableLiveData<Compte> compte = new MutableLiveData<>();
    /**
     * Donnee live contenant l'entreprise du stage
     */
    private final MutableLiveData<Entreprise> entreprise = new MutableLiveData<>();
    /**
     * Donnee live contenant l'heure de debut du stage
     */
    private final MutableLiveData<LocalTime> heureDebutStage = new MutableLiveData<>();
    /**
     * Donnee live contenant l'heure de fin du stage
     */
    private final MutableLiveData<LocalTime> heureFinStage = new MutableLiveData<>();
    /**
     * Donnee live contenant l'heure de debut du diner
     */
    private final MutableLiveData<LocalTime> heureDebutDiner = new MutableLiveData<>();
    /**
     * Donnee live contenant l'heure de fin du diner
     */
    private final MutableLiveData<LocalTime> heureFinDiner = new MutableLiveData<>();
    /**
     * Donnee live contenant le temps d'une visite
     */
    private final MutableLiveData<Integer> tempsVisites = new MutableLiveData<>();
    /**
     * jours du stage
     */
    private final MutableLiveData<Byte> jourStage = new MutableLiveData<>();
    /**
     * Stock les disponibilites du tuteur
     */
    private final MutableLiveData<Integer> dispoTuteur = new MutableLiveData<>();
    private final MutableLiveData<String> commentaire = new MutableLiveData<>();
    /**
     * Propriete contenant le stage si c'est une modification
     */
    private Stage stage;
    /**
     * Valeur de lundi
     */
    public final byte LUNDI = 0x01;
    /**
     * Valeur de mardi
     */
    public final byte MARDI = 0x02;
    /**
     * Valeur de mercredi
     */
    public final byte MERCREDI = 0x04;
    /**
     * Valeur de jeudi
     */
    public final byte JEUDI = 0x08;
    /**
     * Valeur de vendredi
     */
    public final byte VENDREDI = 0x10;


    public void setImage(Bitmap photo) {
        if (photo != null) {
            this.photo.setValue(Utils.getBytes(photo));
        }
    }

    public LiveData<Priorite> getPriorite() {
        return priorite;
    }

    public void setPriorite(Priorite selection) {
        priorite.setValue(selection);
    }

    public LiveData<byte[]> getPhoto() {
        return photo;
    }

    public LiveData<Compte> getCompte() {
        return compte;
    }

    public void setCompte(Compte compte) {
        this.compte.setValue(compte);
    }

    public LiveData<Entreprise> getEntreprise() {
        return entreprise;
    }

    public void setEntreprise(Entreprise entreprise) {
        this.entreprise.setValue(entreprise);
    }

    public LocalTime getHeureDebutStage() {
        return this.heureDebutStage.getValue();
    }

    public void setHeureDebutStage(LocalTime temps) {
        this.heureDebutStage.setValue(temps);
    }

    public LocalTime getHeureFinStage() {
        return this.heureFinStage.getValue();
    }

    public void setHeureFinStage(LocalTime temps) {
        this.heureFinStage.setValue(temps);
    }

    public LocalTime getHeureDebutDiner() {
        return this.heureDebutDiner.getValue();
    }

    public void setHeureDebutDiner(LocalTime temps) {
        this.heureDebutDiner.setValue(temps);
    }

    public LocalTime getHeureFinDiner() {
        return this.heureFinDiner.getValue();
    }

    public void setHeureFinDiner(LocalTime temps) {
        this.heureFinDiner.setValue(temps);
    }

    public Integer getTempsVisites() {
        return this.tempsVisites.getValue();
    }

    public void setTempsVisites(int temps) {
        this.tempsVisites.setValue(temps);
    }

    public Stage getStage() {
        return stage;
    }

    public void setStage(Stage stage) {
        this.stage = stage;
    }

    public Byte getJourStage() {
        return this.jourStage.getValue();
    }

    public void setJourStage(Byte jours) {
        this.jourStage.setValue(jours);
    }

    public Integer getDispoTuteur() {
        return dispoTuteur.getValue();
    }

    public void setDispoTuteur(Integer jours) {
        dispoTuteur.setValue(jours);
    }

    public String getCommentaire() {
        return commentaire.getValue();
    }

    public void setCommentaire(String commentaire) {
        this.commentaire.setValue(commentaire);
    }
}
