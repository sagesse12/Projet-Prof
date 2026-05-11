package Commande;

class CommandeNouvelle implements EtatCommande {
    @Override
    public void demarrer(Commande c) {
        c.setEtat(new CommandeEnCours());
        c.notifierObservateurs("Commande #" + c.getId() + " — préparation démarrée.");
        System.out.println("  Commande #" + c.getId() + " → EN COURS.");
    }
    
    @Override 
    public void marquerPrete(Commande c)  { 
    	System.out.println("   Démarrez d'abord la préparation."); 
    }
    
    @Override 
    public void livrer(Commande c)   {
    	System.out.println("   La commande n'est pas encore prête."); 
    }
    
    @Override 
    public void payer(Commande c)  {
    	System.out.println("  La commande n'a pas été livrée."); 
    }
    
    @Override 
    public void annuler(Commande c) {
        c.setEtat(new CommandeAnnulee());
        System.out.println("  Commande #" + c.getId() + " ANNULÉE.");
    }
    
    @Override
    public String getStatut() { 
    	return "NOUVELLE"; 
    }
    
    @Override 
    public String getCouleurStatut()  {
    	return "\033[36mNOUVELLE\033[0m"; 
    }
}