package main.java.ui;

import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;




public class ChessSquare extends Button implements EventHandler<ActionEvent> {

	private static final String SELECTED_CSS = "-fx-background-color: #8FBC8F;";

	private final int x, y;
	private Controller controller;
	private final ImageView imageView;
	private boolean isSelected;
	private final String cssStyle;

	public ChessSquare(final int x, final int y, final int size, final String cssStyle, final Controller controller, final Image pieceImage) {
		this.x = x;
		this.y = y;
		this.controller = controller;
		this.isSelected = false;
		this.cssStyle = cssStyle;
		this.setStyle(cssStyle);
		this.setPrefSize(size, size);
		this.setMinSize(size, size);
		this.setMaxSize(size, size);
		this.setOnAction(this);
		this.imageView = new ImageView();
		imageView.setPreserveRatio(true);
		imageView.fitWidthProperty().bind(this.widthProperty().multiply(0.8));
		imageView.fitHeightProperty().bind(this.heightProperty().multiply(0.8));
		if(pieceImage != null) {
			imageView.setImage(pieceImage);
		}
		this.setGraphic(imageView);
	}

	public void setSelected(boolean selected) {
		this.isSelected = selected;
		if(isSelected){
			this.setStyle(SELECTED_CSS);
		} else {
			this.setStyle(cssStyle);
		}
	}

	public void setImage(Image pieceImage) {
		imageView.setImage(pieceImage);
	}

	public int getX() {
		return x;
	}

	public int getY() {
		return y;
	}

	@Override
	public void handle(ActionEvent actionEvent) {
		controller.handleBoardClick(x, y);
	}
}
