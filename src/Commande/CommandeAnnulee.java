package Commande;

class CommandeAnnulee implements EtatCommande {
    @Override 
    public void demarrer(Commande c) { 
    	System.out.println(" Commande annulée."); 
    }
    
    @Override 
    public void marquerPrete(Commande c) { 
    	System.out.println(" Commande annulée."); 
    }
    
    @Override
    public void livrer(Commande c)  {
    	System.out.println(" Commande annulée."); 
    }
    
    @Override 
    public void payer(Commande c) { 
    	System.out.println(" Commande annulée."); 
    }
    
    @Override 
    public void annuler(Commande c)  { 
    	System.out.println("  Commande déjà annulée."); 
    }
    
    @Override 
    public String getStatut() {
    	return "ANNULÉE"; 
    }
    
    @Override 
    public String getCouleurStatut()  { 
    	return "\033[31mANNULÉE \033[0m"; 
    }
}