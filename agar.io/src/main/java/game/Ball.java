package game;

import javafx.geometry.Rectangle2D;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

public class Ball {
	
	public static final String PLAYER="Player";
	public static final String FOOD="Food";

	public double[] colors;
	
	private String id;
	private int x;
	private int y;
	private double velocityX;
	private double velocityY;
	private double width;
	private double height;
	private boolean isAlive;
	private double score;

	public Ball(String id) {
		this.id = id;
		x = 0;
		y = 0;
		width=0;
		height=0;
		isAlive=true;
		colors = new double[3];
	}

	public void setAlive(boolean live) {
		isAlive=live;
	}
	
	public void setScore(double newScore) {
		score += newScore;
	}
	
	public double getScore() {
		return score;
	}
	
	public boolean getIsAlive() {
		return isAlive;
	}
	
	public void setMass(double width, double height) {
		this.width += (width/2);
		this.height += (height/2);
	}

	public void setPosition(int x, int y) {
		this.x = x;
		this.y = y;
	}

	public double x() {
		return x;
	}

	public double y() {
		return y;
	}
	
	public String getId() {
		return id;
	}

	public double width() {
		return width;
	}

	public double height() {
		return height;
	}

	public void setVelocity(double x, double y) {
		velocityX = x;
		velocityY = y;
	}

	public void addVelocity(double x, double y) {
		velocityX += x;
		velocityY += y;
	}

	public void changePosition(int x, int y) {
		x += x;
		y += y;
	}

	public void update(double time) {
		x += velocityX * time;
		y += velocityY * time;
	}

	public Rectangle2D getBoundary() {
		return new Rectangle2D(x-(width/2), y-(height/2), width, height);
	}

	public boolean intersects(Ball s) {
		return s.getBoundary().intersects(this.getBoundary());
	}

	public String toString() {
		if(isAlive) {
			return x+","+y+","+width+","+height+","+id+","+"T"+","+colors[0]+","+colors[1]+","+colors[2];	
		}
		else {
			return x+","+y+","+width+","+height+","+id+","+"F"+","+colors[0]+","+colors[1]+","+colors[2];
		}
	}
}
