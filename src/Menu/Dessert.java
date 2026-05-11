package Menu;

class Dessert extends ElementMenu {
    public Dessert(String id, String nom, double prix, String description) {
        super(id, nom, prix, description);
    }
    
    @Override 
    public String getCategorie() {
    	return "DESSERT"; 
    }
}