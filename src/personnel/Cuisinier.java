package personnel;

/**
 * HERITAGE : Cuisinier etend Personnel.
 * POLYMORPHISME : redefinit getRole() et afficherProfil().
 */
public class Cuisinier extends Personnel {

    private static final long serialVersionUID = 1L;

    // ── Attributs specifiques au cuisinier ────────────────────────────────────
    private String  specialite;        // ex : "Patisserie", "Grill", "Sauces"
    private int     anneesExperience;
    private boolean disponible;        // false si en pause ou conge

    // ── Constructeur ──────────────────────────────────────────────────────────
    public Cuisinier(String nom, String prenom, String telephone,
                     double salaire, String specialite, int anneesExperience) {
        super(nom, prenom, telephone, salaire);
        this.specialite        = specialite;
        this.anneesExperience  = anneesExperience;
        this.disponible        = true;
    }

    // ── POLYMORPHISME : implementation des methodes abstraites ─────────────────

    @Override
    public String getRole() {
        return "CUISINIER";
    }

    @Override
    public String afficherProfil() {
        return String.format(
            "\n--- CUISINIER ---\n" +
            "  Nom          : %s\n" +
            "  Telephone    : %s\n" +
            "  Salaire      : %.2f EUR\n" +
            "  Specialite   : %s\n" +
            "  Experience   : %d ans\n" +
            "  Disponible   : %s\n" +
            "  Statut       : %s",
            getNomComplet(),
            getTelephone(),
            getSalaire(),
            specialite,
            anneesExperience,
            disponible ? "Oui" : "Non (pause/conge)",
            isActif() ? "Actif" : "Inactif"
        );
    }

    // ── Methodes specifiques ──────────────────────────────────────────────────

    public void mettreEnPause() {
        this.disponible = false;
        System.out.println("[OK] " + getNomComplet() + " est en pause.");
    }

    public void reprendreService() {
        this.disponible = true;
        System.out.println("[OK] " + getNomComplet() + " reprend le service.");
    }

    // ── Getters & Setters ─────────────────────────────────────────────────────
    public String  getSpecialite()               { return specialite; }
    public void    setSpecialite(String s)        { this.specialite = s; }

    public int     getAnneesExperience()          { return anneesExperience; }
    public void    setAnneesExperience(int a)     { this.anneesExperience = a; }

    public boolean isDisponible()                 { return disponible; }
    public void    setDisponible(boolean d)       { this.disponible = d; }
}