package ca.qc.bdeb.c5gm.stageplanif.data;

import android.os.Parcel;
import android.os.Parcelable;
import ca.qc.bdeb.c5gm.stageplanif.CalendrierActivity;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;

/**
 * Classe qui stock des visites dans un environnement de stage
 */
public class Visite implements Parcelable {
    /**
     * ID de la visite
     */
    private final String id;
    /**
     * Stage associe a la visite
     */
    private final StagePoidsPlume stage;
    /**
     * Journee du stage avec le temps defini
     */
    private LocalDateTime journee;
    /**
     * Duree definie de la visite
     */
    private Integer duree;

    public Visite(String id, StagePoidsPlume stage, Integer duree, LocalDateTime journee) {
        this.id = id;
        this.stage = stage;
        this.journee = journee;
        if (duree <= 0) {
            this.duree = CalendrierActivity.DUREE_VISITE_STANDARD;
        } else {
            this.duree = duree;
        }
    }

    /**
     * Implémentation de parcelable
     */
    protected Visite(Parcel in) {
        id = in.readString();
        stage = in.readParcelable(StagePoidsPlume.class.getClassLoader());
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        journee = LocalDateTime.parse(in.readString(), formatter);
        duree = in.readInt();
    }

    public static final Creator<Visite> CREATOR = new Creator<Visite>() {
        @Override
        public Visite createFromParcel(Parcel in) {
            return new Visite(in);
        }

        @Override
        public Visite[] newArray(int size) {
            return new Visite[size];
        }
    };

    public String getId() {
        return id;
    }

    public StagePoidsPlume getStage() {
        return stage;
    }


    public int getDuree() {
        return duree;
    }

    public void setDuree(int duree) {
        this.duree = duree;
    }

    public LocalDateTime getJournee() {
        return journee;
    }

    public void setJournee(LocalDateTime journee) {
        this.journee = journee;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(id);
        parcel.writeString(journee.toString());
        parcel.writeParcelable(stage, i);
        parcel.writeInt(duree);
    }
}
