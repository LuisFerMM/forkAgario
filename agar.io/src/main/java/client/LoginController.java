package client;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import gui.UserCanvas;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import settings.ServerMessage;

public class LoginController {

	@FXML
	private TextField txt_email;

	@FXML
	private TextField txt_password;

	@FXML
	private Button btn_play;

	@FXML
	private Button btn_singin;
	
	  @FXML
	    private Button btnConnect;
	private Client client;

	private Stage stage;

	public void putClient(Client client) {
		this.client = client;
	}

	@FXML
	void login(ActionEvent event) {
		stage = new Stage();

		client.sendToServer(txt_email.getText() + " " + txt_password.getText());
		try {
			int cont = 5;
			while(cont > 0 && client.getStatus() != Client.PLAYING && client.getStatus() != Client.READY && client.getStatus() != Client.WATCHING) {
				Thread.sleep(200);
				cont--;
				System.out.println("busca estados " + cont);				
			}
		if (client.getStatus() == Client.PLAYING || client.getStatus() == Client.READY) {
			cont = 120;
			while (client.getStatus() != Client.PLAYING && cont > 0) {
				System.out.println("Entra a esperar");
					Thread.sleep(1000);
					cont--;
			}
			startGame();
		} else if(client.getStatus() == Client.WATCHING) {
			Thread.sleep(1000);
			System.out.println("modo espectador iniciado");
			startGame();
		}
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	@FXML
	void sign_In(ActionEvent event) {
		FXMLLoader loader = new FXMLLoader();
		stage = new Stage();
		try {
			AnchorPane root = loader.load(getClass().getResource("signin.fxml").openStream());

			SignInController signinC = loader.getController();
			signinC.putClient(client);
			Scene scene = new Scene(root);
			stage.setScene(scene);
			stage.show();
			stage.setOnCloseRequest(new EventHandler<WindowEvent>() {

				public void handle(WindowEvent event) {
					stage.close();
				}
			});
		} catch (Exception e) {
			e.printStackTrace();
		} catch (Throwable e) {
			e.printStackTrace();
		}
	}

	void startGame() {
		UserCanvas canvas = new UserCanvas();
		canvas.setClient(client);
		canvas.start();
		Scene scene = new Scene(canvas);
		stage.setScene(scene);
		stage.show();
	}
	
	@FXML
    void connect(ActionEvent event) {
		client.connectToServer();
    }

	public void showMessage(String line) {

		if (line.equals(ServerMessage.REGISTER_SUCCESS.getMessage())) {
			JOptionPane.showMessageDialog(new JFrame(), ServerMessage.REGISTER_SUCCESS.getMessage(),
					"Register complete", JOptionPane.INFORMATION_MESSAGE);
		} else if (line.equals(ServerMessage.SESSION_FAILED.getMessage())) {
			JOptionPane.showMessageDialog(new JFrame(), ServerMessage.SESSION_FAILED.getMessage(), "Session failed",
					JOptionPane.ERROR_MESSAGE);
		}
	}
}
