package main.game.pieces;

import main.game.evaluation.BoardEvaluator;
import main.game.moves.DoublePawnMove;
import main.game.moves.EnPassantMove;
import main.game.moves.ExclusiveCaptureMove;
import main.game.moves.Move;
import main.game.moves.PromotionMove;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PieceFactory {

	private static Map<Byte, Piece> pieceMap = initializePieces();

	private static Map<Byte, Piece> initializePieces() {
		final Map<Byte, Piece> pieceMap = new HashMap<>();
		final byte inf = Move.infinity;
		final List<Move> knightMoves = Arrays.asList(
				new Move(new byte[]{-2, -1}),
				new Move(new byte[]{-2, 1}),
				new Move(new byte[]{2, -1}),
				new Move(new byte[]{2, 1}),
				new Move(new byte[]{-1, -2}),
				new Move(new byte[]{-1, 2}),
				new Move(new byte[]{1, -2}),
				new Move(new byte[]{1, 2})
		);
		final List<Move> bishopMoves = Arrays.asList(
				new Move(new byte[]{-inf, -inf}),
				new Move(new byte[]{-inf, inf}),
				new Move(new byte[]{inf, -inf}),
				new Move(new byte[]{inf, inf})
		);
		final List<Move> whitePawnMoves = Arrays.asList(
				new Move(new byte[]{0, 1}),
				new DoublePawnMove(new byte[]{0, 2}),
				new ExclusiveCaptureMove(new byte[]{-1, 1}),
				new ExclusiveCaptureMove(new byte[]{1, 1}),
				new EnPassantMove(new byte[]{-1, 1}, new byte[]{-1, 0}),
				new EnPassantMove(new byte[]{1, 1}, new byte[]{1, 0})
		);
		final List<Move> blackPawnMoves = Arrays.asList(
				new Move(new byte[]{0, -1}),
				new DoublePawnMove(new byte[]{0, -2}),
				new ExclusiveCaptureMove(new byte[]{-1, -1}),
				new ExclusiveCaptureMove(new byte[]{1, -1}),
				new EnPassantMove(new byte[]{-1, -1}, new byte[]{-1, 0}),
				new EnPassantMove(new byte[]{1, -1}, new byte[]{1, 0})
		);
		final List<Move> rookMoves = Arrays.asList(
				new Move(new byte[]{inf, 0}),
				new Move(new byte[]{-inf, 0}),
				new Move(new byte[]{0, -inf}),
				new Move(new byte[]{0, inf})
		);
		final List<Move> kingMoves = Arrays.asList(
				new Move(new byte[]{-1, -1}),
				new Move(new byte[]{-1, 1}),
				new Move(new byte[]{1, -1}),
				new Move(new byte[]{1, 1}),
				new Move(new byte[]{1, 0}),
				new Move(new byte[]{-1, 0}),
				new Move(new byte[]{0, -1}),
				new Move(new byte[]{0, 1}),
				new Move(new byte[]{-2, 0}, true),
				new Move(new byte[]{2, 0}, true)
		);
		final List<Move> queenMoves = new ArrayList<>(rookMoves);
		queenMoves.addAll(bishopMoves);

		pieceMap.put((byte) 0, new Piece(Piece.emptyId, 0, new ArrayList<>(), "Empty"));//put the empty piece with no possible moves
		pieceMap.put(Piece.pawnId, new Piece(Piece.pawnId, BoardEvaluator.pawnValue, whitePawnMoves, "WhitePawn"));
		pieceMap.put((byte) -Piece.pawnId, new Piece((byte) -Piece.pawnId, -BoardEvaluator.pawnValue, blackPawnMoves, "BlackPawn"));
		Arrays.asList(-1, 1).forEach(
				side -> {
					String sideStr = side == 1 ? "White" : "Black";
					pieceMap.put((byte) (side * Piece.knightId),
						new Piece((byte) (side * Piece.knightId), side * BoardEvaluator.knightValue, knightMoves, sideStr+"Knight"));
					pieceMap.put((byte) (side * Piece.bishopId),
							new Piece((byte) (side * Piece.bishopId), side * BoardEvaluator.bishopValue, bishopMoves, sideStr+"Bishop"));
					pieceMap.put((byte) (side * Piece.rookId),
							new Piece((byte) (side * Piece.rookId), side * BoardEvaluator.rookValue, rookMoves, sideStr+"Rook"));
					pieceMap.put((byte) (side * Piece.queenId),
							new Piece((byte) (side * Piece.queenId), side * BoardEvaluator.queenValue, queenMoves, sideStr+"Queen"));
					pieceMap.put((byte) (side * Piece.kingId),
							new Piece((byte) (side * Piece.kingId), side * BoardEvaluator.kingValue, kingMoves, sideStr+"King"));
				}

		);

		return pieceMap;
	}


	public static List<Move> getPieceMoves(final byte id) {
		if(!pieceMap.containsKey(id)){
			return null;
		}
		return pieceMap.get(id).getMoves();
	}

	public static String getPieceName(final byte id) {
		if(!pieceMap.containsKey(id)){
			return null;
		}
		return pieceMap.get(id).getName();
	}
}
