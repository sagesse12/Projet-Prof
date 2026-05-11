package Commande;

public interface EtatCommande {
    void demarrer(Commande commande);
    void marquerPrete(Commande commande);
    void livrer(Commande commande);
    void payer(Commande commande);
    void annuler(Commande commande);
    String getStatut();
    String getCouleurStatut();
}