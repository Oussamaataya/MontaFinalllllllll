package Entities;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Produit {
    private String nom_produit;
    private String description;
    private String image;
    private int id;
    private int quantity;
    private double prix;
    private String type_produit;
    private Date date_fabrication;
    private Date date_expiration;
    private List<Commande> commandes;

    public Produit() {
        commandes = new ArrayList<>();
    }

    public Produit(String nom_produit, String description, String image, int quantity, double prix, String type_produit, Date date_fabrication, Date date_expiration) {
        this.nom_produit = nom_produit;
        this.description = description;
        this.image = image;
        this.quantity = quantity;
        this.prix = prix;
        this.type_produit = type_produit;
        this.date_fabrication = date_fabrication;
        this.date_expiration = date_expiration;
        commandes = new ArrayList<>();
    }

    public String getNom_produit() {
        return nom_produit;
    }

    public void setNom_produit(String nom_produit) {
        this.nom_produit = nom_produit;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public double getPrix() {
        return prix;
    }

    public void setPrix(double prix) {
        this.prix = prix;
    }

    public String getType_produit() {
        return type_produit;
    }

    public void setType_produit(String type_produit) {
        this.type_produit = type_produit;
    }

    public Date getDate_fabrication() {
        return date_fabrication;
    }

    public void setDate_fabrication(Date date_fabrication) {
        this.date_fabrication = date_fabrication;
    }

    public Date getDate_expiration() {
        return date_expiration;
    }

    public void setDate_expiration(Date date_expiration) {
        this.date_expiration = date_expiration;
    }

    public List<Commande> getCommandes() {
        return commandes;
    }

    public void setCommandes(List<Commande> commandes) {
        this.commandes = commandes;
    }
}