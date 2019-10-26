package main.java.ui;

import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import main.java.management.PropertiesManager;

import java.io.IOException;
import java.util.Properties;

public class Window extends Application {

	private static Controller controller = new Controller();

	public static void main(String[] args) throws IOException {
		launch(args);
	}


	@Override
	public void start(Stage stage) {
		final Properties properties = PropertiesManager.properties;
		stage.setTitle(properties.getProperty("WINDOW_NAME"));
		final int height = Integer.parseInt(properties.getProperty("WINDOW_HEIGHT"));
		final int width = Integer.parseInt(properties.getProperty("WINDOW_WIDTH"));
		final int squareSize = (int)(((height + width) / 2.0) / 12.0);
		final String[] squareCss = new String[]{
				"-fx-background-color: white;",
				"-fx-background-color: gray;"
		};
		int index = 0;
		final BorderPane mainUIPane = new BorderPane();
		final GridPane chessBoardPane = new GridPane();
		ChessSquare[][] squares = new ChessSquare[8][8];
		for(int y = 0; y < 8; y++){
			for(int x = 0; x < 8; x++){
				final ChessSquare square = new ChessSquare(x, 7-y, squareSize, squareCss[index++], controller, null);
				if(index > 1) {
					index = 0;
				}
				chessBoardPane.add(square, x, y);
				squares[x][7-y] = square;
			}
			if(++index > 1) {
				index = 0;
			}
		}
		controller.init(squares,true, true);
		BorderPane.setAlignment(chessBoardPane, Pos.CENTER);
		mainUIPane.setCenter(chessBoardPane);

		final Scene scene = new Scene(mainUIPane, width, height);
		stage.setScene(scene);
		stage.show();
	}

}
