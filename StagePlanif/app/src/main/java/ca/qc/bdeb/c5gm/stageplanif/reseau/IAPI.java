package ca.qc.bdeb.c5gm.stageplanif.reseau;

import java.util.HashMap;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.Path;

/**
 * Interface permettant de communiquer avec l'API de la BD externe
 */
public interface IAPI {
    @POST("/auth/connexion")
    @Headers({
            "Content-Type:application/json",
            "Authorization:Token"
    })
    /**
     * Se connecter a la BD externe
     */
    Call<ResponseBody> connecter(@Body HashMap<String, Object> loginData);

    /**
     * Methode qui permet de se deconnecter de la BD externe
     * @param token le token de connexion a l'API
     */
    @POST("/auth/deconnexion")
    Call<ResponseBody> deconnecter(@Header("Authorization") String token);

    /**
     * Tester la connexion a l'API
     * @param token token de connexion
     * @param userId id de l'utilisateur
     */
    @POST("/auth/testerconnexion")
    Call<ResponseBody> testerConnexion(@Header("Authorization") String token, @Body HashMap<String, Object> userId);

    /**
     * Demander les donnees des entreprises a l'API
     * @param token token de connexion
     */
    @GET("/entreprise")
    Call<ResponseBody> getEntreprises(@Header("Authorization") String token);

    /**
     * Demander les donnees des stages a l'API
     * @param token le token de connexion
     */
    @GET("/stage")
    Call<ResponseBody> getStages(@Header("Authorization") String token);

    /**
     * Demander les donnees d'un stage particulier a l'API
     * @param token token de connexion
     * @param idStage ID du stage a chercher
     */
    @GET("/stage/{idStage}")
    Call<ResponseBody> getStage(@Header("Authorization") String token, @Path("idStage") String idStage);

    /**
     * Demander les comptes des etudiants actifs a l'API
     * @param token token de connexion
     */
    @GET("/compte/getcomptesetudiantsactifs")
    Call<ResponseBody> getComptesEleves(@Header("Authorization") String token);

    /**
     * Ajouter un stage a la BD externe
     * @param token token de connection
     * @param data Les donnees a mettre dans la BD
     */
    @POST("/stage")
    Call<ResponseBody> ajouterStage(@Header("Authorization") String token, @Body HashMap<String, Object> data);

    /**
     * Supprimer un stage dans la BD externe
     * @param token token de connexion
     * @param idStage id du stage a supprimer
     */
    @DELETE("/stage/{idStage}")
    Call<ResponseBody> supprStage(@Header("Authorization") String token, @Path("idStage") String idStage);
}
