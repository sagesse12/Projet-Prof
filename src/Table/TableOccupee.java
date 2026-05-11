package Table;

public class TableOccupee implements EtatTable {

    @Override
    public void assignerClient(Table table) {
        System.out.println(" Table " + table.getNumero() + " est déjà occupée.");
    }

    @Override
    public void liberer(Table table) {
        table.setEtat(new TableLibre());
        System.out.println(" Table " + table.getNumero() + " est maintenant LIBRE.");
    }

    @Override
    public void reserver(Table table) {
        System.out.println("  Impossible : la table " + table.getNumero() + " est occupée.");
    }

    @Override
    public String getStatut() { 
    	return "OCCUPÉE"; 
    }

    @Override
    public String getCouleurStatut() { 
    	return "\033[32mLIBRE\033[0m";
    } 
}