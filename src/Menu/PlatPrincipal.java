package Menu;

class PlatPrincipal extends ElementMenu {
    public PlatPrincipal(String id, String nom, double prix, String description) {
        super(id, nom, prix, description);
    }
    
    @Override 
    public String getCategorie() { 
    	return "PLAT"; 
    }
}