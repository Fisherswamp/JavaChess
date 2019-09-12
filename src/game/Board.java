package game;

import debug.Logger;
import debug.Utility;
import game.moves.EnPassantMove;
import game.moves.Move;
import game.pieces.Piece;
import jdk.jshell.execution.Util;

import java.util.Arrays;

public class Board {
	/* a 2d array of bytes representing the board*/
	private final byte[][] board;

	private static final byte defaultMetaData = (byte) ((positionToByte(new byte[]{4, 7}) << 6) + (positionToByte(new byte[]{4, 0})) << 3);

	private static final int boardDimension = 8;
	/* metadata for the board
	* 								 8 7 6   5 4 3   2   1   0
	* [0 0 0 0 0 0] [0 0 0] [0 0 0] [0 0 0] [0 0 0] [0] [0] [0]
	* [U T S R Q P] [O N M] [L K J] [I H G] [F E D] [C] [B] [A]
	* A: Whether the King's Rook has moved
	* B: Whether the King has moved
	* C: Whether the Queen's Rook has moved
	* DEF: White King's X Position
	* GHI: White King's Y Position
	* JKL: Black King's X Position
	* MNO: Black King's Y Position
	* PQRSTU: Number of moves on board since a Piece capture or pawn move
	*/
	private final int boardMetaData;

	public  Board() {
		this(initializedBoard());
	}

	private Board(final byte[][] copyOf) {
		this(copyOf, defaultMetaData);
	}

	private Board(final byte[][] copyOf, final int boardMetaData) {
		this.board = copyOf;
		this.boardMetaData = boardMetaData;
	}

	public Board move(final Move move, final byte[] positionOfPieceToMove, final byte pieceIdValidation){
		if(canApplyMove(move, positionOfPieceToMove, pieceIdValidation)) {
			return blitheMove(move, positionOfPieceToMove);
		}
		return null;
	}
	//TODO: Update boardmetadata after a move
	public Board blitheMove(final Move move, final byte[] positionOfPieceToMove) {
		final byte[][] newBoard = board.clone();
		final byte[] delta = move.getDeltaPosition();
		final byte directionX = Utility.getSign(delta[0]);
		final int oldX = positionOfPieceToMove[0];
		final int oldY = positionOfPieceToMove[1];
		final int newX = oldX + delta[0];
		final int newY = oldY + delta[1];
		newBoard[newX][newY] = newBoard[oldX][oldY];
		newBoard[oldX][oldY] = Piece.emptyId;
		if(move.isCastle()) { //If castle, must also move the rook
			final int rookOldX = directionX == -1 ? 0 : getBoardDimension()-1;
			final int rookNewX = newX + -directionX;
			final int rookY = newY;
			newBoard[rookNewX][rookY] = newBoard[rookOldX][rookY];
			newBoard[rookOldX][rookY]= Piece.emptyId;
		} else if(move.isEnPassantCapture()) {
			final EnPassantMove enPassantMove = (EnPassantMove) move;
			final byte[] pawnCapturePosition = enPassantMove.getPawnCapturePosition();
			newBoard[pawnCapturePosition[0]][pawnCapturePosition[1]] = Piece.emptyId;
		}
		return new Board(newBoard, boardMetaData);
	}

	public boolean canApplyMove(final Move move, final byte[] positionOfPieceToMove, final byte pieceIdValidation) {
		final byte oldX = positionOfPieceToMove[0];
		final byte oldY = positionOfPieceToMove[1];
		final byte[] delta = move.getDeltaPosition();
		final byte directionX = Utility.getSign(delta[0]);
		final int newX = oldX + delta[0];
		final int newY = oldY + delta[1];
		if(newX < 0 || newX >= getBoardDimension()){
			return false;
		}
		if(newY < 0 || newY >= getBoardDimension()){
			return false;
		}
		byte pieceValue = board[oldX][oldY];
		byte moveToValue = board[newX][newY];
		if(pieceValue != pieceIdValidation){
			Logger.log("Did not find Piece ID: " + pieceIdValidation +
					" at position [" + oldX + " , " + oldY + "] instead found ID: " + pieceValue);
			return false;
		}
		//Cant take piece with same value as itself
		if(Utility.getSign(pieceValue) == Utility.getSign(moveToValue)) {
			return false;
		}

		if(move.isCastle()) {
			if (kingsMoved()) {
				return false;
			}
			if (directionX == -1) {
				if (queensRookMoved()) {
					return false;
				}
			} else {
				if (kingsRookMoved()) {
					return false;
				}
			}
			//check if castling through, or out of check (not allowed). into check will be checked at the end
			byte xPosition = oldX;
			while (xPosition != newX) {
				if (isPositionInChecK(new byte[]{xPosition, oldY})) {
					return false;
				}
				xPosition += directionX;
			}
		}
		if(move.isExclusiveCaptureMove()) {
			//Must be a capture -> cant move to empty square
			if(moveToValue == Piece.emptyId) {
				return false;
			}
		}
		//can only double pawn move on starting square.
		if(move.isDoublePawnMove() && oldX != 1 && oldX != 6){
			return false;
		}

		//TODO: Move and then check if king can be taken
		//TODO: Store position of king

		return true;
	}

	public boolean isPositionInChecK(final byte[] position){
		return false;
	}

	public int getBoardDimension() {
		return board.length;
	}

	public boolean kingsRookMoved() {
		return (boardMetaData & 1) == 1;
	}

	public boolean kingsMoved() {
		return ((boardMetaData >> 1) & 1) == 1;
	}

	public boolean queensRookMoved() {
		return ((boardMetaData >> 2) & 1) == 1;
	}

	public byte[] getKingPosition(final int side) {
		byte[] kingPosition = new byte[2];
		final int startingOffset = side == 1 ? 3 : 9;
		kingPosition[0] = (byte) ((boardMetaData >> startingOffset) & 7);
		kingPosition[1] = (byte) ((boardMetaData >> (startingOffset + 3)) & 7);
		return kingPosition;
	}

	public byte getMovesSinceBoardstateChange() {
		return (byte) ((boardMetaData >> 15) & 63);
	}

	public int createMetaData(final byte kingsRookMoved, final byte kingMoved, final byte queensRookMoved,
							 final byte whiteKingPosition, final byte blackKingPosition, final byte movesSinceBoardstateChange) {
		return  (kingsRookMoved + (kingMoved << 1) + (queensRookMoved << 2) + (whiteKingPosition << 3) + (blackKingPosition << 9) + (movesSinceBoardstateChange << 15));
	}

	private byte getKingPositionAsByte(final int side) {
		final int startingOffset = side == 1 ? 3 : 9;
		return (byte) ((boardMetaData >> startingOffset) & 63);
	}

	private static byte positionToByte(byte[] position) {
		return (byte) ((position[1] << 3) + (position[0]));
	}

	private static byte[][] initializedBoard(){
		byte[][] newBoard = new byte[boardDimension][boardDimension];
		Arrays.asList(-1, 1).forEach(
				side -> {
					//fill pawns
					final int yPPos = side == 1 ? 1 : 6;
					for(int i = 0; i < boardDimension; i++){
						newBoard[i][yPPos] = (byte) (Piece.pawnId * side);
					}
					//Fill Pieces
					final int yPos = side == 1 ? 0 : 7;
					newBoard[0][yPos] = (byte) (Piece.rookId * side);
					newBoard[7][yPos] = (byte) (Piece.rookId * side);
					newBoard[1][yPos] = (byte) (Piece.knightId * side);
					newBoard[6][yPos] = (byte) (Piece.knightId * side);
					newBoard[2][yPos] = (byte) (Piece.bishopId * side);
					newBoard[5][yPos] = (byte) (Piece.bishopId * side);
					newBoard[3][yPos] = (byte) (Piece.queenId * side);
					newBoard[4][yPos] = (byte) (Piece.kingId * side);
				}
		);
		return newBoard;
	}

}
