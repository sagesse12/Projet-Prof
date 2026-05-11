package StocksIngredients;
import Commande.Observateur;


import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class GestionnaireStock {

	private static GestionnaireStock instance;

    public static GestionnaireStock getInstance() {
        if (instance == null) instance = new GestionnaireStock();
        return instance;
    }

    private List<Ingredient> ingredients;
    private List<Observateur> observateurs;

    private GestionnaireStock() {
        this.ingredients = new ArrayList<>();
        this.observateurs = new ArrayList<>();
    }

    public void abonner(Observateur o) { 
    	observateurs.add(o); 
    }
    
    private void notifier(String msg) { 
    	observateurs.forEach(o -> o.recevoirNotification(msg)); 
    }
    
    public void ajouter(String id, String nom, double qte, double seuil, String unite, double prix) {
        if (trouver(id).isPresent()) { 
            System.out.println("Ingrédient '" + id + "' existe déjà."); 
            return; 
        }
        ingredients.add(new Ingredient(id, nom, qte, seuil, unite, prix));
        System.out.println("Ingrédient ajouté : " + nom + " (" + qte + " " + unite + ").");
    }

    public void supprimer(String id) {
        boolean ok = ingredients.removeIf(i -> i.getId().equals(id));
        System.out.println(ok ? "Ingrédient supprimé." : "Ingrédient introuvable.");
    }

    public void modifierNom(String id, String nom) {
        trouver(id).ifPresentOrElse(i -> { 
        	i.setNom(nom); System.out.println("Nom mis à jour."); 
        },() -> System.out.println("Ingrédient introuvable."));
    }

    public void modifierPrix(String id, double prix) {
        trouver(id).ifPresentOrElse(i -> { i.setPrixUnitaire(prix); System.out.printf("Prix mis à jour : %.2f €%n", prix); },() -> System.out.println("Ingrédient introuvable."));
    }

    public boolean consommer(String id, double qte) {
        Optional<Ingredient> opt = trouver(id);
        if (opt.isEmpty()) { 
            System.out.println("Ingrédient '" + id + "' introuvable."); 
            return false; 
        }
        Ingredient ing = opt.get();
        if (!ing.consommer(qte)) {
            System.out.printf("Stock insuffisant pour %s (dispo: %.2f %s).%n",
                    ing.getNom(), ing.getQuantite(), ing.getUnite());
            return false;
        }
        System.out.printf("Consommé %.2f %s de %s (restant: %.2f).%n",
                qte, ing.getUnite(), ing.getNom(), ing.getQuantite());

        if (ing.estEpuise()) notifier("EPUISE : " + ing.getNom() + " - réapprovisionnement urgent !");
        else if (ing.estSousSeuilAlerte()) notifier("Stock bas : " + ing.getNom()
                + " -> " + String.format("%.2f", ing.getQuantite()) + " " + ing.getUnite()
                + " (seuil: " + ing.getSeuilAlerte() + ")");
        return true;
    }

    public void reapprovisionner(String id, double qte) {
        trouver(id).ifPresentOrElse(
            ing -> {
                ing.reapprovisionner(qte);
                System.out.printf("%s réapprovisionné : +%.2f %s (total: %.2f).%n",
                        ing.getNom(), qte, ing.getUnite(), ing.getQuantite());
            },
            () -> System.out.println("Ingrédient introuvable.")
        );
    }

    public void modifierSeuil(String id, double seuil) {
        trouver(id).ifPresentOrElse(
            ing -> { ing.setSeuilAlerte(seuil); System.out.println("Seuil de " + ing.getNom() + " -> " + seuil); },() -> System.out.println("Ingrédient introuvable.")
        );
    }

    public void afficherStock() {
        System.out.println(" ");
        System.out.println("STOCK DES INGREDIENTS");
        System.out.println(" ");
        if (ingredients.isEmpty()) 
        	System.out.println("Aucun ingrédient.");
        else 
        	ingredients.forEach(i -> System.out.println("" + i));
        System.out.println(" ");
        long bas = ingredients.stream().filter(Ingredient::estSousSeuilAlerte).count();
        long ep = ingredients.stream().filter(Ingredient::estEpuise).count();
        System.out.printf("Résumé -> Total: %d | OK: %d | Stock bas: %d | Épuisés: %d%n",
                ingredients.size(), ingredients.size() - bas - ep, bas, ep);
    }

    public void afficherAlertes() {
        System.out.println("ALERTES STOCK :");
        boolean aucune = true;
        for (Ingredient i : ingredients) {
            if (i.estEpuise()) {
                System.out.println("[EPUISE] " + i.getNom());
                aucune = false;
            } else if (i.estSousSeuilAlerte()) {
                System.out.printf("[STOCK BAS] %-20s -> %.2f %s%n",
                        i.getNom(), i.getQuantite(), i.getUnite());
                aucune = false;
            }
        }
        if (aucune) System.out.println("Tous les stocks sont suffisants.");
    }

    public Optional<Ingredient> trouver(String id) {
        return ingredients.stream().filter(i -> i.getId().equals(id)).findFirst();
    }

    public List<Ingredient> getIngredients() { return ingredients; }
}