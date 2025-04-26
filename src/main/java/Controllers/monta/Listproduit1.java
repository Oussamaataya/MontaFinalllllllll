package Controllers.monta;

import Entities.Cart;
import Entities.Produit;
import Services.ProduitService;
import Utils.ImageManager;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TableRow;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.geometry.Pos;
import javafx.scene.layout.HBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.geometry.Insets;

import java.io.File;
import java.text.SimpleDateFormat;

import java.util.Date;
import java.util.List;

public class Listproduit1 {

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

    private final ProduitService ps = new ProduitService();
    private final Cart cart = Cart.getInstance();

    @FXML
    void initialize() {
        List<Produit> produits = ps.getAll();
        ObservableList<Produit> observableList = FXCollections.observableList(produits);
        tableview.setItems(observableList);

        // Style général de la table
        tableview.setStyle("-fx-background-color: white; -fx-border-color: #e0e0e0; -fx-border-radius: 5; -fx-padding: 10;");
        tableview.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        // Style des en-têtes de colonnes
        tableview.getStyleClass().add("table-view");
        for (TableColumn<?, ?> column : tableview.getColumns()) {
            column.getStyleClass().add("table-column");
        }

        // Configuration des colonnes
        nomRcol.setCellValueFactory(new PropertyValueFactory<>("nom_produit"));
        nomRcol.setStyle("-fx-alignment: CENTER; -fx-font-weight: bold; -fx-font-size: 14px;");
        nomRcol.setMinWidth(150);

        descriptionCol.setCellValueFactory(new PropertyValueFactory<>("description"));
        descriptionCol.setStyle("-fx-alignment: CENTER;");
        descriptionCol.setMinWidth(200);

        // Configuration de la colonne d'image
        imageCol.setCellValueFactory(new PropertyValueFactory<>("image"));
        imageCol.setMinWidth(150);
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
                        javafx.scene.image.ImageView imageView = new javafx.scene.image.ImageView();

                        // Utiliser ImageManager pour charger l'image
                        javafx.scene.image.Image image = ImageManager.loadImage(imagePath);

                        imageView.setImage(image);
                        // Augmenter la taille de l'image
                        imageView.setFitHeight(120);
                        imageView.setFitWidth(120);
                        imageView.setPreserveRatio(true);
                        imageView.setStyle("-fx-cursor: hand; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.2), 10, 0, 0, 0);");

                        // Créer des copies finales pour l'utilisation dans le lambda
                        final javafx.scene.image.Image finalImage = image;
                        final String finalImagePath = imagePath;

                        // Ajouter une action au clic pour agrandir l'image
                        imageView.setOnMouseClicked(event -> {
                            showLargeImage(finalImage, finalImagePath);
                        });

                        // Centrer l'image dans la cellule
                        setAlignment(Pos.CENTER);
                        setGraphic(imageView);
                        setText(null);
                    } catch (Exception e) {
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

            // Méthode pour afficher l'image en grand format
            private void showLargeImage(javafx.scene.image.Image image, String imagePath) {
                // Créer une nouvelle fenêtre
                javafx.stage.Stage imageStage = new javafx.stage.Stage();
                imageStage.setTitle("Image du produit");

                // Créer une ImageView pour l'image agrandie
                javafx.scene.image.ImageView largeImageView = new javafx.scene.image.ImageView(image);

                // Permettre le redimensionnement tout en maintenant le ratio
                largeImageView.setPreserveRatio(true);

                // Limiter la taille maximale de l'image à 80% de la résolution de l'écran
                javafx.geometry.Rectangle2D screenBounds = javafx.stage.Screen.getPrimary().getVisualBounds();
                double maxWidth = screenBounds.getWidth() * 0.8;
                double maxHeight = screenBounds.getHeight() * 0.8;

                largeImageView.setFitWidth(Math.min(image.getWidth(), maxWidth));
                largeImageView.setFitHeight(Math.min(image.getHeight(), maxHeight));

                // Créer un bouton pour fermer la fenêtre
                javafx.scene.control.Button closeButton = new javafx.scene.control.Button("Fermer");
                closeButton.setOnAction(e -> imageStage.close());

                // Créer un layout pour contenir l'image et le bouton
                javafx.scene.layout.VBox layout = new javafx.scene.layout.VBox(10);
                layout.setAlignment(javafx.geometry.Pos.CENTER);
                layout.setPadding(new javafx.geometry.Insets(10));
                layout.getChildren().addAll(largeImageView, closeButton);

                // Créer la scène et l'ajouter à la fenêtre
                javafx.scene.Scene scene = new javafx.scene.Scene(layout);
                imageStage.setScene(scene);

                // Configurer la fenêtre comme modale pour forcer l'utilisateur à fermer celle-ci avant de retourner à l'application principale
                imageStage.initModality(javafx.stage.Modality.APPLICATION_MODAL);

                // Afficher la fenêtre
                imageStage.show();
            }
        });

        idCol.setCellValueFactory(new PropertyValueFactory<>("id"));
        idCol.setStyle("-fx-alignment: CENTER;");
        idCol.setMinWidth(50);

        quantityCol.setCellValueFactory(new PropertyValueFactory<>("quantity"));
        quantityCol.setStyle("-fx-alignment: CENTER;");
        quantityCol.setMinWidth(80);

        prixCol.setCellValueFactory(new PropertyValueFactory<>("prix"));
        prixCol.setStyle("-fx-alignment: CENTER; -fx-font-weight: bold; -fx-text-fill: #4CAF50; -fx-font-size: 14px;");
        prixCol.setMinWidth(100);
        prixCol.setCellFactory(col -> new TableCell<Produit, Double>() {
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
        typeCol.setStyle("-fx-alignment: CENTER;");
        typeCol.setMinWidth(100);

        dateFabricationCol.setCellValueFactory(new PropertyValueFactory<>("date_fabrication"));
        dateFabricationCol.setStyle("-fx-alignment: CENTER;");
        dateFabricationCol.setMinWidth(120);

        dateExpirationCol.setCellValueFactory(new PropertyValueFactory<>("date_expiration"));
        dateExpirationCol.setStyle("-fx-alignment: CENTER;");
        dateExpirationCol.setMinWidth(120);

        // Configuration de la colonne des actions
        actionsCol.setMinWidth(200);
        actionsCol.setCellFactory(column -> new TableCell<>() {
            private final HBox buttonBox = new HBox(10);
            private final Button detailsButton = new Button("Détails");
            private final Button addButton = new Button("Ajouter au panier");

            {
                // Style des boutons
                detailsButton.setStyle("-fx-background-color: #2196F3; -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 8 15; -fx-background-radius: 5; -fx-font-size: 12px;");
                addButton.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 8 15; -fx-background-radius: 5; -fx-font-size: 12px;");

                // Effet de survol pour les boutons
                detailsButton.setOnMouseEntered(e -> detailsButton.setStyle("-fx-background-color: #1976D2; -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 8 15; -fx-background-radius: 5; -fx-font-size: 12px;"));
                detailsButton.setOnMouseExited(e -> detailsButton.setStyle("-fx-background-color: #2196F3; -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 8 15; -fx-background-radius: 5; -fx-font-size: 12px;"));

                addButton.setOnMouseEntered(e -> addButton.setStyle("-fx-background-color: #388E3C; -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 8 15; -fx-background-radius: 5; -fx-font-size: 12px;"));
                addButton.setOnMouseExited(e -> addButton.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 8 15; -fx-background-radius: 5; -fx-font-size: 12px;"));

                // Centrer les boutons
                buttonBox.setAlignment(Pos.CENTER);
                buttonBox.getChildren().addAll(detailsButton, addButton);

                // Action pour le bouton Détails
                detailsButton.setOnAction(event -> {
                    Produit produit = getTableView().getItems().get(getIndex());
                    showProductDetails(produit);
                });

                // Action pour le bouton Ajouter au panier
                addButton.setOnAction(event -> {
                    Produit produit = getTableView().getItems().get(getIndex());
                    addToCart(produit);
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    setGraphic(buttonBox);
                }
            }
        });

        // Cacher les colonnes non nécessaires
        idCol.setVisible(false);
        descriptionCol.setVisible(false);
        quantityCol.setVisible(false);
        typeCol.setVisible(false);
        dateFabricationCol.setVisible(false);
        dateExpirationCol.setVisible(false);

        // Style des lignes de la table
        tableview.setRowFactory(tv -> {
            TableRow<Produit> row = new TableRow<>();
            row.setStyle("-fx-background-color: white;");
            row.setOnMouseEntered(event -> row.setStyle("-fx-background-color: #f5f5f5;"));
            row.setOnMouseExited(event -> row.setStyle("-fx-background-color: white;"));
            return row;
        });
    }

    private void addToCart(Produit produit) {
        cart.addProduit(produit);
        System.out.println("Produit ajouté au panier: " + produit.getNom_produit());
    }

    private void showProductDetails(Produit produit) {
        // Créer une nouvelle fenêtre pour afficher les détails
        Stage detailsStage = new Stage();
        detailsStage.setTitle("Détails du produit");

        // Créer un layout pour les détails
        VBox detailsLayout = new VBox(20);
        detailsLayout.setPadding(new Insets(20));
        detailsLayout.setStyle("-fx-background-color: white;");

        // Image du produit
        ImageView productImage = new ImageView();
        try {
            String imagePath = produit.getImage();

            // Utiliser ImageManager pour charger l'image
            Image image = ImageManager.loadImage(imagePath);

            productImage.setImage(image);
            productImage.setFitHeight(200);
            productImage.setFitWidth(200);
            productImage.setPreserveRatio(true);
            productImage.setStyle("-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.2), 10, 0, 0, 0);");
        } catch (Exception e) {
            Image defaultImage =ImageManager.loadDefaultImage();
            if (defaultImage != null) {
                productImage.setImage(defaultImage);
            } else {
                // Si même l'image par défaut ne peut pas être chargée, afficher un message d'erreur
                productImage.setVisible(false);
            }
        }

        // Informations détaillées
        Label nameLabel = new Label("Nom: " + produit.getNom_produit());
        nameLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");

        Label descriptionLabel = new Label("Description: " + produit.getDescription());
        descriptionLabel.setWrapText(true);
        descriptionLabel.setMaxWidth(400);

        Label priceLabel = new Label("Prix: " + String.format("%.2f €", produit.getPrix()));
        priceLabel.setStyle("-fx-font-size: 16px; -fx-text-fill: #4CAF50; -fx-font-weight: bold;");

        Label quantityLabel = new Label("Quantité disponible: " + produit.getQuantity());
        Label typeLabel = new Label("Type: " + produit.getType_produit());

        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
        Label fabricationLabel = new Label("Date de fabrication: " + dateFormat.format(produit.getDate_fabrication()));
        Label expirationLabel = new Label("Date d'expiration: " + dateFormat.format(produit.getDate_expiration()));

        // Bouton pour fermer la fenêtre
        Button closeButton = new Button("Fermer");
        closeButton.setStyle("-fx-background-color: #f44336; -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 8 15; -fx-background-radius: 5;");
        closeButton.setOnAction(e -> detailsStage.close());

        // Ajouter tous les éléments au layout
        detailsLayout.getChildren().addAll(
            productImage,
            nameLabel,
            descriptionLabel,
            priceLabel,
            quantityLabel,
            typeLabel,
            fabricationLabel,
            expirationLabel,
            closeButton
        );

        // Créer la scène et l'ajouter à la fenêtre
        Scene scene = new Scene(detailsLayout);
        detailsStage.setScene(scene);
        detailsStage.initModality(Modality.APPLICATION_MODAL);
        detailsStage.show();
    }
}
