package Controllers.monta;


import Entities.Produit;
import Services.ProduitService;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class EditProduit {
    @FXML
    private TextField nomProduittf;

    @FXML
    private TextField descriptiontf;

    @FXML
    private TextField imagetf;

    @FXML
    private TextField idtf;

    @FXML
    private TextField quantitytf;

    @FXML
    private TextField prixtf;

    @FXML
    private TextField typeProduittf;

    @FXML
    private TextField dateFabricationtf;

    @FXML
    private TextField dateExpirationtf;

    private final ProduitService ps = new ProduitService();

    private Produit produitToEdit;

    public void setProduitToEdit(Produit produit) {
        this.produitToEdit = produit;
        // Afficher les données du produit dans les champs correspondants
        nomProduittf.setText(produit.getNom_produit());
        descriptiontf.setText(produit.getDescription());
        imagetf.setText(produit.getImage());
        idtf.setText(String.valueOf(produit.getId()));
        quantitytf.setText(String.valueOf(produit.getQuantity()));
        prixtf.setText(String.valueOf(produit.getPrix()));
        typeProduittf.setText(produit.getType_produit());
        dateFabricationtf.setText(new SimpleDateFormat("dd-MM-yyyy").format(produit.getDate_fabrication()));
        dateExpirationtf.setText(new SimpleDateFormat("dd-MM-yyyy").format(produit.getDate_expiration()));
    }

    @FXML
    void EditProduit(ActionEvent event) {
        if (isValidInput()) {
            try {
                // Ne pas modifier le chemin de l'image, le service s'en chargera
                String imagetfText = imagetf.getText();

                SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
                Date dateFabrication = dateFormat.parse(dateFabricationtf.getText());
                Date dateExpiration = dateFormat.parse(dateExpirationtf.getText());

                // Vérifier que la date d'expiration est après la date de fabrication
                if (dateExpiration.before(dateFabrication)) {
                    showErrorAlert("La date d'expiration ne peut pas être antérieure à la date de fabrication.");
                    return;
                }

                // Mettre à jour les données du produit avec les nouvelles valeurs des champs
                produitToEdit.setNom_produit(nomProduittf.getText());
                produitToEdit.setDescription(descriptiontf.getText());
                produitToEdit.setImage(imagetfText);
                produitToEdit.setId(Integer.parseInt(idtf.getText()));
                produitToEdit.setQuantity(Integer.parseInt(quantitytf.getText()));
                produitToEdit.setPrix(Double.parseDouble(prixtf.getText()));
                produitToEdit.setType_produit(typeProduittf.getText());
                produitToEdit.setDate_fabrication(dateFabrication);
                produitToEdit.setDate_expiration(dateExpiration);

                // Appeler la méthode de mise à jour dans le service
                ps.update(produitToEdit);

                // Confirmation de mise à jour
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Succès");
                alert.setContentText("Le produit a été mis à jour avec succès.");
                alert.showAndWait();

                // Fermer la fenêtre et retourner à la liste des produits
                naviguer2(event);

            } catch (ParseException e) {
                showErrorAlert("Format de date invalide. Utilisez le format dd-MM-yyyy.");
            } catch (NumberFormatException e) {
                showErrorAlert("L'ID, la quantité et le prix doivent être des nombres valides.");
            }
        } else {
            showErrorAlert("Veuillez remplir tous les champs avec des données valides.");
        }
    }

    @FXML
    void browseImage(ActionEvent event) {
        Stage primaryStage = new Stage();
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Choisir une image");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.gif", "*.jpeg"));

        File selectedFile = fileChooser.showOpenDialog(primaryStage);

        if (selectedFile != null) {
            String fileUrl = selectedFile.toURI().toString();
            imagetf.setText(fileUrl);
            System.out.println("Image sélectionnée: " + fileUrl);
        }
    }

    @FXML
    void naviguer2(ActionEvent event) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/FXML/monta/listProduit.fxml"));
            Stage window = (Stage) ((Node) event.getSource()).getScene().getWindow();
            window.setScene(new Scene(root));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private boolean isValidInput() {
        return isValidTextField(nomProduittf) &&
                isValidTextField(descriptiontf) &&
                isValidTextField(imagetf) &&
                isValidTextField(idtf) &&
                isValidTextField(quantitytf) &&
                isValidTextField(prixtf) &&
                isValidTextField(typeProduittf) &&
                isValidTextField(dateFabricationtf) &&
                isValidTextField(dateExpirationtf);
    }

    private boolean isValidTextField(TextField textField) {
        return textField.getText() != null && !textField.getText().isEmpty();
    }

    private void showErrorAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Erreur de saisie");
        alert.setContentText(message);
        alert.showAndWait();
    }
}
