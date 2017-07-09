package com.example.historialmedico;


import android.content.Intent;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Toast;

import com.example.historialmedico.dto.UsuarioDTO;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.gson.Gson;
import com.squareup.okhttp.Response;

import org.json.JSONException;
import org.json.JSONObject;

public class LoginActivity extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener {

    private GoogleApiClient googleApiClient;

    public String host = "http://localhost:8080";

    private SignInButton signInButton;

    static{
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
    }

    public static final int SIGN_IN_CODE = 777;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();

        googleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();

        signInButton = (SignInButton) findViewById(R.id.sign_in_button);

        signInButton.setSize(SignInButton.SIZE_WIDE);

        signInButton.setColorScheme(SignInButton.COLOR_DARK);

        signInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = Auth.GoogleSignInApi.getSignInIntent(googleApiClient);
                startActivityForResult(intent, SIGN_IN_CODE);
            }
        });
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == SIGN_IN_CODE) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            handleSignInResult(result);
        }
    }

    private void handleSignInResult(GoogleSignInResult result) {
        if (result.isSuccess()) {
            goMainScreen(result);
        } else {
            Toast.makeText(this, "No se pudo iniciar la sesion", Toast.LENGTH_SHORT).show();
        }
    }

    private void goMainScreen(GoogleSignInResult result) {

        GoogleSignInAccount account = result.getSignInAccount();

        MyHttpClient myHttpClient = new MyHttpClient();
        String jsonString = null;

        try {
            jsonString =  myHttpClient.doPostRequest("usuario/validarUsuario/", account.getEmail());
        } catch (Exception e) {
            e.printStackTrace();
        }
        if(jsonString == null) {
            Toast.makeText(this, "Ocurrio un problema al verificar el usuario", Toast.LENGTH_SHORT).show();
            return;
        }else {
            Gson gson = new Gson();
            UsuarioDTO usuarioDTO  = gson.fromJson(jsonString, UsuarioDTO.class);

            if (usuarioDTO.getValido() == Boolean.FALSE) {
                Toast.makeText(this, "Usuario invalido", Toast.LENGTH_SHORT).show();
                return;
            }

            Intent intent = new Intent(this, MainActivity.class);
            intent.putExtra("idUsuario", usuarioDTO.getId());
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        }

    }
}

