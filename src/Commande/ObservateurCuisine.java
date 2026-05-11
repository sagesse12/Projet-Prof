package Commande;

public class ObservateurCuisine implements Observateur {
    @Override
    public void recevoirNotification(String message) {
        System.out.println("  [CUISINE] " + message);
    }

    @Override
    public String getNom() { return "Cuisine"; }
}
