package Controllers.monta;


import Entities.Commande;
import Entities.Produit;
import Entities.UserSession;
import Services.CommandeService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.List;

public class Listcommande1 {
    @FXML
    private TableColumn<Commande, Void> actionsCol;

    @FXML
    private TableColumn<Commande, String> clientAdresseCol;

    @FXML
    private TableColumn<Commande, String> clientFamilyNameCol;

    @FXML
    private TableColumn<Commande, String> clientNameCol;

    @FXML
    private TableColumn<Commande, String> clientPhoneCol;

    @FXML
    private TableColumn<Commande, String> dateCol;

    @FXML
    private TableColumn<Commande, String> etatCommandeCol;

    @FXML
    private TableColumn<Commande, String> instructionSpecialeCol;

    @FXML
    private TableColumn<Commande, String> methodePaiementCol;

    @FXML
    private TableColumn<Commande, Float> prixtotalCol;

    @FXML
    private TableView<Commande> tableview;

    private final CommandeService commandeService = new CommandeService();

    @FXML
    void initialize() {
        // Récupérer l'utilisateur connecté
UserSession userSession = UserSession.getInstance();
        
        List<Commande> commandes;
        
        // Si un utilisateur est connecté, récupérer ses commandes uniquement
        if (userSession.isLoggedIn() && userSession.getUser() != null) {
            int userId = userSession.getUser().getId();
            commandes = commandeService.getOrdersByUserId(userId);
            
            // Afficher un message si aucune commande trouvée
            if (commandes.isEmpty()) {
                tableview.setPlaceholder(new Label("Vous n'avez pas encore de commandes."));
            }
        } else {
            // Si aucun utilisateur n'est connecté, afficher un message
            commandes = new ArrayList<>();
            tableview.setPlaceholder(new Label("Veuillez vous connecter pour voir vos commandes."));
        }
        
        ObservableList<Commande> observableList = FXCollections.observableList(commandes);
        tableview.setItems(observableList);

        // Style général de la table
        tableview.setStyle("-fx-background-color: white; -fx-border-color: #e0e0e0; -fx-border-radius: 5; -fx-padding: 10;");
        tableview.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        // Style des en-têtes de colonnes
        tableview.getStyleClass().add("table-view");
        for (TableColumn<?, ?> column : tableview.getColumns()) {
            column.getStyleClass().add("table-column");
            column.setPrefWidth(Region.USE_COMPUTED_SIZE);
        }

        // Configuration des colonnes avec styles
        clientNameCol.setCellValueFactory(new PropertyValueFactory<>("client_name"));
        clientNameCol.setStyle("-fx-alignment: CENTER; -fx-font-weight: bold; -fx-font-size: 13px;");
        clientNameCol.setMinWidth(120);

        clientFamilyNameCol.setCellValueFactory(new PropertyValueFactory<>("client_family_name"));
        clientFamilyNameCol.setStyle("-fx-alignment: CENTER; -fx-font-weight: bold; -fx-font-size: 13px;");
        clientFamilyNameCol.setMinWidth(120);

        clientAdresseCol.setCellValueFactory(new PropertyValueFactory<>("client_adresse"));
        clientAdresseCol.setStyle("-fx-alignment: CENTER; -fx-font-size: 12px;");
        clientAdresseCol.setMinWidth(200);
        clientAdresseCol.setVisible(false); // Cachée par défaut, visible dans les détails
        
        clientPhoneCol.setCellValueFactory(new PropertyValueFactory<>("client_phone"));
        clientPhoneCol.setStyle("-fx-alignment: CENTER; -fx-font-size: 12px;");
        clientPhoneCol.setMinWidth(120);
        clientPhoneCol.setVisible(false); // Cachée par défaut, visible dans les détails
        
        methodePaiementCol.setCellValueFactory(new PropertyValueFactory<>("methodePaiement"));
        methodePaiementCol.setStyle("-fx-alignment: CENTER; -fx-font-size: 12px;");
        methodePaiementCol.setMinWidth(120);
        methodePaiementCol.setVisible(false); // Cachée par défaut, visible dans les détails
        
        etatCommandeCol.setCellValueFactory(new PropertyValueFactory<>("etatCommande"));
        etatCommandeCol.setStyle("-fx-alignment: CENTER; -fx-font-size: 12px;");
        etatCommandeCol.setMinWidth(120);
        etatCommandeCol.setCellFactory(column -> {
            return new TableCell<Commande, String>() {
                @Override
                protected void updateItem(String etat, boolean empty) {
                    super.updateItem(etat, empty);
                    if (empty || etat == null) {
                        setText(null);
                        setStyle("-fx-alignment: CENTER; -fx-font-size: 12px;");
                    } else {
                        setText(etat);
                        if (etat.equalsIgnoreCase("En attente")) {
                            setStyle("-fx-alignment: CENTER; -fx-text-fill: #FFA500; -fx-font-weight: bold; -fx-font-size: 12px;");
                        } else if (etat.equalsIgnoreCase("Livrée") || etat.equalsIgnoreCase("Complétée")) {
                            setStyle("-fx-alignment: CENTER; -fx-text-fill: #4CAF50; -fx-font-weight: bold; -fx-font-size: 12px;");
                        } else if (etat.equalsIgnoreCase("Annulée")) {
                            setStyle("-fx-alignment: CENTER; -fx-text-fill: #F44336; -fx-font-weight: bold; -fx-font-size: 12px;");
                        } else {
                            setStyle("-fx-alignment: CENTER; -fx-font-size: 12px;");
                        }
                    }
                }
            };
        });
        
        dateCol.setCellValueFactory(new PropertyValueFactory<>("date"));
        dateCol.setStyle("-fx-alignment: CENTER; -fx-font-size: 12px;");
        dateCol.setMinWidth(120);
        
        instructionSpecialeCol.setCellValueFactory(new PropertyValueFactory<>("instructionSpeciale"));
        instructionSpecialeCol.setVisible(false); // Cachée par défaut, visible dans les détails
        
        prixtotalCol.setCellValueFactory(new PropertyValueFactory<>("prixtotal"));
        prixtotalCol.setStyle("-fx-alignment: CENTER; -fx-font-weight: bold; -fx-text-fill: #4CAF50; -fx-font-size: 13px;");
        prixtotalCol.setMinWidth(100);
        prixtotalCol.setCellFactory(column -> {
            return new TableCell<Commande, Float>() {
                @Override
                protected void updateItem(Float prix, boolean empty) {
                    super.updateItem(prix, empty);
                    if (empty || prix == null) {
                        setText(null);
                    } else {
                        setText(String.format("%.2f €", prix));
                    }
                }
            };
        });

        // Configuration de la colonne des actions
        actionsCol.setMinWidth(200);
        actionsCol.setStyle("-fx-alignment: CENTER;");
        actionsCol.setCellFactory(column -> {
            return new TableCell<>() {
                private final HBox buttonBox = new HBox(10);
                private final Button detailsButton = new Button("Détails");
                private final Button cancelButton = new Button("Annuler");

                {
                    // Style des boutons
                    detailsButton.setStyle("-fx-background-color: #2196F3; -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 8 15; -fx-background-radius: 5; -fx-font-size: 12px;");
                    cancelButton.setStyle("-fx-background-color: #F44336; -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 8 15; -fx-background-radius: 5; -fx-font-size: 12px;");
                    
                    // Effet de survol pour les boutons
                    detailsButton.setOnMouseEntered(e -> detailsButton.setStyle("-fx-background-color: #1976D2; -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 8 15; -fx-background-radius: 5; -fx-font-size: 12px;"));
                    detailsButton.setOnMouseExited(e -> detailsButton.setStyle("-fx-background-color: #2196F3; -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 8 15; -fx-background-radius: 5; -fx-font-size: 12px;"));
                    
                    cancelButton.setOnMouseEntered(e -> cancelButton.setStyle("-fx-background-color: #D32F2F; -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 8 15; -fx-background-radius: 5; -fx-font-size: 12px;"));
                    cancelButton.setOnMouseExited(e -> cancelButton.setStyle("-fx-background-color: #F44336; -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 8 15; -fx-background-radius: 5; -fx-font-size: 12px;"));
                    
                    // Centrer les boutons
                    buttonBox.setAlignment(Pos.CENTER);
                    buttonBox.getChildren().addAll(detailsButton, cancelButton);

                    // Action pour le bouton Détails
                    detailsButton.setOnAction(event -> {
                        Commande commande = getTableView().getItems().get(getIndex());
                        showCommandeDetails(commande);
                    });

                    // Action pour le bouton Annuler
                    cancelButton.setOnAction(event -> {
                        Commande commande = getTableView().getItems().get(getIndex());
                        deleteCommande(commande);
                    });
                }

                @Override
                protected void updateItem(Void item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty) {
                        setGraphic(null);
                    } else {
                        Commande commande = getTableView().getItems().get(getIndex());
                        // Si la commande est déjà annulée, désactiver le bouton Annuler
                        if (commande.getEtatCommande().equalsIgnoreCase("Annulée")) {
                            cancelButton.setDisable(true);
                            cancelButton.setStyle("-fx-background-color: #9E9E9E; -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 8 15; -fx-background-radius: 5; -fx-font-size: 12px;");
                        } else {
                            cancelButton.setDisable(false);
                            cancelButton.setStyle("-fx-background-color: #F44336; -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 8 15; -fx-background-radius: 5; -fx-font-size: 12px;");
                        }
                        setGraphic(buttonBox);
                    }
                }
            };
        });

        // Style des lignes de la table
        tableview.setRowFactory(tv -> {
            TableRow<Commande> row = new TableRow<Commande>() {
                @Override
                protected void updateItem(Commande commande, boolean empty) {
                    super.updateItem(commande, empty);
                    if (empty || commande == null) {
                        setStyle("-fx-background-color: white;");
                    } else {
                        // Style de base pour toutes les lignes
                        setStyle("-fx-background-color: white; -fx-border-color: #f0f0f0; -fx-border-width: 0 0 1 0;");
                        
                        // Styles spécifiques selon l'état de la commande
                        if (commande.getEtatCommande() != null) {
                            if (commande.getEtatCommande().equalsIgnoreCase("En attente")) {
                                setStyle("-fx-background-color: #FFF8E1; -fx-border-color: #f0f0f0; -fx-border-width: 0 0 1 0;");
                            } else if (commande.getEtatCommande().equalsIgnoreCase("Livrée") || 
                                    commande.getEtatCommande().equalsIgnoreCase("Complétée")) {
                                setStyle("-fx-background-color: #E8F5E9; -fx-border-color: #f0f0f0; -fx-border-width: 0 0 1 0;");
                            } else if (commande.getEtatCommande().equalsIgnoreCase("Annulée")) {
                                setStyle("-fx-background-color: #FFEBEE; -fx-border-color: #f0f0f0; -fx-border-width: 0 0 1 0;");
                            }
                        }
                    }
                }
            };
            
            // Effet de survol
            row.setOnMouseEntered(event -> {
                if (!row.isEmpty()) {
                    Commande commande = row.getItem();
                    String baseStyle = "-fx-border-color: #f0f0f0; -fx-border-width: 0 0 1 0;";
                    if (commande.getEtatCommande() != null) {
                        if (commande.getEtatCommande().equalsIgnoreCase("En attente")) {
                            row.setStyle("-fx-background-color: #FFE0B2; " + baseStyle);
                        } else if (commande.getEtatCommande().equalsIgnoreCase("Livrée") || 
                                commande.getEtatCommande().equalsIgnoreCase("Complétée")) {
                            row.setStyle("-fx-background-color: #C8E6C9; " + baseStyle);
                        } else if (commande.getEtatCommande().equalsIgnoreCase("Annulée")) {
                            row.setStyle("-fx-background-color: #FFCDD2; " + baseStyle);
                        } else {
                            row.setStyle("-fx-background-color: #f5f5f5; " + baseStyle);
                        }
                    } else {
                        row.setStyle("-fx-background-color: #f5f5f5; " + baseStyle);
                    }
                }
            });
            
            // Retour au style normal lors de la sortie
            row.setOnMouseExited(event -> {
                if (!row.isEmpty() && !row.isSelected()) {
                    Commande commande = row.getItem();
                    String baseStyle = "-fx-border-color: #f0f0f0; -fx-border-width: 0 0 1 0;";
                    if (commande.getEtatCommande() != null) {
                        if (commande.getEtatCommande().equalsIgnoreCase("En attente")) {
                            row.setStyle("-fx-background-color: #FFF8E1; " + baseStyle);
                        } else if (commande.getEtatCommande().equalsIgnoreCase("Livrée") || 
                                commande.getEtatCommande().equalsIgnoreCase("Complétée")) {
                            row.setStyle("-fx-background-color: #E8F5E9; " + baseStyle);
                        } else if (commande.getEtatCommande().equalsIgnoreCase("Annulée")) {
                            row.setStyle("-fx-background-color: #FFEBEE; " + baseStyle);
                        } else {
                            row.setStyle("-fx-background-color: white; " + baseStyle);
                        }
                    } else {
                        row.setStyle("-fx-background-color: white; " + baseStyle);
                    }
                }
            });

            return row;
        });
    }

    private void deleteCommande(Commande commande) {
        // Si la commande est déjà annulée, ne rien faire
        if (commande.getEtatCommande().equalsIgnoreCase("Annulée")) {
            return;
        }
        
        // Mettre à jour l'état de la commande à "Annulée"
        commande.setEtatCommande("Annulée");
        commandeService.update(commande);
        
        // Rafraîchir la table
        tableview.refresh();
    }

    private void showCommandeDetails(Commande commande) {
        // Créer une nouvelle fenêtre pour afficher les détails
        Stage detailsStage = new Stage();
        detailsStage.setTitle("Détails de la commande");

        // Créer un layout pour les détails
        VBox detailsLayout = new VBox(15);
        detailsLayout.setPadding(new Insets(20));
        detailsLayout.setStyle("-fx-background-color: white;");
        detailsLayout.setAlignment(Pos.TOP_CENTER);

        // Titre
        Label titleLabel = new Label("Détails de la commande");
        titleLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-underline: true;");

        // Informations du client
        VBox clientInfoBox = new VBox(5);
        clientInfoBox.setStyle("-fx-background-color: #f5f5f5; -fx-padding: 10; -fx-background-radius: 5;");
        
        Label clientLabel = new Label("Informations du client");
        clientLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");
        
        Label nameLabel = new Label("Nom: " + commande.getClient_name() + " " + commande.getClient_family_name());
        nameLabel.setStyle("-fx-font-size: 14px;");
        
        Label addressLabel = new Label("Adresse: " + commande.getClient_adresse());
        addressLabel.setStyle("-fx-font-size: 14px;");
        
        Label phoneLabel = new Label("Téléphone: " + commande.getClient_phone());
        phoneLabel.setStyle("-fx-font-size: 14px;");
        
        clientInfoBox.getChildren().addAll(clientLabel, nameLabel, addressLabel, phoneLabel);

        // Informations de la commande
        VBox orderInfoBox = new VBox(5);
        orderInfoBox.setStyle("-fx-background-color: #f5f5f5; -fx-padding: 10; -fx-background-radius: 5;");
        
        Label orderLabel = new Label("Informations de la commande");
        orderLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");
        
        Label dateLabel = new Label("Date: " + commande.getDate());
        dateLabel.setStyle("-fx-font-size: 14px;");
        
        Label statusLabel = new Label("État: " + commande.getEtatCommande());
        statusLabel.setStyle("-fx-font-size: 14px; -fx-font-weight: bold;");
        if (commande.getEtatCommande().equalsIgnoreCase("En attente")) {
            statusLabel.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: #FFA500;");
        } else if (commande.getEtatCommande().equalsIgnoreCase("Livrée") || commande.getEtatCommande().equalsIgnoreCase("Complétée")) {
            statusLabel.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: #4CAF50;");
        } else if (commande.getEtatCommande().equalsIgnoreCase("Annulée")) {
            statusLabel.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: #F44336;");
        }
        
        Label paymentLabel = new Label("Méthode de paiement: " + commande.getMethodePaiement());
        paymentLabel.setStyle("-fx-font-size: 14px;");
        
        Label priceLabel = new Label("Prix total: " + String.format("%.2f €", commande.getPrixtotal()));
        priceLabel.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: #4CAF50;");
        
        orderInfoBox.getChildren().addAll(orderLabel, dateLabel, statusLabel, paymentLabel, priceLabel);

        // Section produits
        VBox produitsBox = new VBox(5);
        produitsBox.setStyle("-fx-background-color: #f5f5f5; -fx-padding: 10; -fx-background-radius: 5;");
        
        Label produitsLabel = new Label("Produits commandés");
        produitsLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");
        produitsBox.getChildren().add(produitsLabel);
        
        if (commande.getProduits() != null && !commande.getProduits().isEmpty()) {
            // Tableau pour afficher les produits
            VBox productListBox = new VBox(5);
            
            // En-tête du tableau
            HBox headerBox = new HBox(10);
            headerBox.setStyle("-fx-background-color: #e0e0e0; -fx-padding: 5; -fx-background-radius: 3;");
            
            Label nomHeader = new Label("Nom du produit");
            nomHeader.setPrefWidth(150);
            nomHeader.setStyle("-fx-font-weight: bold;");
            
            Label quantiteHeader = new Label("Quantité");
            quantiteHeader.setPrefWidth(80);
            quantiteHeader.setStyle("-fx-font-weight: bold;");
            
            Label prixHeader = new Label("Prix unitaire");
            prixHeader.setPrefWidth(100);
            prixHeader.setStyle("-fx-font-weight: bold;");
            
            Label totalHeader = new Label("Total");
            totalHeader.setPrefWidth(100);
            totalHeader.setStyle("-fx-font-weight: bold;");
            
            headerBox.getChildren().addAll(nomHeader, quantiteHeader, prixHeader, totalHeader);
            productListBox.getChildren().add(headerBox);
            
            // Lignes pour chaque produit
            for (Produit produit : commande.getProduits()) {
                HBox productRow = new HBox(10);
                productRow.setStyle("-fx-padding: 5; -fx-border-color: #e0e0e0; -fx-border-width: 0 0 1 0;");
                
                Label nomProduit = new Label(produit.getNom_produit());
                nomProduit.setPrefWidth(150);
                nomProduit.setWrapText(true);
                
                Label quantiteProduit = new Label(String.valueOf(produit.getQuantity()));
                quantiteProduit.setPrefWidth(80);
                
                Label prixProduit = new Label(String.format("%.2f €", produit.getPrix()));
                prixProduit.setPrefWidth(100);
                
                double totalProduit = produit.getPrix() * produit.getQuantity();
                Label totalProduitLabel = new Label(String.format("%.2f €", totalProduit));
                totalProduitLabel.setPrefWidth(100);
                
                productRow.getChildren().addAll(nomProduit, quantiteProduit, prixProduit, totalProduitLabel);
                productListBox.getChildren().add(productRow);
            }
            
            produitsBox.getChildren().add(productListBox);
        } else {
            Label noProduits = new Label("Aucun produit dans cette commande");
            noProduits.setStyle("-fx-font-style: italic;");
            produitsBox.getChildren().add(noProduits);
        }

        // Instructions spéciales
        VBox instructionsBox = new VBox(5);
        instructionsBox.setStyle("-fx-background-color: #f5f5f5; -fx-padding: 10; -fx-background-radius: 5;");
        
        Label instructionsLabel = new Label("Instructions spéciales");
        instructionsLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");
        
        Label instructionsTextLabel = new Label(commande.getInstructionSpeciale() != null && !commande.getInstructionSpeciale().isEmpty() 
                                              ? commande.getInstructionSpeciale() 
                                              : "Aucune instruction spéciale");
        instructionsTextLabel.setStyle("-fx-font-size: 14px;");
        instructionsTextLabel.setWrapText(true);
        instructionsTextLabel.setMaxWidth(400);
        
        instructionsBox.getChildren().addAll(instructionsLabel, instructionsTextLabel);

        // Bouton pour fermer la fenêtre
        Button closeButton = new Button("Fermer");
        closeButton.setStyle("-fx-background-color: #f44336; -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 8 15; -fx-background-radius: 5;");
        closeButton.setOnAction(e -> detailsStage.close());

        // Ajouter tous les éléments au layout
        detailsLayout.getChildren().addAll(
            titleLabel,
            clientInfoBox,
            orderInfoBox,
            produitsBox,
            instructionsBox,
            closeButton
        );

        // Créer la scène et l'ajouter à la fenêtre
        Scene scene = new Scene(detailsLayout, 500, 650);
        detailsStage.setScene(scene);
        detailsStage.initModality(Modality.APPLICATION_MODAL);
        detailsStage.show();
    }
}

