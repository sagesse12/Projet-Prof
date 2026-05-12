package personnel;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * COHESION : une seule responsabilite = gerer le personnel.
 * POLYMORPHISME utilise : on manipule des objets Personnel (classe mere),
 *   mais chaque objet garde le comportement de sa sous-classe
 *   (Serveur, Cuisinier, Manager).
 */
public class GestionPersonnelManager {

    // Liste du personnel (stockage local)
    private List<Personnel> personnel;

    // ── Constructeur ──────────────────────────────────────────────────────────
    public GestionPersonnelManager() {
        this.personnel = new ArrayList<>();
    }

    // ─────────────────────────────────────────────────────────────────────────
    // AJOUTER
    // ─────────────────────────────────────────────────────────────────────────

    /** Ajoute un serveur */
    public Serveur ajouterServeur(String nom, String prenom,
                                  String telephone, double salaire) {
        Serveur s = new Serveur(nom, prenom, telephone, salaire);
        personnel.add(s);
        System.out.println("[OK] Serveur ajoute : " + s.getNomComplet());
        return s;
    }

    /** Ajoute un cuisinier */
    public Cuisinier ajouterCuisinier(String nom, String prenom,
                                      String telephone, double salaire,
                                      String specialite, int anneesExp) {
        Cuisinier c = new Cuisinier(nom, prenom, telephone,
                salaire, specialite, anneesExp);
        personnel.add(c);
        System.out.println("[OK] Cuisinier ajoute : " + c.getNomComplet());
        return c;
    }

    /** Ajoute un manager */
    public Manager ajouterManager(String nom, String prenom,
                                   String telephone, double salaire,
                                   String departement, int niveauAcces) {
        Manager m = new Manager(nom, prenom, telephone,
                salaire, departement, niveauAcces);
        personnel.add(m);
        System.out.println("[OK] Manager ajoute : " + m.getNomComplet());
        return m;
    }

    // ─────────────────────────────────────────────────────────────────────────
    // SUPPRIMER / DESACTIVER
    // ─────────────────────────────────────────────────────────────────────────

    /** Supprime definitivement un membre */
    public boolean supprimerPersonnel(int id) {
        Personnel p = trouverParId(id);
        if (p == null) {
            System.out.println("[ERREUR] Personnel #" + id + " introuvable.");
            return false;
        }
        personnel.remove(p);
        System.out.println("[OK] " + p.getNomComplet() + " supprime.");
        return true;
    }

    /** Desactive un membre (il reste dans la liste mais isActif = false) */
    public boolean desactiverPersonnel(int id) {
        Personnel p = trouverParId(id);
        if (p == null) {
            System.out.println("[ERREUR] Personnel #" + id + " introuvable.");
            return false;
        }
        p.setActif(false);
        System.out.println("[OK] " + p.getNomComplet() + " desactive.");
        return true;
    }

    /** Reactive un membre */
    public boolean reactiverPersonnel(int id) {
        Personnel p = trouverParId(id);
        if (p == null) {
            System.out.println("[ERREUR] Personnel #" + id + " introuvable.");
            return false;
        }
        p.setActif(true);
        System.out.println("[OK] " + p.getNomComplet() + " reactive.");
        return true;
    }

    // ─────────────────────────────────────────────────────────────────────────
    // AFFECTATIONS (specifiques aux serveurs)
    // ─────────────────────────────────────────────────────────────────────────

    /** Affecte un serveur a une table */
    public boolean affecterServeurTable(int staffId, int numeroTable) {
        Personnel p = trouverParId(staffId);
        if (p == null) {
            System.out.println("[ERREUR] Personnel #" + staffId + " introuvable.");
            return false;
        }
        if (!(p instanceof Serveur)) {
            System.out.println("[ERREUR] #" + staffId + " n'est pas un serveur.");
            return false;
        }
        if (!p.isActif()) {
            System.out.println("[ERREUR] Ce serveur est inactif.");
            return false;
        }
        ((Serveur) p).assignerTable(numeroTable);
        return true;
    }

    /** Retire une table d'un serveur */
    public boolean libererTableServeur(int staffId, int numeroTable) {
        Personnel p = trouverParId(staffId);
        if (!(p instanceof Serveur)) return false;
        ((Serveur) p).libererTable(numeroTable);
        return true;
    }

    /** Quel serveur gere la table X ? */
    public Serveur trouverServeurDeTable(int numeroTable) {
        return personnel.stream()
                .filter(p -> p instanceof Serveur)
                .map(p -> (Serveur) p)
                .filter(s -> s.gereTable(numeroTable))
                .findFirst()
                .orElse(null);
    }

    // ─────────────────────────────────────────────────────────────────────────
    // RECHERCHES
    // ─────────────────────────────────────────────────────────────────────────

    /** Chercher par id */
    public Personnel trouverParId(int id) {
        return personnel.stream()
                .filter(p -> p.getId() == id)
                .findFirst()
                .orElse(null);
    }

    /** Tout le personnel */
    public List<Personnel> listerTout() {
        return personnel;
    }

    /** Personnel actif uniquement */
    public List<Personnel> listerActifs() {
        return personnel.stream()
                .filter(Personnel::isActif)
                .collect(Collectors.toList());
    }

    /**
     * Filtrer par role.
     * POLYMORPHISME : getRole() retourne des valeurs differentes
     *   selon le type reel de chaque objet.
     */
    public List<Personnel> listerParRole(String role) {
        return personnel.stream()
                .filter(p -> p.getRole().equalsIgnoreCase(role))
                .collect(Collectors.toList());
    }

    /** Tous les serveurs actifs */
    public List<Serveur> getServeursActifs() {
        return personnel.stream()
                .filter(p -> p instanceof Serveur && p.isActif())
                .map(p -> (Serveur) p)
                .collect(Collectors.toList());
    }

    /** Tous les cuisiniers disponibles */
    public List<Cuisinier> getCuisiniersDispo() {
        return personnel.stream()
                .filter(p -> p instanceof Cuisinier)
                .map(p -> (Cuisinier) p)
                .filter(c -> c.isDisponible() && c.isActif())
                .collect(Collectors.toList());
    }

    // ─────────────────────────────────────────────────────────────────────────
    // AFFICHAGE
    // ─────────────────────────────────────────────────────────────────────────

    /** Affiche tout le personnel (POLYMORPHISME : toString different par type) */
    public void afficherTout() {
        System.out.println("\n--- PERSONNEL (" + personnel.size() + " membres) ---");
        if (personnel.isEmpty()) {
            System.out.println("  Aucun membre enregistre.");
        } else {
            personnel.forEach(System.out::println);
        }
    }

    /** Affiche le profil complet d'un membre */
    public void afficherProfil(int id) {
        Personnel p = trouverParId(id);
        if (p == null) {
            System.out.println("[ERREUR] Personnel #" + id + " introuvable.");
            return;
        }
        // POLYMORPHISME : afficherProfil() specifique a chaque sous-classe
        System.out.println(p.afficherProfil());
    }

    // ─────────────────────────────────────────────────────────────────────────
    // ACCES A LA LISTE (pour la persistance)
    // ─────────────────────────────────────────────────────────────────────────
    public List<Personnel> getPersonnel()                { return personnel; }
    public void setPersonnel(List<Personnel> personnel)  { this.personnel = personnel; }
}