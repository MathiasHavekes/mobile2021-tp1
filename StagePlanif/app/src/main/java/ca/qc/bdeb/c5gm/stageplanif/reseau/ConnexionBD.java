package ca.qc.bdeb.c5gm.stageplanif.reseau;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

import ca.qc.bdeb.c5gm.stageplanif.ConnectUtils;
import ca.qc.bdeb.c5gm.stageplanif.data.Entreprise;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ConnexionBD {
    private static IAPI client = APIClient.getRetrofit().create(IAPI.class);
    private static JSONArray entreprises;
    private static ArrayList<Entreprise> entreprisesListe;

    public static ArrayList<Entreprise> getEntreprises() {
        client.getEntreprises(ConnectUtils.authToken).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                Log.i("justine_tag", response.toString());
                try {
                    if (response.code() == 200) {
                        entreprises = new JSONArray(response.body().string());
                        for (int i = 0; i < entreprises.length(); i++) {
                            JSONObject entreprise = entreprises.getJSONObject(i);
                            entreprisesListe.add(new Entreprise(entreprise.getString("id"),
                                    entreprise.getString("nom"), entreprise.getString("adresse"),
                                    entreprise.getString("")))
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
        return entreprisesListe;
    }
}
