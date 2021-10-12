package ca.qc.bdeb.c5gm.stageplanif;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;

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

    private static final String SQL_CREATE_ENTREPRISE =
            "CREATE TABLE " + Entreprise.NOM_TABLE + " (" +
                    Entreprise._ID + " TEXT PRIMARY KEY," +
                    Entreprise.ENTREPRISE_NOM + " TEXT," +
                    Entreprise.ENTREPRISE_ADRESSE + " TEXT," +
                    Entreprise.ENTREPRISE_VILLE + " TEXT," +
                    Entreprise.ENTREPRISE_PROVINCE + " TEXT," +
                    Entreprise.ENTREPRISE_CP + " TEXT);";
    private static final String SQL_CREATE_COMPTE =
            "CREATE TABLE " + CompteDB.NOM_TABLE + " (" +
                    CompteDB._ID + " INTEGER PRIMARY KEY," +
                    CompteDB.COMPTE_EMAIL + " TEXT," +
                    CompteDB.COMPTE_EST_ACTIF + " NUMERIC," +
                    CompteDB.COMPTE_MOT_PASSE + " TEXT," +
                    CompteDB.COMPTE_NOM + " TEXT," +
                    CompteDB.COMPTE_PRENOM + " TEXT," +
                    CompteDB.COMPTE_PHOTO + " BLOB," +
                    CompteDB.COMPTE_TYPE_COMPTE + " INTEGER);";
    private static final String SQL_CREATE_STAGE =
            "CREATE TABLE " + Stage.NOM_TABLE + " (" +
                    Stage._ID + " TEXT PRIMARY KEY," +
                    Stage.STAGE_ANNEE_SCOLAIRE + " TEXT," +
                    Stage.STAGE_ENTREPRISE_ID + " TEXT," +
                    Stage.STAGE_ETUDIANT_ID + " INTEGER," +
                    Stage.STAGE_PROFESSEUR_ID + " INTEGER," +
                    "FOREIGN KEY (" + Stage.STAGE_ENTREPRISE_ID + ") REFERENCES " +
                    Entreprise.NOM_TABLE + "(" + Entreprise._ID + ")," +
                    "FOREIGN KEY (" + Stage.STAGE_ETUDIANT_ID + ") REFERENCES " +
                    CompteDB.NOM_TABLE + "(" + CompteDB._ID + ")," +
                    "FOREIGN KEY (" + Stage.STAGE_PROFESSEUR_ID + ") REFERENCES " +
                    CompteDB.NOM_TABLE + "(" + CompteDB._ID + "));";
    private static final String SQL_CREATE_VISITE =
                    "CREATE TABLE " + Visite.NOM_TABLE + " (" +
                    Visite._ID + " TEXT PRIMARY KEY," +
                    Visite.STAGE_ID + " TEXT," +
                    Visite.VISITE_DATE + " NUMERIC," +
                    Visite.VISITE_HEURE_DEBUT + " NUMERIC," +
                    Visite.VISITE_DUREE + " INTEGER," +
                    " FOREIGN KEY (" + Visite.STAGE_ID + ") REFERENCES " +
                    Stage.NOM_TABLE + "(" + Stage._ID + "));";

    private static final String SQL_DELETE_VISITE = "DROP TABLE IF EXISTS " + Visite.NOM_TABLE;
    private static final String SQL_DELETE_STAGE = "DROP TABLE IF EXISTS " + Stage.NOM_TABLE;
    private static final String SQL_DELETE_ENTREPRISE = "DROP TABLE IF EXISTS " + Entreprise.NOM_TABLE;
    private static final String SQL_DELETE_COMPTE = "DROP TABLE IF EXISTS " + CompteDB.NOM_TABLE;

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
    public static class CompteDB implements BaseColumns {
        public static final String NOM_TABLE = "compte";
        public static final String COMPTE_EMAIL = "email";
        public static final String COMPTE_EST_ACTIF = "est_actif";
        public static final String COMPTE_MOT_PASSE = "mot_passe";
        public static final String COMPTE_NOM = "nom";
        public static final String COMPTE_PRENOM = "prenom";
        public static final String COMPTE_PHOTO = "photo";
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
    }

    /**
     * Classe interne qui définit le contenu de notre table
     */
    public static class Visite implements BaseColumns {
        public static final String NOM_TABLE = "visite";
        public static final String VISITE_DATE = "date";
        public static final String VISITE_HEURE_DEBUT = "heure_debut";
        public static final String VISITE_DUREE = "duree";
        public static final String STAGE_ID = "stage_id";
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        String requete = SQL_CREATE_ENTREPRISE + " " + SQL_CREATE_COMPTE + " " + SQL_CREATE_STAGE
                + " " + SQL_CREATE_VISITE;
        sqLiteDatabase.execSQL(SQL_CREATE_COMPTE);
        sqLiteDatabase.execSQL(SQL_CREATE_ENTREPRISE);
        sqLiteDatabase.execSQL(SQL_CREATE_STAGE);
        sqLiteDatabase.execSQL(SQL_CREATE_VISITE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
        sqLiteDatabase.execSQL(SQL_DELETE_VISITE);
        sqLiteDatabase.execSQL(SQL_DELETE_STAGE);
        sqLiteDatabase.execSQL(SQL_DELETE_ENTREPRISE);
        sqLiteDatabase.execSQL(SQL_DELETE_COMPTE);
        onCreate(sqLiteDatabase);
    }

    public void ajouterClient(Compte compte) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(CompteDB.COMPTE_NOM, compte.getNom()); // Nom du client
        values.put(CompteDB.COMPTE_PRENOM, compte.getPrenom()); // #tel du client
        values.put(CompteDB.COMPTE_EMAIL, compte.getPrenom() + "." + compte.getNom() + "@test.com");
        values.put(CompteDB.COMPTE_TYPE_COMPTE, compte.getTypeCompte());
        values.put(CompteDB.COMPTE_PHOTO, compte.getPhoto());
        long id = db.insert(CompteDB.NOM_TABLE, null, values);
    }
}
