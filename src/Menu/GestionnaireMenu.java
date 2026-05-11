package Menu;


import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class GestionnaireMenu {

    private List<ElementMenu> menu;

    public GestionnaireMenu() {
        this.menu = new ArrayList<>();
    }

    public void ajouterElement(String categorie, String id, String nom,
                                double prix, String description) {
        if (trouver(id).isPresent()) {
            System.out.println(" Un élément avec l'id '" + id + "' existe déjà.");
            return;
        }
        try {
            ElementMenu e = MenuFactory.creer(categorie, id, nom, prix, description);
            menu.add(e);
            System.out.println("  Ajouté au menu [" + e.getCategorie() + "] : " + e.getNom() + " — " + prix + " €");
        } catch (IllegalArgumentException ex) {
            System.out.println( ex.getMessage());
        }
    }

  
    public void supprimerElement(String id) {
        boolean ok = menu.removeIf(e -> e.getId().equals(id));
        System.out.println(ok ? "  Élément '" + id + "' supprimé du menu."
                              : "  Élément '" + id + "' introuvable.");
    }

    public void modifierNom(String id, String nouveauNom) {
        trouver(id).ifPresentOrElse(
            e -> { e.setNom(nouveauNom);
                   System.out.println(" Nom mis à jour : " + nouveauNom); },
            () -> System.out.println(" Élément '" + id + "' introuvable.")
        );
    }

    public void modifierPrix(String id, double nouveauPrix) {
        trouver(id).ifPresentOrElse(
            e -> { e.setPrix(nouveauPrix);
                   System.out.printf("  Prix mis à jour : %.2f €%n", nouveauPrix); },
            () -> System.out.println("  Élément '" + id + "' introuvable.")
        );
    }

    public void modifierDescription(String id, String desc) {
        trouver(id).ifPresentOrElse(
            e -> { e.setDescription(desc);
                   System.out.println("  Description mise à jour."); },
            () -> System.out.println("  Élément '" + id + "' introuvable.")
        );
    }

    public void changerDisponibilite(String id, boolean dispo) {
        trouver(id).ifPresentOrElse(
            e -> { e.setDisponible(dispo);
                   System.out.println( e.getNom() + " marqué " + (dispo ? "DISPONIBLE" : "INDISPONIBLE")); },
            () -> System.out.println("  Élément '" + id + "' introuvable.")
        );
    }

    public void afficherMenu() {
        
        System.out.println("                          MENU DU RESTAURANT                   ");
       

        String[] categories = {"ENTRÉE", "PLAT", "DESSERT", "BOISSON"};
        for (String cat : categories) {
            List<ElementMenu> filtres = filtrerParCategorie(cat);
            if (!filtres.isEmpty()) {
                System.out.println("  ║  ── " + cat + " " + "─".repeat(Math.max(0, 54 - cat.length())) + "║");
                filtres.forEach(e -> e.afficher());
            }
        }

        if (menu.isEmpty()) System.out.println("  ║  (menu vide)                                                 ║");

        System.out.println("  Total : " + menu.size() + " élément(s) | Disponibles : "
                + menu.stream().filter(ElementMenu::isDisponible).count());
    }

    public void afficherCategorie(String categorie) {
        System.out.println("\n  ── " + categorie.toUpperCase() + " ──");
        List<ElementMenu> liste = filtrerParCategorie(categorie);
        if 
        (liste.isEmpty()) System.out.println("  (aucun élément dans cette catégorie)");
        else 
        	liste.forEach(e -> e.afficher());
    }

    public void afficherDisponibles() {
        System.out.println("\n  Éléments disponibles :");
        menu.stream().filter(ElementMenu::isDisponible).forEach(e -> e.afficher());
    }


    public Optional<ElementMenu> trouver(String id) {
        return menu.stream().filter(e -> e.getId().equals(id)).findFirst();
    }

    private List<ElementMenu> filtrerParCategorie(String categorie) {
        return menu.stream().filter(e -> e.getCategorie().equalsIgnoreCase(categorie)).toList();
    }

    public List<ElementMenu> getMenu() { 
    	return menu; 
    }
}