package persistence;

import personnel.Personnel;
import reservation.Reservation;
import reservation.ReservationManager;
import personnel.GestionPersonnelManager;

import java.io.*;
import java.nio.file.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

/**
 * COHESION : une seule responsabilite = gerer la persistance des donnees.
 *
 * Fonctionnement :
 *  - sauvegarder()  : prend un snapshot de l'etat actuel → fichier .ser
 *  - charger()      : lit le fichier .ser → restaure l'etat
 *  - creerBackup()  : sauvegarde horodatee (pour l'historique)
 *  - exporterCSV()  : export lisible dans Excel
 *
 * Le fichier de sauvegarde est place dans le dossier "data/"
 * qui est cree automatiquement s'il n'existe pas.
 */
public class SauvegardeManager {

    // ── Chemins des fichiers ──────────────────────────────────────────────────
    private static final String DOSSIER           = "data/";
    private static final String FICHIER_PRINCIPAL = DOSSIER + "restaurant.ser";
    private static final String FICHIER_CSV       = DOSSIER + "reservations_export.csv";

    // Sauvegarde automatique toutes les N actions
    private static final int SEUIL_AUTO = 10;
    private int compteurActions = 0;

    // References vers les managers (injectees via le constructeur)
    private final ReservationManager     reservationManager;
    private final GestionPersonnelManager personnelManager;
    private final String                 nomRestaurant;

    // ── Constructeur ──────────────────────────────────────────────────────────
    public SauvegardeManager(ReservationManager reservationManager,
                              GestionPersonnelManager personnelManager,
                              String nomRestaurant) {
        this.reservationManager = reservationManager;
        this.personnelManager   = personnelManager;
        this.nomRestaurant      = nomRestaurant;
        creerDossierSiNecessaire();
    }

    // ─────────────────────────────────────────────────────────────────────────
    // SAUVEGARDE PRINCIPALE
    // ─────────────────────────────────────────────────────────────────────────

    /**
     * Sauvegarde l'etat complet dans le fichier principal restaurant.ser
     * Retourne true si la sauvegarde a reussi.
     */
    public boolean sauvegarder() {
        return sauvegarderVers(FICHIER_PRINCIPAL);
    }

    /**
     * Sauvegarde dans un fichier specifique.
     * Utilise la serialisation Java (ObjectOutputStream).
     */
    public boolean sauvegarderVers(String chemin) {
        try {
            // 1. Creer le snapshot avec l'etat actuel
            RestaurantSnapshot snapshot = new RestaurantSnapshot(
                    reservationManager.getReservations(),
                    personnelManager.getPersonnel(),
                    nomRestaurant
            );

            // 2. Serialiser (ecrire en binaire)
            try (ObjectOutputStream oos = new ObjectOutputStream(
                    new FileOutputStream(chemin))) {
                oos.writeObject(snapshot);
            }

            snapshot.afficherResume();
            System.out.println("[OK] Sauvegarde reussie → " + chemin);
            return true;

        } catch (IOException e) {
            System.out.println("[ERREUR] Sauvegarde echouee : " + e.getMessage());
            return false;
        }
    }

    // ─────────────────────────────────────────────────────────────────────────
    // CHARGEMENT
    // ─────────────────────────────────────────────────────────────────────────

    /**
     * Charge l'etat depuis le fichier principal restaurant.ser
     * et remet les donnees dans les managers.
     * Retourne true si le chargement a reussi.
     */
    public boolean charger() {
        return chargerDepuis(FICHIER_PRINCIPAL);
    }

    /**
     * Charge depuis un fichier specifique.
     * Utilise la deserialisation Java (ObjectInputStream).
     */
    public boolean chargerDepuis(String chemin) {
        File fichier = new File(chemin);

        // Verifier que le fichier existe
        if (!fichier.exists()) {
            System.out.println("[INFO] Aucune sauvegarde trouvee : " + chemin);
            return false;
        }

        try (ObjectInputStream ois = new ObjectInputStream(
                new FileInputStream(fichier))) {

            // Deserialiser : lire l'objet depuis le fichier
            RestaurantSnapshot snapshot = (RestaurantSnapshot) ois.readObject();

            // Restaurer les donnees dans les managers
            restaurerDepuisSnapshot(snapshot);

            snapshot.afficherResume();
            System.out.println("[OK] Chargement reussi depuis : " + chemin);
            return true;

        } catch (IOException e) {
            System.out.println("[ERREUR] Chargement echoue (fichier illisible) : "
                    + e.getMessage());
            return false;
        } catch (ClassNotFoundException e) {
            System.out.println("[ERREUR] Chargement echoue (version incompatible) : "
                    + e.getMessage());
            return false;
        }
    }

    /**
     * Remet les donnees du snapshot dans les managers.
     */
    private void restaurerDepuisSnapshot(RestaurantSnapshot snapshot) {
        // Vider les listes actuelles
        reservationManager.getReservations().clear();
        personnelManager.getPersonnel().clear();

        // Recharger depuis le snapshot
        reservationManager.getReservations()
                .addAll(snapshot.getReservations());
        personnelManager.getPersonnel()
                .addAll(snapshot.getPersonnel());
    }

    // ─────────────────────────────────────────────────────────────────────────
    // BACKUP HORODATE
    // ─────────────────────────────────────────────────────────────────────────

    /**
     * Cree une copie de sauvegarde avec la date dans le nom.
     * Exemple : data/backup_2025-12-25_19h30.ser
     */
    public boolean creerBackup() {
        String date = LocalDateTime.now()
                .format(DateTimeFormatter.ofPattern("yyyy-MM-dd_HH'h'mm"));
        String chemin = DOSSIER + "backup_" + date + ".ser";
        System.out.println("[INFO] Creation du backup : " + chemin);
        return sauvegarderVers(chemin);
    }

    // ─────────────────────────────────────────────────────────────────────────
    // SAUVEGARDE AUTOMATIQUE
    // ─────────────────────────────────────────────────────────────────────────

    /**
     * A appeler apres chaque action importante (ajout, suppression...).
     * Sauvegarde automatiquement tous les SEUIL_AUTO appels.
     */
    public void signalerAction() {
        compteurActions++;
        if (compteurActions % SEUIL_AUTO == 0) {
            System.out.println("\n[AUTO-SAVE] Sauvegarde automatique apres "
                    + compteurActions + " actions...");
            sauvegarder();
        }
    }

    // ─────────────────────────────────────────────────────────────────────────
    // LISTER LES SAUVEGARDES DISPONIBLES
    // ─────────────────────────────────────────────────────────────────────────

    public void listerSauvegardes() {
        File dossier = new File(DOSSIER);
        File[] fichiers = dossier.listFiles(
                f -> f.getName().endsWith(".ser"));

        System.out.println("\n--- SAUVEGARDES DISPONIBLES ---");
        if (fichiers == null || fichiers.length == 0) {
            System.out.println("  Aucune sauvegarde trouvee dans " + DOSSIER);
            return;
        }
        for (File f : fichiers) {
            long tailleKo = f.length() / 1024;
            System.out.printf("  %-40s  %d Ko%n", f.getName(), tailleKo);
        }
        System.out.println("-------------------------------");
    }

    // ─────────────────────────────────────────────────────────────────────────
    // SUPPRIMER UNE SAUVEGARDE
    // ─────────────────────────────────────────────────────────────────────────

    public boolean supprimerSauvegarde(String nomFichier) {
        // Securite : empecher de supprimer le fichier principal
        if (nomFichier.equals("restaurant.ser")) {
            System.out.println("[ERREUR] Impossible de supprimer le fichier principal.");
            return false;
        }
        File f = new File(DOSSIER + nomFichier);
        if (!f.exists()) {
            System.out.println("[ERREUR] Fichier introuvable : " + nomFichier);
            return false;
        }
        boolean ok = f.delete();
        System.out.println(ok
                ? "[OK] Supprime : " + nomFichier
                : "[ERREUR] Impossible de supprimer.");
        return ok;
    }

    // ─────────────────────────────────────────────────────────────────────────
    // EXPORT CSV DES RESERVATIONS
    // ─────────────────────────────────────────────────────────────────────────

    /**
     * Genere un fichier CSV des reservations,
     * lisible dans Excel ou LibreOffice Calc.
     */
    public boolean exporterReservationsCSV() {
        try (PrintWriter pw = new PrintWriter(
                new FileWriter(FICHIER_CSV))) {

            // En-tete CSV
            pw.println("ID;Nom Client;Telephone;Nb Personnes;"
                    + "Table;Date Heure;Occasion;Statut");

            DateTimeFormatter fmt =
                    DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

            for (Reservation r : reservationManager.getReservations()) {
                pw.printf("%d;%s;%s;%d;%d;%s;%s;%s%n",
                        r.getId(),
                        r.getNomClient(),
                        r.getTelephone(),
                        r.getNombrePersonnes(),
                        r.getNumeroTable(),
                        r.getDateHeure().format(fmt),
                        r.getOccasion().isBlank() ? "-" : r.getOccasion(),
                        r.getStatut()
                );
            }

            System.out.println("[OK] Export CSV cree : " + FICHIER_CSV);
            System.out.println("     Ouvrez ce fichier dans Excel / LibreOffice.");
            return true;

        } catch (IOException e) {
            System.out.println("[ERREUR] Export CSV echoue : " + e.getMessage());
            return false;
        }
    }

    // ─────────────────────────────────────────────────────────────────────────
    // VERIFICATION D'INTEGRITE
    // ─────────────────────────────────────────────────────────────────────────

    /**
     * Verifie la coherence des donnees :
     * - pas de reservations avec des champs vides
     * - pas de personnel sans nom
     */
    public boolean verifierIntegrite() {
        System.out.println("\n--- VERIFICATION INTEGRITE ---");
        boolean ok = true;

        // Verifier les reservations
        for (Reservation r : reservationManager.getReservations()) {
            if (r.getNomClient() == null || r.getNomClient().isBlank()) {
                System.out.println("[ALERTE] Reservation #" + r.getId()
                        + " : nom client vide !");
                ok = false;
            }
            if (r.getNumeroTable() <= 0) {
                System.out.println("[ALERTE] Reservation #" + r.getId()
                        + " : numero de table invalide !");
                ok = false;
            }
        }

        // Verifier le personnel
        for (personnel.Personnel p : personnelManager.getPersonnel()) {
            if (p.getNom() == null || p.getNom().isBlank()) {
                System.out.println("[ALERTE] Personnel #" + p.getId()
                        + " : nom vide !");
                ok = false;
            }
        }

        System.out.println(ok
                ? "[OK] Integrite verifiee — aucun probleme detecte."
                : "[ATTENTION] Des anomalies ont ete detectees.");
        System.out.println("-------------------------------");
        return ok;
    }

    // ─────────────────────────────────────────────────────────────────────────
    // UTILITAIRES
    // ─────────────────────────────────────────────────────────────────────────

    /** Cree le dossier "data/" s'il n'existe pas */
    private void creerDossierSiNecessaire() {
        try {
            Files.createDirectories(Paths.get(DOSSIER));
        } catch (IOException e) {
            System.out.println("[AVERTISSEMENT] Impossible de creer le dossier "
                    + DOSSIER + " : " + e.getMessage());
        }
    }

    /** Verifie si une sauvegarde principale existe deja */
    public boolean sauvegardeExiste() {
        return new File(FICHIER_PRINCIPAL).exists();
    }

    /** Retourne le chemin du fichier principal */
    public String getCheminFichierPrincipal() {
        return FICHIER_PRINCIPAL;
    }
}