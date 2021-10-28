package ca.qc.bdeb.c5gm.stageplanif;

import android.graphics.Bitmap;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class InfoStageViewModel extends ViewModel {
    private final MutableLiveData<Priorite> priorite = new MutableLiveData<>();
    private final MutableLiveData<Bitmap> photo = new MutableLiveData<>();
    private final MutableLiveData<Compte> compte = new MutableLiveData<>();
    private final MutableLiveData<Entreprise> entreprise = new MutableLiveData<>();
    private Stage stage;
    private Boolean changements;

    public void setPriorite(Priorite selection) {
        if (priorite.getValue() != selection) {
            priorite.setValue(selection);
            changements = true;
        }
    }

    public void setImage(Bitmap photo) {
        if (this.photo.getValue() != photo) {
            this.photo.setValue(photo);
            changements = true;
        }
    }

    public void setCompte(Compte compte) {
        if (this.compte.getValue() != compte) {
            this.compte.setValue(compte);
            changements = true;
        }
    }

    public void setEntreprise(Entreprise entreprise) {
        this.entreprise.setValue(entreprise);
    }

    public void setStage(Stage stage) {
        this.stage = stage;
    }

    public LiveData<Priorite> getPriorite() {
        return priorite;
    }

    public LiveData<Bitmap> getPhoto() {
        return photo;
    }

    public LiveData<Compte> getCompte() {
        return compte;
    }

    public LiveData<Entreprise> getEntreprise() {
        return entreprise;
    }

    public Stage getStage() {
        return stage;
    }

}
