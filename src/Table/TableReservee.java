package Table;

public class TableReservee implements EtatTable {

    @Override
    public void assignerClient(Table table) {
        table.setEtat(new TableOccupee());
        System.out.println("  Table " + table.getNumero() + " : réservation confirmée → OCCUPÉE.");
    }

    @Override
    public void liberer(Table table) {
        table.setEtat(new TableLibre());
        System.out.println(" Table " + table.getNumero() + " : réservation annulée → LIBRE.");
    }

    @Override
    public void reserver(Table table) {
        System.out.println(" Table " + table.getNumero() + " est déjà réservée.");
    }

    @Override
    public String getStatut() { 
    	return "RÉSERVÉE"; 
    }

    @Override
    public String getCouleurStatut() { 
    	return "\033[33mRÉSERVÉE\033[0m"; 
    } 
}