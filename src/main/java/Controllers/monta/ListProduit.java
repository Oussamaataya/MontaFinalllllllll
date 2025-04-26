package Controllers.monta;


import Entities.Produit;
import Services.ProduitService;
import Utils.ImageManager;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class ListProduit {
    @FXML
    private TableColumn<Produit, String> nomRcol;

    @FXML
    private TableColumn<Produit, String> descriptionCol;

    @FXML
    private TableColumn<Produit, String> imageCol;

    @FXML
    private TableColumn<Produit, Integer> idCol;

    @FXML
    private TableColumn<Produit, Integer> quantityCol;

    @FXML
    private TableColumn<Produit, Double> prixCol;

    @FXML
    private TableColumn<Produit, String> typeCol;

    @FXML
    private TableColumn<Produit, Date> dateFabricationCol;

    @FXML
    private TableColumn<Produit, Date> dateExpirationCol;

    @FXML
    private TableColumn<Produit, Void> actionsCol;

    @FXML
    private TableView<Produit> tableview;

    @FXML
    private Button addMealButton; // Correspond à l'ID dans le FXML

    private final ProduitService ps = new ProduitService();

    @FXML
    void initialize() {
        List<Produit> produits = ps.getAll();
        ObservableList<Produit> observableList = FXCollections.observableList(produits);
        tableview.setItems(observableList);

        // Style général de la table avec meilleur affichage
        tableview.setStyle("-fx-background-color: white; -fx-border-color: #e0e0e0; -fx-border-radius: 5;");
        tableview.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        // Configuration des colonnes avec styles améliorés
        nomRcol.setCellValueFactory(new PropertyValueFactory<>("nom_produit"));
        nomRcol.setStyle("-fx-alignment: CENTER-LEFT; -fx-font-weight: bold; -fx-font-size: 13px;");
        nomRcol.setMinWidth(150);

        descriptionCol.setCellValueFactory(new PropertyValueFactory<>("description"));
        descriptionCol.setStyle("-fx-alignment: CENTER-LEFT; -fx-font-size: 12px;");
        descriptionCol.setMinWidth(200);
        descriptionCol.setVisible(true); // Montrer la description

        imageCol.setCellValueFactory(new PropertyValueFactory<>("image"));
        imageCol.setStyle("-fx-alignment: CENTER;");
        imageCol.setMinWidth(150);
        imageCol.setPrefWidth(250);
        imageCol.setCellFactory(column -> new TableCell<Produit, String>() {
            @Override
            protected void updateItem(String imagePath, boolean empty) {
                super.updateItem(imagePath, empty);

                if (empty || imagePath == null || imagePath.isEmpty()) {
                    setText(null);
                    setGraphic(null);
                } else {
                    try {
                        // Créer une ImageView pour afficher l'image
                        ImageView imageView = new ImageView();

                        // Utiliser ImageManager pour charger l'image
                        Image image = ImageManager.loadImage(imagePath);

                        imageView.setImage(image);
                        imageView.setFitHeight(80); // Hauteur de l'image
                        imageView.setFitWidth(80);  // Largeur de l'image
                        imageView.setPreserveRatio(true);
                        imageView.setStyle("-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.2), 3, 0, 0, 0);");

                        // Ajouter un padding pour mieux voir l'image
                        setStyle("-fx-padding: 5;");

                        // Centrer l'image dans la cellule
                        setAlignment(Pos.CENTER);
                        setGraphic(imageView);
                        setText(null);
                    } catch (Exception e) {
                        System.out.println("Erreur lors du chargement de l'image: " + e.getMessage() + " - Chemin: " + imagePath);

                        // Créer un label avec un style visible
                        Label errorLabel = new Label("Image\nnon disponible");
                        errorLabel.setStyle("-fx-text-fill: red; -fx-font-weight: bold; -fx-alignment: center; -fx-text-alignment: center;");
                        errorLabel.setWrapText(true);
                        errorLabel.setMaxWidth(100);

                        // Créer un conteneur avec une bordure rouge
                        VBox errorBox = new VBox(errorLabel);
                        errorBox.setStyle("-fx-border-color: red; -fx-border-width: 1; -fx-padding: 10; -fx-background-color: #ffeeee;");
                        errorBox.setAlignment(Pos.CENTER);
                        errorBox.setPrefHeight(80);
                        errorBox.setPrefWidth(80);

                        setGraphic(errorBox);
                        setText(null);
                    }
                }
            }
        });

        idCol.setCellValueFactory(new PropertyValueFactory<>("id"));
        idCol.setStyle("-fx-alignment: CENTER;");
        idCol.setVisible(false); // Cacher la colonne ID

        quantityCol.setCellValueFactory(new PropertyValueFactory<>("quantity"));
        quantityCol.setStyle("-fx-alignment: CENTER; -fx-font-size: 12px;");
        quantityCol.setMinWidth(80);
        quantityCol.setVisible(true); // Afficher la quantité

        prixCol.setCellValueFactory(new PropertyValueFactory<>("prix"));
        prixCol.setStyle("-fx-alignment: CENTER; -fx-font-weight: bold; -fx-text-fill: #4CAF50; -fx-font-size: 13px;");
        prixCol.setMinWidth(80);
        prixCol.setCellFactory(column -> new TableCell<Produit, Double>() {
            @Override
            protected void updateItem(Double prix, boolean empty) {
                super.updateItem(prix, empty);
                if (empty || prix == null) {
                    setText(null);
                } else {
                    setText(String.format("%.2f €", prix));
                }
            }
        });

        typeCol.setCellValueFactory(new PropertyValueFactory<>("type_produit"));
        typeCol.setStyle("-fx-alignment: CENTER; -fx-font-size: 12px;");
        typeCol.setMinWidth(100);
        typeCol.setVisible(true); // Afficher le type

        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");

        dateFabricationCol.setCellValueFactory(new PropertyValueFactory<>("date_fabrication"));
        dateFabricationCol.setStyle("-fx-alignment: CENTER; -fx-font-size: 12px;");
        dateFabricationCol.setVisible(false); // Cacher la date de fabrication
        dateFabricationCol.setCellFactory(column -> new TableCell<Produit, Date>() {
            @Override
            protected void updateItem(Date date, boolean empty) {
                super.updateItem(date, empty);
                if (empty || date == null) {
                    setText(null);
                } else {
                    setText(dateFormat.format(date));
                }
            }
        });

        dateExpirationCol.setCellValueFactory(new PropertyValueFactory<>("date_expiration"));
        dateExpirationCol.setStyle("-fx-alignment: CENTER; -fx-font-size: 12px;");
        dateExpirationCol.setVisible(false); // Cacher la date d'expiration
        dateExpirationCol.setCellFactory(column -> new TableCell<Produit, Date>() {
            @Override
            protected void updateItem(Date date, boolean empty) {
                super.updateItem(date, empty);
                if (empty || date == null) {
                    setText(null);
                } else {
                    setText(dateFormat.format(date));
                }
            }
        });

        actionsCol.setMinWidth(180); // Réduire légèrement la largeur
        actionsCol.setStyle("-fx-alignment: CENTER;");
        actionsCol.setCellFactory(column -> {
            return new TableCell<>() {
                final Button viewButton = new Button("Voir");
                final Button editButton = new Button("Modifier");
                final Button deleteButton = new Button("Supprimer");
                final HBox container = new HBox(5); // Réduire l'espacement

                {
                    // Style des boutons plus compact
                    viewButton.setStyle("-fx-background-color: #2196F3; -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 3 5; -fx-background-radius: 3; -fx-font-size: 10px;");
                    editButton.setStyle("-fx-background-color: #FF9800; -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 3 5; -fx-background-radius: 3; -fx-font-size: 10px;");
                    deleteButton.setStyle("-fx-background-color: #F44336; -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 3 5; -fx-background-radius: 3; -fx-font-size: 10px;");

                    // Effet de survol
                    viewButton.setOnMouseEntered(e -> viewButton.setStyle("-fx-background-color: #1976D2; -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 3 5; -fx-background-radius: 3; -fx-font-size: 10px;"));
                    viewButton.setOnMouseExited(e -> viewButton.setStyle("-fx-background-color: #2196F3; -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 3 5; -fx-background-radius: 3; -fx-font-size: 10px;"));

                    editButton.setOnMouseEntered(e -> editButton.setStyle("-fx-background-color: #F57C00; -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 3 5; -fx-background-radius: 3; -fx-font-size: 10px;"));
                    editButton.setOnMouseExited(e -> editButton.setStyle("-fx-background-color: #FF9800; -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 3 5; -fx-background-radius: 3; -fx-font-size: 10px;"));

                    deleteButton.setOnMouseEntered(e -> deleteButton.setStyle("-fx-background-color: #D32F2F; -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 3 5; -fx-background-radius: 3; -fx-font-size: 10px;"));
                    deleteButton.setOnMouseExited(e -> deleteButton.setStyle("-fx-background-color: #F44336; -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 3 5; -fx-background-radius: 3; -fx-font-size: 10px;"));

                    // Centrer les boutons
                    container.setAlignment(Pos.CENTER);
                    container.getChildren().addAll(viewButton, editButton, deleteButton);

                    // Actions des boutons
                    viewButton.setOnAction(event -> {
                        Produit produit = getTableView().getItems().get(getIndex());
                        showProductDetails(produit);
                    });

                    editButton.setOnAction(event -> {
                        Produit produit = getTableView().getItems().get(getIndex());
                        editProduit(produit);
                    });

                    deleteButton.setOnAction(event -> {
                        Produit produit = getTableView().getItems().get(getIndex());
                        deleteProduit(produit);
                    });
                }

                @Override
                protected void updateItem(Void item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty) {
                        setGraphic(null);
                    } else {
                        setGraphic(container);
                    }
                }
            };
        });

        // Style des lignes avec alternance de couleurs
        tableview.setRowFactory(tv -> {
            TableRow<Produit> row = new TableRow<Produit>() {
                @Override
                protected void updateItem(Produit produit, boolean empty) {
                    super.updateItem(produit, empty);
                    if (empty || produit == null) {
                        setStyle("-fx-background-color: white;");
                    } else {
                        if (getIndex() % 2 == 0) {
                            setStyle("-fx-background-color: white; -fx-border-color: #f0f0f0; -fx-border-width: 0 0 1 0;");
                        } else {
                            setStyle("-fx-background-color: #f8f8f8; -fx-border-color: #f0f0f0; -fx-border-width: 0 0 1 0;");
                        }
                    }
                }
            };

            // Effet de survol
            row.setOnMouseEntered(event -> {
                if (!row.isEmpty()) {
                    row.setStyle("-fx-background-color: #e3f2fd; -fx-border-color: #f0f0f0; -fx-border-width: 0 0 1 0;");
                }
            });

            // Retour au style normal lors de la sortie
            row.setOnMouseExited(event -> {
                if (!row.isEmpty() && !row.isSelected()) {
                    Produit produit = row.getItem();
                    if (row.getIndex() % 2 == 0) {
                        row.setStyle("-fx-background-color: white; -fx-border-color: #f0f0f0; -fx-border-width: 0 0 1 0;");
                    } else {
                        row.setStyle("-fx-background-color: #f8f8f8; -fx-border-color: #f0f0f0; -fx-border-width: 0 0 1 0;");
                    }
                }
            });

            return row;
        });

        // Améliorer le style du bouton d'ajout (sans changer sa position)
        if (addMealButton != null) {
            addMealButton.setText("Ajouter Produit");
            addMealButton.setStyle("-fx-background-color: #9dc023; -fx-text-fill: white; -fx-font-weight: bold; " +
                    "-fx-padding: 8 15; -fx-background-radius: 5; -fx-font-size: 13px;");

            // Ajouter effet de survol
            addMealButton.setOnMouseEntered(e -> addMealButton.setStyle("-fx-background-color: #388E3C; -fx-text-fill: white; " +
                    "-fx-font-weight: bold; -fx-padding: 8 15; -fx-background-radius: 5; -fx-font-size: 13px;"));
            addMealButton.setOnMouseExited(e -> addMealButton.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white; " +
                    "-fx-font-weight: bold; -fx-padding: 8 15; -fx-background-radius: 5; -fx-font-size: 13px;"));
        }
    }

    private void showProductDetails(Produit produit) {
        // Créer une nouvelle fenêtre pour afficher les détails
        Stage detailsStage = new Stage();
        detailsStage.setTitle("Détails du produit");

        // Créer un layout pour les détails
        VBox detailsLayout = new VBox(15);
        detailsLayout.setPadding(new Insets(20));
        detailsLayout.setStyle("-fx-background-color: white;");
        detailsLayout.setAlignment(Pos.TOP_CENTER);

        // Titre
        Label titleLabel = new Label("Détails du produit");
        titleLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-underline: true;");

        // Image du produit
        ImageView productImage = new ImageView();
        try {
            String imagePath = produit.getImage();

            // Utiliser ImageManager pour charger l'image
            Image image = ImageManager.loadImage(imagePath);

            productImage.setImage(image);
            productImage.setFitHeight(180); // Légèrement plus petit
            productImage.setFitWidth(180);  // Légèrement plus petit
            productImage.setPreserveRatio(true);
            productImage.setStyle("-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.2), 8, 0, 0, 0);");
        } catch (Exception e) {
            System.out.println("Erreur détails produit: " + e.getMessage() + " - Chemin: " + produit.getImage());
            Image defaultImage = ImageManager.loadDefaultImage();
            if (defaultImage != null) {
                productImage.setImage(defaultImage);
            } else {
                // Si même l'image par défaut ne peut pas être chargée, afficher un message d'erreur
                productImage.setVisible(false);
            }
        }

        // Informations du produit dans une grille plus organisée
        Label nameLabel = new Label("Nom: " + produit.getNom_produit());
        nameLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");

        VBox productInfoGrid = new VBox(10);
        productInfoGrid.setStyle("-fx-background-color: #f9f9f9; -fx-padding: 15; -fx-background-radius: 5;");

        HBox priceBox = new HBox(10);
        priceBox.setAlignment(Pos.CENTER_LEFT);
        Label priceTitle = new Label("Prix:");
        priceTitle.setStyle("-fx-font-weight: bold; -fx-min-width: 100;");
        Label priceValue = new Label(String.format("%.2f €", produit.getPrix()));
        priceValue.setStyle("-fx-text-fill: #4CAF50; -fx-font-weight: bold;");
        priceBox.getChildren().addAll(priceTitle, priceValue);

        HBox quantityBox = new HBox(10);
        quantityBox.setAlignment(Pos.CENTER_LEFT);
        Label quantityTitle = new Label("Quantité:");
        quantityTitle.setStyle("-fx-font-weight: bold; -fx-min-width: 100;");
        Label quantityValue = new Label(Integer.toString(produit.getQuantity()));
        quantityBox.getChildren().addAll(quantityTitle, quantityValue);

        HBox typeBox = new HBox(10);
        typeBox.setAlignment(Pos.CENTER_LEFT);
        Label typeTitle = new Label("Type:");
        typeTitle.setStyle("-fx-font-weight: bold; -fx-min-width: 100;");
        Label typeValue = new Label(produit.getType_produit());
        typeBox.getChildren().addAll(typeTitle, typeValue);

        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");

        HBox fabricationBox = new HBox(10);
        fabricationBox.setAlignment(Pos.CENTER_LEFT);
        Label fabricationTitle = new Label("Fabriqué le:");
        fabricationTitle.setStyle("-fx-font-weight: bold; -fx-min-width: 100;");
        Label fabricationValue = new Label(dateFormat.format(produit.getDate_fabrication()));
        fabricationBox.getChildren().addAll(fabricationTitle, fabricationValue);

        HBox expirationBox = new HBox(10);
        expirationBox.setAlignment(Pos.CENTER_LEFT);
        Label expirationTitle = new Label("Expire le:");
        expirationTitle.setStyle("-fx-font-weight: bold; -fx-min-width: 100;");
        Label expirationValue = new Label(dateFormat.format(produit.getDate_expiration()));
        expirationBox.getChildren().addAll(expirationTitle, expirationValue);

        productInfoGrid.getChildren().addAll(priceBox, quantityBox, typeBox, fabricationBox, expirationBox);

        // Description avec style amélioré
        VBox descriptionBox = new VBox(5);
        descriptionBox.setStyle("-fx-background-color: #f9f9f9; -fx-padding: 15; -fx-background-radius: 5;");

        Label descriptionTitle = new Label("Description");
        descriptionTitle.setStyle("-fx-font-weight: bold; -fx-font-size: 14px;");

        Label descriptionValue = new Label(produit.getDescription());
        descriptionValue.setWrapText(true);
        descriptionValue.setMaxWidth(400);
        descriptionValue.setStyle("-fx-font-size: 13px;");

        descriptionBox.getChildren().addAll(descriptionTitle, descriptionValue);

        // Boutons d'action
        HBox buttonsBox = new HBox(15);
        buttonsBox.setAlignment(Pos.CENTER);

        Button editButton = new Button("Modifier");
        editButton.setStyle("-fx-background-color: #FF9800; -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 8 20; -fx-background-radius: 5;");
        editButton.setOnAction(e -> {
            detailsStage.close();
            editProduit(produit);
        });

        Button closeButton = new Button("Fermer");
        closeButton.setStyle("-fx-background-color: #9E9E9E; -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 8 20; -fx-background-radius: 5;");
        closeButton.setOnAction(e -> detailsStage.close());

        buttonsBox.getChildren().addAll(editButton, closeButton);

        // Ajouter tous les éléments au layout
        detailsLayout.getChildren().addAll(
            titleLabel,
            productImage,
            nameLabel,
            productInfoGrid,
            descriptionBox,
            buttonsBox
        );

        // Créer la scène avec une largeur fixe pour éviter les problèmes d'affichage
        Scene scene = new Scene(detailsLayout, 450, 680);
        detailsStage.setScene(scene);
        detailsStage.initModality(Modality.APPLICATION_MODAL);
        detailsStage.show();
    }

    private void editProduit(Produit produit) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/FXML/monta/EditProduit.fxml"));
            Parent root = loader.load();
            EditProduit controller = loader.getController();
            controller.setProduitToEdit(produit);
            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void deleteProduit(Produit produit) {
        Produit produitToDelete = ps.getOne(produit.getId());
        ps.delete(produitToDelete);
        tableview.getItems().remove(produit);
    }

    @FXML
    void naviguertoaddProduit(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/FXML/monta/AjouterProduit.fxml"));
            Parent root = loader.load();
            Scene scene = new Scene(root);
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            System.out.println("Erreur de navigation: " + e.getMessage());
        }
    }
}
