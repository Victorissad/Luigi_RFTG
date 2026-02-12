package com.example.applicationrftg;

import android.app.Activity;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * AsyncTask pour ajouter un film au panier via l'API
 * POST /cart/add avec { "customerId": X, "filmId": Y }
 */
public class AjouterAuPanierTask extends AsyncTask<Void, Void, String> {

    private Activity activity;
    private int customerId;
    private int filmId;

    public AjouterAuPanierTask(Activity activity, int customerId, int filmId) {
        this.activity = activity;
        this.customerId = customerId;
        this.filmId = filmId;
    }

    @Override
    protected String doInBackground(Void... voids) {
        HttpURLConnection urlConnection = null;
        try {
            URL url = new URL(UrlManager.getURLConnexion() + "/cart/add");
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("POST");
            urlConnection.setRequestProperty("Content-Type", "application/json");
            urlConnection.setRequestProperty("Accept", "application/json");
            urlConnection.setRequestProperty("Authorization", "Bearer " + UrlManager.getJwtToken());
            urlConnection.setRequestProperty("User-Agent", System.getProperty("http.agent"));
            urlConnection.setDoOutput(true);
            urlConnection.setConnectTimeout(5000);
            urlConnection.setReadTimeout(5000);

            // Envoyer le JSON
            String jsonBody = "{\"customerId\":" + customerId + ",\"filmId\":" + filmId + "}";
            OutputStream os = urlConnection.getOutputStream();
            os.write(jsonBody.getBytes("UTF-8"));
            os.flush();
            os.close();

            int responseCode = urlConnection.getResponseCode();
            Log.d("mydebug", ">>>AjouterAuPanierTask - responseCode=" + responseCode);

            if (responseCode == HttpURLConnection.HTTP_OK) {
                BufferedReader in = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
                StringBuilder response = new StringBuilder();
                String inputLine;
                while ((inputLine = in.readLine()) != null) {
                    response.append(inputLine);
                }
                in.close();
                Log.d("mydebug", ">>>AjouterAuPanierTask - resultat=" + response.toString());
                return "OK";
            } else if (responseCode == HttpURLConnection.HTTP_NOT_FOUND) {
                return "INDISPONIBLE";
            } else {
                return "ERREUR";
            }

        } catch (Exception e) {
            Log.e("mydebug", ">>>AjouterAuPanierTask - Exception: " + e.toString());
            return "ERREUR";
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
        }
    }

    @Override
    protected void onPostExecute(String resultat) {
        if (resultat.equals("OK")) {
            Toast.makeText(activity, "Film ajouté au panier", Toast.LENGTH_SHORT).show();
        } else if (resultat.equals("INDISPONIBLE")) {
            Toast.makeText(activity, "Aucun exemplaire disponible", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(activity, "Erreur lors de l'ajout au panier", Toast.LENGTH_SHORT).show();
        }
    }
}
