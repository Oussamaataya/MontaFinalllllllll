package Controllers.monta;


import Entities.Commande;
import Entities.UserSession;
import Services.CommandeService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class OrderHistory {

    @FXML
    private TableView<Commande> orderTableView;

    @FXML
    private TableColumn<Commande, Integer> idCol;

    @FXML
    private TableColumn<Commande, LocalDateTime> dateCol;

    @FXML
    private TableColumn<Commande, String> statusCol;

    @FXML
    private TableColumn<Commande, Float> totalCol;

    @FXML
    private TableColumn<Commande, String> paymentMethodCol;

    @FXML
    private Button viewDetailsBtn;

    @FXML
    private Button cancelOrderBtn;

    @FXML
    private Button retourBtn;

    @FXML
    private Label noOrdersLabel;

    private final CommandeService commandeService = new CommandeService();

    @FXML
    void initialize() {
        setupTable();
        loadOrders();

        // Setup button events
        viewDetailsBtn.setDisable(true);
        cancelOrderBtn.setDisable(true);

        orderTableView.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            boolean hasSelection = newSelection != null;
            viewDetailsBtn.setDisable(!hasSelection);
            
            // Only allow cancellation if status is "En attente"
            if (hasSelection && "En attente".equals(newSelection.getEtatCommande())) {
                cancelOrderBtn.setDisable(false);
            } else {
                cancelOrderBtn.setDisable(true);
            }
        });
    }

    private void setupTable() {
        idCol.setCellValueFactory(new PropertyValueFactory<>("id"));
        
        dateCol.setCellValueFactory(new PropertyValueFactory<>("date"));
        dateCol.setCellFactory(col -> new TableCell<Commande, LocalDateTime>() {
            private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
            
            @Override
            protected void updateItem(LocalDateTime date, boolean empty) {
                super.updateItem(date, empty);
                if (empty || date == null) {
                    setText(null);
                } else {
                    setText(formatter.format(date));
                }
            }
        });
        
        statusCol.setCellValueFactory(new PropertyValueFactory<>("etatCommande"));
        totalCol.setCellValueFactory(new PropertyValueFactory<>("prixtotal"));
        paymentMethodCol.setCellValueFactory(new PropertyValueFactory<>("methodePaiement"));
    }

    private void loadOrders() {
        // Get current user
        UserSession session = UserSession.getInstance();
        int userId = session.getUser().getId();
        
        // Load orders for this user (assuming user_id is stored in the commande table)
        List<Commande> orders = commandeService.getOrdersByUserId(userId);
        
        if (orders.isEmpty()) {
            noOrdersLabel.setVisible(true);
            orderTableView.setVisible(false);
        } else {
            noOrdersLabel.setVisible(false);
            orderTableView.setVisible(true);
            
            ObservableList<Commande> observableList = FXCollections.observableList(orders);
            orderTableView.setItems(observableList);
        }
    }

    @FXML
    private void viewOrderDetails() {
        Commande selectedOrder = orderTableView.getSelectionModel().getSelectedItem();
        if (selectedOrder == null) {
            return;
        }
        
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/FXML/monta/OrderDetails.fxml"));
            Parent root = loader.load();
            
            OrderDetails controller = loader.getController();
            controller.setCommande(selectedOrder);
            
            Stage stage = new Stage();
            stage.setTitle("Détails de la commande #" + selectedOrder.getId());
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Erreur", "Impossible d'afficher les détails de la commande");
        }
    }

    @FXML
    private void cancelOrder() {
        Commande selectedOrder = orderTableView.getSelectionModel().getSelectedItem();
        if (selectedOrder == null || !"En attente".equals(selectedOrder.getEtatCommande())) {
            return;
        }
        
        Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION);
        confirmAlert.setTitle("Confirmation");
        confirmAlert.setHeaderText(null);
        confirmAlert.setContentText("Êtes-vous sûr de vouloir annuler cette commande ?");
        
        if (confirmAlert.showAndWait().orElse(ButtonType.CANCEL) == ButtonType.OK) {
            // Update order status
            selectedOrder.setEtatCommande("Annulée");
            commandeService.update(selectedOrder);
            
            // Refresh the table
            loadOrders();
            
            showAlert(Alert.AlertType.INFORMATION, "Succès", "Commande annulée avec succès");
        }
    }

    @FXML
    private void retourAccueil() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/FXML/monta/Acceuil.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) retourBtn.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Erreur", "Impossible de retourner à l'accueil");
        }
    }
    
    private void showAlert(Alert.AlertType type, String title, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
} 
