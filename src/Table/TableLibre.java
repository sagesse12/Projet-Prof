package Table;


public class TableLibre implements EtatTable{

	@Override
	public void assignerClient(Table table) {
		table.setEtat(new TableOccupee());
		System.out.println("Table " +table.getNumero()+ "est maintenant occupée");
		
	}

	@Override
	public void liberer(Table table) {
		System.out.println("Table " +table.getNumero()+ "est déjà libre");
		
	}

	@Override
	public void reserver(Table table) {
		table.setEtat(new TableReservee());
		System.out.println("Table " +table.getNumero()+ "a été reservée");
		
	}

	@Override
	public String getStatut() {
		return "LIBRE";
	}

	@Override
	public String getCouleurStatut() {
		return "\033[32mLIBRE\033[0m";
	}

}
