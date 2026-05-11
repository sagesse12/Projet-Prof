package Reporting;

import Commande.Commande;
import java.util.*;
import java.util.stream.Collectors;

public class RapportActiviteTables implements Rapport {

    @Override
    public String getNom() { return "Rapport d'Activité par Table"; }

    @Override
    public void generer(List<Commande> commandes) {
        // Regroupe les commandes payées par numéro de table
        Map<Integer, List<Commande>> parTable = commandes.stream()
                .filter(c -> c.getEtat().getStatut().equals("PAYEE"))
                .collect(Collectors.groupingBy(Commande::getNumeroTable));

        // Trie par CA décroissant
        List<Map.Entry<Integer, List<Commande>>> classement = parTable.entrySet().stream()
                .sorted(Comparator.comparingDouble(
                        (Map.Entry<Integer, List<Commande>> e) ->
                        e.getValue().stream().mapToDouble(Commande::getTotal).sum()).reversed())
                .collect(Collectors.toList());

        //System.out.println("╔══════════════════════════════════════════════╗");
        System.out.println("║       RAPPORT D'ACTIVITÉ PAR TABLE           ║");
        //System.out.println("╠════════════╦══════════════════╦══════════════╣");
        System.out.println("║   Table    ║  Nb commandes    ║     CA (€)   ║");
        //System.out.println("╠════════════╬══════════════════╬══════════════╣");

        if (classement.isEmpty()) {
            System.out.println("║         Aucune commande payée disponible.      ║");
        } else {
            for (Map.Entry<Integer, List<Commande>> e : classement) {
                double ca = e.getValue().stream().mapToDouble(Commande::getTotal).sum();
                System.out.printf("║  Table %-3d  ║       %-10d ║   %8.2f   ║%n",
                        e.getKey(), e.getValue().size(), ca);
            }
        }
        //System.out.println("╚════════════╩══════════════════╩══════════════╝");
    }
}