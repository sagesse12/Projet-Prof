package facturation;

/**
 * STRATEGY PATTERN
 * ─────────────────
 * Interface definissant le contrat pour toutes les strategies de paiement.
 *
 * L'objectif du pattern Strategy : on peut changer la facon de payer
 * sans modifier FacturationManager. On passe juste une implementation
 * differente a la methode traiterPaiement().
 *
 * Les 3 strategies concretes sont dans ce meme fichier pour simplicite.
 * Dans un vrai projet on mettrait chacune dans son propre fichier.
 */
public interface StrategyPaiement {

    /**
     * Realise le paiement du montant donne.
     * @param montant montant a encaisser
     * @return true si le paiement est valide et accepte
     */
    boolean payer(double montant);

    /**
     * Retourne le nom de la methode de paiement.
     * POLYMORPHISME : chaque classe retourne son propre libelle.
     */
    String getLibelleMethode();

    /**
     * Retourne la reference du paiement (numero de carte, cheque...).
     * Peut etre vide si non applicable.
     */
    String getReference();
}


// ─────────────────────────────────────────────────────────────────────────────
// STRATEGIE 1 : Paiement en especes
// ─────────────────────────────────────────────────────────────────────────────
class PaiementEspeces implements StrategyPaiement {

    private final double montantRemis; // ce que le client donne physiquement

    public PaiementEspeces(double montantRemis) {
        this.montantRemis = montantRemis;
    }

    @Override
    public boolean payer(double montant) {
        if (montantRemis < montant) {
            System.out.printf(
                "[ERREUR] Especes insuffisantes. Remis: %.2f EUR | Du: %.2f EUR%n",
                montantRemis, montant);
            return false;
        }
        double monnaie = montantRemis - montant;
        System.out.printf(
            "[OK] Paiement especes : %.2f EUR remis. Monnaie a rendre : %.2f EUR%n",
            montantRemis, monnaie);
        return true;
    }

    @Override
    public String getLibelleMethode() { return "ESPECES"; }

    @Override
    public String getReference()      { return ""; }
}


// ─────────────────────────────────────────────────────────────────────────────
// STRATEGIE 2 : Paiement par carte bancaire
// ─────────────────────────────────────────────────────────────────────────────
class PaiementCarte implements StrategyPaiement {

    private final String derniers4Chiffres; // reference partielle de la carte

    public PaiementCarte(String derniers4Chiffres) {
        this.derniers4Chiffres = derniers4Chiffres;
    }

    @Override
    public boolean payer(double montant) {
        System.out.printf(
            "[CB] Carte **** **** **** %s — Montant : %.2f EUR%n",
            derniers4Chiffres, montant);
        System.out.println("[CB] Autorisation en cours...");
        // En vrai : appel a un terminal TPE ou API bancaire
        System.out.println("[CB] Transaction autorisee. Paiement valide.");
        return true;
    }

    @Override
    public String getLibelleMethode() { return "CARTE_BANCAIRE"; }

    @Override
    public String getReference()      { return "****" + derniers4Chiffres; }
}


// ─────────────────────────────────────────────────────────────────────────────
// STRATEGIE 3 : Ticket Restaurant
// ─────────────────────────────────────────────────────────────────────────────
class PaiementTicketResto implements StrategyPaiement {

    private final double valeurTickets;     // total des tickets presentes
    private final double complementCB;      // complement paye par CB si insuffisant

    /** Tickets seulement (doit couvrir la totalite du montant) */
    public PaiementTicketResto(double valeurTickets) {
        this(valeurTickets, 0.0);
    }

    /** Tickets + complement CB */
    public PaiementTicketResto(double valeurTickets, double complementCB) {
        this.valeurTickets = valeurTickets;
        this.complementCB  = complementCB;
    }

    @Override
    public boolean payer(double montant) {
        double totalCouvre = valeurTickets + complementCB;
        if (totalCouvre < montant) {
            System.out.printf(
                "[ERREUR] Montant insuffisant. Tickets : %.2f EUR + CB : %.2f EUR = %.2f EUR < %.2f EUR%n",
                valeurTickets, complementCB, totalCouvre, montant);
            return false;
        }
        System.out.printf(
            "[OK] Tickets resto : %.2f EUR%n", valeurTickets);
        if (complementCB > 0)
            System.out.printf("     Complement CB : %.2f EUR%n", complementCB);
        System.out.printf("     Total couvert  : %.2f EUR pour %.2f EUR — OK%n",
            totalCouvre, montant);
        return true;
    }

    @Override
    public String getLibelleMethode() { return "TICKET_RESTAURANT"; }

    @Override
    public String getReference()      { return ""; }
}


// ─────────────────────────────────────────────────────────────────────────────
// STRATEGIE 4 : Virement bancaire
// ─────────────────────────────────────────────────────────────────────────────
class PaiementVirement implements StrategyPaiement {

    private final String referenceVirement;

    public PaiementVirement(String referenceVirement) {
        this.referenceVirement = referenceVirement;
    }

    @Override
    public boolean payer(double montant) {
        System.out.printf(
            "[VIR] Virement bancaire ref. %s — %.2f EUR%n",
            referenceVirement, montant);
        System.out.println("[VIR] Paiement enregistre en attente de confirmation bancaire.");
        return true;
    }

    @Override
    public String getLibelleMethode() { return "VIREMENT"; }

    @Override
    public String getReference()      { return referenceVirement; }
}