package Controllers.monta;


import Entities.*;
import Services.EmailService;
import Services.PaymentService;
import io.github.palexdev.materialfx.controls.MFXButton;
import io.github.palexdev.materialfx.controls.MFXTextField;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.List;

public class Paiement {

    @FXML
    private MFXTextField nomField;

    @FXML
    private MFXTextField prenomField;

    @FXML
    private MFXTextField adresseField;

    @FXML
    private MFXTextField telephoneField;

    @FXML
    private MFXTextField emailField;

    @FXML
    private RadioButton paiementEnLigneRadio;

    @FXML
    private RadioButton paiementLivraisonRadio;

    @FXML
    private ToggleGroup typePaiementGroup;

    @FXML
    private RadioButton carteBancaireRadio;

    @FXML
    private RadioButton paypalRadio;

    @FXML
    private ToggleGroup methodePaiementGroup;

    @FXML
    private RadioButton especesRadio;

    @FXML
    private RadioButton chequeRadio;

    @FXML
    private ToggleGroup methodeLivraisonGroup;

    @FXML
    private VBox methodePaiementContainer;

    @FXML
    private VBox methodesEnLigneContainer;

    @FXML
    private VBox methodesLivraisonContainer;

    @FXML
    private VBox cardDetailsContainer;

    @FXML
    private MFXTextField cardNumberField;

    @FXML
    private MFXTextField expiryDateField;

    @FXML
    private MFXTextField cvcField;

    @FXML
    private MFXTextField cardholderNameField;

    @FXML
    private MFXTextField instructionsField;

    @FXML
    private Label totalLabel;

    @FXML
    private MFXButton retourBtn;

    @FXML
    private MFXButton confirmerBtn;

    private final PaymentService paymentService = new PaymentService();
    private final EmailService emailService = new EmailService();
    private final Cart cart = Cart.getInstance();

    @FXML
    void initialize() {
        // Set total amount with currency format
        totalLabel.setText("Total à payer: " + String.format("%.2f", cart.getTotal()) + " €");

        // Par défaut, sélectionner Paiement en ligne
        paiementEnLigneRadio.setSelected(true);

        // Écouter les changements sur le type de paiement
        paiementEnLigneRadio.setOnAction(e -> updatePaymentMethodsVisibility());
        paiementLivraisonRadio.setOnAction(e -> updatePaymentMethodsVisibility());

        // Écouter les changements sur la méthode de paiement
        carteBancaireRadio.setOnAction(e -> updateCardDetailsVisibility());
        paypalRadio.setOnAction(e -> updateCardDetailsVisibility());

        // Initialiser la visibilité
        updatePaymentMethodsVisibility();

        // Pré-remplir les champs avec les informations du client connecté
        prefillUserData();
    }

    private void prefillUserData() {
        // Vérifier si un utilisateur est connecté
        if (UserSession.getInstance().isLoggedIn()) {
            User user = UserSession.getInstance().getUser();

            // Remplir les champs avec les données de l'utilisateur
            nomField.setText(user.getFirstName() != null ? user.getFirstName() : "");
            prenomField.setText(user.getLastName() != null ? user.getLastName() : "");
            emailField.setText(user.getEmail() != null ? user.getEmail() : "");
            telephoneField.setText(user.getPhoneNumber() != 0 ? String.valueOf(user.getPhoneNumber()) : "");

            if (user.getAdress() != null) {
                adresseField.setText(user.getAdress());
            }
        }
    }

    private void updatePaymentMethodsVisibility() {
        if (paiementEnLigneRadio.isSelected()) {
            methodesEnLigneContainer.setVisible(true);
            methodesLivraisonContainer.setVisible(false);
            // Sélectionner la première option par défaut
            carteBancaireRadio.setSelected(true);
        } else if (paiementLivraisonRadio.isSelected()) {
            methodesEnLigneContainer.setVisible(false);
            methodesLivraisonContainer.setVisible(true);
            // Sélectionner la première option par défaut
            especesRadio.setSelected(true);
        }

        // Mettre à jour la visibilité des détails de carte
        updateCardDetailsVisibility();
    }

    private void updateCardDetailsVisibility() {
        // Afficher les détails de carte uniquement si Carte bancaire est sélectionnée
        cardDetailsContainer.setVisible(paiementEnLigneRadio.isSelected() && carteBancaireRadio.isSelected());
    }

    @FXML
    void retourPanier() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/FXML/monta/ConsulterPanier.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) retourBtn.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Erreur", "Impossible de retourner au panier");
        }
    }

    @FXML
    void confirmerCommande() {
        // Validate form
        if (!validateForm()) {
            return;
        }

        // Validate card info if payment method is credit card
        if (paiementEnLigneRadio.isSelected() && carteBancaireRadio.isSelected()) {
            if (!validateCardInfo()) {
                return;
            }
        }

        // Get order data
        String nom = nomField.getText();
        String prenom = prenomField.getText();
        String adresse = adresseField.getText();
        String telephone = telephoneField.getText();
        String email = emailField.getText();

        // Déterminer le type et la méthode de paiement en fonction des boutons radio sélectionnés
        String typePaiement = paiementEnLigneRadio.isSelected() ? "Paiement en ligne" : "Paiement à la livraison";
        String methodePaiement;

        if (paiementEnLigneRadio.isSelected()) {
            methodePaiement = carteBancaireRadio.isSelected() ? "Carte bancaire" : "PayPal";
        } else {
            methodePaiement = especesRadio.isSelected() ? "Espèces" : "Chèque";
        }

        String instructions = instructionsField.getText();
        List<Produit> produits = cart.getProduits();
        float total = (float) cart.getTotal();

        // Combine type and method for storage
        String fullPaymentMethod = typePaiement + " - " + methodePaiement;

        // Process payment
        Commande commande = paymentService.processPayment(
                prenom,
                nom,
                adresse,
                telephone,
                email,
                fullPaymentMethod,
                produits,
                instructions,
                total
        );
        if (commande != null) {
            // Envoyer email de confirmation
            boolean emailSent = emailService.sendOrderConfirmationEmail(commande);

            // Clear cart after successful order
            cart.clearCart();

            // Show success message
            String successMessage = "Commande confirmée avec succès!\nNuméro de commande: " + commande.getId();
            if (emailSent) {
                successMessage += "\nUn email de confirmation a été envoyé à " + email;
            }
            showAlert(Alert.AlertType.INFORMATION, "Succès", successMessage);

            // Navigate to home page
            navigateToHome();
        } else {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Une erreur est survenue lors du traitement de la commande");
        }
    }

    private boolean validateForm() {
        if (nomField.getText().trim().isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Champ manquant", "Veuillez saisir votre nom");
            return false;
        }

        if (prenomField.getText().trim().isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Champ manquant", "Veuillez saisir votre prénom");
            return false;
        }

        if (adresseField.getText().trim().isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Champ manquant", "Veuillez saisir votre adresse");
            return false;
        }

        if (telephoneField.getText().trim().isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Champ manquant", "Veuillez saisir votre numéro de téléphone");
            return false;
        }

        if (emailField.getText().trim().isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Champ manquant", "Veuillez saisir votre email");
            return false;
        }

        if (cart.getProduits().isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Panier vide", "Votre panier est vide");
            return false;
        }

        return true;
    }

    private boolean validateCardInfo() {
        String cardNumber = cardNumberField.getText();
        String expiryDate = expiryDateField.getText();
        String cvc = cvcField.getText();

        if (cardholderNameField.getText().trim().isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Champ manquant", "Veuillez saisir le nom du titulaire de la carte");
            return false;
        }

        if (!paymentService.validateCardInfo(cardNumber, expiryDate, cvc)) {
            showAlert(Alert.AlertType.WARNING, "Informations invalides", "Les informations de carte bancaire sont invalides");
            return false;
        }

        return true;
    }

    private void showAlert(Alert.AlertType type, String title, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

    private void navigateToHome() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/FXML/monta/Acceuil.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) confirmerBtn.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Erreur", "Impossible de naviguer vers l'accueil");
        }
    }
}
