package com.example.applicationrftgvis;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Locale;

/**
 * Adapter pour afficher les items du panier dans une ListView
 * Chaque item correspond à un rental côté serveur (quantité = 1)
 * La suppression se fait via l'API DELETE /cart/{rentalId}
 */
public class PanierAdapter extends BaseAdapter {

    private PanierActivity activity;
    private ArrayList<ItemPanier> items;

    public PanierAdapter(PanierActivity activity, ArrayList<ItemPanier> items) {
        this.activity = activity;
        this.items = items;
    }

    @Override
    public int getCount() {
        return items.size();
    }

    @Override
    public Object getItem(int position) {
        return items.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;

        // Pattern ViewHolder pour optimiser les performances (principe du cours)
        if (convertView == null) {
            convertView = LayoutInflater.from(activity).inflate(R.layout.item_panier, parent, false);
            holder = new ViewHolder();
            holder.tvTitre = convertView.findViewById(R.id.tvItemTitre);
            holder.tvType = convertView.findViewById(R.id.tvItemType);
            holder.tvQuantite = convertView.findViewById(R.id.tvQuantite);
            holder.tvPrix = convertView.findViewById(R.id.tvItemPrix);
            holder.btnDiminuer = convertView.findViewById(R.id.btnDiminuer);
            holder.btnAugmenter = convertView.findViewById(R.id.btnAugmenter);
            holder.btnSupprimer = convertView.findViewById(R.id.btnSupprimer);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        // Récupérer l'item à la position donnée
        ItemPanier item = items.get(position);
        Film film = item.getFilm();

        // Remplir les vues avec les données
        holder.tvTitre.setText(film.getTitle());
        holder.tvType.setText("DVD");
        holder.tvQuantite.setText(String.valueOf(item.getQuantite()));
        holder.tvPrix.setText(String.format(Locale.FRANCE, "%.2f €", item.getPrixTotal()));

        // Masquer les boutons +/- (chaque rental = 1 exemplaire via l'API)
        holder.btnDiminuer.setVisibility(View.GONE);
        holder.btnAugmenter.setVisibility(View.GONE);

        // Bouton supprimer - appel API DELETE /cart/{rentalId}
        holder.btnSupprimer.setOnClickListener(v -> {
            int rentalId = item.getRentalId();
            Log.d("PanierAdapter", "Suppression du rental rentalId=" + rentalId);
            if (rentalId > 0) {
                new SupprimerDuPanierTask(activity, rentalId).execute();
            } else {
                Log.e("PanierAdapter", "rentalId invalide: " + rentalId);
            }
        });

        return convertView;
    }

    // ViewHolder pour optimiser les performances (principe du cours)
    static class ViewHolder {
        TextView tvTitre;
        TextView tvType;
        TextView tvQuantite;
        TextView tvPrix;
        Button btnDiminuer;
        Button btnAugmenter;
        Button btnSupprimer;
    }
}
