package gui;

import java.awt.MouseInfo;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.ArrayList;

import client.Client;
import javafx.animation.AnimationTimer;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.ArcType;
import javafx.scene.shape.Circle;
import javafx.stage.Stage;
import javafx.util.Duration;

public class UserCanvas extends BorderPane {

	public static final int WIDTH_SCREEN = Toolkit.getDefaultToolkit().getScreenSize().width;
	public static final int HEIGHT_SCREEN = Toolkit.getDefaultToolkit().getScreenSize().height;
	public int[] px = { 100, 100, 100, 100 };
	public int[] py = { 50, 100, 150, 200 };
	public int[] cx = { 100, 100, 100, 100 };
	public int[] cy = { HEIGHT_SCREEN*3/4, HEIGHT_SCREEN*13/16, HEIGHT_SCREEN*7/8, HEIGHT_SCREEN*15/16};
	private Client user;
	private TextField writerChat;
	private VBox chatPane;
	private Button butSend;
	public void start() {

		Canvas canvas = new Canvas(WIDTH_SCREEN-300, HEIGHT_SCREEN);
		
		chatPane = new VBox();
        chatPane.setPrefSize(400, 700);
        chatPane.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
        ScrollPane scrollPane = new ScrollPane(chatPane);
        scrollPane.setPrefSize(300, 300);
        scrollPane.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
        scrollPane.setFitToWidth(true);
        scrollPane.setFitToHeight(true);
        scrollPane.setStyle("-fx-focus-color: transparent;");
        if(user.getStatus()== Client.WATCHING) {
        writerChat = new TextField("Write a Message");
        writerChat.setPrefWidth(WIDTH_SCREEN-300);
        FlowPane contWriter = new FlowPane();
        butSend = new Button("Send");
        butSend.setOnAction(new EventHandler<ActionEvent>() {
        	public void handle(ActionEvent t) {
   	              if(!writerChat.getText().equals("")) {
   	            	  user.sendToChat(writerChat.getText());
   	            	  writerChat.clear();
   	              }
   	              System.out.println("lee esta mierda");
        	}
		});
        contWriter.getChildren().add(writerChat);
        contWriter.getChildren().add(butSend);
        this.setBottom(contWriter);
        canvas.setHeight(HEIGHT_SCREEN-100);
        }
        this.setRight(scrollPane);
		final GraphicsContext gc = canvas.getGraphicsContext2D();
		this.setCenter(canvas);

		drawShapes(gc);

		canvas.setOnMouseClicked(new EventHandler<MouseEvent>() {
			public void handle(MouseEvent e) {
				System.out.println(e.getX() + " " + e.getY());

			}
		});

	}

	private void drawShapes(final GraphicsContext gc) {
		// infoPlayer = {x,y,w,h,id,T/F,R,G,B} is the same for enemies and food
		final String[] infoPlayer = user.getInfoPlayer().split(",");
		int user_x = (int) Double.parseDouble(infoPlayer[0]);
		int user_y = (int) Double.parseDouble(infoPlayer[1]);
		final String user_id = user.getNickName();
		final double R = Double.parseDouble(infoPlayer[6]);
		final double G = Double.parseDouble(infoPlayer[7]);
		final double B = Double.parseDouble(infoPlayer[8]);
		final Sprite player;
		if (infoPlayer[5].equals("T"))
			player = new Sprite(user_id, true);
		else
			player = new Sprite(user_id, false);
		player.setPosition(user_x, user_y);
		player.setColor(R, G, B, 0.9);

		Timeline gameLoop = new Timeline();
		gameLoop.setCycleCount(Timeline.INDEFINITE);
		KeyFrame kf = new KeyFrame(Duration.seconds(0.04), // 0.017 -> 60 FPS
				new EventHandler<ActionEvent>() {
					public void handle(ActionEvent ae) {
						gc.clearRect(0, 0, WIDTH_SCREEN, HEIGHT_SCREEN);
						drawBackGround(gc);
						String[] players = user.getPlayersFromGame();
						for (int i = 0; i < players.length; i++) {
							String[] infoPlayer = players[i].split(",");
							if (infoPlayer[5].equals("T") && user.getPosPlayer() != i) {
								int user_x = Integer.parseInt(infoPlayer[0]);
								int user_y = Integer.parseInt(infoPlayer[1]);
								double user_w = Double.parseDouble(infoPlayer[2]);
								double user_h = Double.parseDouble(infoPlayer[3]);
								String user_id = infoPlayer[4];
								double R = Double.parseDouble(infoPlayer[6]);
								double G = Double.parseDouble(infoPlayer[7]);
								double B = Double.parseDouble(infoPlayer[8]);
								final Sprite player = new Sprite(user_id, true);
								player.setPosition(user_x, user_y);
								player.setMass(user_w, user_h);
								player.setLive(infoPlayer[5]);
								player.setColor(R, G, B, 0.9);
								player.render(gc);
							}

						}

						String[] food = user.getFoodFromGame();
						for (String info : food) {
							String[] infoFood = info.split(",");
							if (infoFood[5].equals("T")) {
								int food_x = Integer.parseInt(infoFood[0]);
								int food_y = Integer.parseInt(infoFood[1]);
								double food_w = Double.parseDouble(infoFood[2]);
								double food_h = Double.parseDouble(infoFood[3]);

								double R = Double.parseDouble(infoFood[6]);
								double G = Double.parseDouble(infoFood[7]);
								double B = Double.parseDouble(infoFood[8]);

								final Sprite aFood = new Sprite(infoFood[4], true);
								aFood.setPosition(food_x, food_y);
								aFood.setMass(food_w, food_h);
								aFood.setLive("T");
								aFood.setColor(R, G, B, 0.9);
								aFood.render(gc);
							}

						}

						String[] infoPlayer = user.getInfoPlayer().split(",");
						double user_w = Double.parseDouble(infoPlayer[2]);
						double user_h = Double.parseDouble(infoPlayer[3]);

						player.setLive(infoPlayer[5]);

						if (player.getLive()) {
							Point xy = MouseInfo.getPointerInfo().getLocation();
							double dx = xy.getX() - player.x();
							double dy = xy.getY() - player.y();

							double theta = Math.atan2(dy, dx);

							int nx = (int) (player.velocityX * Math.cos(theta));
							int ny = (int) (player.velocityY * Math.sin(theta));
							player.changePosition(nx, ny);
							player.setMass(user_w, user_h);
							player.render(gc);

							StringBuilder state = new StringBuilder();
							state.append(player.x());
							state.append(",");
							state.append(player.y());
							state.append(",");
							state.append(player.width());
							state.append(",");
							state.append(player.height());
							state.append(",");
							state.append(user.getNickName());
							state.append(",");
							if (player.getLive())
								state.append("T");
							else
								state.append("F");
							state.append(",");
							state.append(R);
							state.append(",");
							state.append(G);
							state.append(",");
							state.append(B);
							user.updatePlayer(state.toString());
						}
						// Print Scores
						String[] scores = user.getScores();
						gc.setFill(new Color(Color.BLACK.getRed(), Color.BLACK.getBlue(), Color.BLACK.getGreen(), 0.3));
						gc.fillRect(30, 30, 180, 250);
						for (int i = 0; i < scores.length; i++) {
							gc.setFill(Color.BLACK);
							String[] chain = scores[i].split(",");
							gc.fillText(chain[0] + "  " + chain[1], px[i], py[i]);
							gc.setStroke(Color.BLACK);
							gc.strokeText(chain[0] + "  " + chain[1], px[i], py[i]);
						} // finish scores
						chatPane.getChildren().clear();
						ArrayList<String> chat = user.getChat();
						for (int i = 0; i < user.getChat().size(); i++) {
							chatPane.getChildren().add(new Label(chat.get(i)));
						}
					}
				});
		gameLoop.getKeyFrames().add(kf);
		gameLoop.play();

	}

	public void drawBackGround(GraphicsContext gc) {
		int spaceX = 30;
		int spaceY = 30;
		gc.setStroke(new Color(Color.DARKGRAY.getRed(), Color.DARKGRAY.getBlue(), Color.DARKGRAY.getGreen(), 0.4));
		for (int i = 0; i < 64; i++) {
			gc.strokeLine(spaceX, 0, spaceX, 1080);
//			gc.fillRect(spaceX, 0, spaceX, 1080);
			spaceX += 30;
		}
		for (int i = 0; i < 36; i++) {
			gc.strokeLine(0, spaceY, 1920, spaceY);
//			gc.fillRect(0, spaceY, 1920, spaceY);
			spaceY += 30;
		}
	}

	public void setClient(Client client) {
		user = client;
	}

	public Client getClient() {
		return user;
	}
	
}
