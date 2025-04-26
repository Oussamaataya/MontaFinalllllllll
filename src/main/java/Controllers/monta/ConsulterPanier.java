package Controllers.monta;


import Entities.Cart;
import Entities.Produit;
import Services.ProduitService;
import Utils.ImageManager;
import io.github.palexdev.materialfx.controls.MFXButton;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.IOException;
import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

public class ConsulterPanier {

    @FXML
    private TableView<Produit> cartTableView;

    @FXML
    private TableColumn<Produit, ImageView> imageCol;

    @FXML
    private TableColumn<Produit, String> nomCol;

    @FXML
    private TableColumn<Produit, Double> prixUnitaireCol;

    @FXML
    private TableColumn<Produit, Integer> quantityCol;

    @FXML
    private TableColumn<Produit, Double> prixTotalCol;

    @FXML
    private TableColumn<Produit, Void> actionsCol;

    @FXML
    private Label totalSumLabel;

    @FXML
    private MFXButton viderPanierBtn;

    @FXML
    private MFXButton passerPaiementBtn;

    @FXML
    private MFXButton continuerAchatsBtn;

    @FXML
    private HBox recommendationsBox;

    @FXML
    private Label recommendationsLabel;

    private final Cart cart = Cart.getInstance();
    private final ProduitService produitService = new ProduitService();
    private final NumberFormat currencyFormat = NumberFormat.getCurrencyInstance(Locale.FRANCE);

    @FXML
    void initialize() {
        setupTableColumns();
        refreshCartItems();

        passerPaiementBtn.setDisable(cart.getProduits().isEmpty());
        updateRecommendationsVisibility();
        loadRecommendedProducts();

        cartTableView.getItems().addListener((javafx.collections.ListChangeListener.Change<? extends Produit> c) -> {
            updateRecommendationsVisibility();
        });
    }

    private void setupTableColumns() {
        imageCol.setCellValueFactory(cellData -> {
            String imagePath = cellData.getValue().getImage();
            ImageView imageView = new ImageView();

            // Utiliser ImageManager pour charger l'image
            Image image = ImageManager.loadImage(imagePath);
            imageView.setImage(image);

            imageView.setFitHeight(50);
            imageView.setFitWidth(50);
            imageView.setPreserveRatio(true);
            return new SimpleObjectProperty<>(imageView);
        });

        nomCol.setCellValueFactory(new PropertyValueFactory<>("nom_produit"));
        prixUnitaireCol.setCellValueFactory(new PropertyValueFactory<>("prix"));
        prixUnitaireCol.setCellFactory(col -> new TableCell<Produit, Double>() {
            @Override
            protected void updateItem(Double prix, boolean empty) {
                super.updateItem(prix, empty);
                if (empty || prix == null) {
                    setText(null);
                } else {
                    setText(currencyFormat.format(prix));
                }
            }
        });

        quantityCol.setCellValueFactory(new PropertyValueFactory<>("quantity"));
        quantityCol.setCellFactory(col -> new TableCell<Produit, Integer>() {
            @Override
            protected void updateItem(Integer quantity, boolean empty) {
                super.updateItem(quantity, empty);
                if (empty || quantity == null) {
                    setGraphic(null);
                } else {
                    HBox quantityBox = new HBox(5);
                    quantityBox.setAlignment(Pos.CENTER);

                    MFXButton minusBtn = new MFXButton("-");
                    minusBtn.setPrefSize(30, 30);
                    minusBtn.setOnAction(e -> {
                        Produit produit = getTableRow().getItem();
                        if (produit != null) {
                            int newQuantity = produit.getQuantity() - 1;
                            if (newQuantity > 0) {
                                cart.updateQuantity(produit, newQuantity);
                                refreshCartItems();
                            } else {
                                // Si la quantité est 0, supprimer le produit
                                cart.removeProduit(produit);
                                refreshCartItems();
                            }
                        }
                    });

                    Label quantityLabel = new Label(quantity.toString());
                    quantityLabel.setPrefWidth(30);
                    quantityLabel.setAlignment(Pos.CENTER);

                    MFXButton plusBtn = new MFXButton("+");
                    plusBtn.setPrefSize(30, 30);
                    plusBtn.setOnAction(e -> {
                        Produit produit = getTableRow().getItem();
                        if (produit != null) {
                            int newQuantity = produit.getQuantity() + 1;
                            cart.updateQuantity(produit, newQuantity);
                            refreshCartItems();
                        }
                    });

                    quantityBox.getChildren().addAll(minusBtn, quantityLabel, plusBtn);
                    setGraphic(quantityBox);
                }
            }
        });

        prixTotalCol.setCellValueFactory(cellData -> {
            Produit produit = cellData.getValue();
            double total = produit.getPrix() * produit.getQuantity();
            return new SimpleDoubleProperty(total).asObject();
        });
        prixTotalCol.setCellFactory(col -> new TableCell<Produit, Double>() {
            @Override
            protected void updateItem(Double total, boolean empty) {
                super.updateItem(total, empty);
                if (empty || total == null) {
                    setText(null);
                } else {
                    setText(currencyFormat.format(total));
                }
            }
        });

        actionsCol.setCellFactory(col -> new TableCell<Produit, Void>() {
            private final MFXButton deleteButton = new MFXButton("Supprimer");

            {
                deleteButton.setStyle("-fx-background-color: #f44336;");
                deleteButton.setTextFill(javafx.scene.paint.Color.WHITE);
                deleteButton.setOnAction(event -> {
                    Produit produit = getTableRow().getItem();
                    if (produit != null) {
                        cart.removeProduit(produit);
                        refreshCartItems();
                    }
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    setGraphic(deleteButton);
                }
            }
        });
    }

    private void refreshCartItems() {
        ObservableList<Produit> observableList = FXCollections.observableList(cart.getProduits());
        cartTableView.setItems(observableList);
        updateTotalSum();

        boolean cartIsEmpty = cart.getProduits().isEmpty();
        passerPaiementBtn.setDisable(cartIsEmpty);
        viderPanierBtn.setDisable(cartIsEmpty);

        updateRecommendationsVisibility();
    }

    @FXML
    private void viderPanier() {
        Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION);
        confirmAlert.setTitle("Vider le panier");
        confirmAlert.setHeaderText(null);
        confirmAlert.setContentText("Êtes-vous sûr de vouloir vider votre panier ?");

        if (confirmAlert.showAndWait().orElse(ButtonType.CANCEL) == ButtonType.OK) {
            cart.clearCart();
            refreshCartItems();
        }
    }

    @FXML
    private void passerPaiement() {
        if (cart.getProduits().isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Panier vide", "Votre panier est vide.");
            return;
        }

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/FXML/monta/Paiement.fxml"));
            Parent root = loader.load();

            // Passer les données au contrôleur de paiement
            Paiement paiementController = loader.getController();

            Scene scene = new Scene(root);
            Stage stage = (Stage) passerPaiementBtn.getScene().getWindow();
            stage.setScene(scene);
            stage.setTitle("Paiement");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Erreur", "Impossible de naviguer vers la page de paiement.");
        }
    }

    @FXML
    private void continuerAchats() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/FXML/monta/ListProduit1.fxml"));
            Parent root = loader.load();
            Scene scene = new Scene(root);
            Stage stage = (Stage) continuerAchatsBtn.getScene().getWindow();
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Erreur", "Impossible de naviguer vers la page d'accueil.");
        }
    }

    private void updateTotalSum() {
        double totalSum = cart.getTotal();
        totalSumLabel.setText(currencyFormat.format(totalSum));
    }

    private void loadRecommendedProducts() {
        try {
            // Récupérer quelques produits recommandés (par exemple, les plus populaires)
            List<Produit> recommendedProducts = produitService.getRandomProduits(4);

            recommendationsBox.getChildren().clear();
            for (Produit produit : recommendedProducts) {
                // Créer une vignette pour chaque produit recommandé
                VBox productCard = createProductCard(produit);
                recommendationsBox.getChildren().add(productCard);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private VBox createProductCard(Produit produit) {
        VBox card = new VBox(10);
        card.setAlignment(Pos.CENTER);
        card.setPadding(new Insets(10));
        card.setPrefWidth(200);
        card.setStyle("-fx-border-color: #e0e0e0; -fx-border-radius: 5; -fx-background-color: white;");

        // Image du produit
        ImageView imageView = new ImageView();

        // Utiliser ImageManager pour charger l'image
        String imagePath = produit.getImage();
        Image image =ImageManager.loadImage(imagePath);
        imageView.setImage(image);

        imageView.setFitWidth(120);
        imageView.setFitHeight(120);
        imageView.setPreserveRatio(true);

        // Nom et prix
        Label nameLabel = new Label(produit.getNom_produit());
        nameLabel.setStyle("-fx-font-weight: bold;");
        Label priceLabel = new Label(currencyFormat.format(produit.getPrix()));

        // Bouton pour ajouter au panier
        MFXButton addButton = new MFXButton("Ajouter au panier");
        addButton.setStyle("-fx-background-color: #4CAF50;");
        addButton.setTextFill(javafx.scene.paint.Color.WHITE);
        addButton.setOnAction(e -> {
            cart.addProduit(produit);
            refreshCartItems();
        });

        card.getChildren().addAll(imageView, nameLabel, priceLabel, addButton);

        return card;
    }

    private void showAlert(Alert.AlertType type, String title, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

    private void updateRecommendationsVisibility() {
        boolean isEmpty = cartTableView.getItems().isEmpty();
        recommendationsLabel.setVisible(isEmpty);
        recommendationsBox.setVisible(isEmpty);
    }

    private static class SimpleObjectProperty<T> extends javafx.beans.property.SimpleObjectProperty<T> {
        public SimpleObjectProperty(T initialValue) {
            super(initialValue);
        }
    }
}
