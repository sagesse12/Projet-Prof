package personnel;

/**
 * HERITAGE : Manager etend Personnel.
 * POLYMORPHISME : redefinit getRole() et afficherProfil().
 */
public class Manager extends Personnel {

    private static final long serialVersionUID = 1L;

    // ── Attributs specifiques au manager ──────────────────────────────────────
    private String departement;   // "Salle", "Cuisine", "Accueil", "Global"
    private int    niveauAcces;   // 1 = basique | 2 = admin | 3 = super-admin

    // ── Constructeur ──────────────────────────────────────────────────────────
    public Manager(String nom, String prenom, String telephone,
                   double salaire, String departement, int niveauAcces) {
        super(nom, prenom, telephone, salaire);
        this.departement  = departement;
        this.niveauAcces  = niveauAcces;
    }

    // ── POLYMORPHISME : implementation des methodes abstraites ─────────────────

    @Override
    public String getRole() {
        return "MANAGER";
    }

    @Override
    public String afficherProfil() {
        return String.format(
            "\n--- MANAGER ---\n" +
            "  Nom           : %s\n" +
            "  Telephone     : %s\n" +
            "  Salaire       : %.2f EUR\n" +
            "  Departement   : %s\n" +
            "  Niveau acces  : %d / 3\n" +
            "  Statut        : %s",
            getNomComplet(),
            getTelephone(),
            getSalaire(),
            departement,
            niveauAcces,
            isActif() ? "Actif" : "Inactif"
        );
    }

    // ── Methodes specifiques ──────────────────────────────────────────────────

    /**
     * Verifie si ce manager a un niveau d'acces suffisant.
     * ex : peutGerer(2) → true si niveauAcces >= 2
     */
    public boolean peutGerer(int niveauRequis) {
        return this.niveauAcces >= niveauRequis;
    }

    // ── Getters & Setters ─────────────────────────────────────────────────────
    public String getDepartement()              { return departement; }
    public void   setDepartement(String d)      { this.departement = d; }

    public int    getNiveauAcces()              { return niveauAcces; }
    public void   setNiveauAcces(int n)         { this.niveauAcces = n; }
}