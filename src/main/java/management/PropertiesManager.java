package main.java.management;

import main.java.ui.Window;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class PropertiesManager {
	public static Properties properties = initProperties();


	private static Properties initProperties() {
		properties = new Properties();

		try(InputStream inputStream = Window.class.getResourceAsStream("/chess.properties")) {
			properties.load(inputStream);
			return properties;
		} catch (IOException | NullPointerException e) {
			e.printStackTrace();
		}

		return null;
	}

}
