package Commande;


import java.util.*;
import java.time.*;
import java.time.format.*;

public class Commande implements Sujet {

    private static int compteurId = 1;

    private int id;
    private int numeroTable;
    private List<ElementCommande> elements;
    private EtatCommande etat;
    private LocalDateTime heureCreation;
    private List<Observateur> observateurs;

    public Commande(int numeroTable, EtatCommande etatInitial) {
        this.id            = compteurId++;
        this.numeroTable   = numeroTable;
        this.elements      = new ArrayList<>();
        this.etat          = etatInitial;
        this.heureCreation = LocalDateTime.now();
        this.observateurs  = new ArrayList<>();
    }

    
    /**
	 * @return the compteurId
	 */
	public static int getCompteurId() {
		return compteurId;
	}


	/**
	 * @param compteurId the compteurId to set
	 */
	public static void setCompteurId(int compteurId) {
		Commande.compteurId = compteurId;
	}


	/**
	 * @return the id
	 */
	public int getId() {
		return id;
	}


	/**
	 * @param id the id to set
	 */
	public void setId(int id) {
		this.id = id;
	}


	/**
	 * @return the numeroTable
	 */
	public int getNumeroTable() {
		return numeroTable;
	}


	/**
	 * @param numeroTable the numeroTable to set
	 */
	public void setNumeroTable(int numeroTable) {
		this.numeroTable = numeroTable;
	}


	/**
	 * @return the elements
	 */
	public List<ElementCommande> getElements() {
		return elements;
	}


	/**
	 * @param elements the elements to set
	 */
	public void setElements(List<ElementCommande> elements) {
		this.elements = elements;
	}


	/**
	 * @return the etat
	 */
	public EtatCommande getEtat() {
		return etat;
	}


	/**
	 * @param etat the etat to set
	 */
	public void setEtat(EtatCommande etat) {
		this.etat = etat;
	}


	/**
	 * @return the heureCreation
	 */
	public LocalDateTime getHeureCreation() {
		return heureCreation;
	}


	/**
	 * @param heureCreation the heureCreation to set
	 */
	public void setHeureCreation(LocalDateTime heureCreation) {
		this.heureCreation = heureCreation;
	}


	/**
	 * @return the observateurs
	 */
	public List<Observateur> getObservateurs() {
		return observateurs;
	}


	/**
	 * @param observateurs the observateurs to set
	 */
	public void setObservateurs(List<Observateur> observateurs) {
		this.observateurs = observateurs;
	}


	public void ajouterElement(ElementCommande element) {
        for (ElementCommande ec : elements) {
            if (ec.getElement().getId().equals(element.getElement().getId())) {
                ec.setQuantite(ec.getQuantite() + element.getQuantite());
                System.out.println("  Quantité mise à jour : " + ec);
                return;
            }
        }
        elements.add(element);
        System.out.println(" Ajouté : " + element);
    }

    public boolean retirerElement(String idElement) {
        return elements.removeIf(ec -> ec.getElement().getId().equals(idElement));
    }

    public double getTotal() {
        return elements.stream().mapToDouble(ElementCommande::getSousTotal).sum();
    }

    public void demarrer()      { 
    	etat.demarrer(this); 
    }
    
    public void marquerPrete()  {
    	etat.marquerPrete(this); 
    }
    
    public void livrer()        {
    	etat.livrer(this); 
    }
    
    public void payer()         {
    	etat.payer(this); 
    }
    
    public void annuler()       {
    	etat.annuler(this); 
    }

    @Override
    public void ajouterObservateur(Observateur o)  {
    	observateurs.add(o); 
    }
    
    @Override
    public void retirerObservateur(Observateur o)  {
    	observateurs.remove(o); 
    }
    
    @Override
    public void notifierObservateurs(String msg) {
        for (Observateur o : observateurs) o.recevoirNotification(msg);
    }


    public void afficher() {
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("HH:mm:ss");
        System.out.println("  │ Commande #" + id + " | Table " + numeroTable+ " | " + etat.getCouleurStatut()+ " | " + heureCreation.format(fmt));
        if (elements.isEmpty()) {
            System.out.println("  │  (aucun élément)");
        } else {
            for (ElementCommande ec : elements) System.out.println("  │" + ec);
        }
        System.out.println("  ------------------------------------------");
        System.out.printf("  TOTAL : %.2f €%n", getTotal());
        System.out.println("  ------------------------------------------");
    }
 
}
