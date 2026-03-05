package com.example.applicationrftgvis;

import android.os.AsyncTask;
import android.util.Log;

import java.io.BufferedInputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * AsyncTask pour charger le panier depuis l'API
 * GET /cart/{customerId}
 */
public class ChargerPanierTask extends AsyncTask<Void, Void, String> {

    private PanierActivity activity;
    private int customerId;

    public ChargerPanierTask(PanierActivity activity, int customerId) {
        this.activity = activity;
        this.customerId = customerId;
    }

    @Override
    protected String doInBackground(Void... voids) {
        HttpURLConnection urlConnection = null;
        String sResultatAppel = "";
        try {
            URL url = new URL(UrlManager.getURLConnexion() + "/cart/" + customerId);
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.setRequestProperty("Content-Type", "application/json");
            urlConnection.setRequestProperty("Accept", "application/json");
            urlConnection.setRequestProperty("Authorization", "Bearer " + UrlManager.getJwtToken());
            urlConnection.setRequestProperty("User-Agent", System.getProperty("http.agent"));
            urlConnection.setConnectTimeout(5000);
            urlConnection.setReadTimeout(5000);

            int responseCode = urlConnection.getResponseCode();
            Log.d("mydebug", ">>>ChargerPanierTask - responseCode=" + responseCode);

            if (responseCode == HttpURLConnection.HTTP_OK) {
                InputStream in = new BufferedInputStream(urlConnection.getInputStream());
                int codeCaractere;
                while ((codeCaractere = in.read()) != -1) {
                    sResultatAppel = sResultatAppel + (char) codeCaractere;
                }
                in.close();
                Log.d("mydebug", ">>>ChargerPanierTask - resultat=" + sResultatAppel);
            }

        } catch (Exception e) {
            Log.e("mydebug", ">>>ChargerPanierTask - Exception: " + e.toString());
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
        }
        return sResultatAppel;
    }

    @Override
    protected void onPostExecute(String resultat) {
        activity.onPanierCharge(resultat);
    }
}
