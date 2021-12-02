package ca.qc.bdeb.c5gm.stageplanif.reseau;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import ca.qc.bdeb.c5gm.stageplanif.ConnectUtils;
import ca.qc.bdeb.c5gm.stageplanif.Utils;
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
                            Boolean entrepriseExists = dbHelper.entrepriseExists(id);
                            if (entrepriseExists) {
                                dbHelper.modifierEntreprise(id, nom, adresse, ville, province, codePostal);
                            } else {
                                dbHelper.ajouterEntreprise(id, nom, adresse, ville, province, codePostal);
                            }
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
            Boolean compteExists = dbHelper.compteExists(id);
            if (compteExists) {
                dbHelper.modifierCompte(id, nom, prenom, email, typeCompteId);
            } else {
                dbHelper.ajouterCompte(id, nom, prenom, email, typeCompteId);
            }
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
                            String idEtudiant = jsonEtudiant.getString("id");
                            JSONObject professeur = stage.getJSONObject("professeur");
                            String professeurId = professeur.getString("id");
                            JSONObject entreprise = stage.getJSONObject("entreprise");
                            String entrepriseId = entreprise.getString("id");
                            String priorite = stage.getString("priorite");
                            String commentaire = stage.getString("commentaire");
                            String heureDebut = stage.getString("heureDebut");
                            String heureFin = stage.getString("heureFin");
                            String heureDebutPause = stage.getString("heureDebutPause");
                            String heureFinPause = stage.getString("heureFinPause");
                            Boolean stageExists = dbHelper.stageExists(id);
                            if (stageExists) {
                            } else {
                            }
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

}
