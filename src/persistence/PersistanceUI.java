package persistence;

import java.util.Scanner;

/**
 * Interface console du module persistance.
 * Appelle SauvegardeManager pour toutes les operations.
 * Cette classe ne fait QUE l'affichage et la saisie.
 */
public class PersistanceUI {

    private final SauvegardeManager manager;
    private final Scanner           scanner;

    // ── Constructeur ──────────────────────────────────────────────────────────
    public PersistanceUI(SauvegardeManager manager, Scanner scanner) {
        this.manager = manager;
        this.scanner = scanner;
    }

    // ── Menu principal du module ───────────────────────────────────────────────
    public void afficherMenu() {
        boolean retour = false;
        while (!retour) {
            System.out.println("\n" +
                "+-----------------------------------------+\n" +
                "|       SAUVEGARDE & PERSISTANCE          |\n" +
                "+-----------------------------------------+\n" +
                "|  1. Sauvegarder maintenant              |\n" +
                "|  2. Charger la sauvegarde principale    |\n" +
                "|  3. Creer un backup horodate            |\n" +
                "|  4. Lister les sauvegardes              |\n" +
                "|  5. Charger depuis un backup specifique |\n" +
                "|  6. Supprimer une sauvegarde            |\n" +
                "|  7. Exporter reservations en CSV        |\n" +
                "|  8. Verifier l'integrite des donnees    |\n" +
                "|  0. Retour au menu principal            |\n" +
                "+-----------------------------------------+");

            int choix = lireInt("Votre choix");
            switch (choix) {
                case 1 -> sauvegarderMaintenant();
                case 2 -> chargerPrincipal();
                case 3 -> creerBackup();
                case 4 -> manager.listerSauvegardes();
                case 5 -> chargerDepuisBackup();
                case 6 -> supprimerSauvegarde();
                case 7 -> manager.exporterReservationsCSV();
                case 8 -> manager.verifierIntegrite();
                case 0 -> retour = true;
                default -> System.out.println("[ERREUR] Choix invalide.");
            }
        }
    }

    // ── 1. Sauvegarder ────────────────────────────────────────────────────────
    private void sauvegarderMaintenant() {
        System.out.println("\n[INFO] Sauvegarde en cours...");
        boolean ok = manager.sauvegarder();
        if (!ok) {
            System.out.println("[CONSEIL] Verifiez que le dossier 'data/' "
                    + "est accessible en ecriture.");
        }
    }

    // ── 2. Charger principal ──────────────────────────────────────────────────
    private void chargerPrincipal() {
        if (!manager.sauvegardeExiste()) {
            System.out.println("[INFO] Aucune sauvegarde principale trouvee.");
            System.out.println("       Effectuez d'abord une sauvegarde (option 1).");
            return;
        }

        System.out.println("\n[ATTENTION] Le chargement va remplacer toutes");
        System.out.println("            les donnees actuelles.");
        System.out.print("\n> Confirmer ? (o/n) : ");
        String conf = scanner.nextLine().trim();

        if (conf.equalsIgnoreCase("o")) {
            manager.charger();
        } else {
            System.out.println("[INFO] Chargement annule.");
        }
    }

    // ── 3. Backup horodate ────────────────────────────────────────────────────
    private void creerBackup() {
        System.out.println("\n[INFO] Creation d'un backup horodate...");
        boolean ok = manager.creerBackup();
        if (ok) {
            System.out.println("[CONSEIL] Les backups sont dans le dossier 'data/'.");
        }
    }

    // ── 5. Charger depuis un backup ───────────────────────────────────────────
    private void chargerDepuisBackup() {
        manager.listerSauvegardes();

        System.out.print("\n> Nom du fichier a charger (ex: backup_2025-12-25_19h30.ser) : ");
        String nomFichier = scanner.nextLine().trim();

        if (nomFichier.isBlank()) {
            System.out.println("[INFO] Chargement annule.");
            return;
        }

        System.out.println("\n[ATTENTION] Ceci va remplacer toutes les donnees actuelles.");
        System.out.print("> Confirmer ? (o/n) : ");
        String conf = scanner.nextLine().trim();

        if (conf.equalsIgnoreCase("o")) {
            manager.chargerDepuis("data/" + nomFichier);
        } else {
            System.out.println("[INFO] Chargement annule.");
        }
    }

    // ── 6. Supprimer ──────────────────────────────────────────────────────────
    private void supprimerSauvegarde() {
        manager.listerSauvegardes();

        System.out.print("\n> Nom du fichier a supprimer : ");
        String nomFichier = scanner.nextLine().trim();

        if (nomFichier.isBlank()) {
            System.out.println("[INFO] Suppression annulee.");
            return;
        }

        System.out.print("> Confirmer la suppression de '"
                + nomFichier + "' ? (o/n) : ");
        String conf = scanner.nextLine().trim();

        if (conf.equalsIgnoreCase("o")) {
            manager.supprimerSauvegarde(nomFichier);
        } else {
            System.out.println("[INFO] Suppression annulee.");
        }
    }

    // ─────────────────────────────────────────────────────────────────────────
    // UTILITAIRES
    // ─────────────────────────────────────────────────────────────────────────

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
}