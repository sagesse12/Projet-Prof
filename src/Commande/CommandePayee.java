package Commande;

class CommandePayee implements EtatCommande {
    @Override 
    public void demarrer(Commande c) { 
    	System.out.println(" Commande déjà payée."); 
    }
    
    @Override 
    public void marquerPrete(Commande c) { 
    	System.out.println(" Commande déjà payée."); 
    }
    
    @Override 
    public void livrer(Commande c) { 
    	System.out.println(" Commande déjà payée."); 
    }
    
    @Override 
    public void payer(Commande c) { 
    	System.out.println(" Commande déjà payée."); 
    }
    
    @Override 
    public void annuler(Commande c){ 
    	System.out.println(" Impossible d'annuler une commande payée."); 
    }
    
    @Override 
    public String getStatut() { 
    	return "PAYÉE"; 
    }
    
    @Override 
    public String getCouleurStatut()  { 
    	return "\033[32mPAYÉE   \033[0m"; 
    }
}