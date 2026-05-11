package Commande;

class CommandeEnCours implements EtatCommande {
    @Override 
    public void demarrer(Commande c) { 
    	System.out.println("  Commande déjà en cours."); 
    }
    
    @Override 
    public void marquerPrete(Commande c) {
        c.setEtat(new CommandePrete());
        c.notifierObservateurs("Commande #" + c.getId() + " PRÊTE — table " + c.getNumeroTable() + " !");
        System.out.println("  Commande #" + c.getId() + " → PRÊTE.");
    }
    
    @Override public void livrer(Commande c) {
    	System.out.println("  Marquez d'abord la commande comme prête.");
    }
    
    @Override 
    public void payer(Commande c) { 
    	System.out.println("  La commande n'a pas été livrée."); 
    }
    
    @Override 
    public void annuler(Commande c) {
        c.setEtat(new CommandeAnnulee());
        System.out.println(" Commande #" + c.getId() + " ANNULÉE.");
    }
    
    @Override 
    public String getStatut() {
    	return "EN_COURS"; 
    }
    
    @Override 
    public String getCouleurStatut()  { 
    	return "\033[33mEN COURS\033[0m"; 
    }
}