package test.game;

import main.game.Board;
import main.game.Game;
import main.game.moves.ChessMove;

import java.util.List;
import java.util.stream.Collectors;

public class BoardTest {
	public static void main(String[] args) {
		Game game = new Game();
		List<ChessMove> legalMoves = game.getLegalMoves();
		game.move(legalMoves.get((int) (Math.random() * legalMoves.size())));
		System.out.println(game.getLegalMoves().toString());
	}
}
