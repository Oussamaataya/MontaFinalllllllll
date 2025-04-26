package Controllers.monta;


import Entities.UserSession;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.layout.VBox;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.List;
import java.util.ResourceBundle;

public class Admin implements Initializable {
    @FXML
    private VBox box;
    @FXML
    private VBox vbox;
    
    public void deconnection(ActionEvent actionEvent) throws IOException {
        UserSession userSession = UserSession.getInstance();
        userSession.logout();
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/FXML/monta/Login.fxml"));
        Parent root = loader.load();
        vbox.getScene().setRoot(root);
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        FXMLLoader fxl = new FXMLLoader();
        fxl.setLocation(getClass().getResource("/FXML/monta/sidebar.fxml"));
        Parent root = null;
        try {
            root = fxl.load();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        box.getChildren().add(root);
        // User card display functionality has been removed
        // This section previously loaded cardutulisateur.fxml for each user
    }
}
