package Commande;

public class Observateurs implements Observateur {
    private String nom;

    public Observateurs(String nom) {
        this.nom = nom;
    }

    @Override
    public void recevoirNotification(String message) {
        System.out.println("  [SERVEUR " + nom.toUpperCase() + "] " + message);
    }

    @Override
    public String getNom() { return nom; }
}
