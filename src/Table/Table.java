package Table;


public class Table {

    private int numero;
    private int capacite;
    private String section;
    private EtatTable etat;
    private String clientActuel;  

    public Table(int numero, int capacite, String section) {
        this.numero      = numero;
        this.capacite    = capacite;
        this.section     = section;
        this.etat        = new TableLibre(); 
        this.clientActuel = null;
    }

   
    public void assignerClient(String nomClient) {
        this.clientActuel = nomClient;
        etat.assignerClient(this);
    }

    public void liberer() {
        this.clientActuel = null;
        etat.liberer(this);
    }

    public void reserver(String nomClient) {
        this.clientActuel = nomClient;
        etat.reserver(this);
    }

    
    public void setEtat(EtatTable etat) {
    	this.etat = etat; 
    }

    public int getNumero()              
    { 
    	return numero; 
    }
    
    public int getCapacite()            
    { 
    	return capacite; 
    }
    
    public String getSection()          
    { 
    	return section; 
    }
    
    public EtatTable getEtat()          
    { 
    	return etat; 
    }
    
    public String getStatut()           
    { 
    	return etat.getStatut(); 
    }
    
    public String getStatutCouleur()    
    { 
    	return etat.getCouleurStatut(); 
    }
    
    public String getClientActuel()     
    { 
    	return clientActuel != null ? clientActuel : "-"; 
    }


	@Override
	public String toString() {
		return "Table [numero=" + numero + ", capacite=" + capacite + ", section=" + section + ", etat=" + etat+ ", clientActuel=" + clientActuel + "]";
	}
    
}
