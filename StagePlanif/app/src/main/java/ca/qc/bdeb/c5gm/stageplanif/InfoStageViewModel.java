package ca.qc.bdeb.c5gm.stageplanif;

import android.graphics.Bitmap;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class InfoStageViewModel extends ViewModel {
    private final MutableLiveData<Priorite> priorite = new MutableLiveData<>();
    private final MutableLiveData<byte[]> photo = new MutableLiveData<>();
    private final MutableLiveData<Compte> compte = new MutableLiveData<>();
    private final MutableLiveData<Entreprise> entreprise = new MutableLiveData<>();
    private Stage stage;


    public void setPriorite(Priorite selection) {
        priorite.setValue(selection);
    }

    public void setImage(Bitmap photo) {
        if (photo != null){
            this.photo.setValue(Utils.getBytes(photo));
        }
    }

    public void setCompte(Compte compte) {
        this.compte.setValue(compte);
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

    public LiveData<byte[]> getPhoto() {
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
