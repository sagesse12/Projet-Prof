package Commande;
import Menu.ElementMenu;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;

public class GestionnaireCommande {
	private List<Commande>     commandes;
    private List<Observateur>  observateursGlobaux;
 
    public GestionnaireCommande() {
        this.commandes            = new ArrayList<>();
        this.observateursGlobaux  = new ArrayList<>();
    }
    
    public void abonner(Observateur o) {
        observateursGlobaux.add(o);
        System.out.println(o.getNom() + " abonné aux notifications.");
    }
    
    public Commande creerCommande(int numeroTable) {
        Commande cmd = new Commande(numeroTable, new CommandeNouvelle());
        observateursGlobaux.forEach(cmd::ajouterObservateur);
        commandes.add(cmd);
        System.out.println(" Commande #" + cmd.getId() + " créée pour la table " + numeroTable + ".");
        cmd.notifierObservateurs("Nouvelle commande #" + cmd.getId() + " — table " + numeroTable + ".");
        return cmd;
    }
    
    public void ajouterPlat(int idCmd, ElementMenu plat, int qte, String notes) {
        trouver(idCmd).ifPresentOrElse(
            cmd -> {
            	if (cmd.getEtat().getStatut().equals("NOUVELLE") || cmd.getEtat().getStatut().equals("EN_COURS")) {
                    cmd.ajouterElement(new ElementCommande(plat, qte, notes));
                } else {
                    System.out.println(" Impossible de modifier une commande " + cmd.getEtat() + ".");
                }
            },
            () -> System.out.println(" Commande #" + idCmd + " introuvable.")
        );
    }
    
    public void retirerPlat(int idCmd, String idPlat) {
        trouver(idCmd).ifPresentOrElse(
            cmd -> System.out.println(cmd.retirerElement(idPlat)
                    ? "  ✅ Plat retiré." : " Plat introuvable dans la commande."),
            () -> System.out.println(" Commande #" + idCmd + " introuvable.")
        );
    }
    
    public Optional<Commande> trouver(int id) {
        return commandes.stream().filter(c -> c.getId() == id).findFirst();
    }
    
    public List<Commande> getCommandes() { 
    	return commandes; 
    }
    
    public void demarrer(int id)   { 
    	exec(id, Commande::demarrer); 
    }
    
    public void prete(int id) {
    	exec(id, Commande::marquerPrete); 
    }
    
    public void livrer(int id) {
    	exec(id, Commande::livrer); 
    }
    
    public void payer(int id)  {
    	exec(id, Commande::payer); 
    }
    
    public void annuler(int id) {
    	exec(id, Commande::annuler); 
    }
 
    private void exec(int id, Consumer<Commande> action) {
        trouver(id).ifPresentOrElse(action,
                () -> System.out.println(" Commande #" + id + " introuvable."));
    }
    
    public void afficherUne(int id) {
        trouver(id).ifPresentOrElse(Commande::afficher,
                () -> System.out.println(" Commande #" + id + " introuvable."));
    }
 
    public void afficherParStatut(String statut) {
        System.out.println("\n  Commandes [" + statut + "] :");
        commandes.stream().filter(c -> c.getEtat().getStatut().equalsIgnoreCase(statut)).forEach(Commande::afficher);
    }
}
