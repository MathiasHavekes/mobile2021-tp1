package ca.qc.bdeb.c5gm.stageplanif;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;

import ca.qc.bdeb.c5gm.stageplanif.data.Stockage;
import ca.qc.bdeb.c5gm.stageplanif.data.TypeCompte;
import ca.qc.bdeb.c5gm.stageplanif.reseau.ConnexionBD;
import ca.qc.bdeb.c5gm.stageplanif.reseau.IAPI;
import ca.qc.bdeb.c5gm.stageplanif.reseau.APIClient;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ConnexionActivity extends AppCompatActivity {
    private EditText loginEditText;
    private EditText passwordEditText;
    private IAPI client;
    private Button btnLogin;
    private ProgressBar loadingProgressBar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_connexion);
        loginEditText = findViewById(R.id.login);
        passwordEditText = findViewById(R.id.password);
        btnLogin = findViewById(R.id.loginBtn);
        loadingProgressBar = findViewById(R.id.loading);
        Utils.context = getApplicationContext();
        btnLogin.setEnabled(false);

        client = APIClient.getRetrofit().create(IAPI.class);

        TextWatcher afterTextChangedListener = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // ignore
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // ignore
            }

            @Override
            public void afterTextChanged(Editable s) {
                loginDataChanged(loginEditText.getText().toString(),
                        passwordEditText.getText().toString());
            }
        };
        loginEditText.addTextChangedListener(afterTextChangedListener);
        passwordEditText.addTextChangedListener(afterTextChangedListener);

    }

    public void connecter(View view) {

        loadingProgressBar.setVisibility(View.VISIBLE);
        HashMap<String, Object> loginData = new HashMap<>();
        if (!loginEditText.getText().toString().isEmpty()){
            loginData.put("email", loginEditText.getText().toString() );
        }
        if (!passwordEditText.getText().toString().isEmpty()){
            loginData.put("mot_de_passe", passwordEditText.getText().toString() );
        }

        Call<ResponseBody> call = client.connecter(loginData);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if(response.code() == 200) {
                    try {
                        JSONObject rep = new JSONObject(response.body().string());
                        ConnectUtils.authToken = rep.getString("access_token");
                        ConnectUtils.authId = rep.getString("id");
                        ConnexionBD.updateCompte(rep);
                        Toast.makeText(getApplicationContext(), "Connexion réussie!!", Toast.LENGTH_LONG).show();
                        finish();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    } finally {
                        loadingProgressBar.setVisibility(View.GONE);
                    }
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.e("TAG", t.toString());
                loadingProgressBar.setVisibility(View.GONE);
                Toast.makeText(getApplicationContext(), "Echec de connexion!!", Toast.LENGTH_LONG).show();
            }
        });
    }

    public void loginDataChanged(String username, String password) {
        if (!isUserNameValid(username)) {
            loginEditText.setError(getString(R.string.invalid_username));
        } else if (!isPasswordValid(password)) {
            passwordEditText.setError(getString(R.string.invalid_password));
        } else {
            loginEditText.setError(null);
            passwordEditText.setError(null);
            btnLogin.setEnabled(true);
        }
    }
    // A placeholder username validation check
    private boolean isUserNameValid(String username) {
        if (username == null) {
            return false;
        }
        if (username.contains("@")) {
            return Patterns.EMAIL_ADDRESS.matcher(username).matches();
        } else {
            return !username.trim().isEmpty();
        }
    }

    // A placeholder password validation check
    private boolean isPasswordValid(String password) {
        return password != null && password.trim().length() > 5;
    }
}
