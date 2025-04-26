package Controllers.monta;


import Entities.User;
import Entities.UserSession;
import Services.UserService;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

import java.io.IOException;
import java.sql.SQLException;

public class Login {
    @FXML
    private Button cnxbtn;
    @FXML
    private Button sinscrirebtn;
    @FXML
    private Label emailcc;
    @FXML
    private Label pwdcc;
    @FXML
    private Label cnxcc;
    @FXML
    private TextField email;
    @FXML
    private PasswordField mdp;
    public void sinscrire(ActionEvent actionEvent) throws IOException {
        // Registration functionality has been removed
        // Previously loaded Registre.fxml
        this.cnxcc.setText("Registration functionality has been removed");
    }

    public void connection(ActionEvent actionEvent) throws SQLException, IOException {
        int t=0;
        UserService userService=new UserService();
        if (email.getText().isEmpty()){
            t = 1;
            this.emailcc.setText("Vous devez saisir votre email");
        } else {
            this.emailcc.setText("");
        }
        if (mdp.getText().isEmpty()){
            t = 1;
            this.pwdcc.setText("Vous devez saisir votre mot de passe");
        } else {
            this.pwdcc.setText("");
        }
        if(t==0){
            User user=new User();
            user.setPassword(mdp.getText());
            user.setEmail(email.getText());
            user=userService.login(user);
            if(user==null)
                this.cnxcc.setText("Desole email ou mot de passe incorrect");
            else if (user.getId()==0) {
                this.cnxcc.setText("Desole Mot de passe incorrect");
            }
            else {
                UserSession userSession=UserSession.getInstance();
                userSession.setId((long) user.getId());
                userSession.setUser(user);
                if(user.getRole().equals("[\"ROLE_ADMIN\"]")){
                    FXMLLoader loader=new FXMLLoader(getClass().getResource("/FXML/monta/ListProduit.fxml"));
                    Parent root=loader.load();
                    mdp.getScene().setRoot(root);
                }else {
                    FXMLLoader loader=new FXMLLoader(getClass().getResource("/FXML/monta/Acceuil.fxml"));
                    Parent root=loader.load();
                    mdp.getScene().setRoot(root);
                }
            }
        }
    }
}
