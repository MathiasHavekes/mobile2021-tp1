package ca.qc.bdeb.c5gm.stageplanif;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;

import java.sql.Date;

public class Stockage extends SQLiteOpenHelper {
    /**
     * Nom de fichier de base de donnees
     */
    private static final String DB_NAME = "ca.qc.bdeb.c5gm.stageplanif";
    /**
     * numéro actuel de version de BD
     */
    public static final int DB_VERSION = 1;
    private Context context;
    /**
     * L’unique instance de DbHelperpossible
     */
    private static Stockage instance = null;

    private static final String SQL_CREATE_CLIENTS =
            "CREATE TABLE " + Entreprise.NOM_TABLE + " (" +
                    Entreprise._ID + " INTEGER PRIMARY KEY," +
                    Entreprise.ENTREPRISE_NOM + " TEXT," +
                    Entreprise.ENTREPRISE_ADRESSE + " TEXT," +
                    Entreprise.ENTREPRISE_VILLE + " TEXT," +
                    Entreprise.ENTREPRISE_PROVINCE + " TEXT," +
                    Entreprise.ENTREPRISE_CP + " TEXT)";
    private static final String SQL_DELETE_CLIENTS =
            "DROP TABLE IF EXISTS " + Clients.NOM_TABLE_COMPTE;
    private Stockage(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
        this.context= context;
    }

    public static Stockage getInstance(Context context) {
        if (instance == null){
            instance = new Stockage(context.getApplicationContext());
        }
        return instance;
    }

    /**
     * Classe interne qui définit le contenu de notre table
     */
    public static class Entreprise implements BaseColumns {
        public static final String NOM_TABLE = "entreprise";
        public static final String ENTREPRISE_NOM = "nom";
        public static final String ENTREPRISE_ADRESSE = "adresse";
        public static final String ENTREPRISE_VILLE = "ville";
        public static final String ENTREPRISE_PROVINCE = "province";
        public static final String ENTREPRISE_CP = "cp";
    }

    /**
     * Classe interne qui définit le contenu de notre table
     */
    public static class Compte implements BaseColumns {
        public static final String NOM_TABLE = "compte";
        public static final String COMPTE_CREATED_AT = "created_at";
        public static final String COMPTE_DELETED_AT = "deleted_at";
        public static final String COMPTE_EMAIL = "email";
        public static final String COMPTE_EST_ACTIF = "est_actif";
        public static final String COMPTE_MOT_PASSE = "mot_passe";
        public static final String COMPTE_NOM = "nom";
        public static final String COMPTE_PRENOM = "prenom";
        public static final String COMPTE_PHOTO = "photo";
        public static final String COMPTE_UPDATED_AT = "updated_at";
        public static final String COMPTE_TYPE_COMPTE = "type_compte";
    }

    /**
     * Classe interne qui définit le contenu de notre table
     */
    public static class Stage implements BaseColumns {
        public static final String NOM_TABLE = "stage";
        public static final String STAGE_ANNEE_SCOLAIRE = "annee_scolaire";
        public static final String STAGE_ENTREPRISE_ID = "entreprise_id";
        public static final String STAGE_ETUDIANT_ID = "etudiant_id";
        public static final String STAGE_PROFESSEUR_ID = "professeur_id";
        public static final String NOM_TABLE_VISITE = "visite";
        public static final String VISITE_DATE = "date";
        public static final String VISITE_HEURE_DEBUT = "heure_debut";
        public static final String VISITE_DUREE = "duree";
    }

    /**
     * Classe interne qui définit le contenu de notre table
     */
    public static class Visite implements BaseColumns {
        public static final String NOM_TABLE = "visite";
        public static final String VISITE_DATE = "date";
        public static final String VISITE_HEURE_DEBUT = "heure_debut";
        public static final String VISITE_DUREE = "duree";
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {

    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }
}
