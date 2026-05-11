package Table;


import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class GestionnaireTable {

    private List<Table> tables;

    public GestionnaireTable() {
        this.tables = new ArrayList<>();
    }

    public void ajouterTable(int numero, int capacite, String section) {
        if (trouver(numero).isPresent()) {
            System.out.println(" Table n°" + numero + " existe déjà.");
            return;
        }
        tables.add(new Table(numero, capacite, section));
        System.out.println(" Table n°" + numero + " ajoutée (capacité: " + capacite + ", section: " + section + ").");
    }

    public void supprimerTable(int numero) {
        Optional<Table> opt = trouver(numero);
        if (opt.isEmpty()) { 
        	System.out.println(" Table n°" + numero + " introuvable."); 
        	return; 
        }
        if (!opt.get().getStatut().equals("LIBRE")) {
        	System.out.println(" Table n°" + numero + " est " + opt.get().getStatut() + " — impossible de la supprimer."); 
        	return; 
        }
        tables.remove(opt.get());
        System.out.println(" Table n°" + numero + " supprimée.");
    }

    public void assignerClient(int numero, String nom) {
        trouver(numero).ifPresentOrElse(t -> t.assignerClient(nom),
            () -> System.out.println(" Table n°" + numero + " introuvable.")
        );
    }

    public void libererTable(int numero) {
        trouver(numero).ifPresentOrElse(
            Table::liberer,
            () -> System.out.println(" Table n°" + numero + " introuvable.")
        );
    }

    public void reserverTable(int numero, String nom) {
        trouver(numero).ifPresentOrElse(
            t -> t.reserver(nom),
            () -> System.out.println(" Table n°" + numero + " introuvable.")
        );
    }

    public void afficherToutes() {
        
        System.out.println("  ║                    ÉTAT DES TABLES DU RESTAURANT                 ║");
        
        if (tables.isEmpty()) System.out.println("  ║  Aucune table.                                                    ║");
        else tables.forEach(t -> System.out.println(t));
        
        System.out.printf("  Résumé → Libres: %d | Occupées: %d | Réservées: %d%n",
                compter("LIBRE"), compter("OCCUPÉE"), compter("RÉSERVÉE"));
    }

    public void afficherLibres() {
        System.out.println("\n  Tables LIBRES :");
        tables.stream().filter(t -> t.getStatut().equals("LIBRE")).forEach(System.out::println);
    }

    public Optional<Table> trouver(int numero) {
        return tables.stream().filter(t -> t.getNumero() == numero).findFirst();
    }

    private long compter(String statut) {
        return tables.stream().filter(t -> t.getStatut().equals(statut)).count();
    }

    public List<Table> getTables() { return tables; }
}