package com.example.applicationrftgvis;

import android.os.AsyncTask;
import android.util.Log;

import java.net.HttpURLConnection;
import java.net.URL;

/**
 * AsyncTask pour supprimer un item du panier via l'API
 * DELETE /cart/{rentalId}
 */
public class SupprimerDuPanierTask extends AsyncTask<Void, Void, String> {

    private PanierActivity activity;
    private int rentalId;

    public SupprimerDuPanierTask(PanierActivity activity, int rentalId) {
        this.activity = activity;
        this.rentalId = rentalId;
    }

    @Override
    protected String doInBackground(Void... voids) {
        HttpURLConnection urlConnection = null;
        try {
            URL url = new URL(UrlManager.getURLConnexion() + "/cart/" + rentalId);
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("DELETE");
            urlConnection.setRequestProperty("Content-Type", "application/json");
            urlConnection.setRequestProperty("Accept", "application/json");
            urlConnection.setRequestProperty("Authorization", "Bearer " + UrlManager.getJwtToken());
            urlConnection.setRequestProperty("User-Agent", System.getProperty("http.agent"));
            urlConnection.setConnectTimeout(5000);
            urlConnection.setReadTimeout(5000);

            int responseCode = urlConnection.getResponseCode();
            Log.d("mydebug", ">>>SupprimerDuPanierTask - responseCode=" + responseCode);

            if (responseCode == HttpURLConnection.HTTP_OK) {
                return "OK";
            } else {
                return "ERREUR";
            }

        } catch (Exception e) {
            Log.e("mydebug", ">>>SupprimerDuPanierTask - Exception: " + e.toString());
            return "ERREUR";
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
        }
    }

    @Override
    protected void onPostExecute(String resultat) {
        activity.onItemSupprime(resultat);
    }
}
