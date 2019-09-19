package test.game;

import main.game.Board;
import main.game.Game;
import main.game.moves.ChessMove;

import java.util.List;
import java.util.stream.Collectors;

public class BoardTest {
	public static void main(String[] args) {
		Game game = new Game();
		var whiteKingPosition  = game.getCurrentBoardState().getKingPosition(1);
		var blackKingPosition  = game.getCurrentBoardState().getKingPosition(-1);
		System.out.println("White King: " + whiteKingPosition[0] + ", " + whiteKingPosition[1]);
		System.out.println("Black King: " + blackKingPosition[0] + ", " + blackKingPosition[1]);
		List<ChessMove> legalMoves = game.getLegalMoves();
		game.move(legalMoves.get((int) (Math.random() * legalMoves.size())));
		//todo: debug why this is empty for the blacks
		legalMoves = game.getLegalMoves();
		System.out.println(legalMoves.toString());
	}
}
