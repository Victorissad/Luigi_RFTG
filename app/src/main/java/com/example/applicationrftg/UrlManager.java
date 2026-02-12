package com.example.applicationrftg;

/**
 * Classe pour gérer l'URL du serveur
 * Permet de basculer entre serveur local et distant
 */
public class UrlManager {

    // URL par défaut (émulateur Android -> localhost)
    private static String URLConnexion = "http://10.0.2.2:8180";

    // Getter pour récupérer l'URL actuelle
    public static String getURLConnexion() {
        return URLConnexion;
    }

    // Setter pour modifier l'URL
    public static void setURLConnexion(String url) {
        URLConnexion = url;
    }
}
