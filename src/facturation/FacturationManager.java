package facturation;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * COHESION : une seule responsabilite = gerer les factures et les paiements.
 *
 * C'est le "cerveau" du module facturation :
 *   - il cree et stocke les factures
 *   - il orchestre les paiements via le STRATEGY PATTERN
 *   - il expose les statistiques (chiffre d'affaires, historique)
 *
 * Il n'a PAS de dependance vers reservation/ ou personnel/
 * pour rester independant. On lui passe les infos dont il a besoin
 * (nomClient, idReservation) en parametre.
 */
public class FacturationManager {

    // ── Stockage local ────────────────────────────────────────────────────────
    private List<Facture>  factures;
    private List<Paiement> paiements;

    // ── Constructeur ──────────────────────────────────────────────────────────
    public FacturationManager() {
        this.factures  = new ArrayList<>();
        this.paiements = new ArrayList<>();
    }

    // ─────────────────────────────────────────────────────────────────────────
    // CREER UNE FACTURE
    // ─────────────────────────────────────────────────────────────────────────

    /** Cree une facture simple (sans reservation liee) */
    public Facture creerFacture(String nomClient) {
        Facture f = new Facture(nomClient);
        factures.add(f);
        System.out.println("[OK] Facture #" + f.getId()
                + " creee pour " + nomClient + ".");
        return f;
    }

    /** Cree une facture liee a une reservation */
    public Facture creerFacturePourReservation(String nomClient, int idReservation) {
        // Verifier qu'une facture n'existe pas deja pour cette reservation
        boolean existe = factures.stream()
                .anyMatch(f -> f.getIdReservation() == idReservation
                            && f.getStatut() != Facture.StatutFacture.ANNULEE);
        if (existe) {
            System.out.println("[AVERTISSEMENT] Une facture existe deja "
                    + "pour la reservation #" + idReservation + ".");
        }
        Facture f = new Facture(nomClient, idReservation);
        factures.add(f);
        System.out.println("[OK] Facture #" + f.getId()
                + " creee pour la reservation #" + idReservation
                + " (client : " + nomClient + ").");
        return f;
    }

    // ─────────────────────────────────────────────────────────────────────────
    // AJOUTER DES PRESTATIONS
    // ─────────────────────────────────────────────────────────────────────────

    /**
     * Ajoute une ligne de prestation a une facture existante.
     * Ne peut pas modifier une facture payee ou annulee.
     */
    public boolean ajouterPrestation(int idFacture, String description,
                                      int quantite, double prixUnitaire) {
        Facture f = trouverParId(idFacture);
        if (f == null) {
            System.out.println("[ERREUR] Facture #" + idFacture + " introuvable.");
            return false;
        }
        if (f.getStatut() == Facture.StatutFacture.PAYEE
                || f.getStatut() == Facture.StatutFacture.ANNULEE) {
            System.out.println("[ERREUR] Impossible de modifier la facture #"
                    + idFacture + " (statut : " + f.getStatut() + ").");
            return false;
        }
        if (quantite <= 0 || prixUnitaire < 0) {
            System.out.println("[ERREUR] Quantite ou prix invalide.");
            return false;
        }
        f.ajouterLigne(description, quantite, prixUnitaire);
        System.out.printf("[OK] Ajoute : %dx %s (%.2f EUR/u) a la facture #%d%n",
                quantite, description, prixUnitaire, idFacture);
        return true;
    }

    // ─────────────────────────────────────────────────────────────────────────
    // TRAITER UN PAIEMENT — STRATEGY PATTERN
    // ─────────────────────────────────────────────────────────────────────────

    /**
     * Traite un paiement pour une facture en utilisant la strategie fournie.
     *
     * STRATEGY PATTERN : on passe la strategie en parametre.
     * FacturationManager ne sait pas comment le paiement est fait
     * (especes, carte...) — il delege ca a la strategie.
     *
     * @param idFacture  id de la facture a payer
     * @param montant    montant de CE paiement (peut etre partiel)
     * @param strategie  la strategie de paiement choisie
     * @return true si le paiement a ete accepte et enregistre
     */
    public boolean traiterPaiement(int idFacture, double montant,
                                    StrategyPaiement strategie) {
        Facture f = trouverParId(idFacture);
        if (f == null) {
            System.out.println("[ERREUR] Facture #" + idFacture + " introuvable.");
            return false;
        }
        if (f.getStatut() == Facture.StatutFacture.PAYEE) {
            System.out.println("[INFO] Facture #" + idFacture + " deja soldee.");
            return false;
        }
        if (f.getStatut() == Facture.StatutFacture.ANNULEE) {
            System.out.println("[ERREUR] Impossible de payer une facture annulee.");
            return false;
        }
        double resteAPayer = f.getResteAPayer();
        if (montant > resteAPayer) {
            System.out.printf("[AVERTISSEMENT] Montant saisi (%.2f) > reste a payer "
                    + "(%.2f). Ajuste au reste.%n", montant, resteAPayer);
            montant = resteAPayer;
        }

        // STRATEGY : delegation du traitement physique du paiement
        System.out.printf("%n-- Traitement paiement de %.2f EUR -- Methode : %s%n",
                montant, strategie.getLibelleMethode());
        boolean ok = strategie.payer(montant);

        if (ok) {
            // Enregistrer dans la facture
            f.enregistrerPaiement(montant);
            // Creer un objet Paiement pour l'historique
            Paiement p = new Paiement(
                    idFacture, montant,
                    Paiement.MethodePaiement.valueOf(strategie.getLibelleMethode()),
                    strategie.getReference()
            );
            paiements.add(p);
            // Afficher le recu
            System.out.println(p.afficherRecapitulatif());
        } else {
            System.out.println("[ERREUR] Paiement refuse par la strategie.");
        }
        return ok;
    }

    // ─────────────────────────────────────────────────────────────────────────
    // ANNULER
    // ─────────────────────────────────────────────────────────────────────────

    public boolean annulerFacture(int idFacture) {
        Facture f = trouverParId(idFacture);
        if (f == null) {
            System.out.println("[ERREUR] Facture #" + idFacture + " introuvable.");
            return false;
        }
        return f.annuler();
    }

    // ─────────────────────────────────────────────────────────────────────────
    // RECHERCHES
    // ─────────────────────────────────────────────────────────────────────────

    public Facture trouverParId(int id) {
        return factures.stream()
                .filter(f -> f.getId() == id)
                .findFirst().orElse(null);
    }

    public List<Facture> listerToutes() {
        return factures;
    }

    public List<Facture> listerNonPayees() {
        return factures.stream()
                .filter(f -> f.getStatut() != Facture.StatutFacture.PAYEE
                          && f.getStatut() != Facture.StatutFacture.ANNULEE)
                .collect(Collectors.toList());
    }

    public List<Facture> listerPayees() {
        return factures.stream()
                .filter(f -> f.getStatut() == Facture.StatutFacture.PAYEE)
                .collect(Collectors.toList());
    }

    public List<Paiement> listerPaiements() {
        return paiements;
    }

    /** Historique des paiements pour une facture donnee */
    public List<Paiement> getPaiementsDeFacture(int idFacture) {
        return paiements.stream()
                .filter(p -> p.getIdFacture() == idFacture)
                .collect(Collectors.toList());
    }

    // ─────────────────────────────────────────────────────────────────────────
    // STATISTIQUES / RAPPORT
    // ─────────────────────────────────────────────────────────────────────────

    /** Calcule le chiffre d'affaires total (factures payees) */
    public double getChiffreAffaires() {
        return factures.stream()
                .filter(f -> f.getStatut() == Facture.StatutFacture.PAYEE)
                .mapToDouble(Facture::getMontantTotal)
                .sum();
    }

    /** Total des impayees (factures non soldees et non annulees) */
    public double getTotalImpaye() {
        return factures.stream()
                .filter(f -> f.getStatut() != Facture.StatutFacture.PAYEE
                          && f.getStatut() != Facture.StatutFacture.ANNULEE)
                .mapToDouble(Facture::getResteAPayer)
                .sum();
    }

    /** Genere un rapport textuel complet */
    public String genererRapport() {
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        long nbPayees     = listerPayees().size();
        long nbEnAttente  = listerNonPayees().size();
        long nbAnnulees   = factures.stream()
                .filter(f -> f.getStatut() == Facture.StatutFacture.ANNULEE)
                .count();

        StringBuilder sb = new StringBuilder();
        sb.append("\n=================================================\n");
        sb.append("        RAPPORT FACTURATION & PAIEMENTS\n");
        sb.append("        Au ").append(LocalDate.now().format(fmt)).append("\n");
        sb.append("=================================================\n");
        sb.append(String.format("  Factures payees        : %d%n",   nbPayees));
        sb.append(String.format("  Factures en attente    : %d%n",   nbEnAttente));
        sb.append(String.format("  Factures annulees      : %d%n",   nbAnnulees));
        sb.append(String.format("  Total transactions     : %d%n",   factures.size()));
        sb.append("-------------------------------------------------\n");
        sb.append(String.format("  CA total (paye)        : %10.2f EUR%n",
                getChiffreAffaires()));
        sb.append(String.format("  Total impaye           : %10.2f EUR%n",
                getTotalImpaye()));
        sb.append("-------------------------------------------------\n");
        sb.append("  Factures non soldees :\n");
        listerNonPayees().forEach(f ->
                sb.append(String.format("    #%-4d %-20s  Reste: %.2f EUR%n",
                        f.getId(), f.getNomClient(), f.getResteAPayer())));
        sb.append("=================================================\n");
        return sb.toString();
    }

    // ─────────────────────────────────────────────────────────────────────────
    // ACCES POUR LA PERSISTANCE
    // ─────────────────────────────────────────────────────────────────────────

    public List<Facture>  getFactures()                            { return factures; }
    public void           setFactures(List<Facture> factures)      { this.factures = factures; }
    public List<Paiement> getPaiements()                           { return paiements; }
    public void           setPaiements(List<Paiement> paiements)   { this.paiements = paiements; }
}