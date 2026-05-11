package Menu;

class Boisson extends ElementMenu {
    public Boisson(String id, String nom, double prix, String description) {
        super(id, nom, prix, description);
    }
    
    @Override
    public String getCategorie() { 
    	return "BOISSON"; 
    }
}