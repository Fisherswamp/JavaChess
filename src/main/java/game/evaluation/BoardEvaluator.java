package main.java.game.evaluation;

import main.java.debug.Logger;
import main.java.game.Board;
import main.java.game.moves.ChessMove;
import main.java.game.pieces.Piece;
import main.java.management.Utility;
import main.java.ui.Window;
import org.json.simple.JSONArray;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

public class BoardEvaluator {

	private static final int[][] pawnPositionMap = loadPawnPositionMap();
	private static final double pawnMultiplierRatio = 200.0;

	public static final int pawnValue = 1;
	public static final int knightValue = 3;
	public static final int bishopValue = 3;
	public static final int rookValue = 5;
	public static final int queenValue = 9;
	public static final int kingValue = 1000;

	/**
	 *
	 * @param board Board to evaluate
	 * @param side Side who's turn it is for the board
	 * @param depth How deep the evaluation is
	 * @return Evaluation with a value; Positive means good for white, negative means good for black
	 */
	public static main.java.game.evaluation.Evaluation evaluateBoard(final Board board, final byte side, final int depth) {
		final List<ChessMove> legalMoves = board.getLegalMoves(side);
		if(legalMoves.isEmpty()) {
			//Either stalemate or checkmate
			if(board.isKingInCheck(side)) {
				return new main.java.game.evaluation.Evaluation(-side*1000, true, depth);
			} else {
				return new main.java.game.evaluation.Evaluation(0, true, depth);
			}
		}
		//TODO add 50 move rule and 3 exact same positions rule
//		if(board.getMovesSinceBoardStateChange() >= 100) {
//
//		}
		double boardValue = 0;
		for(byte y = 0; y < board.getBoardDimension(); y++) {
			for(byte x = 0; x < board.getBoardDimension(); x++){
				boardValue += evaluatePiece(board.getBoard(), x, y);
			}
		}
		return new main.java.game.evaluation.Evaluation(boardValue);
	}

	public static double evaluatePiece(final byte[][] board, final byte x, final byte y) {
		final int piece = Math.abs(board[x][y]);
		if(piece == Piece.emptyId){
			return 0;
		}
		if(piece == Piece.pawnId) {
			return evaluatePawn(board, x, y);
		}
		if(piece == Piece.rookId) {
			return evaluateRook(board, x, y);
		}
		if(piece == Piece.knightId) {
			return evaluateKnight(board, x, y);
		}
		if(piece == Piece.bishopId) {
			return evaluateBishop(board, x, y);
		}
		if(piece == Piece.queenId) {
			return evaluateQueen(board, x, y);
		}
		if(piece == Piece.kingId) {
			return evaluateKing(board, x, y);
		}

		return Double.NaN;
	}

	private static double evaluatePawn(final byte[][] board, final byte x, final byte y){
		int sign = Utility.getSign(board[x][y]);
		double value =  pawnValue;
		value += (sign == 1 ? pawnPositionMap[x][y] : pawnPositionMap[x][7-y])/pawnMultiplierRatio;
		return value*sign;
	}
	private static double evaluateBishop(final byte[][] board, final byte x, final byte y){
		return bishopValue * Utility.getSign(board[x][y]);
	}
	private static double evaluateRook(final byte[][] board, final byte x, final byte y){
		return rookValue * Utility.getSign(board[x][y]);
	}
	private static double evaluateKnight(final byte[][] board, final byte x, final byte y){
		return knightValue * Utility.getSign(board[x][y]);
	}
	private static double evaluateQueen(final byte[][] board, final byte x, final byte y){
		return queenValue * Utility.getSign(board[x][y]);
	}
	private static double evaluateKing(final byte[][] board, final byte x, final byte y){
		return kingValue * Utility.getSign(board[x][y]);
	}

	private static int[][] loadPawnPositionMap() {
		final int[][] pawnMap = new int[8][8];
		final JSONParser parser = new JSONParser();
		try(final InputStream inputStream = Window.class.getResourceAsStream("/evaluation/pawn.json")) {
			final JSONArray pawnArray = (JSONArray) parser.parse(new InputStreamReader(inputStream));
			int y = 0;
			for (JSONArray pawnRowArray : (Iterable<JSONArray>) pawnArray) {
				int x = 0;
				for (Long pawnValue : (Iterable<Long>) pawnRowArray) {
					pawnMap[x][y] = Math.toIntExact(pawnValue);
					x++;
				}
				y++;
			}
		} catch (IOException | NullPointerException | ParseException e) {
			e.printStackTrace();
		}
		return pawnMap;
	}


}
