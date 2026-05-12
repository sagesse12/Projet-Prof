package facturation;

import java.util.List;
import java.util.Scanner;

/**
 * Interface console du module facturation.
 * Cette classe ne fait QUE l'affichage et la saisie utilisateur.
 * Toute la logique est deleguee a FacturationManager.
 *
 * Pour utiliser la Factory des strategies de paiement,
 * on propose a l'utilisateur de choisir sa methode,
 * puis on construit la bonne implementation de StrategyPaiement.
 */
public class FacturationUI {

    private final FacturationManager manager;
    private final Scanner            scanner;

    // ── Constructeur ──────────────────────────────────────────────────────────
    public FacturationUI(FacturationManager manager, Scanner scanner) {
        this.manager = manager;
        this.scanner = scanner;
    }

    // ── Menu principal ────────────────────────────────────────────────────────
    public void afficherMenu() {
        boolean retour = false;
        while (!retour) {
            System.out.println("\n"
                + "+-----------------------------------------+\n"
                + "|     FACTURATION & GESTION PAIEMENTS     |\n"
                + "+-----------------------------------------+\n"
                + "|  1. Voir toutes les factures            |\n"
                + "|  2. Voir factures non soldees           |\n"
                + "|  3. Afficher le detail d'une facture    |\n"
                + "|  4. Creer une nouvelle facture          |\n"
                + "|  5. Creer facture liee a reservation    |\n"
                + "|  6. Ajouter une prestation a facture    |\n"
                + "|  7. Encaisser un paiement               |\n"
                + "|  8. Historique des paiements            |\n"
                + "|  9. Annuler une facture                 |\n"
                + "| 10. Rapport facturation                 |\n"
                + "|  0. Retour au menu principal            |\n"
                + "+-----------------------------------------+");

            int choix = lireInt("Votre choix");
            switch (choix) {
                case 1  -> afficherToutes();
                case 2  -> afficherNonSoldees();
                case 3  -> afficherDetail();
                case 4  -> creerFactureSimple();
                case 5  -> creerFactureReservation();
                case 6  -> ajouterPrestation();
                case 7  -> encaisserPaiement();
                case 8  -> afficherHistoriquePaiements();
                case 9  -> annulerFacture();
                case 10 -> System.out.println(manager.genererRapport());
                case 0  -> retour = true;
                default -> System.out.println("[ERREUR] Choix invalide.");
            }
        }
    }

    // ── 1. Voir toutes ────────────────────────────────────────────────────────
    private void afficherToutes() {
        List<Facture> liste = manager.listerToutes();
        System.out.println("\n--- TOUTES LES FACTURES ("
                + liste.size() + ") ---");
        if (liste.isEmpty()) {
            System.out.println("  Aucune facture.");
        } else {
            liste.forEach(System.out::println);
        }
    }

    // ── 2. Factures non soldees ───────────────────────────────────────────────
    private void afficherNonSoldees() {
        List<Facture> liste = manager.listerNonPayees();
        System.out.println("\n--- FACTURES NON SOLDEES ("
                + liste.size() + ") ---");
        if (liste.isEmpty()) {
            System.out.println("  Toutes les factures sont soldees.");
        } else {
            liste.forEach(f -> System.out.printf(
                    "%s  | Reste : %.2f EUR%n", f, f.getResteAPayer()));
        }
    }

    // ── 3. Detail d'une facture ───────────────────────────────────────────────
    private void afficherDetail() {
        afficherToutes();
        int id = lireIntPositif("ID de la facture");
        Facture f = manager.trouverParId(id);
        if (f == null) {
            System.out.println("[ERREUR] Facture #" + id + " introuvable.");
        } else {
            System.out.println(f.afficherTicket());
        }
    }

    // ── 4. Creer facture simple ───────────────────────────────────────────────
    private void creerFactureSimple() {
        System.out.println("\n--- NOUVELLE FACTURE ---");
        String nom = lireStringObligatoire("Nom du client");
        Facture f = manager.creerFacture(nom);
        System.out.println("  Vous pouvez maintenant ajouter des prestations "
                + "(option 6) pour la facture #" + f.getId() + ".");
    }

    // ── 5. Creer facture liee a une reservation ───────────────────────────────
    private void creerFactureReservation() {
        System.out.println("\n--- FACTURE LIEE A UNE RESERVATION ---");
        String nom  = lireStringObligatoire("Nom du client");
        int    idR  = lireIntPositif("ID de la reservation");
        Facture f   = manager.creerFacturePourReservation(nom, idR);
        System.out.println("  Facture #" + f.getId()
                + " creee. Ajoutez les prestations (option 6).");
    }

    // ── 6. Ajouter une prestation ─────────────────────────────────────────────
    private void ajouterPrestation() {
        afficherNonSoldees();
        System.out.println();
        int    idF  = lireIntPositif("ID de la facture");
        String desc = lireStringObligatoire("Description de la prestation");
        int    qte  = lireIntPositif("Quantite");
        double prix = lireDoublePositif("Prix unitaire (EUR)");
        manager.ajouterPrestation(idF, desc, qte, prix);

        // Afficher le total mis a jour
        Facture f = manager.trouverParId(idF);
        if (f != null) {
            System.out.printf("  Nouveau total facture #%d : %.2f EUR%n",
                    idF, f.getMontantTotal());
        }
    }

    // ── 7. Encaisser un paiement ─────────────────────────────────────────────
    private void encaisserPaiement() {
        afficherNonSoldees();
        if (manager.listerNonPayees().isEmpty()) {
            System.out.println("[INFO] Aucune facture en attente de paiement.");
            return;
        }

        int    idF     = lireIntPositif("ID de la facture a encaisser");
        Facture f = manager.trouverParId(idF);
        if (f == null) {
            System.out.println("[ERREUR] Facture introuvable.");
            return;
        }

        // Afficher le ticket et le reste a payer
        System.out.println(f.afficherTicket());
        System.out.printf("  Reste a payer : %.2f EUR%n", f.getResteAPayer());

        // Saisie du montant
        double montant = lireDoublePositif("Montant a encaisser");

        // Choix de la methode de paiement → STRATEGY PATTERN
        System.out.println("\n  Methode de paiement :");
        System.out.println("    1. Especes");
        System.out.println("    2. Carte bancaire");
        System.out.println("    3. Ticket restaurant");
        System.out.println("    4. Virement");

        int methode = lireInt("Votre choix");

        // Construction de la bonne strategie (Factory-like)
        StrategyPaiement strategie = switch (methode) {
            case 1 -> {
                double remis = lireDoublePositif("Montant remis par le client");
                yield new PaiementEspeces(remis);
            }
            case 2 -> {
                String carte = lireString("4 derniers chiffres de la carte");
                if (carte.isBlank()) carte = "0000";
                yield new PaiementCarte(carte);
            }
            case 3 -> {
                double tickets = lireDoublePositif("Valeur totale des tickets");
                double diff = montant - tickets;
                if (diff > 0) {
                    System.out.printf(
                        "  Il manque %.2f EUR. Complement par CB ? (o/n) : ", diff);
                    String rep = scanner.nextLine().trim();
                    if (rep.equalsIgnoreCase("o")) {
                        yield new PaiementTicketResto(tickets, diff);
                    }
                }
                yield new PaiementTicketResto(tickets);
            }
            case 4 -> {
                String ref = lireStringObligatoire("Reference du virement");
                yield new PaiementVirement(ref);
            }
            default -> {
                System.out.println("[ERREUR] Methode invalide. Annulation.");
                yield null;
            }
        };

        if (strategie != null) {
            manager.traiterPaiement(idF, montant, strategie);
        }
    }

    // ── 8. Historique paiements ───────────────────────────────────────────────
    private void afficherHistoriquePaiements() {
        System.out.println("\n  Voir paiements pour (1) une facture, (2) tous");
        int choix = lireInt("Choix");
        if (choix == 1) {
            afficherToutes();
            int idF = lireIntPositif("ID de la facture");
            List<Paiement> ps = manager.getPaiementsDeFacture(idF);
            System.out.println("--- Paiements de la facture #" + idF + " ---");
            if (ps.isEmpty()) System.out.println("  Aucun paiement.");
            else ps.forEach(System.out::println);
        } else {
            List<Paiement> tous = manager.listerPaiements();
            System.out.println("--- TOUS LES PAIEMENTS (" + tous.size() + ") ---");
            if (tous.isEmpty()) System.out.println("  Aucun paiement.");
            else tous.forEach(System.out::println);
        }
    }

    // ── 9. Annuler une facture ────────────────────────────────────────────────
    private void annulerFacture() {
        afficherNonSoldees();
        int idF = lireIntPositif("ID de la facture a annuler");
        System.out.print("\n> Confirmer l'annulation ? (o/n) : ");
        String conf = scanner.nextLine().trim();
        if (conf.equalsIgnoreCase("o")) {
            manager.annulerFacture(idF);
        } else {
            System.out.println("[INFO] Annulation abandonnee.");
        }
    }

    // ─────────────────────────────────────────────────────────────────────────
    // UTILITAIRES DE SAISIE
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

    private int lireIntPositif(String prompt) {
        int v;
        do {
            v = lireInt(prompt);
            if (v <= 0) System.out.println("[ERREUR] La valeur doit etre > 0.");
        } while (v <= 0);
        return v;
    }

    private double lireDoublePositif(String prompt) {
        while (true) {
            System.out.print("\n> " + prompt + " : ");
            try {
                double v = Double.parseDouble(
                        scanner.nextLine().trim().replace(",", "."));
                if (v <= 0) {
                    System.out.println("[ERREUR] La valeur doit etre > 0.");
                } else {
                    return v;
                }
            } catch (NumberFormatException e) {
                System.out.println("[ERREUR] Entrez un nombre decimal (ex: 12.50).");
            }
        }
    }

    private String lireString(String prompt) {
        System.out.print("\n> " + prompt + " : ");
        return scanner.nextLine().trim();
    }

    private String lireStringObligatoire(String prompt) {
        String s;
        do {
            s = lireString(prompt);
            if (s.isBlank()) System.out.println("[ERREUR] Ce champ est obligatoire.");
        } while (s.isBlank());
        return s;
    }
}