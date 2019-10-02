package main.java.ui;

import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class Window extends Application {

	private static Properties properties = new Properties();

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
		final Button button = new Button();
		button.setText("Click Me!");

		final StackPane layout = new StackPane();
		layout.getChildren().add(button);

		final Scene scene = new Scene(layout, 300, 250);
		stage.setScene(scene);
		stage.show();
	}

}
