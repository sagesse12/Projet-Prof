package Commande;

public interface Observateur {
	void recevoirNotification(String message);
    String getNom();
}
