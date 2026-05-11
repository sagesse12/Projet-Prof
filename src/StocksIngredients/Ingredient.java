package StocksIngredients;


public class Ingredient {
	private String id;
    private String nom;
    private double quantite;
    private double seuilAlerte;
    private String unite;
    private double prixUnitaire;
    
	public Ingredient(String id, String nom, double quantite, double seuilAlerte, String unite, double prixUnitaire) {
		super();
		this.id = id;
		this.nom = nom;
		this.quantite = quantite;
		this.seuilAlerte = seuilAlerte;
		this.unite = unite;
		this.prixUnitaire = prixUnitaire;
	}
    
	public boolean consommer(double qte) {
        if (qte > quantite) return false;
        quantite -= qte;
        return true;
    }
	
	public void reapprovisionner(double qte) { 
		quantite += qte; 
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
	 * @return the quantite
	 */
	public double getQuantite() {
		return quantite;
	}

	/**
	 * @param quantite the quantite to set
	 */
	public void setQuantite(double quantite) {
		this.quantite = quantite;
	}

	/**
	 * @return the seuilAlerte
	 */
	public double getSeuilAlerte() {
		return seuilAlerte;
	}

	/**
	 * @param seuilAlerte the seuilAlerte to set
	 */
	public void setSeuilAlerte(double seuilAlerte) {
		this.seuilAlerte = seuilAlerte;
	}

	/**
	 * @return the unite
	 */
	public String getUnite() {
		return unite;
	}

	/**
	 * @param unite the unite to set
	 */
	public void setUnite(String unite) {
		this.unite = unite;
	}

	/**
	 * @return the prixUnitaire
	 */
	public double getPrixUnitaire() {
		return prixUnitaire;
	}

	/**
	 * @param prixUnitaire the prixUnitaire to set
	 */
	public void setPrixUnitaire(double prixUnitaire) {
		this.prixUnitaire = prixUnitaire;
	}
	
	public boolean estEpuise()  { 
		return quantite <= 0; 
	}
	
	public boolean estSousSeuilAlerte() { 
		return quantite > 0 && quantite <= seuilAlerte; 
	}
	
	@Override
	public String toString() {
		return "Ingredient [id=" + id + ", nom=" + nom + ", quantite=" + quantite + ", seuilAlerte=" + seuilAlerte
				+ ", unite=" + unite + ", prixUnitaire=" + prixUnitaire + "]";
	}
    
}
