package test.game;

import main.game.Board;
import main.game.moves.ChessMove;

import java.util.stream.Collectors;

public class BoardTest {
	public static void main(String[] args) {
		Board board = new Board();
		System.out.println(board.getLegalMoves((byte) 1).toString());
	}
}
