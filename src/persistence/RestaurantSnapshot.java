package persistence;

import personnel.Personnel;

import reservation.Reservation;


import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * RestaurantSnapshot = une "photo" de l'etat complet du restaurant
 * a un instant T.
 *
 * C'est cet objet qui est serialise (sauvegarde) et deserialise (charge).
 *
 * IMPORTANT : pour que la serialisation fonctionne, TOUTES les classes
 * stockees ici doivent implementer Serializable.
 * => Reservation.java    : implements Serializable  ✓ (deja fait)
 * => Personnel.java      : implements Serializable  ✓ (deja fait)
 * => Serveur/Cuisinier/Manager : heritent de Personnel donc OK ✓
 */
public class RestaurantSnapshot implements Serializable {

    private static final long serialVersionUID = 1L;

    // ── Copies de toutes les donnees au moment de la sauvegarde ───────────────
    private List<Reservation> reservations;
    private List<Personnel>   personnel;
    private String            nomRestaurant;
    private String            dateHeureSauvegarde; // info lisible pour l'utilisateur

    // ── Constructeur : on passe toutes les listes a sauvegarder ───────────────
    public RestaurantSnapshot(List<Reservation> reservations,
                               List<Personnel>   personnel,
                               String            nomRestaurant) {
        // Copies defensives : on prend un instantane, pas une reference live
        this.reservations        = new ArrayList<>(reservations);
        this.personnel           = new ArrayList<>(personnel);
        this.nomRestaurant       = nomRestaurant;
        this.dateHeureSauvegarde = java.time.LocalDateTime.now()
                .format(java.time.format.DateTimeFormatter
                        .ofPattern("dd/MM/yyyy HH:mm:ss"));
    }

    // ── Affichage du resume de la sauvegarde ──────────────────────────────────
    public void afficherResume() {
        System.out.println("\n--- RESUME DE LA SAUVEGARDE ---");
        System.out.println("  Restaurant   : " + nomRestaurant);
        System.out.println("  Sauvegarde le: " + dateHeureSauvegarde);
        System.out.println("  Reservations : " + reservations.size());
        System.out.println("  Personnel    : " + personnel.size());
        System.out.println("-------------------------------");
    }

    // ── Getters ───────────────────────────────────────────────────────────────
    public List<Reservation> getReservations()      { return reservations; }
    public List<Personnel>   getPersonnel()          { return personnel; }
    public String            getNomRestaurant()      { return nomRestaurant; }
    public String            getDateHeureSauvegarde(){ return dateHeureSauvegarde; }
}