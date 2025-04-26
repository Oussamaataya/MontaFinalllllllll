package Entities;
import java.util.ArrayList;
import java.util.List;

public class Cart {
    private static Cart instance;
    private final List<Produit> produits;

    private Cart() {
        produits = new ArrayList<>();
    }

    public static Cart getInstance() {
        if (instance == null) {
            instance = new Cart();
        }
        return instance;
    }

    public void addProduit(Produit produit) {
        // Check if the product already exists in cart
        for (Produit p : produits) {
            if (p.getId() == produit.getId()) {
                p.setQuantity(p.getQuantity() + 1);
                return;
            }
        }
        
        // If it's a new product, set quantity to 1 and add it
        produit.setQuantity(1);
        produits.add(produit);
    }

    public void removeProduit(Produit produit) {
        produits.removeIf(p -> p.getId() == produit.getId());
    }
    
    public void updateQuantity(Produit produit, int quantity) {
        for (Produit p : produits) {
            if (p.getId() == produit.getId()) {
                p.setQuantity(quantity);
                return;
            }
        }
    }
    
    public void clearCart() {
        produits.clear();
    }
    
    public double getTotal() {
        return produits.stream()
                .mapToDouble(p -> p.getPrix() * p.getQuantity())
                .sum();
    }
    
    public int getItemCount() {
        return produits.stream()
                .mapToInt(Produit::getQuantity)
                .sum();
    }

    public List<Produit> getProduits() {
        return produits;
    }
}