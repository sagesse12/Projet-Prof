package Menu;


public class MenuFactory {
	public static ElementMenu creer(String categorie, String id, String nom, double prix, String description) {
return switch (categorie.trim().toUpperCase()) {
case "ENTREE", "ENTRÉE"  -> new Entree(id, nom, prix, description);
case "PLAT"              -> new PlatPrincipal(id, nom, prix, description);
case "DESSERT"           -> new Dessert(id, nom, prix, description);
case "BOISSON"           -> new Boisson(id, nom, prix, description);
default -> throw new IllegalArgumentException(
"Catégorie inconnue : '" + categorie + "'. Valeurs : ENTREE, PLAT, DESSERT, BOISSON");
};
}
}
