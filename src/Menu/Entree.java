package Menu;

class Entree extends ElementMenu {
    public Entree(String id, String nom, double prix, String description) {
        super(id, nom, prix, description);
    }
    
    @Override 
    public String getCategorie() { 
    	return "ENTRÉE"; 
    }
}