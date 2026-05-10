package personnel;

import java.util.ArrayList;
import java.util.List;

/**
 * HERITAGE : Serveur etend Personnel.
 * POLYMORPHISME : redefinit getRole() et afficherProfil().
 * AGREGATION : contient une liste de numeros de tables assignees.
 */
public class Serveur extends Personnel {

    private static final long serialVersionUID = 1L;

    // ── Attributs specifiques au serveur ──────────────────────────────────────
    private List<Integer> tablesAssignees; // numeros des tables gerees
    private int           pourboire;       // total des pourboires recus (euros)

    // ── Constructeur ──────────────────────────────────────────────────────────
    public Serveur(String nom, String prenom, String telephone, double salaire) {
        super(nom, prenom, telephone, salaire);
        this.tablesAssignees = new ArrayList<>();
        this.pourboire       = 0;
    }

    // ── POLYMORPHISME : implementation des methodes abstraites ─────────────────

    @Override
    public String getRole() {
        return "SERVEUR";
    }

    @Override
    public String afficherProfil() {
        return String.format(
            "\n--- SERVEUR ---\n" +
            "  Nom         : %s\n" +
            "  Telephone   : %s\n" +
            "  Salaire     : %.2f EUR\n" +
            "  Tables      : %s\n" +
            "  Pourboires  : %d EUR\n" +
            "  Statut      : %s",
            getNomComplet(),
            getTelephone(),
            getSalaire(),
            tablesAssignees.isEmpty() ? "Aucune table assignee"
                                      : tablesAssignees.toString(),
            pourboire,
            isActif() ? "Actif" : "Inactif"
        );
    }

    // ── Methodes specifiques au serveur ───────────────────────────────────────

    /** Assigne une table a ce serveur */
    public void assignerTable(int numero) {
        if (!tablesAssignees.contains(numero)) {
            tablesAssignees.add(numero);
            System.out.println("[OK] Table " + numero
                    + " assignee a " + getNomComplet());
        } else {
            System.out.println("[INFO] " + getNomComplet()
                    + " gere deja la table " + numero);
        }
    }

    /** Retire une table de ce serveur */
    public void libererTable(int numero) {
        boolean supprime = tablesAssignees.remove(Integer.valueOf(numero));
        if (supprime) {
            System.out.println("[OK] Table " + numero
                    + " retiree de " + getNomComplet());
        } else {
            System.out.println("[INFO] " + getNomComplet()
                    + " ne gerait pas la table " + numero);
        }
    }

    /** Verifie si ce serveur gere une table donnee */
    public boolean gereTable(int numero) {
        return tablesAssignees.contains(numero);
    }

    /** Ajoute un montant de pourboire */
    public void ajouterPourboire(int montant) {
        if (montant > 0) {
            this.pourboire += montant;
            System.out.println("[OK] +" + montant + " EUR de pourboire pour "
                    + getNomComplet() + " (total : " + this.pourboire + " EUR)");
        }
    }

    // ── Getters ───────────────────────────────────────────────────────────────
    public List<Integer> getTablesAssignees() { return tablesAssignees; }
    public int           getPourboire()       { return pourboire; }
}