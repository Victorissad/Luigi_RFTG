package com.example.applicationrftg;

import android.os.AsyncTask;
import android.util.Log;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * AsyncTask pour valider le panier via l'API
 * POST /cart/checkout avec { "customerId": X }
 */
public class ValiderPanierTask extends AsyncTask<Void, Void, String> {

    private PanierActivity activity;
    private int customerId;

    public ValiderPanierTask(PanierActivity activity, int customerId) {
        this.activity = activity;
        this.customerId = customerId;
    }

    @Override
    protected String doInBackground(Void... voids) {
        HttpURLConnection urlConnection = null;
        try {
            URL url = new URL(UrlManager.getURLConnexion() + "/cart/checkout");
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("POST");
            urlConnection.setRequestProperty("Content-Type", "application/json");
            urlConnection.setRequestProperty("Accept", "application/json");
            urlConnection.setRequestProperty("Authorization", "Bearer " + UrlManager.getJwtToken());
            urlConnection.setRequestProperty("User-Agent", System.getProperty("http.agent"));
            urlConnection.setDoOutput(true);
            urlConnection.setConnectTimeout(5000);
            urlConnection.setReadTimeout(5000);

            String jsonBody = "{\"customerId\":" + customerId + "}";
            OutputStream os = urlConnection.getOutputStream();
            os.write(jsonBody.getBytes("UTF-8"));
            os.flush();
            os.close();

            int responseCode = urlConnection.getResponseCode();
            Log.d("mydebug", ">>>ValiderPanierTask - responseCode=" + responseCode);

            if (responseCode == HttpURLConnection.HTTP_OK) {
                BufferedReader in = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
                StringBuilder response = new StringBuilder();
                String inputLine;
                while ((inputLine = in.readLine()) != null) {
                    response.append(inputLine);
                }
                in.close();
                Log.d("mydebug", ">>>ValiderPanierTask - resultat=" + response.toString());
                return "OK";
            } else {
                return "ERREUR";
            }

        } catch (Exception e) {
            Log.e("mydebug", ">>>ValiderPanierTask - Exception: " + e.toString());
            return "ERREUR";
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
        }
    }

    @Override
    protected void onPostExecute(String resultat) {
        activity.onPanierValide(resultat);
    }
}
