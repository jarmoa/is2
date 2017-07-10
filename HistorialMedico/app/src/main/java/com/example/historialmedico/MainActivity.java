package com.example.historialmedico;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.app.NotificationCompat;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import com.example.historialmedico.dto.HijoDTO;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.lang.reflect.Type;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    public int idUsuario;

    private ProgressBar progressBar;

    ArrayList<HashMap<String, String>> contactList;

    private ListView listView;

    NotificationCompat.Builder nCompatBuilder;

    public static final int notificationId = 001;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        this.idUsuario = getIntent().getExtras().getInt("idUsuario");

        contactList = new ArrayList<>();

        listView = (ListView) findViewById(R.id.list);

        new obtenerHijos().execute();

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {

                Intent vacunas = new Intent(MainActivity.this, VacunasActivity.class);

                //vacunas.putExtra("idHijo", ((HijoDTO) listView.getAdapter().getItem(position)).getId());
                vacunas.putExtra("idHijo", "1");
                startActivity(vacunas);

            }

        });

        nCompatBuilder =
                (NotificationCompat.Builder) new NotificationCompat.Builder(this)
                        .setSmallIcon(android.R.drawable.ic_notification_clear_all)
                        .setContentTitle("Historial Medico")
                        .setContentText("Verificar vacunas pendientes...");

        //Activity que se lanza al hacer click en la notificacion
        Intent resultIntent = new Intent(this, NotificacionesActivity.class);

        // Como hacer clic en la notificacion abre una actividad nueva
        // no hay necesidad de crear una pila trasera artificial.
        PendingIntent resultPendingIntent =
                PendingIntent.getActivity(
                        this,
                        0,
                        resultIntent,
                        PendingIntent.FLAG_UPDATE_CURRENT
                );
        nCompatBuilder.setContentIntent(resultPendingIntent);

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

        // construye una notificacion.
        notificationManager.notify(notificationId, nCompatBuilder.build());
    }

    private class obtenerHijos extends AsyncTask<Void, Void, Void> {

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
                response = myHttpClient.doGetRequest("hijo/obtener/" + MainActivity.this.idUsuario);
            } catch (IOException e) {
                e.printStackTrace();
            }

            if (response != null) {
                Gson gson = new Gson();

                Type listType = new TypeToken<ArrayList<HijoDTO>>() {
                }.getType();

                List<HijoDTO> hijoDTOList = new Gson().fromJson(response, listType);

                for (HijoDTO hijo : hijoDTOList) {

                    HashMap<String, String> contact = new HashMap<>();

                    // adding each child node to HashMap key => value
                    contact.put("name",  hijo.getNombre());
                    contact.put("email", hijo.getEdad());
                    contact.put("mobile", hijo.getSexo());

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

            // Dismiss the progress dialog
            progressBar.setVisibility(View.INVISIBLE);

            /**
             * Updating parsed JSON data into ListView
             * */
            ListAdapter adapter = new SimpleAdapter(
                    MainActivity.this, contactList,
                    R.layout.list_item, new String[]{"name", "email",
                    "mobile"}, new int[]{R.id.name,
                    R.id.email, R.id.mobile});

            listView.setAdapter(adapter);
        }

    }

}
