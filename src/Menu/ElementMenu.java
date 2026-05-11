package Menu;

public abstract class ElementMenu {

    private String id;
    private String nom;
    private double prix;
    private String description;
    private boolean disponible;

    public ElementMenu(String id, String nom, double prix, String description) {
        this.id          = id;
        this.nom         = nom;
        this.prix        = prix;
        this.description = description;
        this.disponible  = true;
    }

    /**
	 * @return the id
	 */
	public String getId() {
		return id;
	}

	/**
	 * @param id the id to set
	 */
	public void setId(String id) {
		this.id = id;
	}

	/**
	 * @return the nom
	 */
	public String getNom() {
		return nom;
	}

	/**
	 * @param nom the nom to set
	 */
	public void setNom(String nom) {
		this.nom = nom;
	}

	/**
	 * @return the prix
	 */
	public double getPrix() {
		return prix;
	}

	/**
	 * @param prix the prix to set
	 */
	public void setPrix(double prix) {
		this.prix = prix;
	}

	/**
	 * @return the description
	 */
	public String getDescription() {
		return description;
	}

	/**
	 * @param description the description to set
	 */
	public void setDescription(String description) {
		this.description = description;
	}

	/**
	 * @return the disponible
	 */
	public boolean isDisponible() {
		return disponible;
	}

	/**
	 * @param disponible the disponible to set
	 */
	public void setDisponible(boolean disponible) {
		this.disponible = disponible;
	}

	public abstract String getCategorie();

    @Override
    public String toString() {
        return String.format("[%s] %s (%.2f €)", id, nom, prix);
    }
    
    public void afficher() {
        System.out.printf("  [%s] %-30s %.2f €  %s%n", id, nom, prix, disponible ? " " : " ");
        if (description != null && !description.isEmpty())
            System.out.println("       " + description);
    }
}

