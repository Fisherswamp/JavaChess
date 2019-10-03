package main.java.game.moves;

import main.java.management.Utility;
import main.java.game.pieces.PieceFactory;

/**
 * @author Itai Fish
 * Represents a real-world move, with a Piece moving, and where it moves to
 */
public class ChessMove {

	private final Move move;
	private final byte[] positionOfPieceToMove;
	private final byte pieceId;
	private final byte moveAmount;
	private final int[] newPosition;

	public ChessMove(final Move move, final byte[] positionOfPieceToMove, final byte pieceId, final byte moveAmount) {
		this.move = move;
		this.positionOfPieceToMove = positionOfPieceToMove;
		this.pieceId = pieceId;
		this.moveAmount = moveAmount;
		this.newPosition = calcNewPosition();
	}

	private int[] calcNewPosition() {
		byte[] delta = move.getDeltaPosition().clone();
		if(Math.abs(delta[0]) == Move.infinity){
			delta[0] = (byte) (Math.abs(moveAmount) * Utility.getSign(delta[0]));
		}
		if(Math.abs(delta[1]) == Move.infinity){
			delta[1] = (byte) (Math.abs(moveAmount) * Utility.getSign(delta[1]));
		}
		final int oldX = positionOfPieceToMove[0];
		final int oldY = positionOfPieceToMove[1];
		return new int[]{ oldX + delta[0], oldY + delta[1]};
	}

	public String toString() {
		char coordsToLetter[] = {'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h'};

		return PieceFactory.getPieceName(pieceId) + ": " + (char)('a' + newPosition[0]) + "" + (1 + newPosition[1]);
	}

	public Move getMove() {
		return move;
	}

	public byte[] getPositionOfPieceToMove() {
		return positionOfPieceToMove;
	}

	public byte getPieceId() {
		return pieceId;
	}

	public int[] getNewPosition() {
		return newPosition;
	}

	public byte getMoveAmount() {
		return moveAmount;
	}


}
