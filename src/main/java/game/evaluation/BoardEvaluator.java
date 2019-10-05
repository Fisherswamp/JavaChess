package main.java.game.evaluation;

import main.java.game.Board;
import main.java.game.pieces.Piece;

public class BoardEvaluator {

	public static final int pawnValue = 1;
	public static final int knightValue = 3;
	public static final int bishopValue = 3;
	public static final int rookValue = 5;
	public static final int queenValue = 9;
	public static final int kingValue = 1000;

	public static double evaluateBoard(final Board board) {
		double boardValue = 0;
		for(byte y = 0; y < board.getBoardDimension(); y++) {
			for(byte x = 0; x < board.getBoardDimension(); x++){
				boardValue += evaluatePiece(board.getBoard(), x, y);
			}
		}
		return boardValue;
	}

	private static double evaluatePiece(final byte[][] board, final byte x, final byte y) {
		final int piece = Math.abs(board[x][y]);
		if(piece == Piece.emptyId){
			return 0;
		}
		if(piece == Piece.pawnId) {
			return evaluatePawn(board, x, y);
		}

		return 0;
	}

	private static double evaluatePawn(final byte[][] board, final byte x, final byte y){
		return pawnValue;
	}
	private static double evaluateBishop(final byte[][] board, final byte x, final byte y){
		return bishopValue;
	}
	private static double evaluateRook(final byte[][] board, final byte x, final byte y){
		return rookValue;
	}
	private static double evaluateKnight(final byte[][] board, final byte x, final byte y){
		return knightValue;
	}
	private static double evaluateQueen(final byte[][] board, final byte x, final byte y){
		return queenValue;
	}
	private static double evaluateKing(final byte[][] board, final byte x, final byte y){
		return kingValue;
	}


}
