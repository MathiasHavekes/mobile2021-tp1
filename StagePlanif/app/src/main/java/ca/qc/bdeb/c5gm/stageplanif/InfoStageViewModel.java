package ca.qc.bdeb.c5gm.stageplanif;

import android.graphics.Bitmap;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

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
     * Propriete contenant le stage si c'est une modification
     */
    private Stage stage;

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

    public Stage getStage() {
        return stage;
    }

    public void setStage(Stage stage) {
        this.stage = stage;
    }

}
