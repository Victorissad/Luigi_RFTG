package com.example.applicationrftg;

import android.os.AsyncTask;
import android.util.Log;

import java.net.HttpURLConnection;
import java.net.URL;

/**
 * AsyncTask pour vider le panier via l'API
 * DELETE /cart/clear/{customerId}
 */
public class ViderPanierTask extends AsyncTask<Void, Void, String> {

    private PanierActivity activity;
    private int customerId;

    public ViderPanierTask(PanierActivity activity, int customerId) {
        this.activity = activity;
        this.customerId = customerId;
    }

    @Override
    protected String doInBackground(Void... voids) {
        HttpURLConnection urlConnection = null;
        try {
            URL url = new URL(UrlManager.getURLConnexion() + "/cart/clear/" + customerId);
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("DELETE");
            urlConnection.setRequestProperty("Content-Type", "application/json");
            urlConnection.setRequestProperty("Accept", "application/json");
            urlConnection.setRequestProperty("Authorization", "Bearer " + UrlManager.getJwtToken());
            urlConnection.setRequestProperty("User-Agent", System.getProperty("http.agent"));
            urlConnection.setConnectTimeout(5000);
            urlConnection.setReadTimeout(5000);

            int responseCode = urlConnection.getResponseCode();
            Log.d("mydebug", ">>>ViderPanierTask - responseCode=" + responseCode);

            if (responseCode == HttpURLConnection.HTTP_OK) {
                return "OK";
            } else {
                return "ERREUR";
            }

        } catch (Exception e) {
            Log.e("mydebug", ">>>ViderPanierTask - Exception: " + e.toString());
            return "ERREUR";
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
        }
    }

    @Override
    protected void onPostExecute(String resultat) {
        activity.onPanierVide(resultat);
    }
}
