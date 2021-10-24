package ca.qc.bdeb.c5gm.stageplanif;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;

import java.text.Normalizer;
import java.util.ArrayList;
import java.util.Random;
import java.util.UUID;

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
                    StageHelper.STAGE_DRAPEAU + " INTEGER," +
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
        public static final String STAGE_DRAPEAU = "priorite";
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
        ArrayList<Compte> comptes = creerComptes();
        ajouterCompte(comptes, sqLiteDatabase);
        ArrayList<Entreprise> entreprises = creerEntreprise();
        ajouterEntreprise(entreprises, sqLiteDatabase);
        ajouterStage(creerStages(entreprises, comptes), sqLiteDatabase);
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
        listeCompte.add(new Compte(1,"Boucher", "Mikaël", null, 2));
        listeCompte.add(new Compte(2,"Caron", "Thomas", null, 2));
        listeCompte.add(new Compte(3,"Gingras", "Simon", null, 2));
        listeCompte.add(new Compte(4,"Leblanc", "Kevin", null, 2));
        listeCompte.add(new Compte(5,"Masson", "Cédric", null, 2));
        listeCompte.add(new Compte(6,"Monette", "Vanessa", null, 2));
        listeCompte.add(new Compte(7,"Picard", "Vincent", null, 2));
        listeCompte.add(new Compte(8,"Poulain", "Mélissa", null, 2));
        listeCompte.add(new Compte(9,"Vargas", "Diego", null, 2));
        listeCompte.add(new Compte(10,"Tremblay", "Geneviève", null, 2));
        listeCompte.add(new Compte(11,"Prades", "Pierre", null, 1));
        return listeCompte;
    }

    /**
     * Crée les entreprises qui permettent des stages
     * @return une arraylist d'entreprises
     */
    private ArrayList<Entreprise> creerEntreprise() {
        ArrayList<Entreprise> listeEntreprise = new ArrayList<>();
        listeEntreprise.add(new Entreprise(UUID.randomUUID().toString(),"Jean Coutu",
                "4885 Henri-Bourassa Blvd W #731", "Montreal", "QC", "H3L 1P3"));
        listeEntreprise.add(new Entreprise(UUID.randomUUID().toString(),"Garage Tremblay",
                "10142 Boul. Saint-Laurent", "Montréal", "QC", "H3L 2N7"));
        listeEntreprise.add(new Entreprise(UUID.randomUUID().toString(),"Pharmaprix",
                "3611 Rue Jarry E", "Montréal", "QC", "H1Z 2G1"));
        listeEntreprise.add(new Entreprise(UUID.randomUUID().toString(),"Alimentation Générale",
                "1853 Chem. Rockland", "Mont-Royal", "QC", "H3P 2Y7"));
        listeEntreprise.add(new Entreprise(UUID.randomUUID().toString(),"Auto Repair",
                "8490 Rue Saint-Dominique", "Montréal", "QC", "H2P 2L5"));
        listeEntreprise.add(new Entreprise(UUID.randomUUID().toString(),"Subway",
                "775 Rue Chabanel O", "Montréal", "QC", "H4N 3J7"));
        listeEntreprise.add(new Entreprise(UUID.randomUUID().toString(),"Métro",
                "1331 Blvd. de la Côte-Vertu", "Saint-Laurent", "QC", "H4L 1Z1"));
        listeEntreprise.add(new Entreprise(UUID.randomUUID().toString(),"Épicerie les Jardinières",
                "10345 Ave Christophe-Colomb", "Montreal", "QC", "H2C 2V1"));
        listeEntreprise.add(new Entreprise(UUID.randomUUID().toString(), "Boucherie Marien",
                "1499-1415 Rue Jarry E","Montreal", "QC", "H2E 1A7"));
        listeEntreprise.add(new Entreprise(UUID.randomUUID().toString(),"IGA",
                "8921 Rue Lajeunesse","Montreal", "QC", "H2M 1S1"));
        return listeEntreprise;
    }

    /**
     * Crée les objets de stage
     * @param entreprises la arraylist d'entreprises qui permettent des stages
     * @param comptes la arralist des comptes des étudiants
     * @return une arraylist de stages
     */
    private ArrayList<Stage> creerStages(ArrayList<Entreprise> entreprises, ArrayList<Compte> comptes) {
        ArrayList<Stage> listeStages = new ArrayList<>();
        for (int i = 0; i < comptes.size() - 1; i++) {
            Stage stage = new Stage(UUID.randomUUID().toString(),"2021-2022", getDrapeauAleatoire());
            stage.addEntreprise(entreprises.get(i));
            stage.addEtudiant(comptes.get(i));
            stage.addProfesseur(comptes.get(comptes.size() - 1));
            listeStages.add(stage);
        }
        return listeStages;
    }

    /**
     * Méthode qui crée des valeurs de drapeau aléatoire
     * @return un chiffre soit 1, 2 ou 4
     */
    public Integer getDrapeauAleatoire() {
        Integer[] drapeaux = new Integer[] {1, 2, 4};
        Random rand = new Random();
        return drapeaux[rand.nextInt(drapeaux.length)];
    }

    /**
     * Méthode qui ajoute les comptes à la base de données
     * @param listeCompte liste des comptes à créer
     * @param db la base de données qui est utilisée
     */
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

    /**
     * Ajoute les entreprises à la base de données
     * @param listeEntreprise liste des entreprises à ajouter
     * @param db la base de données qui est utilisée
     */
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

    /**
     * Ajouter les stages à la base de données
     * @param listeStages liste des stages à ajouter
     * @param db la base de données qui est utilisée
     */
    private void ajouterStage(ArrayList<Stage> listeStages, SQLiteDatabase db) {
        for (Stage stage: listeStages) {
            ContentValues values = new ContentValues();
            values.put(StageHelper._ID, stage.getId());
            values.put(StageHelper.STAGE_ENTREPRISE_ID, stage.getEntreprise().getId()); // Nom du client
            values.put(StageHelper.STAGE_ETUDIANT_ID, stage.getEtudiant().getId()); // #tel du client
            values.put(StageHelper.STAGE_PROFESSEUR_ID, stage.getProfesseur().getId());
            values.put(StageHelper.STAGE_ANNEE_SCOLAIRE, stage.getAnneeScolaire());
            values.put(StageHelper.STAGE_DRAPEAU, stage.getDrapeau());
            db.insert(StageHelper.NOM_TABLE, null, values);
        }
    }

    /**
     * Recevoir la liste des comptes dans la base de données
     * @return la liste des comptes dans la base de données
     */
    public ArrayList<Compte> getComptes(){
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
        String selection = CompteHelper.COMPTE_TYPE_COMPTE + " > " + 0;
        String orderBy = CompteHelper.COMPTE_NOM + " ASC, " + CompteHelper.COMPTE_PRENOM + " ASC";
        Cursor cursor = db.query(CompteHelper.NOM_TABLE, colonnes, selection, null, null, null, orderBy, null);
        if (cursor != null){
            cursor.moveToFirst();
            do {
                comptes.add(new Compte(cursor.getInt(0),
                        cursor.getString(1),
                        cursor.getString(2),
                        cursor.getBlob(3),
                        cursor.getInt(4)));
            } while (cursor.moveToNext());
            cursor.close();
        }
        return comptes;
    }

    /**
     * Recevoir un compte de la base de données en fonction de l'ID
     * @param id l'id du compte
     * @return le compte correspondant à l'id
     */
    public Compte getCompte(Integer id){
        SQLiteDatabase db = this.getReadableDatabase(); // On veut lire dans la BD
        // les colonnes retournées par la requête:
        String[] colonnes = {
                CompteHelper._ID,
                CompteHelper.COMPTE_NOM,
                CompteHelper.COMPTE_PRENOM,
                CompteHelper.COMPTE_PHOTO,
                CompteHelper.COMPTE_TYPE_COMPTE
        };
        String selection = CompteHelper._ID + " = " + id;
        Cursor cursor = db.query(CompteHelper.NOM_TABLE, colonnes, selection, null, null, null, null, null);
        Compte compte = null;
        if (cursor != null){
            cursor.moveToFirst();
            compte = new Compte(cursor.getInt(0),
                    cursor.getString(1),
                    cursor.getString(2),
                    cursor.getBlob(3),
                    cursor.getInt(4));
            cursor.close();
        }
        return compte;
    }

    /**
     * Recevoir la liste des entreprises dans la base de données
     * @return
     */
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
        Cursor cursor = db.query(EntrepriseHelper.NOM_TABLE, colonnes, null, null, null, null, null, null);
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
            cursor.close();
        }
        return entreprises;
    }

    /**
     * Recevoir une entreprise de la base de données
     * @param id id de l'entreprise
     * @return l'entreprise correspondant à l'id
     */
    public Entreprise getEntreprise(String id){
        SQLiteDatabase db = this.getReadableDatabase(); // On veut lire dans la BD
        // les colonnes retournées par la requête:
        String[] colonnes = {
                EntrepriseHelper._ID,
                EntrepriseHelper.ENTREPRISE_NOM,
                EntrepriseHelper.ENTREPRISE_ADRESSE,
                EntrepriseHelper.ENTREPRISE_VILLE,
                EntrepriseHelper.ENTREPRISE_PROVINCE,
                EntrepriseHelper.ENTREPRISE_CP
        };
        String selection = EntrepriseHelper._ID + " = \"" + id + "\"";
        Cursor cursor = db.query(EntrepriseHelper.NOM_TABLE, colonnes, selection, null, null, null, null, null);
        Entreprise entreprise = null;
        if (cursor != null){
            cursor.moveToFirst();
            entreprise = new Entreprise(cursor.getString(0),
                    cursor.getString(1),
                    cursor.getString(2),
                    cursor.getString(3),
                    cursor.getString(4),
                    cursor.getString(5));
            cursor.close();
        }

        return entreprise;
    }

    /**
     * Recevoir les stages de la base de données
     * @return une liste de stages
     */
    public ArrayList<Stage> getStages() {
        ArrayList<Stage> stages = new ArrayList<>();
        ArrayList<Entreprise> entreprisesConnu = new ArrayList<>();

        Compte professeur = null;
        SQLiteDatabase db = this.getReadableDatabase(); // On veut lire dans la BD
        // les colonnes retournées par la requête:
        String[] colonnes = {
                StageHelper._ID,
                StageHelper.STAGE_ANNEE_SCOLAIRE,
                StageHelper.STAGE_DRAPEAU,
                StageHelper.STAGE_ETUDIANT_ID,
                StageHelper.STAGE_PROFESSEUR_ID,
                StageHelper.STAGE_ENTREPRISE_ID
        };
        Cursor cursor = db.query(StageHelper.NOM_TABLE, colonnes, null, null, null, null, null, null);
        if (cursor != null){
            cursor.moveToFirst();
            do {
                //Crée un nouveau stage
                Stage stage = new Stage(cursor.getString(0), cursor.getString(1), cursor.getInt(2));
                //Verifie si le professeur a ete cree et le cree s'il ne l'a pas ete
                if(professeur == null) {
                    professeur = getCompte(cursor.getInt(4));
                }
                stage.addProfesseur(professeur);
                //Cree l'etudiant associe au stage
                stage.addEtudiant(getCompte(cursor.getInt(3)));
                //Verifie que l'entreprise n'a pas deja ete creefinal
                for (Entreprise entreprise: entreprisesConnu) {
                    if(entreprise.getId().equals(cursor.getString(5))) {
                        stage.addEntreprise(entreprise);
                        break;
                    }
                }
                if(stage.getEntreprise() == null) {
                    Entreprise entreprise = getEntreprise(cursor.getString(5));
                    stage.addEntreprise(entreprise);
                    entreprisesConnu.add(entreprise);
                }
                stages.add(stage);
            } while (cursor.moveToNext());
            cursor.close();
        }
        return stages;
    }
}