package Controllers.monta;

import Entities.Commande;
import Entities.Produit;
import Entities.UserSession;
import Services.CommandeService;
import Services.StripeService;
import com.stripe.model.PaymentIntent;
import io.github.palexdev.materialfx.controls.MFXButton;
import io.github.palexdev.materialfx.controls.MFXTextField;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Worker;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.stage.Stage;
import netscape.javascript.JSObject;
import org.json.JSONObject;

import java.io.IOException;
import java.text.NumberFormat;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class StripePaiement {

    @FXML private TableView<Produit> cartSummaryTable;
    @FXML private TableColumn<Produit, String> productNameCol;
    @FXML private TableColumn<Produit, Integer> productQuantityCol;
    @FXML private TableColumn<Produit, Double> productPriceCol;
    @FXML private TableColumn<Produit, Double> productTotalCol;
    @FXML private Label totalAmountLabel;
    @FXML private MFXTextField prenomField, nomField, adresseField, codePostalField, villeField, telephoneField, emailField, instructionsField;
    @FXML private WebView stripeWebView;
    @FXML private Label paymentStatusLabel;
    @FXML private MFXButton retourPanierBtn, validerCommandeBtn;

    private final StripeService stripeService = new StripeService();
    private final CommandeService commandeService = new CommandeService();
    private final NumberFormat currencyFormat = NumberFormat.getCurrencyInstance(Locale.FRANCE);

    private List<Produit> cartProducts;
    private double totalAmount;
    private PaymentIntent paymentIntent;
    private String paymentMethodId;
    private boolean paymentSuccess = false;

    @FXML
    void initialize() {
        setupTable();
        setupStripeWebView();
        validerCommandeBtn.setDisable(true);

        if (UserSession.getInstance().isLoggedIn()) {
            prefillUserData();
        }
    }

    public void initData(List<Produit> products, double total) {
        this.cartProducts = products;
        this.totalAmount = total;

        ObservableList<Produit> observableList = FXCollections.observableList(cartProducts);
        cartSummaryTable.setItems(observableList);
        totalAmountLabel.setText(currencyFormat.format(totalAmount));
        initializeStripePayment();
    }

    private void setupTable() {
        productNameCol.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getNom_produit()));

        productQuantityCol.setCellValueFactory(cellData ->
                new SimpleIntegerProperty(cellData.getValue().getQuantity()).asObject());

        productPriceCol.setCellValueFactory(cellData ->
                new SimpleDoubleProperty(cellData.getValue().getPrix()).asObject());
        productPriceCol.setCellFactory(col -> new TableCell<Produit, Double>() {
            @Override
            protected void updateItem(Double price, boolean empty) {
                super.updateItem(price, empty);
                if (empty || price == null) {
                    setText(null);
                } else {
                    setText(currencyFormat.format(price));
                }
            }
        });

        productTotalCol.setCellValueFactory(cellData -> {
            Produit produit = cellData.getValue();
            double total = produit.getPrix() * produit.getQuantity();
            return new SimpleDoubleProperty(total).asObject();
        });
        productTotalCol.setCellFactory(col -> new TableCell<Produit, Double>() {
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
    }

    private void setupStripeWebView() {
        WebEngine webEngine = stripeWebView.getEngine();
        webEngine.loadContent(getStripeElementsHTML());

        webEngine.getLoadWorker().stateProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue == Worker.State.SUCCEEDED) {
                JSObject window = (JSObject) webEngine.executeScript("window");
                window.setMember("javaConnector", new JavaConnector());

                javafx.application.Platform.runLater(() -> {
                    if (cartProducts != null && !cartProducts.isEmpty()) {
                        initializeStripePayment();
                    }
                });
            }
        });
    }

    private void initializeStripePayment() {
        try {
            String email = emailField.getText();
            if (email == null || email.isEmpty()) {
                email = "client@example.com";
            }

            JSONObject stripeConfig = new JSONObject();
            stripeConfig.put("publishableKey", "pk_test_51Qtu0I2ZepkYVUKIMHmT3OaEo5oiyE0r1S0wTHHMapBsDzGItG2FhMyoPEh6CMP2YxMEsIAQU9kFEQ2Om8KWlnLA00NZ5fErWn");
            stripeConfig.put("clientSecret", "pi_3QGDnE2ZepkYVUKI1YvbLNTs_secret_Vzqxv7y9qd0nKiQlELICeInFX");
            stripeConfig.put("amount", totalAmount);
            stripeConfig.put("currency", "EUR");
            stripeConfig.put("demoMode", true);

            WebEngine webEngine = stripeWebView.getEngine();
            webEngine.executeScript("window.initializeStripe(" + stripeConfig.toString() + ")");
        } catch (Exception e) {
            enableDemoMode();
        }
    }

    private void enableDemoMode() {
        javafx.application.Platform.runLater(() -> {
            paymentStatusLabel.setText("Mode démonstration activé");
            validerCommandeBtn.setDisable(false);
            validerCommandeBtn.setText("Continuer en mode démo");
        });
    }

    private String getStripeElementsHTML() {
        return "<!DOCTYPE html><html><head><meta charset=\"utf-8\"></body></html>";
    }

    @FXML
    private void retourPanier() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/FXML/monta/ConsulterPanier.fxml"));
            Parent root = loader.load();
            Scene scene = new Scene(root);
            Stage stage = (Stage) retourPanierBtn.getScene().getWindow();
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Impossible de retourner au panier");
        }
    }

    @FXML
    private void validerCommande() {
        if (!validateForm()) {
            return;
        }

        if (!paymentSuccess) {
            if (validerCommandeBtn.getText().contains("démo")) {
                paymentSuccess = true;
                paymentMethodId = "demo_payment";
                createOrder();
                return;
            }

            WebEngine webEngine = stripeWebView.getEngine();
            webEngine.executeScript("window.demoMode = true;");
            webEngine.executeScript("window.confirmPayment()");
        }
    }

    private boolean validateForm() {
        if (prenomField.getText().trim().isEmpty() || nomField.getText().trim().isEmpty() ||
            adresseField.getText().trim().isEmpty() || codePostalField.getText().trim().isEmpty() ||
            villeField.getText().trim().isEmpty() || telephoneField.getText().trim().isEmpty() ||
            emailField.getText().trim().isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Champs manquants", "Veuillez remplir tous les champs obligatoires");
            return false;
        }
        return true;
    }

    private void createOrder() {
        try {
            if (cartProducts == null || cartProducts.isEmpty()) {
                showAlert(Alert.AlertType.ERROR, "Panier vide", "Votre panier est vide");
                return;
            }

            String adresseComplete = adresseField.getText() + ", " + codePostalField.getText() + " " + villeField.getText();

            Commande commande = new Commande(
                    prenomField.getText(),
                    nomField.getText(),
                    adresseComplete,
                    telephoneField.getText(),
                    emailField.getText(),
                    "Stripe",
                    "En attente",
                    LocalDateTime.now(),
                    instructionsField.getText(),
                    (float) totalAmount
            );

            int commandeId = commandeService.add(commande);

            if (commandeId > 0) {
                commande.setId(commandeId);

                List<Produit> produitsToAdd = new ArrayList<>();
                for (Produit p : cartProducts) {
                    Produit copy = new Produit();
                    copy.setId(p.getId());
                    copy.setNom_produit(p.getNom_produit());
                    copy.setDescription(p.getDescription());
                    copy.setImage(p.getImage());
                    copy.setQuantity(p.getQuantity());
                    copy.setPrix(p.getPrix());
                    copy.setType_produit(p.getType_produit());
                    copy.setDate_fabrication(p.getDate_fabrication());
                    copy.setDate_expiration(p.getDate_expiration());
                    produitsToAdd.add(copy);
                }

                commandeService.addProduitToCommande(produitsToAdd, commande);
                commande.setProduits(produitsToAdd);

                showConfirmationDialog();
            }
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Impossible de créer la commande");
        }
    }

    private void showConfirmationDialog() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Commande confirmée");
        alert.setHeaderText("Votre commande a été confirmée");
        alert.setContentText("Merci pour votre achat chez Ecolink!");

        // Ajouter un bouton "Retour à l'accueil"
        ButtonType retourAccueil = new ButtonType("Retour à l'accueil");
        alert.getButtonTypes().setAll(retourAccueil);

        alert.showAndWait().ifPresent(response -> {
            if (response == retourAccueil) {
                try {
                    FXMLLoader loader = new FXMLLoader(getClass().getResource("/FXML/monta/Acceuil.fxml"));
                    Parent root = loader.load();
                    Scene scene = new Scene(root);
                    Stage stage = (Stage) retourPanierBtn.getScene().getWindow();
                    stage.setScene(scene);
                    stage.show();
                } catch (IOException e) {
                    showAlert(Alert.AlertType.ERROR, "Erreur", "Impossible de naviguer vers l'accueil");
                }
            }
        });
    }

    private void prefillUserData() {
        if (UserSession.getInstance().isLoggedIn()) {
            var user = UserSession.getInstance().getUser();

            prenomField.setText(user.getFirstName() != null ? user.getFirstName() : "");
            nomField.setText(user.getLastName() != null ? user.getLastName() : "");
            emailField.setText(user.getEmail() != null ? user.getEmail() : "");
            telephoneField.setText(user.getPhoneNumber() != 0 ? String.valueOf(user.getPhoneNumber()) : "");

            if (user.getAdress() != null) {
                adresseField.setText(user.getAdress());
            }
        }
    }

    private void showAlert(Alert.AlertType type, String title, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

    public class JavaConnector {
        public void testConnexion(String message) {
            // Méthode vide pour réduire les messages d'erreur
        }

        public void onPaymentMethodReady(boolean ready) {
            javafx.application.Platform.runLater(() -> {
                validerCommandeBtn.setDisable(!ready);
            });
        }

        public void onPaymentSuccess(String paymentMethodId) {
            javafx.application.Platform.runLater(() -> {
                paymentSuccess = true;
                StripePaiement.this.paymentMethodId = paymentMethodId;
                paymentStatusLabel.setText("Paiement approuvé");
                createOrder();
            });
        }

        public void onPaymentError(String errorMessage) {
            javafx.application.Platform.runLater(() -> {
                enableDemoMode();
            });
        }
    }
}
