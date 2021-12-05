package ca.qc.bdeb.c5gm.stageplanif.reseau;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import ca.qc.bdeb.c5gm.stageplanif.ConnectUtils;
import ca.qc.bdeb.c5gm.stageplanif.MainActivity;
import ca.qc.bdeb.c5gm.stageplanif.Utils;
import ca.qc.bdeb.c5gm.stageplanif.comparateurs.StageNomComparateur;
import ca.qc.bdeb.c5gm.stageplanif.comparateurs.StagePrenomComparateur;
import ca.qc.bdeb.c5gm.stageplanif.comparateurs.StagePrioriteComparateur;
import ca.qc.bdeb.c5gm.stageplanif.data.Priorite;
import ca.qc.bdeb.c5gm.stageplanif.data.Stage;
import ca.qc.bdeb.c5gm.stageplanif.data.Stockage;
import ca.qc.bdeb.c5gm.stageplanif.data.TypeCompte;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ConnexionBD {
    private static IAPI client = APIClient.getRetrofit().create(IAPI.class);
    private static JSONArray entreprises;

    public static void updateEntreprises() {
        client.getEntreprises(ConnectUtils.authToken).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                Log.i("justine_tag", response.toString());
                try {
                    if (response.code() == 200) {
                        Stockage dbHelper = Stockage.getInstance(Utils.context);
                        entreprises = new JSONArray(response.body().string());
                        for (int i = 0; i < entreprises.length(); i++) {
                            JSONObject entreprise = entreprises.getJSONObject(i);
                            String id = entreprise.get("id").toString();
                            String nom = entreprise.getString("nom");
                            String adresse = entreprise.getString("adresse");
                            String ville = entreprise.getString("ville");
                            String province = entreprise.getString("province");
                            String codePostal = entreprise.getString("codePostal");
                            dbHelper.ajouterOumodifierEntreprise(id, nom, adresse, ville, province, codePostal);
                        }
                    }
                } catch (JSONException | IOException e) {
                    e.printStackTrace();
                }
            }


            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.e("TAG", t.toString());
            }
        });
    }

    public static void updateComptesEleves() {
        client.getComptesEleves(ConnectUtils.authToken).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                Log.i("justine_tag", response.toString());
                try {
                    if (response.code() == 200) {
                        JSONArray comptes = new JSONArray(response.body().string());
                        for (int i = 0; i < comptes.length(); i++) {
                            JSONObject compte = comptes.getJSONObject(i);
                            updateCompte(compte);
                        }
                    }
                } catch (JSONException | IOException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.e("TAG", t.toString());
            }
        });
    }

    public static void updateCompte(JSONObject compte) {
        try {
            Stockage dbHelper = Stockage.getInstance(Utils.context);
            String id = compte.getString("id");
            String nom = compte.getString("nom");
            String prenom = compte.getString("prenom");
            String email = compte.getString("email");
            String typeCompte = compte.getString("type_compte");
            int typeCompteId = TypeCompte.valueOf(typeCompte).getValeur();
            dbHelper.ajouterOuModifierCompte(id, nom, prenom, email, typeCompteId);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public static void updateStages() {
        client.getStages(ConnectUtils.authToken).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                Log.i("justine_tag", response.toString());
                try {
                    if (response.code() == 200) {
                        Stockage dbHelper = Stockage.getInstance(Utils.context);
                        JSONArray stages = new JSONArray(response.body().string());
                        for (int i = 0; i < stages.length(); i++) {
                            JSONObject stage = stages.getJSONObject(i);
                            String id = stage.getString("id");
                            String deletedAt = stage.getString("deletedAt");
                            String anneeScolaire = stage.getString("anneeScolaire");
                            JSONObject jsonEtudiant = stage.getJSONObject("etudiant");
                            String etudiantId = jsonEtudiant.getString("id");
                            JSONObject professeur = stage.getJSONObject("professeur");
                            String professeurId = professeur.getString("id");
                            JSONObject entreprise = stage.getJSONObject("entreprise");
                            String entrepriseId = entreprise.getString("id");
                            String prioriteStr = stage.getString("priorite");
                            int priorite = Priorite.valueOf(prioriteStr).getValeur();
                            String commentaire = stage.getString("commentaire");
                            if(commentaire == "null") {
                                commentaire = null;
                            }
                            String heureDebutStr = stage.getString("heureDebut");
                            String heureFinStr = stage.getString("heureFin");
                            String heureDebutPauseStr = stage.getString("heureDebutPause");
                            String heureFinPauseStr = stage.getString("heureFinPause");
                            int heureDebut = -1;
                            int heureFin = -1;
                            int heureDebutPause = -1;
                            int heureFinPause = -1;
                            try{
                                if(isNumeric(heureDebutStr)) {
                                     heureDebut = Integer.parseInt(heureDebutStr);
                                }
                                if(isNumeric(heureFinStr)) {
                                    heureFin = Integer.parseInt(heureFinStr);
                                }
                                if(isNumeric(heureDebutPauseStr)) {
                                    heureDebutPause = Integer.parseInt(heureDebutPauseStr);
                                }
                                if(isNumeric(heureFinPauseStr)) {
                                    heureFinPause = Integer.parseInt(heureFinPauseStr);
                                }
                            }
                            catch (NumberFormatException ex){
                                ex.printStackTrace();
                            }

                            if(deletedAt == "null") {
                                dbHelper.ajouterouModifierStage(id, anneeScolaire,entrepriseId, etudiantId,
                                        professeurId, commentaire, heureDebut, heureFin, priorite,
                                        heureDebutPause, heureFinPause);
                            } else {
                                if(dbHelper.stageExists(id)) {
                                    dbHelper.deleteStage(id);
                                }
                            }
                        }
                        ArrayList<Stage> stagesArray = dbHelper.getStages();
                        MainActivity.listeStages.clear();
                        for (Stage stage: stagesArray) {
                            MainActivity.listeStages.add(stage);
                        }
                        MainActivity.stageAdapter.filtrerListeStages(7);
                        MainActivity.stageAdapter.trierListeStages(new StagePrioriteComparateur(), new StageNomComparateur(), new StagePrenomComparateur());
                        MainActivity.stageAdapter.notifyDataSetChanged();
                    }
                } catch (JSONException | IOException e) {
                    e.printStackTrace();
                }
            }

            private boolean isNumeric(String str){
                return str != null && str.matches("[0-9.]+");
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.e("TAG", t.toString());
            }
        });
    }

    public static void ajouterOuModifierStage(Stage stage) {
        HashMap<String, Object> stageInfo = new HashMap<>();
        stageInfo.put("id", stage.getId());
        stageInfo.put("annee", stage.getAnneeScolaire());
        stageInfo.put("id_entreprise", stage.getEntreprise().getId());
        stageInfo.put("id_etudiant", stage.getEtudiant().getId());
        stageInfo.put("id_professeur", stage.getProfesseur());
        stageInfo.put("commentaire", stage.getCommentaire());
        stageInfo.put("heureDebut", String.valueOf(stage.getHeureDebut().toSecondOfDay()));
        stageInfo.put("heureFin", String.valueOf(stage.getHeureFinStage().toSecondOfDay()));
        stageInfo.put("priorite", stage.getPriorite().toString());
        stageInfo.put("heureDebutPause", String.valueOf(stage.getHeurePause().toSecondOfDay()));
        stageInfo.put("heureFinPause", String.valueOf(stage.getHeureFinPause().toSecondOfDay()));
        client.ajouterStage(ConnectUtils.authToken, stageInfo).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                Log.i("justine_tag", response.toString());
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.e("TAG", t.toString());
            }
        });
    }

    public static void supprimerStage(String id) {
        client.supprStage(ConnectUtils.authToken, id).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                Log.i("justine_tag", response.toString());
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.e("TAG", t.toString());
            }
        });
    }

}
