package Entities;
public class Produit_Commande {
    public int commande_id;
    public int produit_id;

    public Produit_Commande() {
    }

    public Produit_Commande(int commande_id, int produit_id) {
        this.commande_id = commande_id;
        this.produit_id = produit_id;
    }

    public int getCommande_id() {
        return commande_id;
    }

    public int getProduit_id() {
        return produit_id;
    }

    public void setCommande_id(int commande_id) {
        this.commande_id = commande_id;
    }

    public void setProduit_id(int produit_id) {
        this.produit_id = produit_id;
    }
}
