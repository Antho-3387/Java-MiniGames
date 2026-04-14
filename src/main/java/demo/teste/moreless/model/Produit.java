package demo.teste.moreless.model;

public class Produit {
    private int id;
    private String nom;
    private double prix;
    private String imagePath;
    private String categorie;

    public Produit() {}

    public Produit(int id, String nom, double prix, String imagePath, String categorie) {
        this.id = id;
        this.nom = nom;
        this.prix = prix;
        this.imagePath = imagePath;
        this.categorie = categorie;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getNom() { return nom; }
    public void setNom(String nom) { this.nom = nom; }

    public double getPrix() { return prix; }
    public void setPrix(double prix) { this.prix = prix; }

    public String getImagePath() { return imagePath; }
    public void setImagePath(String imagePath) { this.imagePath = imagePath; }

    public String getCategorie() { return categorie; }
    public void setCategorie(String categorie) { this.categorie = categorie; }

    @Override
    public String toString() { return nom + " (" + prix + " €)"; }
}