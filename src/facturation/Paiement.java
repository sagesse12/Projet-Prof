package facturation;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * ENCAPSULATION : attributs prives.
 * Serializable : pour la persistance.
 *
 * Represente UN paiement effectue sur UNE facture.
 * Une facture peut avoir plusieurs paiements (paiement partiel).
 */
public class Paiement implements Serializable {

    private static final long serialVersionUID = 1L;

    // ── Enum methodes de paiement ─────────────────────────────────────────────
    public enum MethodePaiement {
        ESPECES,
        CARTE_BANCAIRE,
        TICKET_RESTAURANT,
        VIREMENT,
        CHEQUE
    }

    // Auto-increment des IDs
    private static int compteur = 1;

    // ── Attributs ──────────────────────────────────────────────────────────────
    private int              id;
    private int              idFacture;        // facture concernee
    private double           montant;          // montant de ce paiement
    private MethodePaiement  methode;
    private LocalDateTime    dateHeure;
    private String           reference;        // ex: 4 derniers chiffres carte, num cheque

    // ── Constructeur ──────────────────────────────────────────────────────────
    public Paiement(int idFacture, double montant, MethodePaiement methode) {
        this(idFacture, montant, methode, "");
    }

    public Paiement(int idFacture, double montant,
                    MethodePaiement methode, String reference) {
        this.id         = compteur++;
        this.idFacture  = idFacture;
        this.montant    = montant;
        this.methode    = methode;
        this.reference  = reference;
        this.dateHeure  = LocalDateTime.now();
    }

    // ── Affichage ─────────────────────────────────────────────────────────────
    public String afficherRecapitulatif() {
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
        StringBuilder sb = new StringBuilder();
        sb.append("\n--- RECU DE PAIEMENT ---\n");
        sb.append(String.format("  N° paiement : #%d%n",    id));
        sb.append(String.format("  Facture     : #%d%n",    idFacture));
        sb.append(String.format("  Montant     : %.2f EUR%n", montant));
        sb.append(String.format("  Methode     : %s%n",     methode));
        if (!reference.isEmpty())
            sb.append(String.format("  Reference   : %s%n", reference));
        sb.append(String.format("  Date/Heure  : %s%n",     dateHeure.format(fmt)));
        sb.append("------------------------\n");
        return sb.toString();
    }

    @Override
    public String toString() {
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("dd/MM HH:mm");
        return String.format("[#%d] Facture #%d | %.2f EUR | %s | %s",
                id, idFacture, montant, methode, dateHeure.format(fmt));
    }

    // ── Getters ───────────────────────────────────────────────────────────────
    public int             getId()         { return id; }
    public int             getIdFacture()  { return idFacture; }
    public double          getMontant()    { return montant; }
    public MethodePaiement getMethode()    { return methode; }
    public LocalDateTime   getDateHeure()  { return dateHeure; }
    public String          getReference()  { return reference; }
    public void            setReference(String r) { this.reference = r; }
}