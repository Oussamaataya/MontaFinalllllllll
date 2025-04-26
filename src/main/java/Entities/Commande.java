package Entities;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class Commande {
    public int id;
    public String client_name, client_family_name, client_adresse, client_phone, client_email;
    public String methodePaiement;
    public String etatCommande;
    public LocalDateTime date;
    public String instructionSpeciale;
    public float prixtotal;
    public int user_id;
    private List<Produit> produits;

    public Commande() {
        produits = new ArrayList<>();
    }

    public Commande(int id, String client_name, String client_family_name, String client_adresse, String client_phone, String client_email, String methodePaiement, String etatCommande, LocalDateTime date, String instructionSpeciale, float prixtotal, int user_id) {
        this.id = id;
        this.client_name = client_name;
        this.client_family_name = client_family_name;
        this.client_adresse = client_adresse;
        this.client_phone = client_phone;
        this.client_email = client_email;
        this.methodePaiement = methodePaiement;
        this.etatCommande = etatCommande;
        this.date = date;
        this.instructionSpeciale = instructionSpeciale;
        this.prixtotal = prixtotal;
        this.user_id = user_id;
        produits = new ArrayList<>();
    }

    public Commande(String client_name, String client_family_name, String client_adresse, String client_phone, String client_email, String methodePaiement, String etatCommande, LocalDateTime date, String instructionSpeciale, float prixtotal, int user_id) {
        this.client_name = client_name;
        this.client_family_name = client_family_name;
        this.client_adresse = client_adresse;
        this.client_phone = client_phone;
        this.client_email = client_email;
        this.methodePaiement = methodePaiement;
        this.etatCommande = etatCommande;
        this.date = date;
        this.instructionSpeciale = instructionSpeciale;
        this.prixtotal = prixtotal;
        this.user_id = user_id;
        produits = new ArrayList<>();
    }
    
    /**
     * Constructor without user_id for backward compatibility
     */
    public Commande(String client_name, String client_family_name, String client_adresse, String client_phone, String client_email, String methodePaiement, String etatCommande, LocalDateTime date, String instructionSpeciale, float prixtotal) {
        this.client_name = client_name;
        this.client_family_name = client_family_name;
        this.client_adresse = client_adresse;
        this.client_phone = client_phone;
        this.client_email = client_email;
        this.methodePaiement = methodePaiement;
        this.etatCommande = etatCommande;
        this.date = date;
        this.instructionSpeciale = instructionSpeciale;
        this.prixtotal = prixtotal;
        this.user_id = 0; // Default value for user_id
        produits = new ArrayList<>();
    }

    public int getId() {
        return id;
    }

    public String getClient_name() {
        return client_name;
    }

    public String getClient_family_name() {
        return client_family_name;
    }

    public String getClient_adresse() {
        return client_adresse;
    }

    public String getClient_phone() {
        return client_phone;
    }

    public String getClient_email() {
        return client_email;
    }

    public String getMethodePaiement() {
        return methodePaiement;
    }

    public String getEtatCommande() {
        return etatCommande;
    }

    public LocalDateTime getDate() {
        return date;
    }

    public String getInstructionSpeciale() {
        return instructionSpeciale;
    }

    public float getPrixtotal() {
        return prixtotal;
    }

    public int getUserId() {
        return user_id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setClient_name(String client_name) {
        this.client_name = client_name;
    }

    public void setClient_family_name(String client_family_name) {
        this.client_family_name = client_family_name;
    }

    public void setClient_adresse(String client_adresse) {
        this.client_adresse = client_adresse;
    }

    public void setClient_phone(String client_phone) {
        this.client_phone = client_phone;
    }

    public void setClient_email(String client_email) {
        this.client_email = client_email;
    }

    public void setMethodePaiement(String methodePaiement) {
        this.methodePaiement = methodePaiement;
    }

    public void setEtatCommande(String etatCommande) {
        this.etatCommande = etatCommande;
    }

    public void setDate(LocalDateTime date) {
        this.date = date;
    }

    public void setInstructionSpeciale(String instructionSpeciale) {
        this.instructionSpeciale = instructionSpeciale;
    }

    public void setPrixtotal(float prixtotal) {
        this.prixtotal = prixtotal;
    }

    public void setUserId(int user_id) {
        this.user_id = user_id;
    }

    @Override
    public String toString() {
        return "Commande{" +
                "id=" + id +
                ", client_name='" + client_name + '\'' +
                ", client_family_name='" + client_family_name + '\'' +
                ", client_adresse='" + client_adresse + '\'' +
                ", client_phone='" + client_phone + '\'' +
                ", client_email='" + client_email + '\'' +
                ", methodePaiement='" + methodePaiement + '\'' +
                ", etatCommande='" + etatCommande + '\'' +
                ", date=" + date +
                ", instructionSpeciale='" + instructionSpeciale + '\'' +
                ", prixtotal=" + prixtotal +
                ", user_id=" + user_id +
                '}';
    }

    public List<Produit> getProduits() {
        return produits;
    }

    public void setProduits(List<Produit> produits) {
        this.produits = produits;
    }
}