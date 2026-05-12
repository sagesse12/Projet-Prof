// ─── FICHIER : Main.java ──────────────────────────────────────────────────────
// Ce fichier est place a la RACINE de src/ (pas dans un sous-package)
// Il relie les 3 modules : reservation, personnel, persistence

import persistence.PersistanceUI;
import persistence.SauvegardeManager;
import personnel.GestionPersonnelManager;
import reservation.ReservationManager;
import reservation.SystemeReservationUI;

import java.util.Scanner;

/**
 * Point d'entree du programme.
 * Cree les managers, les injecte dans les UI, et affiche le menu principal.
 */
public class Main {

    public static void main(String[] args) {

        Scanner scanner = new Scanner(System.in);

        // ── 1. Creer les managers (logique metier) ────────────────────────────
        ReservationManager      reservationManager = new ReservationManager();
        GestionPersonnelManager personnelManager   = new GestionPersonnelManager();
        SauvegardeManager       sauvegardeManager  = new SauvegardeManager(
                reservationManager,
                personnelManager,
                "Restaurant Java"
        );

        // ── 2. Charger automatiquement la derniere sauvegarde si elle existe ──
        if (sauvegardeManager.sauvegardeExiste()) {
            System.out.println("\n[INFO] Sauvegarde trouvee, chargement automatique...");
            sauvegardeManager.charger();
        } else {
            System.out.println("\n[INFO] Aucune sauvegarde existante — demarrage a vide.");
        }

        // ── 3. Creer les interfaces console ───────────────────────────────────
        SystemeReservationUI reservationUI = new SystemeReservationUI(
                reservationManager, scanner);

        PersistanceUI persistanceUI = new PersistanceUI(
                sauvegardeManager, scanner);

        // ── 4. Menu principal ─────────────────────────────────────────────────
        afficherBanniere();

        boolean quitter = false;
        while (!quitter) {
            System.out.println("\n" +
                "+=============================================+\n" +
                "|       SYSTEME DE GESTION DE RESTAURANT     |\n" +
                "+=============================================+\n" +
                "|  1.  Reservations                          |\n" +
                "|  2.  Gestion du Personnel                  |\n" +
                "|  3.  Sauvegarde & Persistance              |\n" +
                "|  0.  Quitter (sauvegarde automatique)      |\n" +
                "+=============================================+");

            int choix = lireInt(scanner, "Votre choix");

            switch (choix) {
                case 1 -> reservationUI.afficherMenu();

                case 2 -> menuPersonnel(personnelManager, sauvegardeManager, scanner);

                case 3 -> persistanceUI.afficherMenu();

                case 0 -> {
                    // Sauvegarde automatique avant de quitter
                    System.out.println("\n[INFO] Sauvegarde avant fermeture...");
                    sauvegardeManager.sauvegarder();
                    System.out.println("\nAu revoir !\n");
                    quitter = true;
                }

                default -> System.out.println("[ERREUR] Choix invalide.");
            }
        }

        scanner.close();
    }

    // ── Menu Personnel (dans Main pour garder les autres fichiers autonomes) ──
    private static void menuPersonnel(GestionPersonnelManager manager,
                                       SauvegardeManager sauvegardeManager,
                                       Scanner scanner) {
        boolean retour = false;
        while (!retour) {
            System.out.println("\n" +
                "+-----------------------------------------+\n" +
                "|         GESTION DU PERSONNEL            |\n" +
                "+-----------------------------------------+\n" +
                "|  1. Lister tout le personnel            |\n" +
                "|  2. Voir le profil complet              |\n" +
                "|  3. Ajouter un serveur                  |\n" +
                "|  4. Ajouter un cuisinier                |\n" +
                "|  5. Ajouter un manager                  |\n" +
                "|  6. Affecter un serveur a une table     |\n" +
                "|  7. Retirer un serveur d'une table      |\n" +
                "|  8. Desactiver un membre                |\n" +
                "|  9. Supprimer un membre                 |\n" +
                "|  0. Retour                              |\n" +
                "+-----------------------------------------+");

            int choix = lireInt(scanner, "Votre choix");
            switch (choix) {
                case 1 -> manager.afficherTout();

                case 2 -> {
                    manager.afficherTout();
                    int id = lireInt(scanner, "ID du membre");
                    manager.afficherProfil(id);
                }

                case 3 -> {
                    System.out.print("\n> Nom       : "); String nom   = scanner.nextLine().trim();
                    System.out.print("> Prenom    : "); String prenom = scanner.nextLine().trim();
                    System.out.print("> Telephone : "); String tel    = scanner.nextLine().trim();
                    double sal = lireDouble(scanner, "Salaire (EUR)");
                    manager.ajouterServeur(nom, prenom, tel, sal);
                    sauvegardeManager.signalerAction();
                }

                case 4 -> {
                    System.out.print("\n> Nom           : "); String nom   = scanner.nextLine().trim();
                    System.out.print("> Prenom        : "); String prenom = scanner.nextLine().trim();
                    System.out.print("> Telephone     : "); String tel    = scanner.nextLine().trim();
                    double sal  = lireDouble(scanner, "Salaire (EUR)");
                    System.out.print("> Specialite    : "); String spe   = scanner.nextLine().trim();
                    int exp = lireInt(scanner, "Annees d'experience");
                    manager.ajouterCuisinier(nom, prenom, tel, sal, spe, exp);
                    sauvegardeManager.signalerAction();
                }

                case 5 -> {
                    System.out.print("\n> Nom         : "); String nom   = scanner.nextLine().trim();
                    System.out.print("> Prenom      : "); String prenom = scanner.nextLine().trim();
                    System.out.print("> Telephone   : "); String tel    = scanner.nextLine().trim();
                    double sal  = lireDouble(scanner, "Salaire (EUR)");
                    System.out.print("> Departement : "); String dept  = scanner.nextLine().trim();
                    int niv = lireInt(scanner, "Niveau d'acces (1/2/3)");
                    manager.ajouterManager(nom, prenom, tel, sal, dept, niv);
                    sauvegardeManager.signalerAction();
                }

                case 6 -> {
                    manager.afficherTout();
                    int staffId  = lireInt(scanner, "ID du serveur");
                    int numTable = lireInt(scanner, "Numero de table");
                    manager.affecterServeurTable(staffId, numTable);
                }

                case 7 -> {
                    manager.afficherTout();
                    int staffId  = lireInt(scanner, "ID du serveur");
                    int numTable = lireInt(scanner, "Numero de table");
                    manager.libererTableServeur(staffId, numTable);
                }

                case 8 -> {
                    manager.afficherTout();
                    manager.desactiverPersonnel(lireInt(scanner, "ID a desactiver"));
                    sauvegardeManager.signalerAction();
                }

                case 9 -> {
                    manager.afficherTout();
                    int id = lireInt(scanner, "ID a supprimer");
                    System.out.print("> Confirmer ? (o/n) : ");
                    if (scanner.nextLine().trim().equalsIgnoreCase("o")) {
                        manager.supprimerPersonnel(id);
                        sauvegardeManager.signalerAction();
                    }
                }

                case 0 -> retour = true;
                default -> System.out.println("[ERREUR] Choix invalide.");
            }
        }
    }

    // ── Banniere d'accueil ────────────────────────────────────────────────────
    private static void afficherBanniere() {
        System.out.println("\n" +
            "╔═══════════════════════════════════════════╗\n" +
            "║   SYSTEME DE GESTION DE RESTAURANT        ║\n" +
            "║   Module : Reservation / Personnel /      ║\n" +
            "║            Persistance                    ║\n" +
            "╚═══════════════════════════════════════════╝\n");
    }

    // ── Utilitaires de saisie ─────────────────────────────────────────────────
    private static int lireInt(Scanner scanner, String prompt) {
        while (true) {
            System.out.print("\n> " + prompt + " : ");
            try {
                return Integer.parseInt(scanner.nextLine().trim());
            } catch (NumberFormatException e) {
                System.out.println("[ERREUR] Entrez un nombre entier.");
            }
        }
    }

    private static double lireDouble(Scanner scanner, String prompt) {
        while (true) {
            System.out.print("\n> " + prompt + " : ");
            try {
                return Double.parseDouble(
                        scanner.nextLine().trim().replace(",", "."));
            } catch (NumberFormatException e) {
                System.out.println("[ERREUR] Entrez un nombre decimal.");
            }
        }
    }
}