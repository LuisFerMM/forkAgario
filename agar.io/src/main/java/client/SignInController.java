package client;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert.AlertType;

public class SignInController {

    @FXML
    private TextField txt_email;

    @FXML
    private TextField txt_paasword;

    @FXML
    private Button btn_singin;

    @FXML
    private TextField txt_nickname;

    private Client client;

    public void putClient(Client pClient) {
    	client = pClient;
	}

    @FXML
    void sign_In(ActionEvent event) {
    	client.sendToServer(txt_email.getText() + " " + txt_nickname.getText() + " " + txt_paasword.getText());
    }

}
