// ─── FICHIER : Main.java ───────────────────────────────────────────────────────
// A placer a la RACINE de src/ (pas dans un sous-package)
// Integre tous les modules : Reservation, Personnel, Persistence,
//                            Commande, Menu, Table, Stock, Facturation, Reporting

import persistence.PersistanceUI;
import persistence.SauvegardeManager;
import personnel.GestionPersonnelManager;
import reservation.ReservationManager;
import reservation.SystemeReservationUI;

import Commande.GestionnaireCommande;
import Commande.ObservateurCuisine;
import Commande.Observateurs;
import Menu.GestionnaireMenu;
import Menu.ElementMenu;
import Table.GestionnaireTable;
import StocksIngredients.GestionnaireStock;
import StocksIngredients.ObservateurStock;
import facturation.FacturationManager;
import facturation.FacturationUI;
import Reporting.RapportVentes;
import Reporting.RapportPlatsPopulaires;
import Reporting.RapportActiviteTables;

import java.util.Scanner;

/**
 * Point d'entree du programme.
 * Cree tous les managers, les injecte dans les UI, et affiche le menu principal.
 */
public class Main {

    public static void main(String[] args) {

        Scanner scanner = new Scanner(System.in);

        // ── 1. Creer les managers ─────────────────────────────────────────────
        ReservationManager      reservationManager = new ReservationManager();
        GestionPersonnelManager personnelManager   = new GestionPersonnelManager();
        SauvegardeManager       sauvegardeManager  = new SauvegardeManager(
                reservationManager,
                personnelManager,
                "Restaurant Java"
        );

        GestionnaireMenu      gestionnaireMenu    = new GestionnaireMenu();
        GestionnaireTable     gestionnaireTable   = new GestionnaireTable();
        GestionnaireCommande  gestionnaireCmd     = new GestionnaireCommande();
        GestionnaireStock     gestionnaireStock   = GestionnaireStock.getInstance();
        FacturationManager    facturationManager  = new FacturationManager();

        // ── 2. Abonner les observateurs aux commandes ─────────────────────────
        gestionnaireCmd.abonner(new ObservateurCuisine());
        gestionnaireCmd.abonner(new Observateurs("Salle"));
        gestionnaireStock.abonner(new ObservateurStock());

        // ── 3. Charger la sauvegarde si elle existe ───────────────────────────
        if (sauvegardeManager.sauvegardeExiste()) {
            System.out.println("\n[INFO] Sauvegarde trouvee, chargement automatique...");
            sauvegardeManager.charger();
        } else {
            System.out.println("\n[INFO] Aucune sauvegarde existante — demarrage a vide.");
        }

        // ── 4. Creer les interfaces console ──────────────────────────────────
        SystemeReservationUI reservationUI  = new SystemeReservationUI(reservationManager, scanner);
        PersistanceUI        persistanceUI  = new PersistanceUI(sauvegardeManager, scanner);
        FacturationUI        facturationUI  = new FacturationUI(facturationManager, scanner);

        // ── 5. Menu principal ─────────────────────────────────────────────────
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
                "|  4.  Menu du restaurant                    |\n" +
                "|  5.  Gestion des Tables                    |\n" +
                "|  6.  Commandes                             |\n" +
                "|  7.  Stocks & Ingredients                  |\n" +
                "|  8.  Facturation & Paiements               |\n" +
                "|  9.  Rapports                              |\n" +
                "|  0.  Quitter (sauvegarde automatique)      |\n" +
                "+=============================================+");

            int choix = lireInt(scanner, "Votre choix");

            switch (choix) {
                case 1 -> reservationUI.afficherMenu();
                case 2 -> menuPersonnel(personnelManager, sauvegardeManager, scanner);
                case 3 -> persistanceUI.afficherMenu();
                case 4 -> menuMenu(gestionnaireMenu, scanner);
                case 5 -> menuTables(gestionnaireTable, scanner);
                case 6 -> menuCommandes(gestionnaireCmd, gestionnaireMenu, scanner);
                case 7 -> menuStock(gestionnaireStock, scanner);
                case 8 -> facturationUI.afficherMenu();
                case 9 -> menuRapports(gestionnaireCmd, scanner);
                case 0 -> {
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

    // ── Menu Personnel ────────────────────────────────────────────────────────
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
                    System.out.print("\n> Nom       : "); String nom    = scanner.nextLine().trim();
                    System.out.print("> Prenom    : "); String prenom  = scanner.nextLine().trim();
                    System.out.print("> Telephone : "); String tel     = scanner.nextLine().trim();
                    double sal = lireDouble(scanner, "Salaire (EUR)");
                    manager.ajouterServeur(nom, prenom, tel, sal);
                    sauvegardeManager.signalerAction();
                }
                case 4 -> {
                    System.out.print("\n> Nom           : "); String nom    = scanner.nextLine().trim();
                    System.out.print("> Prenom        : "); String prenom  = scanner.nextLine().trim();
                    System.out.print("> Telephone     : "); String tel     = scanner.nextLine().trim();
                    double sal = lireDouble(scanner, "Salaire (EUR)");
                    System.out.print("> Specialite    : "); String spe     = scanner.nextLine().trim();
                    int exp = lireInt(scanner, "Annees d'experience");
                    manager.ajouterCuisinier(nom, prenom, tel, sal, spe, exp);
                    sauvegardeManager.signalerAction();
                }
                case 5 -> {
                    System.out.print("\n> Nom         : "); String nom    = scanner.nextLine().trim();
                    System.out.print("> Prenom      : "); String prenom  = scanner.nextLine().trim();
                    System.out.print("> Telephone   : "); String tel     = scanner.nextLine().trim();
                    double sal = lireDouble(scanner, "Salaire (EUR)");
                    System.out.print("> Departement : "); String dept    = scanner.nextLine().trim();
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

    // ── Menu du restaurant ────────────────────────────────────────────────────
    private static void menuMenu(GestionnaireMenu gestionnaireMenu, Scanner scanner) {
        boolean retour = false;
        while (!retour) {
            System.out.println("\n" +
                "+-----------------------------------------+\n" +
                "|           MENU DU RESTAURANT            |\n" +
                "+-----------------------------------------+\n" +
                "|  1. Afficher le menu complet            |\n" +
                "|  2. Afficher par categorie              |\n" +
                "|  3. Ajouter un element                  |\n" +
                "|  4. Supprimer un element                |\n" +
                "|  5. Modifier le nom                     |\n" +
                "|  6. Modifier le prix                    |\n" +
                "|  7. Changer la disponibilite            |\n" +
                "|  0. Retour                              |\n" +
                "+-----------------------------------------+");

            int choix = lireInt(scanner, "Votre choix");
            switch (choix) {
                case 1 -> gestionnaireMenu.afficherMenu();
                case 2 -> {
                    System.out.print("\n> Categorie (ENTREE / PLAT / DESSERT / BOISSON) : ");
                    String cat = scanner.nextLine().trim();
                    gestionnaireMenu.afficherCategorie(cat);
                }
                case 3 -> {
                    System.out.print("\n> Categorie (ENTREE / PLAT / DESSERT / BOISSON) : ");
                    String cat  = scanner.nextLine().trim();
                    System.out.print("> ID unique (ex: P01)    : "); String id   = scanner.nextLine().trim();
                    System.out.print("> Nom                    : "); String nom  = scanner.nextLine().trim();
                    double prix = lireDouble(scanner, "Prix (EUR)");
                    System.out.print("> Description            : "); String desc = scanner.nextLine().trim();
                    gestionnaireMenu.ajouterElement(cat, id, nom, prix, desc);
                }
                case 4 -> {
                    gestionnaireMenu.afficherMenu();
                    System.out.print("\n> ID a supprimer : "); String id = scanner.nextLine().trim();
                    gestionnaireMenu.supprimerElement(id);
                }
                case 5 -> {
                    gestionnaireMenu.afficherMenu();
                    System.out.print("\n> ID : "); String id  = scanner.nextLine().trim();
                    System.out.print("> Nouveau nom : "); String nom = scanner.nextLine().trim();
                    gestionnaireMenu.modifierNom(id, nom);
                }
                case 6 -> {
                    gestionnaireMenu.afficherMenu();
                    System.out.print("\n> ID : "); String id = scanner.nextLine().trim();
                    double prix = lireDouble(scanner, "Nouveau prix (EUR)");
                    gestionnaireMenu.modifierPrix(id, prix);
                }
                case 7 -> {
                    gestionnaireMenu.afficherMenu();
                    System.out.print("\n> ID : "); String id = scanner.nextLine().trim();
                    System.out.print("> Disponible ? (o/n) : ");
                    boolean dispo = scanner.nextLine().trim().equalsIgnoreCase("o");
                    gestionnaireMenu.changerDisponibilite(id, dispo);
                }
                case 0 -> retour = true;
                default -> System.out.println("[ERREUR] Choix invalide.");
            }
        }
    }

    // ── Gestion des tables ────────────────────────────────────────────────────
    private static void menuTables(GestionnaireTable gestionnaireTable, Scanner scanner) {
        boolean retour = false;
        while (!retour) {
            System.out.println("\n" +
                "+-----------------------------------------+\n" +
                "|          GESTION DES TABLES             |\n" +
                "+-----------------------------------------+\n" +
                "|  1. Afficher toutes les tables          |\n" +
                "|  2. Afficher les tables libres          |\n" +
                "|  3. Ajouter une table                   |\n" +
                "|  4. Supprimer une table                 |\n" +
                "|  5. Assigner un client                  |\n" +
                "|  6. Liberer une table                   |\n" +
                "|  7. Reserver une table                  |\n" +
                "|  0. Retour                              |\n" +
                "+-----------------------------------------+");

            int choix = lireInt(scanner, "Votre choix");
            switch (choix) {
                case 1 -> gestionnaireTable.afficherToutes();
                case 2 -> gestionnaireTable.afficherLibres();
                case 3 -> {
                    int numero   = lireInt(scanner, "Numero de la table");
                    int capacite = lireInt(scanner, "Capacite (personnes)");
                    System.out.print("\n> Section (Salle, Terrasse...) : ");
                    String section = scanner.nextLine().trim();
                    gestionnaireTable.ajouterTable(numero, capacite, section);
                }
                case 4 -> {
                    gestionnaireTable.afficherToutes();
                    int numero = lireInt(scanner, "Numero de la table a supprimer");
                    gestionnaireTable.supprimerTable(numero);
                }
                case 5 -> {
                    gestionnaireTable.afficherLibres();
                    int numero = lireInt(scanner, "Numero de table");
                    System.out.print("\n> Nom du client : ");
                    String nom = scanner.nextLine().trim();
                    gestionnaireTable.assignerClient(numero, nom);
                }
                case 6 -> {
                    gestionnaireTable.afficherToutes();
                    int numero = lireInt(scanner, "Numero de table a liberer");
                    gestionnaireTable.libererTable(numero);
                }
                case 7 -> {
                    gestionnaireTable.afficherLibres();
                    int numero = lireInt(scanner, "Numero de table a reserver");
                    System.out.print("\n> Nom du client : ");
                    String nom = scanner.nextLine().trim();
                    gestionnaireTable.reserverTable(numero, nom);
                }
                case 0 -> retour = true;
                default -> System.out.println("[ERREUR] Choix invalide.");
            }
        }
    }

    // ── Gestion des commandes ─────────────────────────────────────────────────
    private static void menuCommandes(GestionnaireCommande gestionnaireCmd,
                                       GestionnaireMenu gestionnaireMenu,
                                       Scanner scanner) {
        boolean retour = false;
        while (!retour) {
            System.out.println("\n" +
                "+-----------------------------------------+\n" +
                "|          GESTION DES COMMANDES          |\n" +
                "+-----------------------------------------+\n" +
                "|  1. Nouvelle commande                   |\n" +
                "|  2. Ajouter un plat a une commande      |\n" +
                "|  3. Retirer un plat                     |\n" +
                "|  4. Afficher une commande               |\n" +
                "|  5. Afficher par statut                 |\n" +
                "|  6. Demarrer la preparation             |\n" +
                "|  7. Marquer comme prete                 |\n" +
                "|  8. Livrer                              |\n" +
                "|  9. Payer                               |\n" +
                "| 10. Annuler                             |\n" +
                "|  0. Retour                              |\n" +
                "+-----------------------------------------+");

            int choix = lireInt(scanner, "Votre choix");
            switch (choix) {
                case 1 -> {
                    int numTable = lireInt(scanner, "Numero de table");
                    gestionnaireCmd.creerCommande(numTable);
                }
                case 2 -> {
                    gestionnaireMenu.afficherDisponibles();
                    int idCmd = lireInt(scanner, "ID de la commande");
                    System.out.print("\n> ID du plat (ex: P01) : ");
                    String idPlat = scanner.nextLine().trim();
                    ElementMenu plat = gestionnaireMenu.trouver(idPlat).orElse(null);
                    if (plat == null) {
                        System.out.println("[ERREUR] Plat introuvable.");
                    } else {
                        int qte = lireInt(scanner, "Quantite");
                        System.out.print("> Notes / allergies (optionnel) : ");
                        String notes = scanner.nextLine().trim();
                        gestionnaireCmd.ajouterPlat(idCmd, plat, qte, notes);
                    }
                }
                case 3 -> {
                    int idCmd = lireInt(scanner, "ID de la commande");
                    System.out.print("\n> ID du plat a retirer : ");
                    String idPlat = scanner.nextLine().trim();
                    gestionnaireCmd.retirerPlat(idCmd, idPlat);
                }
                case 4 -> {
                    int idCmd = lireInt(scanner, "ID de la commande");
                    gestionnaireCmd.afficherUne(idCmd);
                }
                case 5 -> {
                    System.out.print("\n> Statut (NOUVELLE / EN_COURS / PRÊTE / LIVRÉE / PAYÉE / ANNULÉE) : ");
                    String statut = scanner.nextLine().trim();
                    gestionnaireCmd.afficherParStatut(statut);
                }
                case 6  -> gestionnaireCmd.demarrer(lireInt(scanner, "ID de la commande"));
                case 7  -> gestionnaireCmd.prete(lireInt(scanner, "ID de la commande"));
                case 8  -> gestionnaireCmd.livrer(lireInt(scanner, "ID de la commande"));
                case 9  -> gestionnaireCmd.payer(lireInt(scanner, "ID de la commande"));
                case 10 -> gestionnaireCmd.annuler(lireInt(scanner, "ID de la commande"));
                case 0  -> retour = true;
                default -> System.out.println("[ERREUR] Choix invalide.");
            }
        }
    }

    // ── Gestion du stock ──────────────────────────────────────────────────────
    private static void menuStock(GestionnaireStock stock, Scanner scanner) {
        boolean retour = false;
        while (!retour) {
            System.out.println("\n" +
                "+-----------------------------------------+\n" +
                "|         STOCKS & INGREDIENTS            |\n" +
                "+-----------------------------------------+\n" +
                "|  1. Afficher le stock complet           |\n" +
                "|  2. Afficher les alertes                |\n" +
                "|  3. Ajouter un ingredient               |\n" +
                "|  4. Supprimer un ingredient             |\n" +
                "|  5. Consommer                           |\n" +
                "|  6. Reapprovisionner                    |\n" +
                "|  7. Modifier le seuil d'alerte          |\n" +
                "|  0. Retour                              |\n" +
                "+-----------------------------------------+");

            int choix = lireInt(scanner, "Votre choix");
            switch (choix) {
                case 1 -> stock.afficherStock();
                case 2 -> stock.afficherAlertes();
                case 3 -> {
                    System.out.print("\n> ID unique (ex: ING01) : "); String id    = scanner.nextLine().trim();
                    System.out.print("> Nom                   : "); String nom   = scanner.nextLine().trim();
                    double qte    = lireDouble(scanner, "Quantite initiale");
                    double seuil  = lireDouble(scanner, "Seuil d'alerte");
                    System.out.print("> Unite (kg, L, pcs...) : "); String unite = scanner.nextLine().trim();
                    double prix   = lireDouble(scanner, "Prix unitaire (EUR)");
                    stock.ajouter(id, nom, qte, seuil, unite, prix);
                }
                case 4 -> {
                    stock.afficherStock();
                    System.out.print("\n> ID de l'ingredient a supprimer : ");
                    stock.supprimer(scanner.nextLine().trim());
                }
                case 5 -> {
                    stock.afficherStock();
                    System.out.print("\n> ID de l'ingredient : ");
                    String id  = scanner.nextLine().trim();
                    double qte = lireDouble(scanner, "Quantite a consommer");
                    stock.consommer(id, qte);
                }
                case 6 -> {
                    stock.afficherStock();
                    System.out.print("\n> ID de l'ingredient : ");
                    String id  = scanner.nextLine().trim();
                    double qte = lireDouble(scanner, "Quantite a ajouter");
                    stock.reapprovisionner(id, qte);
                }
                case 7 -> {
                    stock.afficherStock();
                    System.out.print("\n> ID de l'ingredient : ");
                    String id     = scanner.nextLine().trim();
                    double seuil  = lireDouble(scanner, "Nouveau seuil");
                    stock.modifierSeuil(id, seuil);
                }
                case 0 -> retour = true;
                default -> System.out.println("[ERREUR] Choix invalide.");
            }
        }
    }

    // ── Rapports ──────────────────────────────────────────────────────────────
    private static void menuRapports(GestionnaireCommande gestionnaireCmd, Scanner scanner) {
        boolean retour = false;
        while (!retour) {
            System.out.println("\n" +
                "+-----------------------------------------+\n" +
                "|              RAPPORTS                   |\n" +
                "+-----------------------------------------+\n" +
                "|  1. Rapport des ventes                  |\n" +
                "|  2. Plats les plus populaires           |\n" +
                "|  3. Activite par table                  |\n" +
                "|  0. Retour                              |\n" +
                "+-----------------------------------------+");

            int choix = lireInt(scanner, "Votre choix");
            switch (choix) {
                case 1 -> new RapportVentes().generer(gestionnaireCmd.getCommandes());
                case 2 -> new RapportPlatsPopulaires().generer(gestionnaireCmd.getCommandes());
                case 3 -> new RapportActiviteTables().generer(gestionnaireCmd.getCommandes());
                case 0 -> retour = true;
                default -> System.out.println("[ERREUR] Choix invalide.");
            }
        }
    }

    // ── Banniere d'accueil ─────────────────────────────────────────────────────
    private static void afficherBanniere() {
        System.out.println("\n" +
            "╔═══════════════════════════════════════════╗\n" +
            "║   SYSTEME DE GESTION DE RESTAURANT        ║\n" +
            "║   Modules : Reservation / Personnel /     ║\n" +
            "║   Commande / Menu / Table / Stock /       ║\n" +
            "║   Facturation / Reporting / Persistance   ║\n" +
            "╚═══════════════════════════════════════════╝\n");
    }

    // ── Utilitaires de saisie ──────────────────────────────────────────────────
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
                System.out.println("[ERREUR] Entrez un nombre decimal (ex: 12.50).");
            }
        }
    }
}