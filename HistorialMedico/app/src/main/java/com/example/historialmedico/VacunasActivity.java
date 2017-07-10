package com.example.historialmedico;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import com.example.historialmedico.dto.VacunaDTO;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.lang.reflect.Type;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class VacunasActivity extends AppCompatActivity {

    private ProgressBar progressBar;
    private ListView listView;
    private String idHijo;
    Button vacunas;
    ArrayList<HashMap<String, String>> contactList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vacunas);

        contactList = new ArrayList<>();

        listView = (ListView) findViewById(R.id.list);

        this.idHijo = getIntent().getExtras().getString("idHijo");
        //this.order = getIntent().getExtras().getString("order");

        new VacunasActivity.obtenerVacunas().execute();
    }

    private class obtenerVacunas extends AsyncTask<Void, Void, Void> {

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
                response = myHttpClient.
                        doGetRequest("vacuna/obtener/" + VacunasActivity.this.idHijo + "/aplicada");
            } catch (IOException e) {
                e.printStackTrace();
            }

            if (response != null) {
                Gson gson = new Gson();

                Type listType = new TypeToken<ArrayList<VacunaDTO>>() {}.getType();

                List<VacunaDTO> vacunaDTOList = new Gson().fromJson(response, listType);

                for (VacunaDTO vacunaDTO : vacunaDTOList) {
                    HashMap<String, String> contact = new HashMap<>();

                    contact.put("name", vacunaDTO.getNombre());
                    contact.put("email", vacunaDTO.getAplicada());
                    contact.put("mobile", "Dia Aplicacion: "+vacunaDTO.getFecha());

                    contactList.add(contact);
                }
            } else {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getApplicationContext(),
                                "No se encontraron vacunas",
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
                    VacunasActivity.this, contactList,
                    R.layout.list_item, new String[]{"name", "email",
                    "mobile"}, new int[]{R.id.name,
                    R.id.email, R.id.mobile});

            listView.setAdapter(adapter);
        }

    }





}
