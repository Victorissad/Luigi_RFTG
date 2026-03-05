package com.example.applicationrftgvis;

/**
 * Classe pour gérer l'URL du serveur
 * Permet de basculer entre serveur local et distant
 */
public class UrlManager {

    // URL par défaut (émulateur Android -> localhost)
    private static String URLConnexion = "http://10.0.2.2:8180";

    // Token JWT stocké en dur (initialisé depuis strings.xml via init())
    private static String JWT_TOKEN = "";

    // Initialiser le token depuis strings.xml (appelé au démarrage de l'app)
    public static void init(android.content.Context context) {
        JWT_TOKEN = context.getString(R.string.jwt_token);
    }

    // Getter pour le token JWT
    public static String getJwtToken() {
        return JWT_TOKEN;
    }

    // Getter pour récupérer l'URL actuelle
    public static String getURLConnexion() {
        return URLConnexion;
    }

    // Setter pour modifier l'URL
    public static void setURLConnexion(String url) {
        URLConnexion = url;
    }
}
