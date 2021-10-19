package ca.qc.bdeb.c5gm.stageplanif;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;

import java.text.Normalizer;
import java.util.ArrayList;

public class Stockage extends SQLiteOpenHelper {
    /**
     * Nom de fichier de base de donnees
     */
    private static final String DB_NAME = "ca.qc.bdeb.c5gm.stageplanif";
    /**
     * numéro actuel de version de BD
     */
    public static final int DB_VERSION = 3;
    private Context context;
    /**
     * L’unique instance de DbHelper possible
     */
    private static Stockage instance = null;

    private static final String SQL_CREATE_ENTREPRISE =
            "CREATE TABLE " + EntrepriseHelper.NOM_TABLE + " (" +
                    EntrepriseHelper._ID + " TEXT PRIMARY KEY," +
                    EntrepriseHelper.ENTREPRISE_NOM + " TEXT," +
                    EntrepriseHelper.ENTREPRISE_ADRESSE + " TEXT," +
                    EntrepriseHelper.ENTREPRISE_VILLE + " TEXT," +
                    EntrepriseHelper.ENTREPRISE_PROVINCE + " TEXT," +
                    EntrepriseHelper.ENTREPRISE_CP + " TEXT);";
    private static final String SQL_CREATE_COMPTE =
            "CREATE TABLE " + CompteHelper.NOM_TABLE + " (" +
                    CompteHelper._ID + " INTEGER PRIMARY KEY," +
                    CompteHelper.COMPTE_EMAIL + " TEXT," +
                    CompteHelper.COMPTE_EST_ACTIF + " NUMERIC," +
                    CompteHelper.COMPTE_MOT_PASSE + " TEXT," +
                    CompteHelper.COMPTE_NOM + " TEXT," +
                    CompteHelper.COMPTE_PRENOM + " TEXT," +
                    CompteHelper.COMPTE_PHOTO + " BLOB," +
                    CompteHelper.COMPTE_TYPE_COMPTE + " INTEGER);";
    private static final String SQL_CREATE_STAGE =
            "CREATE TABLE " + StageHelper.NOM_TABLE + " (" +
                    StageHelper._ID + " TEXT PRIMARY KEY," +
                    StageHelper.STAGE_ANNEE_SCOLAIRE + " TEXT," +
                    StageHelper.STAGE_ENTREPRISE_ID + " TEXT," +
                    StageHelper.STAGE_ETUDIANT_ID + " INTEGER," +
                    StageHelper.STAGE_PROFESSEUR_ID + " INTEGER," +
                    "FOREIGN KEY (" + StageHelper.STAGE_ENTREPRISE_ID + ") REFERENCES " +
                    EntrepriseHelper.NOM_TABLE + "(" + EntrepriseHelper._ID + ")," +
                    "FOREIGN KEY (" + StageHelper.STAGE_ETUDIANT_ID + ") REFERENCES " +
                    CompteHelper.NOM_TABLE + "(" + CompteHelper._ID + ")," +
                    "FOREIGN KEY (" + StageHelper.STAGE_PROFESSEUR_ID + ") REFERENCES " +
                    CompteHelper.NOM_TABLE + "(" + CompteHelper._ID + "));";
    private static final String SQL_CREATE_VISITE =
                    "CREATE TABLE " + VisiteHelper.NOM_TABLE + " (" +
                    VisiteHelper._ID + " TEXT PRIMARY KEY," +
                    VisiteHelper.STAGE_ID + " TEXT," +
                    VisiteHelper.VISITE_DATE + " NUMERIC," +
                    VisiteHelper.VISITE_HEURE_DEBUT + " NUMERIC," +
                    VisiteHelper.VISITE_DUREE + " INTEGER," +
                    " FOREIGN KEY (" + VisiteHelper.STAGE_ID + ") REFERENCES " +
                    StageHelper.NOM_TABLE + "(" + StageHelper._ID + "));";

    private static final String SQL_DELETE_VISITE = "DROP TABLE IF EXISTS " + VisiteHelper.NOM_TABLE;
    private static final String SQL_DELETE_STAGE = "DROP TABLE IF EXISTS " + StageHelper.NOM_TABLE;
    private static final String SQL_DELETE_ENTREPRISE = "DROP TABLE IF EXISTS " + EntrepriseHelper.NOM_TABLE;
    private static final String SQL_DELETE_COMPTE = "DROP TABLE IF EXISTS " + CompteHelper.NOM_TABLE;

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
    public static class EntrepriseHelper implements BaseColumns {
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
    public static class CompteHelper implements BaseColumns {
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
    public static class StageHelper implements BaseColumns {
        public static final String NOM_TABLE = "stage";
        public static final String STAGE_ANNEE_SCOLAIRE = "annee_scolaire";
        public static final String STAGE_ENTREPRISE_ID = "entreprise_id";
        public static final String STAGE_ETUDIANT_ID = "etudiant_id";
        public static final String STAGE_PROFESSEUR_ID = "professeur_id";
    }

    /**
     * Classe interne qui définit le contenu de notre table
     */
    public static class VisiteHelper implements BaseColumns {
        public static final String NOM_TABLE = "visite";
        public static final String VISITE_DATE = "date";
        public static final String VISITE_HEURE_DEBUT = "heure_debut";
        public static final String VISITE_DUREE = "duree";
        public static final String STAGE_ID = "stage_id";
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(SQL_CREATE_COMPTE);
        sqLiteDatabase.execSQL(SQL_CREATE_ENTREPRISE);
        sqLiteDatabase.execSQL(SQL_CREATE_STAGE);
        sqLiteDatabase.execSQL(SQL_CREATE_VISITE);
        ajouterCompte(creerComptes(), sqLiteDatabase);
        ajouterEntreprise(creerEntreprise(), sqLiteDatabase);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
        sqLiteDatabase.execSQL(SQL_DELETE_VISITE);
        sqLiteDatabase.execSQL(SQL_DELETE_STAGE);
        sqLiteDatabase.execSQL(SQL_DELETE_ENTREPRISE);
        sqLiteDatabase.execSQL(SQL_DELETE_COMPTE);
        onCreate(sqLiteDatabase);
    }

    private ArrayList<Compte> creerComptes() {
        ArrayList<Compte> listeCompte = new ArrayList<>();
        listeCompte.add(new Compte("Boucher", "Mikaël", null, 2));
        listeCompte.add(new Compte("Caron", "Thomas", null, 2));
        listeCompte.add(new Compte("Gingras", "Simon", null, 2));
        listeCompte.add(new Compte("Leblanc", "Kevin", null, 2));
        listeCompte.add(new Compte("Masson", "Cédric", null, 2));
        listeCompte.add(new Compte("Monette", "Vanessa", null, 2));
        listeCompte.add(new Compte("Picard", "Vincent", null, 2));
        listeCompte.add(new Compte("Poulain", "Mélissa", null, 2));
        listeCompte.add(new Compte("Vargas", "Diego", null, 2));
        listeCompte.add(new Compte("Tremblay", "Geneviève", null, 2));
        listeCompte.add(new Compte("Prades", "Pierre", null, 1));
        return listeCompte;
    }

    private ArrayList<Entreprise> creerEntreprise() {
        ArrayList<Entreprise> listeEntreprise = new ArrayList<>();
        listeEntreprise.add(new Entreprise("Jean Coutu", "4885 Henri-Bourassa Blvd W #731",
                "Montreal", "QC", "H3L 1P3"));
        listeEntreprise.add(new Entreprise("Garage Tremblay", "10142 Boul. Saint-Laurent",
                "Montréal", "QC", "H3L 2N7"));
        listeEntreprise.add(new Entreprise("Pharmaprix", "3611 Rue Jarry E",
                "Montréal", "QC", "H1Z 2G1"));
        listeEntreprise.add(new Entreprise("Alimentation Générale", "1853 Chem. Rockland",
                "Mont-Royal", "QC", "H3P 2Y7"));
        listeEntreprise.add(new Entreprise("Auto Repair", "8490 Rue Saint-Dominique",
                "Montréal", "QC", "H2P 2L5"));
        listeEntreprise.add(new Entreprise("Subway", "775 Rue Chabanel O",
                "Montréal", "QC", "H4N 3J7"));
        listeEntreprise.add(new Entreprise("Métro", "1331 Blvd. de la Côte-Vertu",
                "Saint-Laurent", "QC", "H4L 1Z1"));
        listeEntreprise.add(new Entreprise("Épicerie les Jardinières", "10345 Ave Christophe-Colomb",
                "Montreal", "QC", "H2C 2V1"));
        listeEntreprise.add(new Entreprise("Boucherie Marien", "1499-1415 Rue Jarry E",
                "Montreal", "QC", "H2E 1A7"));
        listeEntreprise.add(new Entreprise("IGA", "8921 Rue Lajeunesse",
                "Montreal", "QC", "H2M 1S1"));
        return listeEntreprise;
    }

    private void ajouterCompte(ArrayList<Compte> listeCompte, SQLiteDatabase db) {
        for (Compte compte: listeCompte) {
            String email = Normalizer.normalize(compte.getPrenom() + "." + compte.getNom() + "@test.com", Normalizer.Form.NFD);
            email = email.replaceAll("[^\\p{ASCII}]", "");
            ContentValues values = new ContentValues();
            values.put(CompteHelper.COMPTE_NOM, compte.getNom()); // Nom du client
            values.put(CompteHelper.COMPTE_PRENOM, compte.getPrenom()); // #tel du client
            values.put(CompteHelper.COMPTE_EMAIL, email);
            values.put(CompteHelper.COMPTE_TYPE_COMPTE, compte.getTypeCompte());
            values.put(CompteHelper.COMPTE_PHOTO, compte.getPhoto());
            db.insert(CompteHelper.NOM_TABLE, null, values);
        }
    }

    private void ajouterEntreprise(ArrayList<Entreprise> listeEntreprise, SQLiteDatabase db) {
        for (Entreprise entreprise: listeEntreprise) {
            ContentValues values = new ContentValues();
            values.put(EntrepriseHelper._ID, entreprise.getId());
            values.put(EntrepriseHelper.ENTREPRISE_NOM, entreprise.getNom()); // Nom du client
            values.put(EntrepriseHelper.ENTREPRISE_ADRESSE, entreprise.getAdresse()); // #tel du client
            values.put(EntrepriseHelper.ENTREPRISE_VILLE, entreprise.getVille());
            values.put(EntrepriseHelper.ENTREPRISE_PROVINCE, entreprise.getProvince());
            values.put(EntrepriseHelper.ENTREPRISE_CP, entreprise.getCp());
            db.insert(EntrepriseHelper.NOM_TABLE, null, values);
        }
    }

    public ArrayList<Compte> getComptes(TypeComptes type){
        SQLiteDatabase db = this.getReadableDatabase(); // On veut lire dans la BD
        ArrayList<Compte> comptes = new ArrayList<>();
        // les colonnes retournées par la requête:
        String[] colonnes = {
                CompteHelper._ID,
                CompteHelper.COMPTE_NOM,
                CompteHelper.COMPTE_PRENOM,
                CompteHelper.COMPTE_PHOTO,
                CompteHelper.COMPTE_TYPE_COMPTE
        };
        String selection = CompteHelper.COMPTE_TYPE_COMPTE + " = " + type.getValue();
        Cursor cursor = db.query(CompteHelper.NOM_TABLE, colonnes, selection, null, null, null, null, null);
        if (cursor != null){
            cursor.moveToFirst();
            do {
                comptes.add(new Compte(cursor.getInt(0),
                        cursor.getString(1),
                        cursor.getString(2),
                        cursor.getBlob(3),
                        cursor.getInt(4)));
            } while (cursor.moveToNext());
        }
        cursor.close();
        return comptes;
    }

    public ArrayList<Entreprise> getEntreprises(){
        SQLiteDatabase db = this.getReadableDatabase(); // On veut lire dans la BD
        ArrayList<Entreprise> entreprises = new ArrayList<>();
        // les colonnes retournées par la requête:
        String[] colonnes = {
                EntrepriseHelper._ID,
                EntrepriseHelper.ENTREPRISE_NOM,
                EntrepriseHelper.ENTREPRISE_ADRESSE,
                EntrepriseHelper.ENTREPRISE_VILLE,
                EntrepriseHelper.ENTREPRISE_PROVINCE,
                EntrepriseHelper.ENTREPRISE_CP
        };
        Cursor cursor = db.query(CompteHelper.NOM_TABLE, colonnes, null, null, null, null, null, null);
        if (cursor != null){
            cursor.moveToFirst();
            do {
                entreprises.add(new Entreprise(cursor.getString(0),
                        cursor.getString(1),
                        cursor.getString(2),
                        cursor.getString(3),
                        cursor.getString(4),
                        cursor.getString(5)));
            } while (cursor.moveToNext());
        }
        cursor.close();
        return entreprises;
    }

}
