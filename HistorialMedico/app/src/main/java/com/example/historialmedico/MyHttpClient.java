package com.example.historialmedico;

import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;

import java.io.IOException;

/**
 * Created by jorge on 7/9/2017.
 */

public class MyHttpClient {

    private static final String PATH = "http://10.0.2.2:8080/ServiciosRest/webresources/";

    public static final MediaType JSON
            = MediaType.parse("application/json; charset=utf-8");

    private OkHttpClient client = new OkHttpClient();

    public OkHttpClient getClient() {
        return client;
    }

    public void setClient(OkHttpClient client) {
        this.client = client;
    }

    public String doGetRequest(String url) throws IOException {
        Request request = new Request.Builder()
                .url(PATH + url)
                .build();

        Response response = client.newCall(request).execute();
        return response.body().string();
    }

    public String doPostRequest(String url, String json) throws IOException {
        RequestBody body = RequestBody.create(JSON, json);
        Request request = new Request.Builder()
                .url(PATH + url)
                .post(body)
                .build();

        Response response = client.newCall(request).execute();


        return response.body().string();
    }
}
