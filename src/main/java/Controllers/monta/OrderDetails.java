package Controllers.monta;


import Entities.Commande;
import Entities.Produit;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

import java.time.format.DateTimeFormatter;

public class OrderDetails {

    @FXML
    private Label orderNumberLabel;

    @FXML
    private Label dateLabel;

    @FXML
    private Label statusLabel;

    @FXML
    private Label paymentMethodLabel;

    @FXML
    private Label totalLabel;

    @FXML
    private Label clientNameLabel;

    @FXML
    private Label addressLabel;

    @FXML
    private Label phoneLabel;

    @FXML
    private Label instructionsLabel;

    @FXML
    private TableView<Produit> productsTableView;

    @FXML
    private TableColumn<Produit, String> productNameCol;

    @FXML
    private TableColumn<Produit, Integer> quantityCol;

    @FXML
    private TableColumn<Produit, Double> priceCol;

    @FXML
    private TableColumn<Produit, Double> subtotalCol;

    private Commande commande;

    @FXML
    void initialize() {
        // Setup table columns
        productNameCol.setCellValueFactory(new PropertyValueFactory<>("nom_produit"));
        quantityCol.setCellValueFactory(new PropertyValueFactory<>("quantity"));
        priceCol.setCellValueFactory(new PropertyValueFactory<>("prix"));
        
        // Create subtotal column (price * quantity)
        subtotalCol.setCellValueFactory(cellData -> {
            Produit produit = cellData.getValue();
            double subtotal = produit.getPrix() * produit.getQuantity();
            return new javafx.beans.property.SimpleDoubleProperty(subtotal).asObject();
        });
    }

    public void setCommande(Commande commande) {
        this.commande = commande;
        
        // Display order details
        orderNumberLabel.setText("Commande #" + commande.getId());
        
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
        dateLabel.setText(commande.getDate().format(formatter));
        
        statusLabel.setText(commande.getEtatCommande());
        paymentMethodLabel.setText(commande.getMethodePaiement());
        totalLabel.setText(String.format("%.2f €", commande.getPrixtotal()));
        
        clientNameLabel.setText(commande.getClient_name() + " " + commande.getClient_family_name());
        addressLabel.setText(commande.getClient_adresse());
        phoneLabel.setText(commande.getClient_phone());
        
        String instructions = commande.getInstructionSpeciale();
        if (instructions == null || instructions.trim().isEmpty()) {
            instructionsLabel.setText("Aucune instruction spéciale");
        } else {
            instructionsLabel.setText(instructions);
        }
        
        // Display products
        ObservableList<Produit> products = FXCollections.observableArrayList(commande.getProduits());
        productsTableView.setItems(products);
    }
    
    @FXML
    private void closeWindow() {
        Stage stage = (Stage) orderNumberLabel.getScene().getWindow();
        stage.close();
    }
} 
