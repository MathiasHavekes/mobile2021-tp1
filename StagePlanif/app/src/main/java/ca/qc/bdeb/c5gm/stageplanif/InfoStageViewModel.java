package ca.qc.bdeb.c5gm.stageplanif;

import android.graphics.Bitmap;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class InfoStageViewModel extends ViewModel {
    private final MutableLiveData<Priorite> priorite = new MutableLiveData<>();
    private final MutableLiveData<Bitmap> photo = new MutableLiveData<>();
    private final MutableLiveData<Compte> compte = new MutableLiveData<>();

    public void setPriorite(Priorite selection) {
        priorite.setValue(selection);
    }

    public void setImage(Bitmap photo) {
        this.photo.setValue(photo);
    }

    public void setCompte(Compte compte) {
        this.compte.setValue(compte);
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
}
