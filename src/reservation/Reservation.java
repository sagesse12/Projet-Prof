package reservation;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * ENCAPSULATION : tous les attributs sont prives, acces via getters/setters
 * Serializable : necessaire pour que PersistanceUI puisse sauvegarder
 */
public class Reservation implements Serializable {

    private static final long serialVersionUID = 1L;

    // ── Enum des statuts possibles ─────────────────────────────────────────────
    public enum StatutReservation {
        EN_ATTENTE,   // vient d'etre creee
        CONFIRMEE,    // validee par le gerant
        ANNULEE,      // annulee par le client ou le gerant
        TERMINEE      // la soiree a eu lieu
    }

    // Auto-increment des IDs
    private static int compteur = 1;

    // ── Attributs (ENCAPSULATION : tous prives) ────────────────────────────────
    private int id;
    private String nomClient;
    private String telephone;
    private int nombrePersonnes;
    private int numeroTable;
    private LocalDateTime dateHeure;
    private String occasion;       // anniversaire, repas d'affaires, etc.
    private String observations;   // allergies, demandes speciales
    private StatutReservation statut;

    // ── Constructeur ──────────────────────────────────────────────────────────
    public Reservation(String nomClient, String telephone,
                       int nombrePersonnes, int numeroTable,
                       LocalDateTime dateHeure) {
        this.id             = compteur++;
        this.nomClient      = nomClient;
        this.telephone      = telephone;
        this.nombrePersonnes = nombrePersonnes;
        this.numeroTable    = numeroTable;
        this.dateHeure      = dateHeure;
        this.occasion       = "";
        this.observations   = "";
        this.statut         = StatutReservation.EN_ATTENTE;
    }

    // ── Affichage detaille ────────────────────────────────────────────────────
    public String afficherDetails() {
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("dd/MM/yyyy a HH:mm");
        return String.format(
            "\n======================================\n" +
            "  RESERVATION #%d\n" +
            "  Client     : %s  (%s)\n" +
            "  Date       : %s\n" +
            "  Personnes  : %d\n" +
            "  Table      : %d\n" +
            "  Occasion   : %s\n" +
            "  Notes      : %s\n" +
            "  Statut     : %s\n" +
            "======================================\n",
            id, nomClient, telephone,
            dateHeure.format(fmt),
            nombrePersonnes, numeroTable,
            occasion.isEmpty()     ? "Non precisee" : occasion,
            observations.isEmpty() ? "Aucune"       : observations,
            statut
        );
    }

    // ── toString court pour les listes ────────────────────────────────────────
    @Override
    public String toString() {
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("dd/MM HH:mm");
        return String.format("[#%d] %-20s | %s | %d pers. | Table %d | %s",
                id, nomClient, dateHeure.format(fmt),
                nombrePersonnes, numeroTable, statut);
    }

    // ── Getters & Setters (ENCAPSULATION) ─────────────────────────────────────
    public int getId()                              { return id; }

    public String getNomClient()                    { return nomClient; }
    public void   setNomClient(String n)            { this.nomClient = n; }

    public String getTelephone()                    { return telephone; }
    public void   setTelephone(String t)            { this.telephone = t; }

    public int  getNombrePersonnes()                { return nombrePersonnes; }
    public void setNombrePersonnes(int n)           { this.nombrePersonnes = n; }

    public int  getNumeroTable()                    { return numeroTable; }
    public void setNumeroTable(int n)               { this.numeroTable = n; }

    public LocalDateTime getDateHeure()             { return dateHeure; }
    public void          setDateHeure(LocalDateTime d) { this.dateHeure = d; }

    public String getOccasion()                     { return occasion; }
    public void   setOccasion(String o)             { this.occasion = o; }

    public String getObservations()                 { return observations; }
    public void   setObservations(String o)         { this.observations = o; }

    public StatutReservation getStatut()            { return statut; }
    public void              setStatut(StatutReservation s) { this.statut = s; }
}