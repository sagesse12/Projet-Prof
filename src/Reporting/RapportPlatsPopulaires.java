package Reporting;

import Commande.Commande;
import Commande.ElementCommande;
import java.util.*;
import java.util.stream.Collectors;

public class RapportPlatsPopulaires implements Rapport {

    @Override
    public String getNom() { 
    	return "Rapport des Plats Populaires"; 
    }

    @Override
    public void generer(List<Commande> commandes) {
        // Agrège les quantités par plat (id → quantité totale)
        Map<String, Integer> comptage = new LinkedHashMap<>();
        Map<String, String>  noms  = new LinkedHashMap<>();

        for (Commande c : commandes) {
            for (ElementCommande ec : c.getElements()) {
                String id  = ec.getElement().getId();
                String nom = ec.getElement().getNom();
                comptage.merge(id, ec.getQuantite(), Integer::sum);
                noms.put(id, nom);
            }
        }

        // Trie par quantité décroissante
        List<Map.Entry<String, Integer>> classement = comptage.entrySet().stream()
                .sorted(Map.Entry.<String, Integer>comparingByValue().reversed())
                .collect(Collectors.toList());

        //System.out.println("╔══════════════════════════════════════════╗");
        System.out.println("║       RAPPORT DES PLATS POPULAIRES       ║");
        //System.out.println("╠══════════════════════════════════════════╣");

        if (classement.isEmpty()) {
            System.out.println("║  Aucune donnée disponible.               ║");
        } else {
            int rang = 1;
            for (Map.Entry<String, Integer> e : classement) {
                String ligne = String.format("  %d. %-28s x%d", rang++, noms.get(e.getKey()), e.getValue());
                System.out.printf("║ %-42s ║%n", ligne);
            }
        }
        //System.out.println("╚══════════════════════════════════════════╝");
    }
}