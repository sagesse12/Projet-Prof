package Commande;
import Menu.ElementMenu;
public class ElementCommande {

    private ElementMenu element;
    private int quantite;
    private String notes;          

    public ElementCommande(ElementMenu element, int quantite, String notes) {
        this.element  = element;
        this.quantite = quantite;
        this.notes    = notes != null ? notes : "";
    }

    /**
	 * @return the element
	 */
	public ElementMenu getElement() {
		return element;
	}

	/**
	 * @param element the element to set
	 */
	public void setElement(ElementMenu element) {
		this.element = element;
	}

	/**
	 * @return the quantite
	 */
	public int getQuantite() {
		return quantite;
	}

	/**
	 * @param quantite the quantite to set
	 */
	public void setQuantite(int quantite) {
		this.quantite = quantite;
	}

	/**
	 * @return the notes
	 */
	public String getNotes() {
		return notes;
	}

	/**
	 * @param notes the notes to set
	 */
	public void setNotes(String notes) {
		this.notes = notes;
	}

	public double getSousTotal() {
        return element.getPrix() * quantite;
    }

	@Override
	public String toString() {
	    return String.format("  x%d  %-28s %.2f €%s",
	        quantite, element.getNom(), getSousTotal(),
	        notes.isEmpty() ? "" : "  (" + notes + ")");
	}
    
}
