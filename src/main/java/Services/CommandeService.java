package Services;


import Entities.Commande;
import Entities.Produit;
import Entities.UserSession;
import Utils.MyDataBase;

import java.sql.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Service for Commande management
 */
public class CommandeService implements IService<Commande> {
    private Connection cnx = MyDataBase.getInstance().getConnection();

    public int add(Commande commande) {
        String req = "INSERT INTO commande (client_name, client_family_name, client_adresse, client_phone, client_email, methode_paiement, etat_commande, date, instruction_speciale, prixtotal, user_id) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement ps = cnx.prepareStatement(req, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, commande.getClient_name());
            ps.setString(2, commande.getClient_family_name());
            ps.setString(3, commande.getClient_adresse());
            ps.setString(4, commande.getClient_phone());
            ps.setString(5, commande.getClient_email());
            ps.setString(6, commande.getMethodePaiement());
            ps.setString(7, commande.getEtatCommande());
            ps.setString(8, commande.getDate().toString());
            ps.setString(9, commande.getInstructionSpeciale());
            ps.setFloat(10, commande.getPrixtotal());
            
            // Get current logged-in user if available, otherwise use the user_id from the commande
            int userId = commande.getUserId();
            if (userId <= 0) {
                // Try to get user from session if available
                try {
               UserSession userSession = UserSession.getInstance();
                    if (userSession != null && userSession.getUser() != null) {
                        userId = userSession.getUser().getId();
                    }
                } catch (Exception e) {
                    System.out.println("No user session available, using default user_id: " + e.getMessage());
                }
            }
            ps.setInt(11, userId);
            
            ps.executeUpdate();
            
            // Get the generated ID
            ResultSet generatedKeys = ps.getGeneratedKeys();
            if (generatedKeys.next()) {
                int generatedId = generatedKeys.getInt(1);
                commande.setId(generatedId);
                System.out.println("Commande added successfully with ID: " + generatedId);
                
                // Send confirmation email
                sendOrderConfirmationEmail(commande);
                
                return generatedId;
            } else {
                System.out.println("Failed to retrieve generated ID for commande");
                return -1;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return -1;
        }
    }

    @Override
    public void update(Commande commande) {
        // Fetch the original order to check if status has changed
        Commande originalCommande = getOne(commande.getId());
        boolean statusChangedToDelivery = originalCommande != null && 
                                         !originalCommande.getEtatCommande().equals("En Livraison") && 
                                         commande.getEtatCommande().equals("En Livraison");
        
        // Update the order in the database
        String req = "UPDATE commande SET client_name = ?, client_family_name = ?, client_adresse = ?, client_phone = ?, client_email = ?, methode_paiement = ?, etat_commande = ?, date = ?, instruction_speciale = ?, prixtotal = ?, user_id = ? WHERE id = ?";
        try (PreparedStatement ps = cnx.prepareStatement(req)) {
            ps.setString(1, commande.getClient_name());
            ps.setString(2, commande.getClient_family_name());
            ps.setString(3, commande.getClient_adresse());
            ps.setString(4, commande.getClient_phone());
            ps.setString(5, commande.getClient_email());
            ps.setString(6, commande.getMethodePaiement());
            ps.setString(7, commande.getEtatCommande());
            ps.setString(8, commande.getDate().toString());
            ps.setString(9, commande.getInstructionSpeciale());
            ps.setFloat(10, commande.getPrixtotal());
            ps.setInt(11, commande.getUserId());
            ps.setInt(12, commande.getId());
            ps.executeUpdate();
            System.out.println("Commande updated successfully");
            
            // Send email notification if status changed to "En Livraison"
            if (statusChangedToDelivery) {
                sendDeliveryStatusEmail(commande);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void delete(Commande commande) {
        String req = "DELETE FROM commande WHERE id = ?";
        try (PreparedStatement ps = cnx.prepareStatement(req)) {
            ps.setInt(1, commande.getId());
            ps.executeUpdate();
            System.out.println("Commande deleted successfully");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public List<Commande> getAll() {
        List<Commande> commandes = new ArrayList<>();
        Map<Integer, Commande> commandeMap = new HashMap<>();
        
        String req = "SELECT c.*, pc.produit_id, pc.quantite, pc.prix_unitaire, p.* FROM commande c " +
                    "LEFT JOIN produit_commande pc ON c.id = pc.commande_id " +
                    "LEFT JOIN produit p ON pc.produit_id = p.id " +
                    "ORDER BY c.id";
        
        try (Statement st = cnx.createStatement(); 
             ResultSet res = st.executeQuery(req)) {
            
            while (res.next()) {
                int commandeId = res.getInt("c.id");
                
                // If we haven't processed this order yet, create it
                if (!commandeMap.containsKey(commandeId)) {
                    Commande commande = new Commande();
                    commande.setId(commandeId);
                    commande.setClient_name(res.getString("c.client_name"));
                    commande.setClient_family_name(res.getString("c.client_family_name"));
                    commande.setClient_adresse(res.getString("c.client_adresse"));
                    commande.setClient_phone(res.getString("c.client_phone"));
                    commande.setClient_email(res.getString("c.client_email"));
                    commande.setMethodePaiement(res.getString("c.methode_paiement"));
                    commande.setEtatCommande(res.getString("c.etat_commande"));
                    
                    // Handle possible NULL user_id
                    try {
                        int userId = res.getInt("c.user_id");
                        if (!res.wasNull()) {
                            commande.setUserId(userId);
                        } else {
                            commande.setUserId(0);
                        }
                    } catch (SQLException e) {
                        System.out.println("Error getting user_id, using default value: " + e.getMessage());
                        commande.setUserId(0);
                    }
                    
                    String dateStr = res.getString("c.date");
                    if (dateStr != null && !dateStr.isEmpty()) {
                        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
                        try {
                            commande.setDate(LocalDateTime.parse(dateStr, formatter));
                        } catch (Exception e) {
                            // Try alternative format if the first one fails
                            formatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME;
                            commande.setDate(LocalDateTime.parse(dateStr, formatter));
                        }
                    }
                    
                    commande.setInstructionSpeciale(res.getString("c.instruction_speciale"));
                    commande.setPrixtotal(res.getFloat("c.prixtotal"));
                    
                    commandeMap.put(commandeId, commande);
                    commandes.add(commande);
                }
                
                // Add product to the order if it exists
                Commande commande = commandeMap.get(commandeId);
                int produitId = res.getInt("pc.produit_id");
                if (produitId > 0 && !res.wasNull()) {
                    Produit produit = new Produit();
                    produit.setId(produitId);
                    produit.setNom_produit(res.getString("p.nom_produit"));
                    produit.setDescription(res.getString("p.description"));
                    produit.setImage(res.getString("p.image"));
                    
                    // Utiliser la quantité du produit_commande si disponible
                    try {
                        int quantite = res.getInt("pc.quantite");
                        if (!res.wasNull()) {
                            produit.setQuantity(quantite);
                        } else {
                            produit.setQuantity(1); // Valeur par défaut
                        }
                    } catch (SQLException e) {
                        System.out.println("Colonne quantite non disponible, utilisation de la valeur par défaut 1");
                        produit.setQuantity(1);
                    }
                    
                    // Utiliser le prix unitaire de produit_commande si disponible
                    try {
                        double prixUnitaire = res.getDouble("pc.prix_unitaire");
                        if (!res.wasNull()) {
                            produit.setPrix(prixUnitaire);
                        } else {
                            produit.setPrix(res.getDouble("p.prix"));
                        }
                    } catch (SQLException e) {
                        System.out.println("Colonne prix_unitaire non disponible, utilisation du prix du produit");
                        produit.setPrix(res.getDouble("p.prix"));
                    }
                    
                    produit.setType_produit(res.getString("p.type_produit"));
                    produit.setDate_fabrication(res.getDate("p.date_fabrication"));
                    produit.setDate_expiration(res.getDate("p.date_expiration"));
                    
                    commande.getProduits().add(produit);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        
        return commandes;
    }

    @Override
    public Commande getOne(int id) {
        String req = "SELECT c.*, pc.produit_id, pc.quantite, pc.prix_unitaire, p.* FROM commande c " +
                    "LEFT JOIN produit_commande pc ON c.id = pc.commande_id " +
                    "LEFT JOIN produit p ON pc.produit_id = p.id " +
                    "WHERE c.id = ?";
                    
        try (PreparedStatement ps = cnx.prepareStatement(req)) {
            ps.setInt(1, id);
            ResultSet res = ps.executeQuery();
            
            Commande commande = null;
            
            while (res.next()) {
                if (commande == null) {
                    commande = new Commande();
                    commande.setId(res.getInt("c.id"));
                    commande.setClient_name(res.getString("c.client_name"));
                    commande.setClient_family_name(res.getString("c.client_family_name"));
                    commande.setClient_adresse(res.getString("c.client_adresse"));
                    commande.setClient_phone(res.getString("c.client_phone"));
                    commande.setClient_email(res.getString("c.client_email"));
                    commande.setMethodePaiement(res.getString("c.methode_paiement"));
                    commande.setEtatCommande(res.getString("c.etat_commande"));
                    
                    // Handle possible NULL user_id
                    try {
                        int userId = res.getInt("c.user_id");
                        if (!res.wasNull()) {
                            commande.setUserId(userId);
                        } else {
                            commande.setUserId(0);
                        }
                    } catch (SQLException e) {
                        System.out.println("Error getting user_id, using default value: " + e.getMessage());
                        commande.setUserId(0);
                    }
                    
                    String dateStr = res.getString("c.date");
                    if (dateStr != null && !dateStr.isEmpty()) {
                        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
                        try {
                            commande.setDate(LocalDateTime.parse(dateStr, formatter));
                        } catch (Exception e) {
                            // Try alternative format if the first one fails
                            formatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME;
                            commande.setDate(LocalDateTime.parse(dateStr, formatter));
                        }
                    }
                    
                    commande.setInstructionSpeciale(res.getString("c.instruction_speciale"));
                    commande.setPrixtotal(res.getFloat("c.prixtotal"));
                }
                
                // Add product to the order if it exists
                int produitId = res.getInt("pc.produit_id");
                if (produitId > 0 && !res.wasNull()) {
                    Produit produit = new Produit();
                    produit.setId(produitId);
                    produit.setNom_produit(res.getString("p.nom_produit"));
                    produit.setDescription(res.getString("p.description"));
                    produit.setImage(res.getString("p.image"));
                    
                    // Utiliser la quantité du produit_commande si disponible
                    try {
                        int quantite = res.getInt("pc.quantite");
                        if (!res.wasNull()) {
                            produit.setQuantity(quantite);
                        } else {
                            produit.setQuantity(1); // Valeur par défaut
                        }
                    } catch (SQLException e) {
                        System.out.println("Colonne quantite non disponible, utilisation de la valeur par défaut 1");
                        produit.setQuantity(1);
                    }
                    
                    // Utiliser le prix unitaire de produit_commande si disponible
                    try {
                        double prixUnitaire = res.getDouble("pc.prix_unitaire");
                        if (!res.wasNull()) {
                            produit.setPrix(prixUnitaire);
                        } else {
                            produit.setPrix(res.getDouble("p.prix"));
                        }
                    } catch (SQLException e) {
                        System.out.println("Colonne prix_unitaire non disponible, utilisation du prix du produit");
                        produit.setPrix(res.getDouble("p.prix"));
                    }
                    
                    produit.setType_produit(res.getString("p.type_produit"));
                    produit.setDate_fabrication(res.getDate("p.date_fabrication"));
                    produit.setDate_expiration(res.getDate("p.date_expiration"));
                    
                    commande.getProduits().add(produit);
                }
            }
            
            return commande;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void addProduitToCommande(List<Produit> produits, Commande commande) {
        String req = "INSERT INTO produit_commande (commande_id, produit_id, quantite, prix_unitaire) VALUES (?, ?, ?, ?)";
        try (PreparedStatement ps = cnx.prepareStatement(req)) {
            for (Produit produit : produits) {
                ps.setInt(1, commande.getId());
                ps.setInt(2, produit.getId());
                ps.setInt(3, produit.getQuantity());
                ps.setDouble(4, produit.getPrix());
                ps.addBatch();
            }
            ps.executeBatch();
            System.out.println("Products added to order successfully");
        } catch (SQLException e) {
            e.printStackTrace();
            // Try alternative insert method if the first one fails
            tryAlternativeInsert(produits, commande);
        }
    }

    private void tryAlternativeInsert(List<Produit> produits, Commande commande) {
        String req = "INSERT INTO produit_commande (commande_id, produit_id, quantite) VALUES (?, ?, ?)";
        try (PreparedStatement ps = cnx.prepareStatement(req)) {
            for (Produit produit : produits) {
                ps.setInt(1, commande.getId());
                ps.setInt(2, produit.getId());
                ps.setInt(3, produit.getQuantity());
                ps.addBatch();
            }
            ps.executeBatch();
            System.out.println("Products added to order successfully (alternative method)");
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Failed to add products to order", e);
        }
    }

    public List<Commande> getOrdersByUserId(int userId) {
        List<Commande> commandes = new ArrayList<>();
        Map<Integer, Commande> commandeMap = new HashMap<>();
        
        String req = "SELECT c.*, pc.produit_id, pc.quantite, pc.prix_unitaire, p.* FROM commande c " +
                    "LEFT JOIN produit_commande pc ON c.id = pc.commande_id " +
                    "LEFT JOIN produit p ON pc.produit_id = p.id " +
                    "WHERE c.user_id = ? " +
                    "ORDER BY c.id";
        
        try (PreparedStatement ps = cnx.prepareStatement(req)) {
            ps.setInt(1, userId);
            ResultSet res = ps.executeQuery();
            
            while (res.next()) {
                int commandeId = res.getInt("c.id");
                
                // If we haven't processed this order yet, create it
                if (!commandeMap.containsKey(commandeId)) {
                    Commande commande = new Commande();
                    commande.setId(commandeId);
                    commande.setClient_name(res.getString("c.client_name"));
                    commande.setClient_family_name(res.getString("c.client_family_name"));
                    commande.setClient_adresse(res.getString("c.client_adresse"));
                    commande.setClient_phone(res.getString("c.client_phone"));
                    commande.setClient_email(res.getString("c.client_email"));
                    commande.setMethodePaiement(res.getString("c.methode_paiement"));
                    commande.setEtatCommande(res.getString("c.etat_commande"));
                    commande.setUserId(userId);
                    
                    String dateStr = res.getString("c.date");
                    if (dateStr != null && !dateStr.isEmpty()) {
                        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
                        try {
                            commande.setDate(LocalDateTime.parse(dateStr, formatter));
                        } catch (Exception e) {
                            // Try alternative format if the first one fails
                            formatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME;
                            commande.setDate(LocalDateTime.parse(dateStr, formatter));
                        }
                    }
                    
                    commande.setInstructionSpeciale(res.getString("c.instruction_speciale"));
                    commande.setPrixtotal(res.getFloat("c.prixtotal"));
                    
                    commandeMap.put(commandeId, commande);
                    commandes.add(commande);
                }
                
                // Add product to the order if it exists
                Commande commande = commandeMap.get(commandeId);
                int produitId = res.getInt("pc.produit_id");
                if (produitId > 0 && !res.wasNull()) {
                    Produit produit = new Produit();
                    produit.setId(produitId);
                    produit.setNom_produit(res.getString("p.nom_produit"));
                    produit.setDescription(res.getString("p.description"));
                    produit.setImage(res.getString("p.image"));
                    
                    // Utiliser la quantité du produit_commande si disponible
                    try {
                        int quantite = res.getInt("pc.quantite");
                        if (!res.wasNull()) {
                            produit.setQuantity(quantite);
                        } else {
                            produit.setQuantity(1); // Valeur par défaut
                        }
                    } catch (SQLException e) {
                        System.out.println("Colonne quantite non disponible, utilisation de la valeur par défaut 1");
                        produit.setQuantity(1);
                    }
                    
                    // Utiliser le prix unitaire de produit_commande si disponible
                    try {
                        double prixUnitaire = res.getDouble("pc.prix_unitaire");
                        if (!res.wasNull()) {
                            produit.setPrix(prixUnitaire);
                        } else {
                            produit.setPrix(res.getDouble("p.prix"));
                        }
                    } catch (SQLException e) {
                        System.out.println("Colonne prix_unitaire non disponible, utilisation du prix du produit");
                        produit.setPrix(res.getDouble("p.prix"));
                    }
                    
                    produit.setType_produit(res.getString("p.type_produit"));
                    produit.setDate_fabrication(res.getDate("p.date_fabrication"));
                    produit.setDate_expiration(res.getDate("p.date_expiration"));
                    
                    commande.getProduits().add(produit);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        
        return commandes;
    }

    private void sendOrderConfirmationEmail(Commande commande) {
        // Implementation of email sending
        System.out.println("Sending order confirmation email for order #" + commande.getId());
    }

    private void sendDeliveryStatusEmail(Commande commande) {
        // Implementation of email sending
        System.out.println("Sending delivery status email for order #" + commande.getId());
    }

    public boolean testDeliveryEmail(int orderId) {
        Commande commande = getOne(orderId);
        if (commande != null) {
            sendDeliveryStatusEmail(commande);
            return true;
        }
        return false;
    }

    public String generateOrderPDF(int orderId) {
        Commande commande = getOne(orderId);
        if (commande != null) {
            PDFService pdfService = new PDFService();
            return pdfService.generateOrderPDF(commande);
        }
        return null;
    }

    /**
     * Add a product to an order
     * @param commandeId Order ID
     * @param produitId Product ID
     * @param quantity Quantity of the product
     * @return true if successful, false otherwise
     */
    public boolean addOrderItem(int commandeId, int produitId, int quantity) {
        String req = "INSERT INTO commande_produit (commande_id, produit_id, quantity) VALUES (?, ?, ?)";
        try (PreparedStatement ps = cnx.prepareStatement(req)) {
            ps.setInt(1, commandeId);
            ps.setInt(2, produitId);
            ps.setInt(3, quantity);
            
            int affectedRows = ps.executeUpdate();
            return affectedRows > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}