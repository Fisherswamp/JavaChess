package main.java.game.pieces;

import javafx.scene.image.Image;
import main.java.debug.Logger;
import main.java.game.evaluation.BoardEvaluator;
import main.java.game.moves.DoublePawnMove;
import main.java.game.moves.EnPassantMove;
import main.java.game.moves.ExclusiveCaptureMove;
import main.java.game.moves.Move;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PieceFactory {

	private static final Map<Byte, Map<String, Image>> idAndSkinToImageMap = new HashMap<>();
	private static final Map<Byte, Piece> pieceMap = initializePieces();

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
		//After pieceMap is initialized, set the keys to idAndSkinToImageMap
		pieceMap.keySet().forEach(id -> idAndSkinToImageMap.put(id, new HashMap<>()));
		//
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

	public static Piece getPiece(final byte id) {
		return pieceMap.get(id);
	}

	public static Image getImage(final byte id, final String skinName) {
		final Map<String, Image> skinToImageMap = idAndSkinToImageMap.get(id);
		if(skinToImageMap != null){
			final Image pieceImage = skinToImageMap.get(skinName);
			if(pieceImage != null){
				return  pieceImage;
			} else {
				final String url = "main/resources/images/skins/" + skinName + "/" + getPieceName(id) + ".png";
				try(final InputStream inputStream = PieceFactory.class.getClassLoader().getResourceAsStream(url)) {
					if(inputStream == null){
						return null;
					}
					final Image loadedPieceImage = new Image(inputStream);
					skinToImageMap.put(skinName, loadedPieceImage);
					return loadedPieceImage;

				} catch (IOException e) {
					Logger.log("Error reading url: " + e);
					return null;
				}
			}
		}
		return null;
	}
}
