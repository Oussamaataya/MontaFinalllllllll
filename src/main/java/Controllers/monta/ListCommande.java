package Controllers.monta;


import Entities.Commande;
import Entities.Produit;
import Services.CommandeService;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
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

import java.io.IOException;
import java.util.List;

public class ListCommande {

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
        List<Commande> commandes = commandeService.getAll();
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
        clientAdresseCol.setVisible(false);

        clientPhoneCol.setCellValueFactory(new PropertyValueFactory<>("client_phone"));
        clientPhoneCol.setStyle("-fx-alignment: CENTER; -fx-font-size: 12px;");
        clientPhoneCol.setVisible(false);

        methodePaiementCol.setCellValueFactory(new PropertyValueFactory<>("methodePaiement"));
        methodePaiementCol.setStyle("-fx-alignment: CENTER; -fx-font-size: 12px;");
        methodePaiementCol.setMinWidth(120);

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
        instructionSpecialeCol.setStyle("-fx-alignment: CENTER; -fx-font-size: 12px;");
        instructionSpecialeCol.setVisible(false);

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
        actionsCol.setMinWidth(250);
        actionsCol.setStyle("-fx-alignment: CENTER;");
        actionsCol.setCellFactory(column -> {
            return new TableCell<>() {
                final Button viewButton = new Button("Voir");
                final Button editButton = new Button("Modifier");
                final Button deleteButton = new Button("Supprimer");
                final Button pdfButton = new Button("PDF");
                final HBox container = new HBox(5);

                {
                    // Style des boutons
                    viewButton.setStyle("-fx-background-color: #2196F3; -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 3 5; -fx-background-radius: 3; -fx-font-size: 10px;");
                    editButton.setStyle("-fx-background-color: #FF9800; -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 3 5; -fx-background-radius: 3; -fx-font-size: 10px;");
                    deleteButton.setStyle("-fx-background-color: #F44336; -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 3 5; -fx-background-radius: 3; -fx-font-size: 10px;");
                    pdfButton.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 3 5; -fx-background-radius: 3; -fx-font-size: 10px;");

                    // Effet de survol
                    viewButton.setOnMouseEntered(e -> viewButton.setStyle("-fx-background-color: #1976D2; -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 5 10; -fx-background-radius: 4;"));
                    viewButton.setOnMouseExited(e -> viewButton.setStyle("-fx-background-color: #2196F3; -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 5 10; -fx-background-radius: 4;"));

                    editButton.setOnMouseEntered(e -> editButton.setStyle("-fx-background-color: #F57C00; -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 5 10; -fx-background-radius: 4;"));
                    editButton.setOnMouseExited(e -> editButton.setStyle("-fx-background-color: #FF9800; -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 5 10; -fx-background-radius: 4;"));

                    deleteButton.setOnMouseEntered(e -> deleteButton.setStyle("-fx-background-color: #D32F2F; -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 5 10; -fx-background-radius: 4;"));
                    deleteButton.setOnMouseExited(e -> deleteButton.setStyle("-fx-background-color: #F44336; -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 5 10; -fx-background-radius: 4;"));

                    pdfButton.setOnMouseEntered(e -> pdfButton.setStyle("-fx-background-color: #388E3C; -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 5 10; -fx-background-radius: 4;"));
                    pdfButton.setOnMouseExited(e -> pdfButton.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 5 10; -fx-background-radius: 4;"));

                    // Centrer les boutons
                    container.setAlignment(Pos.CENTER);
                    container.getChildren().addAll(viewButton, editButton, deleteButton, pdfButton);

                    // Actions des boutons
                    viewButton.setOnAction(event -> {
                        Commande commande = getTableView().getItems().get(getIndex());
                        showCommandeDetails(commande);
                    });

                    editButton.setOnAction(event -> {
                        Commande commande = getTableView().getItems().get(getIndex());
                        editCommande(commande);
                    });

                    deleteButton.setOnAction(event -> {
                        Commande commande = getTableView().getItems().get(getIndex());
                        deleteCommande(commande);
                    });

                    pdfButton.setOnAction(event -> {
                        Commande commande = getTableView().getItems().get(getIndex());
                        downloadCommandePDF(commande);
                    });
                }

                @Override
                protected void updateItem(Void item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty) {
                        setGraphic(null);
                    } else {
                        Commande commande = getTableView().getItems().get(getIndex());
                        // Si la commande est déjà annulée, désactiver le bouton Supprimer
                        if (commande.getEtatCommande().equalsIgnoreCase("Annulée")) {
                            deleteButton.setDisable(true);
                            deleteButton.setStyle("-fx-background-color: #9E9E9E; -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 3 5; -fx-background-radius: 3; -fx-font-size: 10px;");
                        } else {
                            deleteButton.setDisable(false);
                            deleteButton.setStyle("-fx-background-color: #F44336; -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 3 5; -fx-background-radius: 3; -fx-font-size: 10px;");
                        }

                        // Désactiver le bouton PDF si l'état n'est pas "En Livraison"
                        if (!commande.getEtatCommande().equalsIgnoreCase("En Livraison")) {
                            pdfButton.setDisable(true);
                            pdfButton.setStyle("-fx-background-color: #9E9E9E; -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 3 5; -fx-background-radius: 3; -fx-font-size: 10px;");
                        } else {
                            pdfButton.setDisable(false);
                            pdfButton.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 3 5; -fx-background-radius: 3; -fx-font-size: 10px;");
                        }

                        setGraphic(container);
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
        Label titleLabel = new Label("Détails de la commande #" + commande.getId());
        titleLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-underline: true;");

        // Informations du client
        VBox clientInfoBox = new VBox(5);
        clientInfoBox.setStyle("-fx-background-color: #f5f5f5; -fx-padding: 15; -fx-background-radius: 5;");

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
        orderInfoBox.setStyle("-fx-background-color: #f5f5f5; -fx-padding: 15; -fx-background-radius: 5;");

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

        // Instructions spéciales
        VBox instructionsBox = new VBox(5);
        instructionsBox.setStyle("-fx-background-color: #f5f5f5; -fx-padding: 15; -fx-background-radius: 5;");

        Label instructionsLabel = new Label("Instructions spéciales");
        instructionsLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");

        Label instructionsTextLabel = new Label(commande.getInstructionSpeciale() != null && !commande.getInstructionSpeciale().isEmpty()
                                              ? commande.getInstructionSpeciale()
                                              : "Aucune instruction spéciale");
        instructionsTextLabel.setStyle("-fx-font-size: 14px;");
        instructionsTextLabel.setWrapText(true);
        instructionsTextLabel.setMaxWidth(400);

        instructionsBox.getChildren().addAll(instructionsLabel, instructionsTextLabel);

        // Liste des produits commandés
        VBox productsBox = new VBox(10);
        productsBox.setStyle("-fx-background-color: #f5f5f5; -fx-padding: 15; -fx-background-radius: 5;");

        Label productsLabel = new Label("Produits commandés");
        productsLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");
        productsBox.getChildren().add(productsLabel);

        if (commande.getProduits() != null && !commande.getProduits().isEmpty()) {
            // Créer la table des produits
            TableView<Produit> productsTable = new TableView<>();
            productsTable.setStyle("-fx-background-color: white; -fx-border-color: #e0e0e0; -fx-border-radius: 3;");
            productsTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
            productsTable.setMaxHeight(200);

            // Colonnes de la table
            TableColumn<Produit, String> nameCol = new TableColumn<>("Nom");
            nameCol.setCellValueFactory(new PropertyValueFactory<>("nom_produit"));
            nameCol.setStyle("-fx-alignment: CENTER-LEFT;");
            nameCol.setMinWidth(150);

            TableColumn<Produit, Integer> quantityCol = new TableColumn<>("Quantité");
            quantityCol.setCellValueFactory(new PropertyValueFactory<>("quantity"));
            quantityCol.setStyle("-fx-alignment: CENTER;");
            quantityCol.setMinWidth(70);

            TableColumn<Produit, Double> priceUnitCol = new TableColumn<>("Prix unitaire");
            priceUnitCol.setCellValueFactory(new PropertyValueFactory<>("prix"));
            priceUnitCol.setStyle("-fx-alignment: CENTER;");
            priceUnitCol.setMinWidth(80);
            priceUnitCol.setCellFactory(column -> new TableCell<Produit, Double>() {
                @Override
                protected void updateItem(Double price, boolean empty) {
                    super.updateItem(price, empty);
                    if (empty || price == null) {
                        setText(null);
                    } else {
                        setText(String.format("%.2f €", price));
                    }
                }
            });

            TableColumn<Produit, Double> priceTotalCol = new TableColumn<>("Prix total");
            priceTotalCol.setCellValueFactory(cellData -> {
                Produit produit = cellData.getValue();
                double total = produit.getPrix() * produit.getQuantity();
                return new SimpleDoubleProperty(total).asObject();
            });
            priceTotalCol.setStyle("-fx-alignment: CENTER;");
            priceTotalCol.setMinWidth(80);
            priceTotalCol.setCellFactory(column -> new TableCell<Produit, Double>() {
                @Override
                protected void updateItem(Double total, boolean empty) {
                    super.updateItem(total, empty);
                    if (empty || total == null) {
                        setText(null);
                    } else {
                        setText(String.format("%.2f €", total));
                    }
                }
            });

            // Ajouter les colonnes à la table
            productsTable.getColumns().addAll(nameCol, quantityCol, priceUnitCol, priceTotalCol);

            // Ajouter les produits à la table
            ObservableList<Produit> productsList = FXCollections.observableArrayList(commande.getProduits());
            productsTable.setItems(productsList);

            productsBox.getChildren().add(productsTable);

            // Ajouter un résumé du nombre de produits et du total
            Label summaryLabel = new Label(String.format("Total: %d produits pour %.2f €",
                commande.getProduits().size(), commande.getPrixtotal()));
            summaryLabel.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: #4CAF50;");
            summaryLabel.setPadding(new Insets(10, 0, 0, 0));
            productsBox.getChildren().add(summaryLabel);
        } else {
            Label noProductsLabel = new Label("Aucun produit dans cette commande");
            noProductsLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: #757575;");
            productsBox.getChildren().add(noProductsLabel);
        }

        // Boutons d'action
        HBox buttonsBox = new HBox(15);
        buttonsBox.setAlignment(Pos.CENTER);

        Button editButton = new Button("Modifier");
        editButton.setStyle("-fx-background-color: #FF9800; -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 8 20; -fx-background-radius: 5;");
        editButton.setOnAction(e -> {
            detailsStage.close();
            editCommande(commande);
        });

        Button closeButton = new Button("Fermer");
        closeButton.setStyle("-fx-background-color: #9E9E9E; -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 8 20; -fx-background-radius: 5;");
        closeButton.setOnAction(e -> detailsStage.close());

        buttonsBox.getChildren().addAll(editButton, closeButton);

        // Ajouter tous les éléments au layout
        detailsLayout.getChildren().addAll(
            titleLabel,
            clientInfoBox,
            orderInfoBox,
            productsBox,
            instructionsBox,
            buttonsBox
        );

        // Créer la scène et l'ajouter à la fenêtre
        Scene scene = new Scene(detailsLayout, 500, 700);
        detailsStage.setScene(scene);
        detailsStage.initModality(Modality.APPLICATION_MODAL);
        detailsStage.show();
    }

    private void editCommande(Commande commande) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/FXML/monta/EditCommande.fxml"));
            Parent root = loader.load();
            EditCommande controller = loader.getController();
            controller.setCommandeToEdit(commande);
            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
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

    @FXML
    void navigateToAddCommande() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/FXML/monta/AjouterCommande.fxml"));
            Parent root = loader.load();
            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Télécharge une commande au format PDF
     * @param commande La commande à télécharger
     */
    private void downloadCommandePDF(Commande commande) {
        // Vérifier que la commande est en état "En Livraison"
        if (!commande.getEtatCommande().equalsIgnoreCase("En Livraison")) {
            // Afficher un message d'erreur
            Stage errorStage = new Stage();
            errorStage.initModality(Modality.APPLICATION_MODAL);
            VBox errorBox = new VBox(10);
            errorBox.setAlignment(Pos.CENTER);
            errorBox.setPadding(new Insets(20));

            Label errorLabel = new Label("Impossible de générer le PDF. La commande doit être en état \"En Livraison\".");
            errorLabel.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: #F44336;");
            errorLabel.setWrapText(true);

            Button okButton = new Button("OK");
            okButton.setStyle("-fx-background-color: #F44336; -fx-text-fill: white; -fx-font-weight: bold;");
            okButton.setOnAction(event -> errorStage.close());

            errorBox.getChildren().addAll(errorLabel, okButton);
            Scene errorScene = new Scene(errorBox, 400, 150);
            errorStage.setScene(errorScene);
            errorStage.setTitle("Action non autorisée");
            errorStage.show();
            return;
        }

        try {
            // Afficher un popup de chargement
            Stage loadingStage = new Stage();
            loadingStage.initModality(Modality.APPLICATION_MODAL);
            VBox loadingBox = new VBox(10);
            loadingBox.setAlignment(Pos.CENTER);
            loadingBox.setPadding(new Insets(20));
            Label loadingLabel = new Label("Génération du PDF en cours...");
            loadingLabel.setStyle("-fx-font-size: 14px;");
            loadingBox.getChildren().add(loadingLabel);
            Scene loadingScene = new Scene(loadingBox, 300, 100);
            loadingStage.setScene(loadingScene);
            loadingStage.setTitle("Génération PDF");
            loadingStage.show();

            // Générer le PDF dans un thread séparé pour ne pas bloquer l'interface
            new Thread(() -> {
                String pdfPath = commandeService.generateOrderPDF(commande.getId());

                // Mettre à jour l'interface sur le thread JavaFX
                javafx.application.Platform.runLater(() -> {
                    loadingStage.close();

                    if (pdfPath != null) {
                        // Afficher un message de succès avec le chemin du fichier
                        Stage successStage = new Stage();
                        successStage.initModality(Modality.APPLICATION_MODAL);
                        VBox successBox = new VBox(10);
                        successBox.setAlignment(Pos.CENTER);
                        successBox.setPadding(new Insets(20));

                        Label successLabel = new Label("PDF généré avec succès !");
                        successLabel.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: #4CAF50;");

                        Label pathLabel = new Label("Enregistré dans:\n" + pdfPath);
                        pathLabel.setStyle("-fx-font-size: 12px;");

                        Button okButton = new Button("OK");
                        okButton.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white; -fx-font-weight: bold;");
                        okButton.setOnAction(e -> successStage.close());

                        successBox.getChildren().addAll(successLabel, pathLabel, okButton);
                        Scene successScene = new Scene(successBox, 400, 150);
                        successStage.setScene(successScene);
                        successStage.setTitle("Succès");
                        successStage.show();
                    } else {
                        // Afficher un message d'erreur
                        Stage errorStage = new Stage();
                        errorStage.initModality(Modality.APPLICATION_MODAL);
                        VBox errorBox = new VBox(10);
                        errorBox.setAlignment(Pos.CENTER);
                        errorBox.setPadding(new Insets(20));

                        Label errorLabel = new Label("Erreur lors de la génération du PDF");
                        errorLabel.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: #F44336;");

                        Button okButton = new Button("OK");
                        okButton.setStyle("-fx-background-color: #F44336; -fx-text-fill: white; -fx-font-weight: bold;");
                        okButton.setOnAction(e -> errorStage.close());

                        errorBox.getChildren().addAll(errorLabel, okButton);
                        Scene errorScene = new Scene(errorBox, 300, 120);
                        errorStage.setScene(errorScene);
                        errorStage.setTitle("Erreur");
                        errorStage.show();
                    }
                });
            }).start();

        } catch (Exception e) {
            e.printStackTrace();
            // Afficher un message d'erreur
            Stage errorStage = new Stage();
            errorStage.initModality(Modality.APPLICATION_MODAL);
            VBox errorBox = new VBox(10);
            errorBox.setAlignment(Pos.CENTER);
            errorBox.setPadding(new Insets(20));

            Label errorLabel = new Label("Erreur lors de la génération du PDF: " + e.getMessage());
            errorLabel.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: #F44336;");

            Button okButton = new Button("OK");
            okButton.setStyle("-fx-background-color: #F44336; -fx-text-fill: white; -fx-font-weight: bold;");
            okButton.setOnAction(event -> errorStage.close());

            errorBox.getChildren().addAll(errorLabel, okButton);
            Scene errorScene = new Scene(errorBox, 400, 150);
            errorStage.setScene(errorScene);
            errorStage.setTitle("Erreur");
            errorStage.show();
        }
    }
}
