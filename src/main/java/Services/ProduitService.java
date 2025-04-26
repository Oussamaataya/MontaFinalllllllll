package Services;


import java.sql.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.io.File;


import Entities.Commande;
import Entities.Produit;
import Utils.ImageManager;
import Utils.MyDataBase;

public class ProduitService implements IService<Produit> {

    private Connection cnx = MyDataBase.getInstance().getConnection();

    private String handleImageUpload(String imagePath) {
        // Utiliser la classe ImageManager pour traiter l'image
        return ImageManager.processProductImage(imagePath);
    }

    @Override
    public int add(Produit produit) {
        // Gérer l'upload de l'image avant d'insérer dans la base de données
        String processedImagePath = handleImageUpload(produit.getImage());
        produit.setImage(processedImagePath);

        String req = "INSERT INTO produit (nom_produit, description, image, quantity, prix, type_produit, date_fabrication, date_expiration) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement ps = cnx.prepareStatement(req, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, produit.getNom_produit());
            ps.setString(2, produit.getDescription());
            ps.setString(3, produit.getImage());
            ps.setInt(4, produit.getQuantity());
            ps.setDouble(5, produit.getPrix());
            ps.setString(6, produit.getType_produit());
            ps.setDate(7, produit.getDate_fabrication() != null ? new java.sql.Date(produit.getDate_fabrication().getTime()) : null);
            ps.setDate(8, produit.getDate_expiration() != null ? new java.sql.Date(produit.getDate_expiration().getTime()) : null);
            ps.executeUpdate();

            // Get the generated ID
            ResultSet generatedKeys = ps.getGeneratedKeys();
            if (generatedKeys.next()) {
                int generatedId = generatedKeys.getInt(1);
                produit.setId(generatedId);
                System.out.println("Produit added successfully with ID: " + generatedId);
                return generatedId;
            }

            System.out.println("Produit added successfully");
            return -1;
        } catch (SQLException e) {
            e.printStackTrace();
            return -1;
        }
    }

    @Override
    public void update(Produit produit) {
        // Gérer l'upload de l'image
        String processedImagePath = handleImageUpload(produit.getImage());
        produit.setImage(processedImagePath);

        String req = "UPDATE produit SET nom_produit = ?, description = ?, image = ?, quantity = ?, prix = ?, type_produit = ?, date_fabrication = ?, date_expiration = ? WHERE id = ?";
        try {
            PreparedStatement ps = cnx.prepareStatement(req);
            ps.setString(1, produit.getNom_produit());
            ps.setString(2, produit.getDescription());
            ps.setString(3, produit.getImage());
            ps.setInt(4, produit.getQuantity());
            ps.setDouble(5, produit.getPrix());
            ps.setString(6, produit.getType_produit());
            ps.setDate(7, new java.sql.Date(produit.getDate_fabrication().getTime()));
            ps.setDate(8, new java.sql.Date(produit.getDate_expiration().getTime()));
            ps.setInt(9, produit.getId());

            ps.executeUpdate();
            System.out.println("Produit updated successfully");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void delete(Produit produit) {
        String req = "DELETE FROM produit WHERE id = " + produit.getId();
        try {
            Statement st = cnx.createStatement();
            st.executeUpdate(req);
            System.out.println("Produit deleted successfully");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public List<Produit> getAll() {
        List<Produit> produits = new ArrayList<>();
        String req = "SELECT p.*, c.* FROM produit p LEFT JOIN produit_commande pc ON p.id = pc.produit_id LEFT JOIN commande c ON pc.commande_id = c.id";
        try {
            Statement st = cnx.createStatement();
            ResultSet res = st.executeQuery(req);
            while (res.next()) {
                Produit produit = new Produit();
                produit.setId(res.getInt("p.id"));
                produit.setNom_produit(res.getString("p.nom_produit"));
                produit.setDescription(res.getString("p.description"));
                produit.setImage(res.getString("p.image"));
                produit.setQuantity(res.getInt("p.quantity"));
                produit.setPrix(res.getDouble("p.prix"));
                produit.setType_produit(res.getString("p.type_produit"));
                produit.setDate_fabrication(res.getDate("p.date_fabrication"));
                produit.setDate_expiration(res.getDate("p.date_expiration"));

                // Création d'une nouvelle instance de Commande associée à ce produit
                Commande commande = new Commande();
                commande.setId(res.getInt("c.id"));
                commande.setClient_name(res.getString("c.client_name"));
                commande.setClient_family_name(res.getString("c.client_family_name"));
                commande.setClient_adresse(res.getString("c.client_adresse"));
                commande.setClient_phone(res.getString("c.client_phone"));
                commande.setMethodePaiement(res.getString("c.methode_paiement"));
                commande.setEtatCommande(res.getString("c.etat_commande"));
                // Parsing de la date à partir de la chaîne de caractères
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
                String dateString = res.getString("c.date");
                LocalDateTime date = null;
                if (dateString != null) {
                    date = LocalDateTime.parse(dateString, formatter);
                }
                commande.setDate(date);
                commande.setInstructionSpeciale(res.getString("c.instruction_speciale"));
                commande.setPrixtotal(res.getInt("c.prixtotal"));

                // Ajout de la commande au produit
                if (produit.getCommandes() != null) {
                    produit.getCommandes().add(commande);
                }

                // Ajout du produit à la liste des produits
                produits.add(produit);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return produits;
    }

    @Override
    public Produit getOne(int id) {
        String req = "SELECT * FROM produit WHERE id = " + id;
        try {
            Statement st = cnx.createStatement();
            ResultSet res = st.executeQuery(req);
            if (res.next()) {
                Produit produit = new Produit();
                produit.setId(res.getInt("id"));
                produit.setNom_produit(res.getString("nom_produit"));
                produit.setDescription(res.getString("description"));
                produit.setImage(res.getString("image"));
                produit.setQuantity(res.getInt("quantity"));
                produit.setPrix(res.getDouble("prix"));
                produit.setType_produit(res.getString("type_produit"));
                produit.setDate_fabrication(res.getDate("date_fabrication"));
                produit.setDate_expiration(res.getDate("date_expiration"));
                return produit;
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return null;
    }

    /**
     * Récupère un nombre spécifié de produits aléatoires pour les recommandations
     *
     * @param count Nombre de produits à récupérer
     * @return Liste de produits aléatoires
     */
    public List<Produit> getRandomProduits(int count) {
        List<Produit> result = new ArrayList<>();
        List<Produit> allProduits = getAll();

        if (allProduits.isEmpty()) {
            return result;
        }

        // Si moins de produits disponibles que demandés, retourner tous les produits
        if (allProduits.size() <= count) {
            return allProduits;
        }

        // Sélectionner aléatoirement count produits
        Random random = new Random();
        Set<Integer> selectedIndices = new HashSet<>();

        while (selectedIndices.size() < count) {
            int index = random.nextInt(allProduits.size());
            if (selectedIndices.add(index)) {
                result.add(allProduits.get(index));
            }
        }

        return result;
    }
}