package reservation;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Scanner;

/**
 * Interface console du module reservation.
 * Cette classe ne fait QUE l'affichage et la saisie.
 * Toute la logique est dans ReservationManager.
 */
public class SystemeReservationUI {

    private final ReservationManager manager;
    private final Scanner scanner;

    // Format de date attendu lors de la saisie
    private static final DateTimeFormatter FMT_SAISIE =
            DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    // ── Constructeur ──────────────────────────────────────────────────────────
    public SystemeReservationUI(ReservationManager manager, Scanner scanner) {
        this.manager = manager;
        this.scanner = scanner;
    }

    // ── Menu principal du module ───────────────────────────────────────────────
    public void afficherMenu() {
        boolean retour = false;
        while (!retour) {
            System.out.println("\n" +
                "+-----------------------------------------+\n" +
                "|          SYSTEME DE RESERVATION         |\n" +
                "+-----------------------------------------+\n" +
                "|  1. Voir toutes les reservations        |\n" +
                "|  2. Reservations a venir                |\n" +
                "|  3. Planning du jour                    |\n" +
                "|  4. Nouvelle reservation                |\n" +
                "|  5. Modifier une reservation            |\n" +
                "|  6. Confirmer une reservation           |\n" +
                "|  7. Annuler une reservation             |\n" +
                "|  8. Rechercher par client               |\n" +
                "|  9. Verifier dispo d'une table          |\n" +
                "|  0. Retour au menu principal            |\n" +
                "+-----------------------------------------+");

            int choix = lireInt("Votre choix");
            switch (choix) {
                case 1 -> afficherToutes();
                case 2 -> afficherAVenir();
                case 3 -> afficherPlanningJour();
                case 4 -> creerNouvelle();
                case 5 -> modifier();
                case 6 -> confirmer();
                case 7 -> annuler();
                case 8 -> rechercherParClient();
                case 9 -> verifierDispo();
                case 0 -> retour = true;
                default -> System.out.println("[ERREUR] Choix invalide.");
            }
        }
    }

    // ── 1. Voir toutes ────────────────────────────────────────────────────────
    private void afficherToutes() {
        List<Reservation> liste = manager.listerToutes();
        System.out.println("\n--- TOUTES LES RESERVATIONS ("
                + liste.size() + ") ---");
        if (liste.isEmpty()) {
            System.out.println("  Aucune reservation enregistree.");
        } else {
            liste.forEach(System.out::println);
        }
    }

    // ── 2. A venir ────────────────────────────────────────────────────────────
    private void afficherAVenir() {
        List<Reservation> liste = manager.getReservationsAVenir();
        System.out.println("\n--- RESERVATIONS A VENIR ("
                + liste.size() + ") ---");
        if (liste.isEmpty()) {
            System.out.println("  Aucune reservation a venir.");
        } else {
            liste.forEach(System.out::println);
        }
    }

    // ── 3. Planning du jour ───────────────────────────────────────────────────
    private void afficherPlanningJour() {
        String saisie = lireString(
                "Date (format jj/MM/aaaa) | Entree = aujourd'hui");
        LocalDate date;
        if (saisie.isBlank()) {
            date = LocalDate.now();
        } else {
            try {
                date = LocalDate.parse(saisie,
                        DateTimeFormatter.ofPattern("dd/MM/yyyy"));
            } catch (DateTimeParseException e) {
                System.out.println("[ERREUR] Format invalide. Exemple : 25/12/2025");
                return;
            }
        }
        manager.afficherPlanningJour(date);
    }

    // ── 4. Creer ─────────────────────────────────────────────────────────────
    private void creerNouvelle() {
        System.out.println("\n--- NOUVELLE RESERVATION ---");

        String nom      = lireStringObligatoire("Nom du client");
        String tel      = lireStringObligatoire("Telephone");
        int nbP         = lireIntPositif("Nombre de personnes");
        int numTable    = lireIntPositif("Numero de table");
        LocalDateTime dt = saisirDateHeure();

        if (dt == null) return; // saisie annulee ou invalide

        Reservation r = manager.creerReservation(nom, tel, nbP, numTable, dt);

        if (r != null) {
            // Champs optionnels
            String occasion = lireString(
                    "Occasion (anniversaire, affaires...) | Entree pour ignorer");
            if (!occasion.isBlank()) r.setOccasion(occasion);

            String notes = lireString(
                    "Notes / observations | Entree pour ignorer");
            if (!notes.isBlank()) r.setObservations(notes);

            System.out.println(r.afficherDetails());
        }
    }

    // ── 5. Modifier ───────────────────────────────────────────────────────────
    private void modifier() {
        afficherToutes();
        int id = lireIntPositif("ID de la reservation a modifier");

        System.out.println("Laissez vide (Entree) pour ne pas modifier un champ.");

        String nom  = lireString("Nouveau nom");
        String tel  = lireString("Nouveau telephone");
        String nbPS = lireString("Nouveau nombre de personnes (0 = inchange)");
        int nbP = nbPS.isBlank() ? 0 : Integer.parseInt(nbPS);

        System.out.print("\n> Modifier la date/heure ? (o/n) : ");
        String repDate = scanner.nextLine().trim();
        LocalDateTime nouvellDate = null;
        if (repDate.equalsIgnoreCase("o")) {
            nouvellDate = saisirDateHeure();
        }

        manager.modifierReservation(id, nom, tel, nbP, nouvellDate);
    }

    // ── 6. Confirmer ──────────────────────────────────────────────────────────
    private void confirmer() {
        afficherToutes();
        int id = lireIntPositif("ID a confirmer");
        manager.confirmerReservation(id);
    }

    // ── 7. Annuler ────────────────────────────────────────────────────────────
    private void annuler() {
        afficherToutes();
        int id = lireIntPositif("ID a annuler");
        System.out.print("\n> Confirmer l'annulation ? (o/n) : ");
        String conf = scanner.nextLine().trim();
        if (conf.equalsIgnoreCase("o")) {
            manager.annulerReservation(id);
        } else {
            System.out.println("[INFO] Annulation abandonnee.");
        }
    }

    // ── 8. Rechercher par client ───────────────────────────────────────────────
    private void rechercherParClient() {
        String nom = lireStringObligatoire("Nom du client (ou partie du nom)");
        List<Reservation> resultats = manager.chercherParClient(nom);
        System.out.println("\n--- RESULTATS POUR \"" + nom + "\" ("
                + resultats.size() + ") ---");
        if (resultats.isEmpty()) {
            System.out.println("  Aucun resultat.");
        } else {
            resultats.forEach(System.out::println);
        }
    }

    // ── 9. Verifier disponibilite ─────────────────────────────────────────────
    private void verifierDispo() {
        int numTable    = lireIntPositif("Numero de table");
        LocalDateTime dt = saisirDateHeure();
        if (dt == null) return;

        boolean dispo = manager.verifierDisponibilite(numTable, dt);
        if (dispo) {
            System.out.println("[OK] La table " + numTable
                    + " est DISPONIBLE pour ce creneau.");
        } else {
            System.out.println("[INFO] La table " + numTable
                    + " est OCCUPEE pour ce creneau.");
        }
    }

    // ─────────────────────────────────────────────────────────────────────────
    // UTILITAIRES DE SAISIE
    // ─────────────────────────────────────────────────────────────────────────

    /** Lit un entier valide, redemande si erreur */
    private int lireInt(String prompt) {
        while (true) {
            System.out.print("\n> " + prompt + " : ");
            try {
                return Integer.parseInt(scanner.nextLine().trim());
            } catch (NumberFormatException e) {
                System.out.println("[ERREUR] Entrez un nombre entier.");
            }
        }
    }

    /** Lit un entier strictement positif */
    private int lireIntPositif(String prompt) {
        int v;
        do {
            v = lireInt(prompt);
            if (v <= 0) System.out.println("[ERREUR] La valeur doit etre > 0.");
        } while (v <= 0);
        return v;
    }

    /** Lit une chaine, peut etre vide */
    private String lireString(String prompt) {
        System.out.print("\n> " + prompt + " : ");
        return scanner.nextLine().trim();
    }

    /** Lit une chaine non vide, redemande si vide */
    private String lireStringObligatoire(String prompt) {
        String s;
        do {
            s = lireString(prompt);
            if (s.isBlank()) System.out.println("[ERREUR] Ce champ est obligatoire.");
        } while (s.isBlank());
        return s;
    }

    /**
     * Saisit une date/heure avec validation.
     * Redemande jusqu'a obtenir un format correct et une date future.
     * Retourne null si l'utilisateur tape "q" pour quitter.
     */
    private LocalDateTime saisirDateHeure() {
        System.out.println("  Format attendu : jj/MM/aaaa HH:mm  |  'q' pour annuler");
        while (true) {
            System.out.print("\n> Date et heure : ");
            String saisie = scanner.nextLine().trim();
            if (saisie.equalsIgnoreCase("q")) return null;
            try {
                LocalDateTime dt = LocalDateTime.parse(saisie, FMT_SAISIE);
                if (dt.isBefore(LocalDateTime.now())) {
                    System.out.println("[ERREUR] La date doit etre dans le futur.");
                } else {
                    return dt;
                }
            } catch (DateTimeParseException e) {
                System.out.println("[ERREUR] Format invalide. Exemple : 25/12/2025 19:30");
            }
        }
    }
}