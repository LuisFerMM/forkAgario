package server;

import java.sql.Time;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Timer;
import java.util.TimerTask;
import javafx.application.Platform;
import javafx.beans.InvalidationListener;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.collections.ListChangeListener;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableView;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TitledPane;

public class ServerStController {

	@FXML
	private Button butStart;

	@FXML
	private TitledPane matchTitledPane;

	@FXML
	private TitledPane usersTitledPane;

	@FXML
	private Label minutes;
	
	@FXML
	private Label seconds;
	
	@FXML
	private Label title;

	@FXML
	private TableView<User> usersTable;

	@FXML
	private TableColumn nickNameColumn;

	@FXML
	private TableColumn emailColumn;
	
	@FXML
	private TableColumn nickScoreColumn;

	@FXML
	private TableColumn scoreColumn;

	private ObservableList <User> playerList;
	
	private ObservableList <User> playerScore;
	
	private Server server;

	@FXML
	void startServer (ActionEvent event) {
		playerList = FXCollections.observableArrayList();
		nickNameColumn.setCellValueFactory(new PropertyValueFactory<User, String>("nickname"));
		emailColumn.setCellValueFactory(new PropertyValueFactory<User, String>("email"));
		if(butStart.getText().equals("Start Server")) {
		Thread receptPlayers = new Thread(new Runnable() {
			
			public void run() {
				server.startWebConnection();
				server.startSSL();
				//siempre se ejecuta de ultimo
				server.startMatchConnection();
			}
		});
		receptPlayers.start();
		butStart.setText("Stop Server");
		new Thread(new Runnable() {
			int min = Integer.parseInt(minutes.getText());
			int sec = Integer.parseInt(seconds.getText());
			
			@SuppressWarnings("restriction")
			public void run() {
				try {
				while(!server.isReady())
					Thread.sleep(1000);
				while(server.isReceiving()){
					 Platform.runLater(new Runnable() {
				            public void run() {
				            	if(sec==00){
									if(min==00) {
										stopServer();
									}
									else {
										min--;
										sec = 59;
									minutes.setText("0"+min);
									seconds.setText(sec+"");
									}
								} else {
									sec--;
									if(sec<10)
									seconds.setText("0"+sec);
									else seconds.setText(sec+"");
								}
				            }
				        });
					 Thread.sleep(1000);
		}
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}).start();
		} else {
			stopServer();
		}
		while(server.isInGame()) {
//			playerScore.add(server.);
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
	
	void stopServer() {
		minutes.setText("00");
		seconds.setText("40");
		butStart.setText("Start Server");
		server.timeOut();
	}
	void putServer(Server server) {
		this.server = server;
	}

	public void clientJoined(User toPlay) {
		playerList.add(toPlay);
		usersTable.setItems(playerList);
	}
}
