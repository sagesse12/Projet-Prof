package personnel;

import java.io.Serializable;

/**
 * HERITAGE : classe abstraite — jamais instanciee directement.
 * ENCAPSULATION : attributs prives, acces via getters/setters.
 * POLYMORPHISME : getRole() et afficherProfil() sont abstraites,
 *                 chaque sous-classe les definit a sa facon.
 * Serializable : pour la persistance (module persistence).
 */
public abstract class Personnel implements Serializable {

    private static final long serialVersionUID = 1L;

    // Auto-increment des IDs
    private static int compteur = 1;

    // ── Attributs communs a tout le personnel ─────────────────────────────────
    private int id;
    private String nom;
    private String prenom;
    private String telephone;
    private double salaire;
    private boolean actif;

    // ── Constructeur (protected : accessible uniquement par les sous-classes) ─
    protected Personnel(String nom, String prenom,
                        String telephone, double salaire) {
        this.id        = compteur++;
        this.nom       = nom;
        this.prenom    = prenom;
        this.telephone = telephone;
        this.salaire   = salaire;
        this.actif     = true;
    }

    // ── Methodes ABSTRAITES : chaque sous-classe DOIT les implementer ─────────

    /** POLYMORPHISME : retourne le role specifique ("SERVEUR", "CUISINIER"...) */
    public abstract String getRole();

    /** POLYMORPHISME : affiche le profil complet avec les infos propres au role */
    public abstract String afficherProfil();

    // ── Methode concrete partagee par tous ────────────────────────────────────
    public String getNomComplet() {
        return prenom + " " + nom;
    }

    /** Affichage court pour les listes */
    @Override
    public String toString() {
        return String.format("[%d] %-22s | %-12s | %s",
                id, getNomComplet(), getRole(),
                actif ? "Actif" : "Inactif");
    }

    // ── Getters & Setters (ENCAPSULATION) ─────────────────────────────────────
    public int    getId()                  { return id; }

    public String getNom()                 { return nom; }
    public void   setNom(String n)         { this.nom = n; }

    public String getPrenom()              { return prenom; }
    public void   setPrenom(String p)      { this.prenom = p; }

    public String getTelephone()           { return telephone; }
    public void   setTelephone(String t)   { this.telephone = t; }

    public double getSalaire()             { return salaire; }
    public void   setSalaire(double s)     { this.salaire = s; }

    public boolean isActif()               { return actif; }
    public void    setActif(boolean a)     { this.actif = a; }
}