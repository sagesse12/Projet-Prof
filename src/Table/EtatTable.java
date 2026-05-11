package Table;


public interface EtatTable {
	void assignerClient(Table table);
    void liberer(Table table);
    void reserver(Table table);
    String getStatut();
    String getCouleurStatut();
}
