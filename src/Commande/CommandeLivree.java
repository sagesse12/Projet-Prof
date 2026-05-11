package Commande;

class CommandeLivree implements EtatCommande {
    @Override 
    public void demarrer(Commande c) { 
    	System.out.println("  Commande déjà livrée."); 
    }
    
    @Override 
    public void marquerPrete(Commande c) {
    	System.out.println("  Commande déjà livrée."); 
    }
    
    @Override 
    public void livrer(Commande c)  { 
    	System.out.println(" Commande déjà livrée."); 
    }
    
    @Override 
    public void payer(Commande c) {
        c.setEtat(new CommandePayee());
        c.notifierObservateurs("Commande #" + c.getId() + " PAYÉE — " + String.format("%.2f", c.getTotal()) + " €");
        System.out.printf(" Commande #%d → PAYÉE (Total : %.2f €).%n", c.getId(), c.getTotal());
    }
    
    @Override 
    public void annuler(Commande c) { 
    	System.out.println(" Impossible d'annuler une commande livrée."); 
    }
    
    @Override 
    public String getStatut() {
    	return "LIVRÉE";
    }
    
    @Override 
    public String getCouleurStatut(){
    	return "\033[35mLIVRÉE  \033[0m"; 
    }
}