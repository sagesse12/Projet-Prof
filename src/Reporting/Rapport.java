package Reporting;

import Commande.Commande;
import java.util.List;

public interface Rapport {
	String getNom();
	void generer(List<Commande> commandes);
}
