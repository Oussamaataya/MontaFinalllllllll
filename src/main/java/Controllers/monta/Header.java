package Controllers.monta;

import Entities.UserSession;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Button;

import java.io.IOException;

public class Header {
    public Button profilebtn;
    public Button publicationbtn;
    public Button commentairebtn;
    public Button decbtn;
    @FXML
    private Button acceuilbtn;
    @FXML
    private Button consulterPanierbtn;

    public void acceuil(ActionEvent actionEvent) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/FXML/monta/Acceuil.fxml"));
        Parent root = loader.load();
        acceuilbtn.getScene().setRoot(root);
    }

    public void logout(ActionEvent actionEvent) throws IOException {
        UserSession userSession = UserSession.getInstance();
        userSession.logout();
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/FXML/monta/Login.fxml"));
        Parent root = loader.load();
        acceuilbtn.getScene().setRoot(root);
    }

    public void ListProduits(ActionEvent actionEvent) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/FXML/monta/Listproduit1.fxml"));
        Parent root = loader.load();
        acceuilbtn.getScene().setRoot(root);
    }

    public void ListCommands(ActionEvent actionEvent) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/FXML/monta/Listcommande1.fxml"));
        Parent root = loader.load();
        acceuilbtn.getScene().setRoot(root);
    }

    public void consulterPanier(ActionEvent actionEvent) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/FXML/monta/ConsulterPanier.fxml"));
        Parent root = loader.load();
        acceuilbtn.getScene().setRoot(root);
    }
}
