package main.game.moves;

/**
 * @author Itai Fish
 * Represents a real-world move, with a Piece moving, and where it moves to
 */
public class ChessMove {

	private Move move;
	private byte[] positionOfPieceToMove;
	private byte pieceId;
	private byte moveAmount;

	public ChessMove(final Move move, final byte[] positionOfPieceToMove, final byte pieceId, final byte moveAmount) {
		this.move = move;
		this.positionOfPieceToMove = positionOfPieceToMove;
		this.pieceId = pieceId;
		this.moveAmount = moveAmount;
	}

	public String toString() {
		return pieceId + ": " + (positionOfPieceToMove[0] + move.getDeltaPosition()[0]) + ", " + (positionOfPieceToMove[1] + move.getDeltaPosition()[1]);
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

	public byte getMoveAmount() {
		return moveAmount;
	}
}
