package Commande;

class CommandePrete implements EtatCommande {
    @Override 
    public void demarrer(Commande c){
    	System.out.println("  Commande déjà prête."); 
    }
    
    @Override 
    public void marquerPrete(Commande c)   { 
    	System.out.println("  Commande déjà prête."); 
    }
    
    @Override 
    public void livrer(Commande c) {
        c.setEtat(new CommandeLivree());
        c.notifierObservateurs("Commande #" + c.getId() + " livrée à la table " + c.getNumeroTable() + ".");
        System.out.println("  Commande #" + c.getId() + " → LIVRÉE.");
    }
    @Override 
    public void payer(Commande c){ 
    	System.out.println("  Livrez d'abord la commande.");
    }
    
    @Override 
    public void annuler(Commande c) {
    	System.out.println("  Impossible d'annuler une commande prête."); 
    }
    
    @Override 
    public String getStatut() { 
    	return "PRÊTE"; 
    }
    
    @Override 
    public String getCouleurStatut(){
    	return "\033[34mPRÊTE   \033[0m"; 
    }
}