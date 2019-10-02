package main.java.ui;

import main.java.game.Game;

public class Controller {

	private boolean isInitialized;
	private Game game;

	public Controller(){
		isInitialized = false;
	}

	public void init() {
		if(!isInitialized) {
			game = new Game();
			isInitialized = true;
		}
	}

	public void reset() {
		game = new Game();
	}

}
