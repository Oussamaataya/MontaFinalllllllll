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

public class AjouterProduit {

    @FXML
    private TextField nomProduittf;

    @FXML
    private TextField descriptiontf;

    @FXML
    private TextField imagetf;

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

    @FXML
    void Ajouter(ActionEvent event) {
        if (isValidInput()) {
            try {
                // Ne pas modifier le chemin de l'image, le service s'en chargera
                String imagetfText = imagetf.getText();

                SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
                Date dateFabrication = new java.sql.Date(dateFormat.parse(dateFabricationtf.getText()).getTime());
                Date dateExpiration = new java.sql.Date(dateFormat.parse(dateExpirationtf.getText()).getTime());

                // Validate if expiration date is after fabrication date
                if (dateExpiration.before(dateFabrication)) {
                    showErrorAlert("The expiration date cannot be before the fabrication date.");
                    return;
                }

                Produit nouveauProduit = new Produit(
                        nomProduittf.getText(),
                        descriptiontf.getText(),
                        imagetfText,
                        Integer.parseInt(quantitytf.getText()),
                        Double.parseDouble(prixtf.getText()),
                        typeProduittf.getText(),
                        dateFabrication,
                        dateExpiration
                );
                int id = ps.add(nouveauProduit);
                if (id > 0) {
                    showSuccessAlert("Produit ajouté avec succès!");
                    // Rediriger vers la liste des produits
                    naviguer2(event);
                }
            } catch (ParseException e) {
                showErrorAlert("Invalid date format. Please use dd-MM-yyyy.");
            } catch (NumberFormatException e) {
                showErrorAlert("Please ensure quantity and price are numeric.");
            }
        } else {
            showErrorAlert("Veuillez remplir tous les champs avec des données valides.");
        }
    }

    @FXML
    void naviguer2(ActionEvent event) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/FXML/monta/ListProduit.fxml"));
            Stage window = (Stage) ((Node) event.getSource()).getScene().getWindow();
            window.setScene(new Scene(root));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void Browse(ActionEvent event) {
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

    private boolean isValidInput() {
        return isValidTextField(nomProduittf) &&
                isValidTextField(descriptiontf) &&
                isValidTextField(imagetf) &&
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

    private void showSuccessAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Succès");
        alert.setContentText(message);
        alert.showAndWait();
    }
}
