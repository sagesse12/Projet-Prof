package Reporting;

import java.util.List;

import Commande.Commande;

public class RapportVentes implements Rapport {

	@Override
	public String getNom() {
		return "Rapport des Ventes";
	}

	@Override
	public void generer(List<Commande> commandes) {
		long nbPayees   = commandes.stream()
                .filter(c -> c.getEtat().getStatut().equals("PAYEE")).count();
        long nbAnnulees = commandes.stream()
                .filter(c -> c.getEtat().getStatut().equals("ANNULEE")).count();
        double caTotal  = commandes.stream()
                .filter(c -> c.getEtat().getStatut().equals("PAYEE"))
                .mapToDouble(Commande::getTotal).sum();
        double ticketMoyen = nbPayees > 0 ? caTotal / nbPayees : 0;
        
        System.out.println("RAPPORT DES VENTES \n");
        System.out.printf( "║  Total commandes       : %-16d║%n", commandes.size());
        System.out.printf( "║  Commandes payées      : %-16d║%n", nbPayees);
        System.out.printf( "║  Commandes annulées    : %-16d║%n", nbAnnulees);
        System.out.printf( "║  Chiffre d'affaires    : %-13.2f € ║%n", caTotal);
        System.out.printf( "║  Ticket moyen          : %-13.2f € ║%n", ticketMoyen);
		
	}

}
