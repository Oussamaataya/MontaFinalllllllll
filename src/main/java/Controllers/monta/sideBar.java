package Controllers.monta;


import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class sideBar {
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
    void navigateTOListCommand(ActionEvent event) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/FXML/monta/ListCommande.fxml"));
            Stage window = (Stage) ((Node) event.getSource()).getScene().getWindow();
            window.setScene(new Scene(root));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @FXML
    void handleLogout(ActionEvent event) {
        try {
            // Load the login page
            Parent root = FXMLLoader.load(getClass().getResource("/FXML/monta/Login.fxml"));
            Stage window = (Stage) ((Node) event.getSource()).getScene().getWindow();
            window.setScene(new Scene(root));
            window.setTitle("Login");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
