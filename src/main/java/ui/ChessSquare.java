package main.java.ui;

import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.scene.layout.Pane;

import java.awt.*;

public class ChessSquare extends Pane implements EventHandler {

	private final int x, y;

	public ChessSquare(final int x, final int y, final int size, final String cssStyle) {
		this.x = x;
		this.y = y;
		this.setStyle(cssStyle);
		this.setPrefSize(size, size);
	}

	public int getX() {
		return x;
	}

	public int getY() {
		return y;
	}

	@Override
	public void handle(Event event) {

	}
}
