package com.example.historialmedico;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import com.example.historialmedico.dto.VacunaDTO;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class NotificacionesActivity extends AppCompatActivity {

    private ListView listView;

    ArrayList<HashMap<String, String>> contactList;

    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notificaciones);

        contactList = new ArrayList<>();
        listView = (ListView) findViewById(R.id.list);

        new getVacunas().execute();
    }

    private class getVacunas extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            progressBar = (ProgressBar)findViewById(R.id.progressBar1);
            progressBar.setVisibility(View.VISIBLE);

        }

        @Override
        protected Void doInBackground(Void... arg0) {
            MyHttpClient myHttpClient = new MyHttpClient();

            String response = null;

            try {
                response = myHttpClient.doGetRequest("vacuna/");
            } catch (IOException e) {
                e.printStackTrace();
            }

            if (response != null) {
                Gson gson = new Gson();

                Type listType = new TypeToken<ArrayList<VacunaDTO>>() {}.getType();

                List<VacunaDTO> vacunaDTOList = new Gson().fromJson(response, listType);

                for (VacunaDTO vacunaDTO : vacunaDTOList) {
                        HashMap<String, String> contact = new HashMap<>();

                        // adding each child node to HashMap key => value
                        contact.put("name", vacunaDTO.getNombre());
                        contact.put("email", vacunaDTO.getAplicada());
                        contact.put("mobile", "Fecha aplicacion: " + vacunaDTO.getFecha());

                        // adding contact to contact list
                        contactList.add(contact);
                    }
            } else {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getApplicationContext(),
                                "No se encontraron registros..",
                                Toast.LENGTH_LONG)
                                .show();
                    }
                });

            }

            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);

            progressBar.setVisibility(View.INVISIBLE);

            /**
             * Updating parsed JSON data into ListView
             * */
            ListAdapter adapter = new SimpleAdapter(
                    NotificacionesActivity.this, contactList,
                    R.layout.list_item, new String[]{"name", "email",
                    "mobile"}, new int[]{R.id.name,
                    R.id.email, R.id.mobile});

            listView.setAdapter(adapter);
        }

    }
}
