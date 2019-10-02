package main.java.ui;

import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.scene.control.Button;


public class ChessSquare extends Button implements EventHandler<ActionEvent> {

	private final int x, y;
	private Controller controller;

	public ChessSquare(final int x, final int y, final int size, final String cssStyle, final Controller controller) {
		this.x = x;
		this.y = y;
		this.controller = controller;
		this.setStyle(cssStyle);
		this.setPrefSize(size, size);
		this.setOnAction(this);
	}

	public int getX() {
		return x;
	}

	public int getY() {
		return y;
	}

	@Override
	public void handle(ActionEvent actionEvent) {
		System.out.println(x + " " + y);
	}
}
