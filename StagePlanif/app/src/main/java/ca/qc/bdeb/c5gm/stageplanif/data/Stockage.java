package ca.qc.bdeb.c5gm.stageplanif.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

import ca.qc.bdeb.c5gm.stageplanif.Utils;
import ca.qc.bdeb.c5gm.stageplanif.reseau.IAPI;

/**
 * Classe qui permet de communiquer avec la BD
 */
public class Stockage extends SQLiteOpenHelper {
    /**
     * numéro actuel de version de BD
     */
    public static final int DB_VERSION = 1;
    /**
     * Nom de fichier de base de donnees
     */
    private static final String DB_NAME = "ca.qc.bdeb.c5gm.stageplanif";
    /**
     * Query SQL de creation de la table entreprise
     */
    private static final String SQL_CREATE_ENTREPRISE =
            "CREATE TABLE " + EntrepriseHelper.NOM_TABLE + " (" +
                    EntrepriseHelper._ID + " TEXT PRIMARY KEY," +
                    EntrepriseHelper.ENTREPRISE_NOM + " TEXT," +
                    EntrepriseHelper.ENTREPRISE_ADRESSE + " TEXT," +
                    EntrepriseHelper.ENTREPRISE_VILLE + " TEXT," +
                    EntrepriseHelper.ENTREPRISE_PROVINCE + " TEXT," +
                    EntrepriseHelper.ENTREPRISE_CP + " TEXT);";
    /**
     * Query SQL de creation de la table compte
     */
    private static final String SQL_CREATE_COMPTE =
            "CREATE TABLE " + CompteHelper.NOM_TABLE + " (" +
                    CompteHelper._ID + " TEXT PRIMARY KEY," +
                    CompteHelper.COMPTE_EMAIL + " TEXT," +
                    CompteHelper.COMPTE_NOM + " TEXT," +
                    CompteHelper.COMPTE_PRENOM + " TEXT," +
                    CompteHelper.COMPTE_PHOTO + " BLOB," +
                    CompteHelper.COMPTE_TYPE_COMPTE + " INTEGER);";
    /**
     * Query SQL de creation de la table stage
     */
    private static final String SQL_CREATE_STAGE =
            "CREATE TABLE " + StageHelper.NOM_TABLE + " (" +
                    StageHelper._ID + " TEXT PRIMARY KEY," +
                    StageHelper.STAGE_ANNEE_SCOLAIRE + " TEXT," +
                    StageHelper.STAGE_ENTREPRISE_ID + " TEXT," +
                    StageHelper.STAGE_ETUDIANT_ID + " INTEGER," +
                    StageHelper.STAGE_PROFESSEUR_ID + " INTEGER," +
                    StageHelper.STAGE_DRAPEAU + " INTEGER," +
                    StageHelper.STAGE_COMMENTAIRE + " TEXT," +
                    StageHelper.STAGE_JOURNEES + " INTEGER," +
                    StageHelper.STAGE_HEURE_DEBUT + " INTEGER," +
                    StageHelper.STAGE_HEURE_FIN +  " INTEGER," +
                    StageHelper.STAGE_HEURE_PAUSE + " INTEGER," +
                    StageHelper.STAGE_HEURE_FIN_PAUSE + " INTEGER," +
                    StageHelper.STAGE_DUREE_VISITE + " INTEGER," +
                    StageHelper.STAGE_DISPONIBILITE_TUTEUR + " INTEGER," +
                    "FOREIGN KEY (" + StageHelper.STAGE_ENTREPRISE_ID + ") REFERENCES " +
                    EntrepriseHelper.NOM_TABLE + "(" + EntrepriseHelper._ID + ")," +
                    "FOREIGN KEY (" + StageHelper.STAGE_ETUDIANT_ID + ") REFERENCES " +
                    CompteHelper.NOM_TABLE + "(" + CompteHelper._ID + ")," +
                    "FOREIGN KEY (" + StageHelper.STAGE_PROFESSEUR_ID + ") REFERENCES " +
                    CompteHelper.NOM_TABLE + "(" + CompteHelper._ID + "));";
    /**
     * Query SQL de creation de la table visite
     */
    private static final String SQL_CREATE_VISITE =
            "CREATE TABLE " + VisiteHelper.NOM_TABLE + " (" +
                    VisiteHelper._ID + " TEXT PRIMARY KEY," +
                    VisiteHelper.STAGE_ID + " TEXT," +
                    VisiteHelper.VISITE_DATE + " TEXT," +
                    VisiteHelper.VISITE_HEURE_DEBUT + " NUMERIC," +
                    VisiteHelper.VISITE_DUREE + " INTEGER," +
                    " FOREIGN KEY (" + VisiteHelper.STAGE_ID + ") REFERENCES " +
                    StageHelper.NOM_TABLE + "(" + StageHelper._ID + "));";
    /**
     * Query SQL de destruction de la table visite
     */
    private static final String SQL_DELETE_VISITE = "DROP TABLE IF EXISTS " + VisiteHelper.NOM_TABLE;
    /**
     * Query SQL de destruction de la table stage
     */
    private static final String SQL_DELETE_STAGE = "DROP TABLE IF EXISTS " + StageHelper.NOM_TABLE;
    /**
     * Query SQL de destruction de la table entreprise
     */
    private static final String SQL_DELETE_ENTREPRISE = "DROP TABLE IF EXISTS " + EntrepriseHelper.NOM_TABLE;
    /**
     * Query SQL de destruction de la table compte
     */
    private static final String SQL_DELETE_COMPTE = "DROP TABLE IF EXISTS " + CompteHelper.NOM_TABLE;
    /**
     * L’unique instance de DbHelper possible
     */
    private static Stockage instance = null;

    private Stockage(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    public static Stockage getInstance(Context context) {
        if (instance == null) {
            instance = new Stockage(context.getApplicationContext());
        }
        return instance;
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
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

    public void viderTables() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DELETE FROM " + VisiteHelper.NOM_TABLE);
        db.execSQL("DELETE FROM " + StageHelper.NOM_TABLE);
        db.execSQL("DELETE FROM " + CompteHelper.NOM_TABLE);
        db.execSQL("DELETE FROM " + EntrepriseHelper.NOM_TABLE);
    }
    /**
     * Méthode qui ajoute les comptes à la base de données
     *
     */
    public void ajouterOuModifierCompte(String id, String nom, String prenom, String email, int typeCompte) {
        SQLiteDatabase db = this.getReadableDatabase();
        ContentValues values = new ContentValues();
        values.put(CompteHelper.COMPTE_NOM, nom);
        values.put(CompteHelper.COMPTE_PRENOM, prenom);
        values.put(CompteHelper.COMPTE_EMAIL, email);
        values.put(CompteHelper.COMPTE_TYPE_COMPTE, typeCompte);
        if(compteExists(id, db)) {
            String whereClause = CompteHelper._ID + " = ?";
            String[] whereArgs = {id};
            db.update(CompteHelper.NOM_TABLE, values, whereClause, whereArgs);
        } else {
            values.put(CompteHelper._ID, id);
            db.insert(CompteHelper.NOM_TABLE, null, values);
        }
    }

    public void ajouterOumodifierEntreprise(String id, String nom, String adresse, String ville, String province, String codePostal) {
        SQLiteDatabase db = this.getReadableDatabase();
        ContentValues values = new ContentValues();
        values.put(EntrepriseHelper.ENTREPRISE_NOM, nom);
        values.put(EntrepriseHelper.ENTREPRISE_ADRESSE, adresse);
        values.put(EntrepriseHelper.ENTREPRISE_VILLE, ville);
        values.put(EntrepriseHelper.ENTREPRISE_PROVINCE, province);
        values.put(EntrepriseHelper.ENTREPRISE_CP, codePostal);
        if(entrepriseExists(id, db)) {
            String whereClause = EntrepriseHelper._ID + " = ?";
            String[] whereArgs = {id};
            db.update(EntrepriseHelper.NOM_TABLE, values, whereClause, whereArgs);
        } else {
            values.put(EntrepriseHelper._ID, id);
            db.insert(EntrepriseHelper.NOM_TABLE, null, values);
        }
    }

    public void ajouterouModifierStage(String id, String anneeScolaire, String entrepriseId, String etudiantId,
                             String professeurId, String commentaire, int heureDebut, int heureFin,
                             int priorite, int heureDebutPause, int heureFinPause) {
        SQLiteDatabase db = this.getReadableDatabase();
        ContentValues values = new ContentValues();
        values.put(StageHelper.STAGE_ENTREPRISE_ID, entrepriseId);
        values.put(StageHelper.STAGE_ETUDIANT_ID, etudiantId);
        values.put(StageHelper.STAGE_PROFESSEUR_ID, professeurId);
        values.put(StageHelper.STAGE_ANNEE_SCOLAIRE, anneeScolaire);
        values.put(StageHelper.STAGE_DRAPEAU, priorite);
        values.put(StageHelper.STAGE_COMMENTAIRE, commentaire);
        if(heureFin < 0) {
            heureFin = LocalTime.of(16,0).toSecondOfDay();
        }
        values.put(StageHelper.STAGE_HEURE_FIN, heureFin);
        if(heureFinPause < 0) {
            heureFinPause = LocalTime.of(12,30).toSecondOfDay();
        }
        values.put(StageHelper.STAGE_HEURE_FIN_PAUSE, heureFinPause);
        if(heureDebut < 0) {
            heureDebut = LocalTime.of(8,0).toSecondOfDay();
        }
        values.put(StageHelper.STAGE_HEURE_DEBUT, heureDebut);
        if(heureDebutPause < 0) {
            heureDebutPause = LocalTime.of(12,0).toSecondOfDay();
        }
        values.put(StageHelper.STAGE_HEURE_PAUSE, heureDebutPause);
        if(stageExists(id, db)) {
            String whereClause = StageHelper._ID + " = ?";
            String[] whereArgs = {id};
            db.update(StageHelper.NOM_TABLE, values, whereClause, whereArgs);
        } else {
            values.put(StageHelper._ID, id);
            db.insert(StageHelper.NOM_TABLE, null, values);
        }
    }

    /**
     * Recevoir la liste des comptes d'un certain type dans la base de données
     *
     * @param type type de compte voulu
     * @return la liste des comptes dans la base de données
     */
    public ArrayList<Compte> getComptes(Integer type) {
        SQLiteDatabase db = this.getReadableDatabase();
        ArrayList<Compte> comptes = new ArrayList<>();
        // les colonnes retournées par la requête:
        String[] colonnes = {
                CompteHelper._ID,
                CompteHelper.COMPTE_NOM,
                CompteHelper.COMPTE_PRENOM,
                CompteHelper.COMPTE_PHOTO,
                CompteHelper.COMPTE_TYPE_COMPTE
        };
        String selection = CompteHelper.COMPTE_TYPE_COMPTE + " = ?";
        String[] selectionArgs = {String.valueOf(type)};
        String orderBy = CompteHelper.COMPTE_NOM + " ASC, " + CompteHelper.COMPTE_PRENOM + " ASC";
        Cursor cursor = db.query(CompteHelper.NOM_TABLE, colonnes, selection, selectionArgs, null, null, orderBy, null);
        if (cursor != null) {
            cursor.moveToFirst();
            do {
                comptes.add(new Compte(cursor.getString(0),
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
    public Compte getCompte(String id) {
        SQLiteDatabase db = this.getReadableDatabase();
        // les colonnes retournées par la requête:
        String[] colonnes = {
                CompteHelper._ID,
                CompteHelper.COMPTE_NOM,
                CompteHelper.COMPTE_PRENOM,
                CompteHelper.COMPTE_PHOTO,
                CompteHelper.COMPTE_TYPE_COMPTE
        };
        String selection = CompteHelper._ID + " = ?";
        String[] selectionArgs = {String.valueOf(id)};
        Cursor cursor = db.query(CompteHelper.NOM_TABLE, colonnes, selection, selectionArgs, null, null, null, null);
        Compte compte = null;
        if (cursor != null) {
            cursor.moveToFirst();
            compte = new Compte(cursor.getString(0),
                    cursor.getString(1),
                    cursor.getString(2),
                    cursor.getBlob(3),
                    cursor.getInt(4));
            cursor.close();
        }
        return compte;
    }

    public void ajouterVisite(Visite visite) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(VisiteHelper._ID, visite.getId());
        values.put(VisiteHelper.STAGE_ID, visite.getStage().getId());
        values.put(VisiteHelper.VISITE_DATE, visite.getJournee().toString());
        values.put(VisiteHelper.VISITE_DUREE, visite.getDuree());
        db.insert(VisiteHelper.NOM_TABLE, null, values);
    }

    public void modifierVisite(Visite visite) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(VisiteHelper._ID, visite.getId());
        values.put(VisiteHelper.STAGE_ID, visite.getStage().getId());
        values.put(VisiteHelper.VISITE_DATE, visite.getJournee().toString());
        values.put(VisiteHelper.VISITE_DUREE, visite.getDuree());
        String whereClause = VisiteHelper._ID + " = ?";
        String[] whereArgs = {visite.getId()};
        db.update(VisiteHelper.NOM_TABLE, values, whereClause, whereArgs);
    }

    public ArrayList<Visite> getVisites() {
        ArrayList<Visite> visites = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        // les colonnes retournées par la requête:
        String[] colonnes = {
                VisiteHelper._ID,
                VisiteHelper.STAGE_ID,
                VisiteHelper.VISITE_DATE,
                VisiteHelper.VISITE_DUREE
        };
        Cursor cursor = db.query(VisiteHelper.NOM_TABLE, colonnes, null, null, null, null, null, null);
        if (cursor != null) {
            if(cursor.getCount() < 1) {
                return visites;
            }
            cursor.moveToFirst();
            do {
                Stage stage = getStage(cursor.getString(1), db);
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm");
                LocalDateTime dateTime = LocalDateTime.parse(cursor.getString(2), formatter);
                Visite visite = new Visite(cursor.getString(0), stage.getStagePoidsPlume(), cursor.getInt(3), dateTime);
                visites.add(visite);
            } while (cursor.moveToNext());
            cursor.close();
        }
        return visites;
    }

    /**
     * Recevoir la liste d'etudiants sans stage
     *
     * @return liste de compte
     */
    public ArrayList<Compte> getEtudiantsSansStage() {
        ArrayList<Compte> comptes = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase(); // On veut lire dans la BD
        String query = String.format("SELECT t1.%s, %s, %s, %s, %s FROM %s t1 LEFT JOIN %s t2 ON t2.%s = t1.%s WHERE t2.%s IS NULL AND t1.%s = 2",
                CompteHelper._ID, CompteHelper.COMPTE_NOM, CompteHelper.COMPTE_PRENOM, CompteHelper.COMPTE_PHOTO,
                CompteHelper.COMPTE_TYPE_COMPTE, CompteHelper.NOM_TABLE, StageHelper.NOM_TABLE,
                StageHelper.STAGE_ETUDIANT_ID, CompteHelper._ID, StageHelper.STAGE_ETUDIANT_ID, CompteHelper.COMPTE_TYPE_COMPTE);
        Cursor cursor = db.rawQuery(query, null);
        if (cursor != null && cursor.getCount() > 0) {
            cursor.moveToFirst();
            do {
                comptes.add(new Compte(cursor.getString(0),
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
     * Recevoir la liste des entreprises dans la base de données
     *
     * @return la liste d'entreprises
     */
    public ArrayList<Entreprise> getEntreprises() {
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
        String orderBy = EntrepriseHelper.ENTREPRISE_NOM + " ASC";
        Cursor cursor = db.query(EntrepriseHelper.NOM_TABLE, colonnes, null, null, null, null, orderBy, null);
        if (cursor != null) {
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
     *
     * @param id id de l'entreprise
     * @return l'entreprise correspondant à l'id
     */
    public Entreprise getEntreprise(String id) {
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
        String selection = EntrepriseHelper._ID + " = ?";
        String[] selectionArgs = {id};
        Cursor cursor = db.query(EntrepriseHelper.NOM_TABLE, colonnes, selection, selectionArgs, null, null, null, null);
        Entreprise entreprise = null;
        if (cursor != null) {
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

    private Stage getStage(String id, SQLiteDatabase db) {
        Stage stage;
        // les colonnes retournées par la requête:
        String[] colonnes = {
                StageHelper._ID,
                StageHelper.STAGE_ANNEE_SCOLAIRE,
                StageHelper.STAGE_DRAPEAU,
                StageHelper.STAGE_ETUDIANT_ID,
                StageHelper.STAGE_PROFESSEUR_ID,
                StageHelper.STAGE_ENTREPRISE_ID,
                StageHelper.STAGE_COMMENTAIRE,
                StageHelper.STAGE_JOURNEES,
                StageHelper.STAGE_HEURE_DEBUT,
                StageHelper.STAGE_HEURE_FIN,
                StageHelper.STAGE_HEURE_PAUSE,
                StageHelper.STAGE_HEURE_FIN_PAUSE,
                StageHelper.STAGE_DUREE_VISITE,
                StageHelper.STAGE_DISPONIBILITE_TUTEUR
        };
        Cursor cursor = db.query(StageHelper.NOM_TABLE, colonnes, null, null, null, null, null, null);
        if (cursor != null) {
            if(cursor.getCount() == 0) {
                cursor.close();
                return null;
            }
            cursor.moveToFirst();
            //Crée un nouveau stage
            stage = new Stage(cursor.getString(0), cursor.getString(1), Priorite.getPriorite(cursor.getInt(2)));
            //Verifie si le professeur a ete cree et le cree s'il ne l'a pas ete
            stage.addProfesseur(cursor.getString(4));
            //Cree l'etudiant associe au stage
            stage.addEtudiant(getCompte(cursor.getString(3)));
            //Verifie que l'entreprise n'a pas deja ete creefinal
            Entreprise entreprise = getEntreprise(cursor.getString(5));
            stage.addEntreprise(entreprise);
            stage.setCommentaire(cursor.getString(6));
            stage.setJournees((byte) cursor.getInt(7));
            stage.setheureDebut(LocalTime.ofSecondOfDay(cursor.getInt(8)));
            stage.setHeureFinStage(LocalTime.ofSecondOfDay(cursor.getInt(9)));
            stage.setHeureDiner(LocalTime.ofSecondOfDay(cursor.getInt(10)));
            stage.setHeureFinDiner(LocalTime.ofSecondOfDay(cursor.getInt(11)));
            stage.setDureeVisite(cursor.getInt(12));
            stage.setDisponibiliteTuteur(cursor.getInt(13));
            cursor.close();
            return stage;
        }
        return null;
    }
    /**
     * Recevoir les stages de la base de données
     *
     * @return une liste de stages
     */
    public ArrayList<Stage> getStages(String professeurId) {
        ArrayList<Stage> stages = new ArrayList<>();
        ArrayList<Entreprise> entreprisesConnu = new ArrayList<>();

        String professeur = null;
        SQLiteDatabase db = this.getReadableDatabase(); // On veut lire dans la BD
        // les colonnes retournées par la requête:
        String[] colonnes = {
                StageHelper._ID,
                StageHelper.STAGE_ANNEE_SCOLAIRE,
                StageHelper.STAGE_DRAPEAU,
                StageHelper.STAGE_ETUDIANT_ID,
                StageHelper.STAGE_PROFESSEUR_ID,
                StageHelper.STAGE_ENTREPRISE_ID,
                StageHelper.STAGE_COMMENTAIRE,
                StageHelper.STAGE_JOURNEES,
                StageHelper.STAGE_HEURE_DEBUT,
                StageHelper.STAGE_HEURE_FIN,
                StageHelper.STAGE_HEURE_PAUSE,
                StageHelper.STAGE_HEURE_FIN_PAUSE,
                StageHelper.STAGE_DUREE_VISITE,
                StageHelper.STAGE_DISPONIBILITE_TUTEUR,
                StageHelper.STAGE_ANNEE_SCOLAIRE
        };
        String selection = StageHelper.STAGE_PROFESSEUR_ID + " = ?";
        String[] selectionArgs = {professeurId};
        Cursor cursor = db.query(StageHelper.NOM_TABLE, colonnes, selection, selectionArgs, null, null, null, null);
        if (cursor != null) {
            if(cursor.getCount() == 0) {
                cursor.close();
                return stages;
            }
            cursor.moveToFirst();
            do {
                //Crée un nouveau stage
                Stage stage = new Stage(cursor.getString(0), cursor.getString(1), Priorite.getPriorite(cursor.getInt(2)));
                //Verifie si le professeur a ete cree et le cree s'il ne l'a pas ete
                if (professeur == null) {
                    professeur = cursor.getString(4);
                }
                stage.addProfesseur(professeur);
                //Cree l'etudiant associe au stage
                stage.addEtudiant(getCompte(cursor.getString(3)));
                //Verifie que l'entreprise n'a pas deja ete creefinal
                for (Entreprise entreprise : entreprisesConnu) {
                    if (entreprise.getId().equals(cursor.getString(5))) {
                        stage.addEntreprise(entreprise);
                        break;
                    }
                }
                if (stage.getEntreprise() == null) {
                    Entreprise entreprise = getEntreprise(cursor.getString(5));
                    stage.addEntreprise(entreprise);
                    entreprisesConnu.add(entreprise);
                }
                stage.setCommentaire(cursor.getString(6));
                stage.setJournees((byte) cursor.getInt(7));
                stage.setheureDebut(LocalTime.ofSecondOfDay(cursor.getInt(8)));
                stage.setHeureFinStage(LocalTime.ofSecondOfDay(cursor.getInt(9)));
                stage.setHeureDiner(LocalTime.ofSecondOfDay(cursor.getInt(10)));
                stage.setHeureFinDiner(LocalTime.ofSecondOfDay(cursor.getInt(11)));
                stage.setDureeVisite(cursor.getInt(12));
                stage.setDisponibiliteTuteur(cursor.getInt(13));
                stage.setAnneeScolaire(cursor.getString(14));
                stages.add(stage);
            } while (cursor.moveToNext());
            cursor.close();
        }
        return stages;
    }

    /**
     * Modifier un stage
     *
     * @param stage le stage a modifier
     */
    public void modifierStage(Stage stage) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(StageHelper.STAGE_ENTREPRISE_ID, stage.getEntreprise().getId());
        values.put(StageHelper.STAGE_ETUDIANT_ID, stage.getEtudiant().getId());
        values.put(StageHelper.STAGE_PROFESSEUR_ID, stage.getProfesseur());
        values.put(StageHelper.STAGE_ANNEE_SCOLAIRE, Utils.getAnneeScolaire());
        values.put(StageHelper.STAGE_DRAPEAU, stage.getPriorite().getValeur());
        values.put(StageHelper.STAGE_COMMENTAIRE, stage.getCommentaire());
        values.put(StageHelper.STAGE_DISPONIBILITE_TUTEUR, stage.getDisponibiliteTuteur());
        values.put(StageHelper.STAGE_DUREE_VISITE, stage.getDureeVisite());
        values.put(StageHelper.STAGE_JOURNEES, stage.getJournees());
        values.put(StageHelper.STAGE_HEURE_FIN, stage.getHeureFinStage().toSecondOfDay());
        values.put(StageHelper.STAGE_HEURE_FIN_PAUSE, stage.getHeureFinPause().toSecondOfDay());
        values.put(StageHelper.STAGE_HEURE_DEBUT, stage.getHeureDebut().toSecondOfDay());
        values.put(StageHelper.STAGE_HEURE_PAUSE, stage.getHeurePause().toSecondOfDay());
        values.put(StageHelper.STAGE_ENTREPRISE_ID, stage.getEntreprise().getId());
        values.put(StageHelper.STAGE_DRAPEAU, stage.getPriorite().getValeur());
        String whereClause = StageHelper._ID + " = ?";
        String[] whereArgs = {stage.getId()};
        db.update(StageHelper.NOM_TABLE, values, whereClause, whereArgs);
    }

    /**
     * Changer la photo d'un compte
     *
     * @param compte le compte a modifier
     */
    public void changerPhotoCompte(Compte compte) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(CompteHelper.COMPTE_PHOTO, compte.getPhoto());
        String whereClause = CompteHelper._ID + " = ?";
        String[] whereArgs = {String.valueOf(compte.getId())};
        db.update(CompteHelper.NOM_TABLE, values, whereClause, whereArgs);
    }

    /**
     * Supprime un stage
     *
     */
    public void deleteStage(String id) {
        SQLiteDatabase db = this.getWritableDatabase();
        String whereClause = StageHelper._ID + " = ?";
        String[] whereArgs = {id};
        db.delete(StageHelper.NOM_TABLE, whereClause, whereArgs);
    }

    private boolean entrepriseExists(String id, SQLiteDatabase db){
        String selection = EntrepriseHelper._ID + " = ?";
        String[] selectionArgs = {id};
        Cursor cursor = db.query(EntrepriseHelper.NOM_TABLE, null, selection, selectionArgs, null, null, null, null);
        boolean count = cursor.getCount() > 0;
        cursor.close();
        return count;
    }

    private boolean compteExists(String id, SQLiteDatabase db){
        String selection = CompteHelper._ID + " = ?";
        String[] selectionArgs = {id};
        Cursor cursor = db.query(CompteHelper.NOM_TABLE, null, selection, selectionArgs, null, null, null, null);
        boolean count = cursor.getCount() > 0;
        cursor.close();
        return count;
    }

    private boolean stageExists(String id, SQLiteDatabase db){
        String selection = StageHelper._ID + " = ?";
        String[] selectionArgs = {id};
        Cursor cursor = db.query(StageHelper.NOM_TABLE, null, selection, selectionArgs, null, null, null, null);
        boolean count = cursor.getCount() > 0;
        cursor.close();
        return count;
    }

    public boolean stageExists(String id) {
        SQLiteDatabase db = this.getReadableDatabase();
        return stageExists(id, db);
    }
    /**
     * Cree un nouveau stage
     *
     * @param stage le stage a creer
     */
    public void createStage(Stage stage) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(StageHelper._ID, stage.getId());
        values.put(StageHelper.STAGE_ENTREPRISE_ID, stage.getEntreprise().getId());
        values.put(StageHelper.STAGE_ETUDIANT_ID, stage.getEtudiant().getId());
        values.put(StageHelper.STAGE_PROFESSEUR_ID, stage.getProfesseur());
        values.put(StageHelper.STAGE_ANNEE_SCOLAIRE, Utils.getAnneeScolaire());
        values.put(StageHelper.STAGE_DRAPEAU, stage.getPriorite().getValeur());
        values.put(StageHelper.STAGE_COMMENTAIRE, stage.getCommentaire());
        values.put(StageHelper.STAGE_DISPONIBILITE_TUTEUR, stage.getDisponibiliteTuteur());
        values.put(StageHelper.STAGE_DUREE_VISITE, stage.getDureeVisite());
        values.put(StageHelper.STAGE_JOURNEES, stage.getJournees());
        values.put(StageHelper.STAGE_HEURE_FIN, stage.getHeureFinStage().toSecondOfDay());
        values.put(StageHelper.STAGE_HEURE_FIN_PAUSE, stage.getHeureFinPause().toSecondOfDay());
        values.put(StageHelper.STAGE_HEURE_DEBUT, stage.getHeureDebut().toSecondOfDay());
        values.put(StageHelper.STAGE_HEURE_PAUSE, stage.getHeurePause().toSecondOfDay());
        db.insert(StageHelper.NOM_TABLE, null, values);
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
        public static final String STAGE_COMMENTAIRE = "commentaire";
        public static final String STAGE_JOURNEES = "journees";
        public static final String STAGE_HEURE_DEBUT = "heure_debut";
        public static final String STAGE_HEURE_FIN = "heure_fin";
        public static final String STAGE_HEURE_PAUSE = "heure_debut_pause";
        public static final String STAGE_HEURE_FIN_PAUSE = "heureFinPause";
        public static final String STAGE_DUREE_VISITE = "duree_visite";
        public static final String STAGE_DISPONIBILITE_TUTEUR = "disponibilite_tuteur";
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
}
