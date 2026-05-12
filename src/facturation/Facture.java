package facturation;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

/**
 * ENCAPSULATION : tous les attributs sont prives.
 * Serializable : pour que SauvegardeManager puisse persister les factures.
 *
 * Une Facture est liee a une reservation (via son id) ou directement a un client.
 * Elle contient plusieurs LigneFacture (une par prestation ou consommation).
 * AGREGATION : une Facture contient une List<LigneFacture>.
 */
public class Facture implements Serializable {

    private static final long serialVersionUID = 1L;

    // ── Enum statut ────────────────────────────────────────────────────────────
    public enum StatutFacture {
        EN_ATTENTE,   // creee, pas encore payee
        PARTIELLEMENT_PAYEE,
        PAYEE,        // soldee
        ANNULEE
    }

    // Auto-increment des IDs
    private static int compteur = 1;

    // ── Attributs ──────────────────────────────────────────────────────────────
    private int             id;
    private String          nomClient;         // nom du client facture
    private int             idReservation;     // -1 si pas de reservation liee
    private List<LigneFacture> lignes;         // AGREGATION
    private double          montantPaye;       // cumul des paiements recus
    private StatutFacture   statut;
    private LocalDateTime   dateCreation;
    private LocalDateTime   datePaiement;      // null tant que non soldee
    private String          notes;

    // ── Classe interne LigneFacture ───────────────────────────────────────────
    /**
     * Represente une ligne de facturation (une prestation).
     * COHESION : contient uniquement les donnees d'une ligne.
     */
    public static class LigneFacture implements Serializable {
        private static final long serialVersionUID = 1L;
        private String  description;
        private int     quantite;
        private double  prixUnitaire;

        public LigneFacture(String description, int quantite, double prixUnitaire) {
            this.description  = description;
            this.quantite     = quantite;
            this.prixUnitaire = prixUnitaire;
        }

        public double getSousTotal() { return quantite * prixUnitaire; }

        @Override
        public String toString() {
            return String.format("  %-30s  x%-3d  %7.2f EUR  =>  %8.2f EUR",
                    description, quantite, prixUnitaire, getSousTotal());
        }

        // Getters
        public String  getDescription()  { return description; }
        public int     getQuantite()      { return quantite; }
        public double  getPrixUnitaire()  { return prixUnitaire; }
        public void    setDescription(String d) { this.description = d; }
        public void    setQuantite(int q)        { this.quantite = q; }
        public void    setPrixUnitaire(double p) { this.prixUnitaire = p; }
    }

    // ── Constructeurs ─────────────────────────────────────────────────────────
    /** Facture pour un client sans reservation liee */
    public Facture(String nomClient) {
        this(nomClient, -1);
    }

    /** Facture liee a une reservation */
    public Facture(String nomClient, int idReservation) {
        this.id            = compteur++;
        this.nomClient     = nomClient;
        this.idReservation = idReservation;
        this.lignes        = new ArrayList<>();
        this.montantPaye   = 0.0;
        this.statut        = StatutFacture.EN_ATTENTE;
        this.dateCreation  = LocalDateTime.now();
        this.datePaiement  = null;
        this.notes         = "";
    }

    // ── Methodes metier ───────────────────────────────────────────────────────

    /** Ajoute une prestation a la facture */
    public void ajouterLigne(String description, int quantite, double prixUnitaire) {
        lignes.add(new LigneFacture(description, quantite, prixUnitaire));
    }

    /** Calcule le montant total HT de la facture */
    public double getMontantTotal() {
        return lignes.stream().mapToDouble(LigneFacture::getSousTotal).sum();
    }

    /** Calcule le reste a payer */
    public double getResteAPayer() {
        return Math.max(0, getMontantTotal() - montantPaye);
    }

    /** Enregistre un paiement et met a jour le statut */
    public boolean enregistrerPaiement(double montant) {
        if (statut == StatutFacture.PAYEE || statut == StatutFacture.ANNULEE) {
            System.out.println("[ERREUR] Facture #" + id
                    + " : impossible d'enregistrer un paiement (statut : " + statut + ").");
            return false;
        }
        if (montant <= 0) {
            System.out.println("[ERREUR] Le montant du paiement doit etre positif.");
            return false;
        }
        this.montantPaye += montant;
        // Mise a jour du statut
        double total = getMontantTotal();
        if (this.montantPaye >= total) {
            this.montantPaye = total; // pas de sur-paiement
            this.statut       = StatutFacture.PAYEE;
            this.datePaiement = LocalDateTime.now();
            System.out.println("[OK] Facture #" + id + " soldee. Merci !");
        } else {
            this.statut = StatutFacture.PARTIELLEMENT_PAYEE;
            System.out.printf("[OK] Paiement partiel enregistre. Reste : %.2f EUR%n",
                    getResteAPayer());
        }
        return true;
    }

    /** Annule la facture (uniquement si non payee) */
    public boolean annuler() {
        if (statut == StatutFacture.PAYEE) {
            System.out.println("[ERREUR] Impossible d'annuler une facture deja payee.");
            return false;
        }
        this.statut = StatutFacture.ANNULEE;
        System.out.println("[OK] Facture #" + id + " annulee.");
        return true;
    }

    // ── Affichage ─────────────────────────────────────────────────────────────

    /** Affiche le ticket/recapitulatif complet de la facture */
    public String afficherTicket() {
        DateTimeFormatter fmt =
                DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
        StringBuilder sb = new StringBuilder();
        sb.append("\n");
        sb.append("=================================================\n");
        sb.append(String.format("  FACTURE #%d\n", id));
        sb.append(String.format("  Client       : %s\n", nomClient));
        if (idReservation > 0)
            sb.append(String.format("  Reservation  : #%d\n", idReservation));
        sb.append(String.format("  Date         : %s\n",
                dateCreation.format(fmt)));
        sb.append(String.format("  Statut       : %s\n", statut));
        sb.append("-------------------------------------------------\n");
        if (lignes.isEmpty()) {
            sb.append("  (Aucune prestation enregistree)\n");
        } else {
            sb.append(String.format("  %-30s  %-5s  %-10s  %-10s%n",
                    "Description", "Qte", "P.U.", "Sous-total"));
            sb.append("  " + "-".repeat(63) + "\n");
            lignes.forEach(l -> sb.append(l.toString()).append("\n"));
        }
        sb.append("-------------------------------------------------\n");
        sb.append(String.format("  TOTAL                              %8.2f EUR%n",
                getMontantTotal()));
        sb.append(String.format("  Deja regle                         %8.2f EUR%n",
                montantPaye));
        sb.append(String.format("  RESTE A PAYER                      %8.2f EUR%n",
                getResteAPayer()));
        if (datePaiement != null)
            sb.append(String.format("  Paye le      : %s\n",
                    datePaiement.format(fmt)));
        if (!notes.isEmpty())
            sb.append(String.format("  Note         : %s\n", notes));
        sb.append("=================================================\n");
        return sb.toString();
    }

    @Override
    public String toString() {
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("dd/MM HH:mm");
        return String.format("[#%d] %-20s | %s | Total: %7.2f EUR | Regle: %7.2f EUR | %s",
                id, nomClient, dateCreation.format(fmt),
                getMontantTotal(), montantPaye, statut);
    }

    // ── Getters & Setters ─────────────────────────────────────────────────────
    public int                getId()              { return id; }
    public String             getNomClient()        { return nomClient; }
    public void               setNomClient(String n){ this.nomClient = n; }
    public int                getIdReservation()   { return idReservation; }
    public List<LigneFacture> getLignes()           { return lignes; }
    public double             getMontantPaye()      { return montantPaye; }
    public StatutFacture      getStatut()           { return statut; }
    public void               setStatut(StatutFacture s) { this.statut = s; }
    public LocalDateTime      getDateCreation()     { return dateCreation; }
    public LocalDateTime      getDatePaiement()     { return datePaiement; }
    public String             getNotes()            { return notes; }
    public void               setNotes(String n)   { this.notes = n; }
}