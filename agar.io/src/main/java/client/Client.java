package client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;

import settings.*;
import javax.net.SocketFactory;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;
import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import javafx.scene.Parent;

public class Client extends Application {
	public static final String TRUSTTORE_LOCATION = "C:/Users/99031510240/alv";
	public static final int OFFLINE = 0;
	public static final int READY = 1;
	public static final int PLAYING = 2;
	public static final int WATCHING = 3;
	public static final String HOST = "localhost";
	
	public static PrintWriter writerGame;
	public static PrintWriter writerChat;
	public static BufferedReader readerGame;
	public static BufferedReader readerChat;
	private static LoginController logInC;
	private StreamingViewer sv;
	private int sessionStatus;
	private int posPlayer;
	private String nickName;
	private String[] enemies;
	private String player;
	private String[] food;
	private String[] scores;
	private ArrayList<String> chat;

	public String getNickName() {
		return nickName;
	}

	@Override
	public void start(final Stage primaryStage) {
		try {
			FXMLLoader loader = new FXMLLoader();
			Parent root = loader.load(getClass().getResource("login.fxml").openStream());
			Scene scene = new Scene(root);
			primaryStage.setScene(scene);
			primaryStage.show();
			primaryStage.setOnCloseRequest(new EventHandler<WindowEvent>() {

				public void handle(WindowEvent event) {
					primaryStage.close();
				}
			});
			logInC = loader.getController();
			logInC.putClient(this);
			connectToServer();
			sessionStatus = OFFLINE;
			posPlayer = 0;
			chat = new ArrayList<>();
			enemies = new String[0];
			food = new String[0];
			scores = new String[0];
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {
		launch(args);
	}

	public void sendToServer(String parameter) {
		writerGame.println(parameter);
	}

	public Object getlogInC() {
		return logInC;
	}

	public void putLogInC(LoginController loginController) {
		logInC = loginController;
	}

	public void connectToServer() {
		final SSLSocket client;
		System.setProperty("javax.net.ssl.trustStore", "agar.io/src/main/resources/client/clientTrustedCerts.jks");
		System.setProperty("javax.net.ssl.trustStorePassword", "123456");
		System.setProperty("javax.net.ssl.keyStore", "agar.io/src/main/resources/client/clientkey.jks");
		System.setProperty("javax.net.ssl.keyStorePassword", "123456");
		SSLSocketFactory sf = (SSLSocketFactory) SSLSocketFactory.getDefault();

		try {
			client = (SSLSocket) sf.createSocket(HOST, Port.LOGIN.getPort());
			String[] supported = client.getSupportedCipherSuites();
			client.setEnabledCipherSuites(supported);
			writerGame = new PrintWriter(client.getOutputStream(), true);

			Thread tServer = new Thread(new Runnable() {

				public void run() {
					try {
						readerGame = new BufferedReader(new InputStreamReader(client.getInputStream()));
						while (client.isConnected()) {
							final String serverAnswer = readerGame.readLine();
							logInC.showMessage(serverAnswer);
							if (serverAnswer.endsWith(ServerMessage.WAITING_MATCH.getMessage())) {
								nickName = serverAnswer.split(" ")[1];
								connectToGame();
								sessionStatus = READY;
							} else if (serverAnswer.endsWith(ServerMessage.JOIN_SPECTATOR.getMessage())) {
								player = 0 + "," + 0 + "," + 10 + "," + 10 + "," + "Spectator" + "," + "F" + "," + 0
										+ "," + 0 + "," + 0;
								posPlayer = -1;
								nickName = serverAnswer.split(" ")[1];
								sv = new StreamingViewer();
								sessionStatus = WATCHING;
								sv.start();
								showStreaming();
							}
							////
						}
					} catch (IOException e) {
						try {
							client.shutdownInput();
						} catch (IOException e1) {
							e1.printStackTrace();
						}
						e.printStackTrace();
					}
				}

			});

			tServer.start();

		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void showStreaming() {
			connectToChat();
			Thread stream = new Thread(new Runnable() {				
				@Override
				public void run() {
					try {
						Thread.sleep(1000);
					while (true) {
						String infoGame = sv.gameState;
//			System.out.println(infoGame);
						if(infoGame!=null)
							updateGame(infoGame);
						Thread.sleep(36);
					}
					
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				}
			});
	}

	public void connectToGame() {
		final Socket client;
		SocketFactory sf = SocketFactory.getDefault();
		connectToChat();
		try {
			client = sf.createSocket(HOST, Port.GAME.getPort());
			writerGame = new PrintWriter(client.getOutputStream(), true);
			writerGame.println(nickName);

			Thread tServer = new Thread(new Runnable() {

				public void run() {
					BufferedReader readerC;
					try {
						readerC = new BufferedReader(new InputStreamReader(client.getInputStream()));
						System.out.println("Se conecta al juego");
						ReceptionAudio ra = new ReceptionAudio();
						ra.start();

						while (client.isConnected()) {
							final String infoGame = readerC.readLine();
//					System.out.println(infoGame);
							if (!infoGame.startsWith(ServerMessage.STARTING_MATCH.getMessage())) {
								updateGame(infoGame);
								sendToServer(getInfoPlayer());
							} else {
								posPlayer = Integer.parseInt(infoGame.substring(infoGame.length() - 1));
								sessionStatus = PLAYING;
							}
//					aquí debería llenar la información del cliente sobre el juego
						}
					} catch (IOException e) {
						try {
							client.shutdownInput();
						} catch (IOException e1) {
							e1.printStackTrace();
						}
						e.printStackTrace();
					}
				}

//		private String setFormatToCommas(String[] infoPlayer) {
//			StringBuilder sb = new StringBuilder();
//			for (int i = 0; i < infoPlayer.length; i++) {
//				sb.append(infoPlayer[i] + ",");
//			}
//			System.out.println(sb.toString());
//			return sb.toString();
//		}
			});

			tServer.start();

		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void connectToChat() {
		final Socket client;
		SocketFactory sf = SocketFactory.getDefault();
		try {
			client = sf.createSocket(HOST, Port.CHAT.getPort());
			writerChat = new PrintWriter(client.getOutputStream(), true);
			writerChat.println(nickName + " is watching");
			readerChat = new BufferedReader(new InputStreamReader(client.getInputStream()));
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		Thread chatThread = new Thread(new Runnable() {

			@Override
			public void run() {
				try {
					String serverAnswer;
					while (true) {

						serverAnswer = readerChat.readLine();
						System.out.println(serverAnswer);
						chat.add(serverAnswer);
						if (chat.size() > 20)
							chat.remove(0);
					}
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		});
		chatThread.start();
	}

	/**
	 * 
	 * @return Array of strings that represent the position x,y and mass (width and
	 *         height) of every food in the game Example : food[0] -> x,y,w,h
	 */
	public String[] getFoodFromGame() {
		return food;
	}

	/**
	 * 
	 * @return Array of strings that represent the position x,y and mass (width and
	 *         height) of every player in the game Example : player[0] -> x,y,w,h,id
	 */
	public String[] getPlayersFromGame() {
		return enemies;
	}

	/**
	 * 
	 * @return Array of strings that represent the initial info of the user. Initial
	 *         position, initial mass (width and height) and id infoPlayer[0]-> x ,
	 *         infoPlayer[1]-> y, infoPlayer[2]-> w, infoPlayer[3]-> h,
	 *         infoPlayer[4]-> id
	 */
	public String getInfoPlayer() {
//		System.out.println(player);
		return player;
	}

	/**
	 * 
	 * @param state position x,y and mass that is represented by a ball
	 */
	public void updatePlayer(String state) {
		player = state;
	}

	public int getPosPlayer() {
		return posPlayer;
	}

	public void updateGame(String entry) {
		String[] arreglos = entry.split("/");
		enemies = arreglos[0].split(" ");
		food = arreglos[1].split(" ");
		scores = arreglos[2].split(" ");
		String statusGame = arreglos[3];
		if(statusGame.equals("false")) {
			if(sessionStatus != OFFLINE) {				
			sessionStatus = OFFLINE;
			logInC.endGame();
			}
		}
		if (player == null)
			player = enemies[posPlayer];
		else {
			String[] oldP = player.split(",");
			if (oldP[5].equals("T")) {
				String[] newP = enemies[posPlayer].split(",");
				StringBuilder sbP = new StringBuilder(oldP[0] + "," + oldP[1]);
				for (int i = 2; i < newP.length; i++) {
					sbP.append(",");
					sbP.append(newP[i]);
				}
				player = sbP.toString();
			}
		}
	}

	public String[] getScores() {
		return scores;
	}

	public int getStatus() {
		return sessionStatus;
	}

	public ArrayList<String> getChat() {
		return chat;
	}

	public void setSession(int status) {
		this.sessionStatus = status;
	}

	public void sendToChat(String message) {
		writerChat.println(message);
	}

}
