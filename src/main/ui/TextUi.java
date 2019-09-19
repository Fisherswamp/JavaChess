package main.ui;

import main.game.Board;
import main.game.pieces.Piece;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Itai
 * @description A static class for displaying a board as text
 */
public class TextUi {
	public static Map<Byte, String> idToIconMap = initializeMap();

	public static Map<Byte, String> initializeMap() {
		final Map<Byte, String> idMap = new HashMap<>();
		idMap.put(Piece.emptyId, null);
		idMap.put(Piece.pawnId, "♙");
		idMap.put((byte) -Piece.pawnId, "♟");
		idMap.put(Piece.knightId, "♘");
		idMap.put((byte) -Piece.knightId, "♞");
		idMap.put(Piece.rookId, "♖");
		idMap.put((byte) -Piece.rookId, "♜");
		idMap.put(Piece.bishopId, "♗");
		idMap.put((byte) -Piece.bishopId, "♝");
		idMap.put(Piece.queenId, "♕");
		idMap.put((byte) -Piece.queenId, "♛");
		idMap.put(Piece.kingId, "♔");
		idMap.put((byte) -Piece.kingId, "♚");
		return idMap;
	}

	public static String stringifyBoard(final Board board){
		final StringBuilder boardStr = new StringBuilder();
		for(int y = board.getBoardDimension() - 1; y >= 0; y--){
			for(int x = 0; x < board.getBoardDimension(); x++) {
				String icon = idToIconMap.get(board.getPiece(x, y));
				if(icon == null) {
					icon = ((y & 1) + (x & 1)) != 1 ? "■" : "□";
				}
				boardStr.append(icon).append("\t");
			}
			boardStr.append("\n");
		}

		return boardStr.toString();
	}



}
