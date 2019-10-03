package main.java.ui;

import javafx.scene.image.Image;
import main.java.debug.Logger;
import main.java.management.PropertiesManager;
import main.java.management.Utility;
import main.java.game.Board;
import main.java.game.Game;
import main.java.game.moves.ChessMove;
import main.java.game.pieces.Piece;
import main.java.game.pieces.PieceFactory;

import java.util.List;
import java.util.stream.Collectors;

public class Controller {

	private boolean isInitialized;
	private Game game;
	private ChessSquare selected;
	private ChessSquare[][] squares;
	private boolean playerControlsWhite, playerControlsBlack;

	public Controller(){
		isInitialized = false;
		selected = null;
	}

	public void init(final ChessSquare[][] squares, final boolean playerControlsWhite, final boolean playerControlsBlack) {
		if(!isInitialized) {
			game = new Game();
			isInitialized = true;
			this.squares = squares;
			this.playerControlsWhite = playerControlsWhite;
			this.playerControlsBlack = playerControlsBlack;
			updateChessSquareImages();
		}
	}

	public void reset() {
		game = new Game();
	}

	public void handleBoardClick(final int boardX, final int boardY) {
		if(!canPlayerInteract()){
			return;
		}
		Logger.log("Click at " + boardX + ", " + boardY);
		final Board board = game.getCurrentBoardState();
		final Piece pieceClickedOn = getPiece(boardX, boardY);
		//if you click on another of your own pieces
		if(Utility.getSign(pieceClickedOn.getId()) == game.getTurn()) {
			if(selected != null){
				selected.setSelected(false);
			}
			selected = squares[boardX][boardY];
			selected.setSelected(true);
		} else if(selected != null) {
			Logger.log("Attempting to move");
			List<ChessMove> relevantMoves = game.getLegalMoves()
					.stream()
					.filter(chessMove -> {
						final byte[] startPosition = chessMove.getPositionOfPieceToMove();
						final int[] movePosition = chessMove.getNewPosition();
						return (movePosition[0] == boardX && movePosition[1] == boardY)
								&& (startPosition[0] == selected.getX() && startPosition[1] == selected.getY());
					}).collect(Collectors.toList());
			if(relevantMoves.size() > 0){
				if(game.move(relevantMoves.get(0))){
					selected.setSelected(false);
					selected = null;
					updateChessSquareImages();
				} else {
					throw new RuntimeException("This should never happen");
				}
			}
		}
	}

	private void updateChessSquareImages() {
		for(var row : squares) {
			for(ChessSquare square : row) {
				final byte currentPiece = game.getCurrentBoardState().getPiece(square.getX(), square.getY());
				if(currentPiece == Piece.emptyId) {
					square.setImage(null);
				} else {
					final Image squarePieceImage = PieceFactory.getImage(currentPiece, PropertiesManager.properties.getProperty("DEFAULT_SKIN"));
					if (squarePieceImage != null) {
						square.setImage(squarePieceImage);
					}
				}
			}
		}
	}

	private Piece getPiece(int boardX, int boardY) {
		return PieceFactory.getPiece(game.getCurrentBoardState().getPiece(boardX, boardY));
	}

	private boolean canPlayerInteract() {
		if(game.getTurn() == 1) {
			return playerControlsWhite;
		}else{
			return playerControlsBlack;
		}
	}

}
