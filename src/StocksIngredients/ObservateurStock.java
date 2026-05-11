package StocksIngredients;
import Commande.Observateur;

public class ObservateurStock implements Observateur {
    @Override
    public void recevoirNotification(String message) {
        System.out.println("  [STOCK] ⚠️  ALERTE : " + message);
    }

    @Override
    public String getNom() { return "Gestionnaire Stock"; }
}
