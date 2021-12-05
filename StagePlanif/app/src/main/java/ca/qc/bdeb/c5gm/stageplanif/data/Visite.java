package ca.qc.bdeb.c5gm.stageplanif.data;

import android.os.Parcel;
import android.os.Parcelable;

public class Visite implements Parcelable {
    private final String id;
    private final StagePoidsPlume stage;
    private Integer journee;
    private Integer heureDeDebut;
    private Integer duree;

    public Visite(String id, StagePoidsPlume stage, Integer heureDeDebut, Integer duree, Integer journee) {
        this.id = id;
        this.stage = stage;
        this.journee = journee;
        this.heureDeDebut = heureDeDebut;
        this.duree = duree;
    }

    /**
     * Implémentation de parcelable
     */
    protected Visite(Parcel in) {
        id = in.readString();
        stage = in.readParcelable(StagePoidsPlume.class.getClassLoader());
        journee = in.readInt();
        heureDeDebut = in.readInt();
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

    public Integer getJournee() {
        return journee;
    }

    public int getHeureDeDebut() {
        return heureDeDebut;
    }

    public int getDuree() {
        return duree;
    }

    public void setJournee(Integer journee) {
        this.journee = journee;
    }

    public void setHeureDeDebut(int heureDeDebut) {
        this.heureDeDebut = heureDeDebut;
    }

    public void setDuree(int duree) {
        this.duree = duree;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(id);
        parcel.writeParcelable(stage, i);
        parcel.writeInt(journee);
        parcel.writeInt(heureDeDebut);
        parcel.writeInt(duree);
    }
}
