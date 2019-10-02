package main.java.ui;

import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class Window extends Application {

	private static Properties properties = new Properties();
	private static Controller controller = new Controller();

	public static void main(String[] args) throws IOException {
		properties = new Properties();

		try(InputStream inputStream = Window.class.getClassLoader().getResourceAsStream("main/resources/chess.properties")) {
			properties.load(inputStream);
		} catch (IOException  | NullPointerException e) {
			e.printStackTrace();
		}
		launch(args);
	}


	@Override
	public void start(Stage stage) throws Exception {
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
		for(int y = 0; y < 8; y++){
			for(int x = 0; x < 8; x++){
				final ChessSquare square = new ChessSquare(x, 7-y, squareSize, squareCss[index++], controller);
				if(index > 1) {
					index = 0;
				}
				chessBoardPane.add(square, x, y);
			}
			if(++index > 1) {
				index = 0;
			}
		}
		BorderPane.setAlignment(chessBoardPane, Pos.CENTER);
		mainUIPane.setCenter(chessBoardPane);

		final Scene scene = new Scene(mainUIPane, width, height);
		stage.setScene(scene);
		stage.show();
	}

}
