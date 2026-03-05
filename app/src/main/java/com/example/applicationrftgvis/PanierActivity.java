package com.example.applicationrftgvis;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.Locale;

/**
 * Activity pour afficher le panier
 * Le panier est chargé depuis l'API GET /cart/{customerId}
 */
public class PanierActivity extends AppCompatActivity {

    private ListView lvPanier;
    private TextView tvNombreItems;
    private TextView tvPrixTotal;
    private TextView tvPanierVide;
    private PanierAdapter adapter;
    private SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_panier);

        // Initialiser le SessionManager
        sessionManager = new SessionManager(this);

        // Initialiser les vues
        lvPanier = findViewById(R.id.lvPanier);
        tvNombreItems = findViewById(R.id.tvNombreItems);
        tvPrixTotal = findViewById(R.id.tvPrixTotal);
        tvPanierVide = findViewById(R.id.tvPanierVide);

        // Créer l'adapter et l'associer à la ListView
        adapter = new PanierAdapter(this, Panier.getInstance().getItems());
        lvPanier.setAdapter(adapter);

        // Charger le panier depuis l'API
        chargerPanierDepuisApi();
    }

    // Charger le panier depuis l'API
    private void chargerPanierDepuisApi() {
        int customerId = sessionManager.getCustomerId();
        Log.d("PanierActivity", "Chargement du panier pour customerId=" + customerId);
        new ChargerPanierTask(this, customerId).execute();
    }

    // Callback appelé quand le panier est chargé depuis l'API
    public void onPanierCharge(String resultat) {
        Log.d("PanierActivity", "onPanierCharge resultat=" + resultat);

        // Vider le panier local
        Panier.getInstance().viderPanier();

        if (resultat != null && !resultat.trim().isEmpty()) {
            try {
                JSONArray jsonArray = new JSONArray(resultat);
                Log.d("PanierActivity", "Nombre de rentals reçus: " + jsonArray.length());

                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject rental = jsonArray.getJSONObject(i);
                    int rentalId = rental.getInt("rentalId");

                    // Extraire le film depuis inventory.film
                    JSONObject inventory = rental.getJSONObject("inventory");
                    JSONObject filmJson = inventory.getJSONObject("film");

                    Film film = new Film();
                    film.setFilm_id(String.valueOf(filmJson.getInt("filmId")));
                    film.setTitle(filmJson.getString("title"));

                    if (filmJson.has("description") && !filmJson.isNull("description")) {
                        film.setDescription(filmJson.getString("description"));
                    }
                    if (filmJson.has("rentalRate") && !filmJson.isNull("rentalRate")) {
                        film.setRental_rate(String.valueOf(filmJson.getDouble("rentalRate")));
                    }
                    if (filmJson.has("releaseYear") && !filmJson.isNull("releaseYear")) {
                        film.setRelease_year(String.valueOf(filmJson.getInt("releaseYear")));
                    }

                    // Ajouter au panier local avec le rentalId
                    Panier.getInstance().ajouterItem(film, rentalId);
                    Log.d("PanierActivity", "Film ajouté: " + film.getTitle() + " (rentalId=" + rentalId + ")");
                }

            } catch (Exception e) {
                Log.e("PanierActivity", "Erreur parsing JSON panier: " + e.toString());
            }
        }

        mettreAJourAffichage();
    }

    // Callback appelé après validation du panier
    public void onPanierValide(String resultat) {
        if (resultat.equals("OK")) {
            Toast.makeText(this, "Commande validée avec succès !", Toast.LENGTH_LONG).show();
            Log.d("PanierActivity", "Panier validé avec succès");
            Panier.getInstance().viderPanier();
            mettreAJourAffichage();
        } else {
            Toast.makeText(this, "Erreur lors de la validation", Toast.LENGTH_SHORT).show();
        }
    }

    // Callback appelé après suppression d'un item
    public void onItemSupprime(String resultat) {
        if (resultat.equals("OK")) {
            Toast.makeText(this, "Film retiré du panier", Toast.LENGTH_SHORT).show();
            // Recharger le panier depuis l'API
            chargerPanierDepuisApi();
        } else {
            Toast.makeText(this, "Erreur lors de la suppression", Toast.LENGTH_SHORT).show();
        }
    }

    // Callback appelé après avoir vidé le panier
    public void onPanierVide(String resultat) {
        if (resultat.equals("OK")) {
            Toast.makeText(this, "Panier vidé", Toast.LENGTH_SHORT).show();
            Panier.getInstance().viderPanier();
            mettreAJourAffichage();
        } else {
            Toast.makeText(this, "Erreur lors du vidage du panier", Toast.LENGTH_SHORT).show();
        }
    }

    // Mettre à jour l'affichage du panier
    private void mettreAJourAffichage() {
        Panier panier = Panier.getInstance();
        int nombreItems = panier.getNombreItems();

        // Afficher le nombre d'items
        tvNombreItems.setText(nombreItems + " film(s)");

        // Afficher le prix total
        tvPrixTotal.setText(String.format(Locale.FRANCE, "Total: %.2f €", panier.getPrixTotal()));

        // Afficher ou masquer le message "panier vide"
        if (nombreItems == 0) {
            lvPanier.setVisibility(View.GONE);
            tvPanierVide.setVisibility(View.VISIBLE);
        } else {
            lvPanier.setVisibility(View.VISIBLE);
            tvPanierVide.setVisibility(View.GONE);
        }

        // Notifier l'adapter que les données ont changé
        adapter.notifyDataSetChanged();
    }

    // Bouton "Vider le panier" - appel API
    public void onViderPanierClicked(View view) {
        if (Panier.getInstance().getNombreItems() == 0) {
            Toast.makeText(this, "Le panier est déjà vide", Toast.LENGTH_SHORT).show();
            return;
        }
        int customerId = sessionManager.getCustomerId();
        Log.d("PanierActivity", "Vidage du panier via API pour customerId=" + customerId);
        new ViderPanierTask(this, customerId).execute();
    }

    // Bouton "Valider la réservation" - appel API checkout
    public void onValiderClicked(View view) {
        if (Panier.getInstance().getNombreItems() == 0) {
            Toast.makeText(this, "Votre panier est vide", Toast.LENGTH_SHORT).show();
            return;
        }
        int customerId = sessionManager.getCustomerId();
        Log.d("PanierActivity", "Validation du panier via API pour customerId=" + customerId);
        new ValiderPanierTask(this, customerId).execute();
    }

    // Bouton "Continuer les achats"
    public void onContinuerAchatsClicked(View view) {
        Log.d("PanierActivity", "Retour à la liste des films");
        finish(); // Retour à l'activité précédente
    }
}
