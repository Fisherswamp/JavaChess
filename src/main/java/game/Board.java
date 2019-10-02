package main.java.game;

import main.java.debug.Logger;
import main.java.debug.Utility;
import main.java.game.moves.ChessMove;
import main.java.game.moves.EnPassantMove;
import main.java.game.moves.ExclusiveCapturePromotionMove;
import main.java.game.moves.Move;
import main.java.game.moves.PromotionMove;
import main.java.game.pieces.Piece;
import main.java.game.pieces.PieceFactory;

import static main.java.debug.Utility.boolToByte;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Board {
	/* a 2d array of bytes representing the board*/
	private final byte[][] board;

	private static final int defaultMetaData = (positionToByte(new byte[]{4, 7}) << 9) + (positionToByte(new byte[]{4, 0}) << 3);

	private static final int boardDimension = 8;
	/* metadata for the board
	* 								 8 7 6   5 4 3   2   1   0
	* [0] [0] [0] [0 0 0 0 0 0] [0 0 0] [0 0 0] [0 0 0] [0 0 0] [0] [0] [0]
	* [X] [W] [V] [U T S R Q P] [O N M] [L K J] [I H G] [F E D] [C] [B] [A]
	* A: Whether the White King's Rook has moved
	* B: Whether the White King has moved
	* C: Whether the White Queen's Rook has moved
	* DEF: White King's X Position
	* GHI: White King's Y Position
	* JKL: Black King's X Position
	* MNO: Black King's Y Position
	* PQRSTU: Number of moves on board since a Piece capture or pawn move
	* V: Whether the Black King's Rook has moved
	* W: Whether the Black King has Moved
	* X: Whether the black Queen's rook has moved
	*/
	private final int boardMetaData;

	public Board() {
		this(initializedBoard());
	}

	private Board(final byte[][] copyOf) {
		this(copyOf, defaultMetaData);
	}

	private Board(final byte[][] copyOf, final int boardMetaData) {
		this.board = copyOf;
		this.boardMetaData = boardMetaData;
	}

	public Board move(final ChessMove chessMove, final boolean checkMove){
		if(checkMove){
			if(canApplyMove(chessMove)) {
				return blitheMove(chessMove);
			}
			return null;
		}
		return blitheMove(chessMove);
	}

	public List<ChessMove> getLegalMoves(byte side){
		final List<ChessMove> legalMoves = new ArrayList<>();
		for(int y = 0; y < board.length; y++){
			for(int x = 0; x < board[y].length; x++){
				final byte piece = board[x][y];
				if(Utility.getSign(piece) != side){
					continue;
				}
				final List<Move> possibleMoves = PieceFactory.getPieceMoves(piece);
				final byte[] position = new byte[]{(byte) x, (byte) y};
				getChessMovesFromMoveList(possibleMoves, position, piece).forEach(chessMove -> {
					if(canApplyMove(chessMove)) {
						legalMoves.add(chessMove);
					}
				});
			}
		}
		return legalMoves;
	}

	private List<ChessMove> getChessMovesFromMoveList(final List<Move> moveList, final byte[] position, final byte pieceId){
		List<ChessMove> chessMoveList = new ArrayList<>();
		moveList.forEach(move -> {
			final byte[] delta = move.getDeltaPosition();
			byte[] direction = {0, 0};
			boolean infinityExists = false;
			if(Math.abs(delta[0]) == Move.infinity) {
				direction[0] = Utility.getSign(delta[0]);
				infinityExists = true;
			}
			if(Math.abs(delta[1]) == Move.infinity) {
				direction[1] = Utility.getSign(delta[1]);
				infinityExists = true;
			}
			if(!infinityExists) {
				if(Math.abs(pieceId) == Piece.pawnId &&
						position[1] == (Utility.getSign(pieceId) == 1 ? 6 : 1)) {
					for(byte promotion : new byte[]{Piece.bishopId, Piece.knightId, Piece.rookId, Piece.queenId}) {
						//copy move over into new move type
						if(move.isExclusiveCaptureMove()){
							chessMoveList.add(new ChessMove(new ExclusiveCapturePromotionMove(move.getDeltaPosition(), promotion), position, pieceId, (byte) -1));
						}else {
							chessMoveList.add(new ChessMove(new PromotionMove(move.getDeltaPosition(), promotion), position, pieceId, (byte) -1));
						}
					}
				} else {
					chessMoveList.add(new ChessMove(move, position, pieceId, (byte) -1));
				}
			} else {
				byte[] currentPosition = {(byte) (position[0] + direction[0]), (byte) (position[1] + direction[1])};
				if(!isWithinBoard(currentPosition[0], currentPosition[1])){
					return;
				}
				byte currentPiece = board[currentPosition[0]][currentPosition[1]];
				byte amount = 1;
				while (currentPiece == Piece.emptyId){
					chessMoveList.add(new ChessMove(move, position, pieceId, amount));
					amount++;
					currentPosition[0] += direction[0];
					currentPosition[1] += direction[1];
					if(!isWithinBoard(currentPosition[0], currentPosition[1])){
						return;
					}
					currentPiece = board[currentPosition[0]][currentPosition[1]];
				}
				if(Utility.getSign(currentPiece) == -Utility.getSign(pieceId)){//can end on capture
					chessMoveList.add(new ChessMove(move, position, pieceId, amount));
				}
			}
		});
		return chessMoveList;
	}

	private Board blitheMove(final ChessMove chessMove) {
		final Move move = chessMove.getMove();
		final byte[] positionOfPieceToMove = chessMove.getPositionOfPieceToMove();

		final byte[][] newBoard = deepCopyBoard(board);
		byte[] delta = move.getDeltaPosition();
		if(Math.abs(delta[0]) == Move.infinity){
			delta[0] = chessMove.getMoveAmount();
		}
		if(Math.abs(delta[1]) == Move.infinity){
			delta[1] = chessMove.getMoveAmount();
		}
		final byte directionX = Utility.getSign(delta[0]);
		final int oldX = positionOfPieceToMove[0];
		final int oldY = positionOfPieceToMove[1];
		final int newX = oldX + delta[0];
		final int newY = oldY + delta[1];
		final byte pieceMoving = newBoard[oldX][oldY];
		final byte sideMoving = Utility.getSign(pieceMoving);
		int newBoardMetaData = -1;
		byte numMoves = (byte) Math.max(getMovesSinceBoardStateChange() + 1, 50);

		//if move is a capture or pawn move reset count
		if(Math.abs(pieceMoving) == Piece.pawnId || newBoard[newX][newY] != Piece.emptyId){
			numMoves = 0;
		}
		//actually move
		newBoard[newX][newY] = newBoard[oldX][oldY];
		newBoard[oldX][oldY] = Piece.emptyId;
		//
		if(move.isCastle()) { //If castle, must also move the rook
			final int rookOldX = directionX == -1 ? 0 : getBoardDimension()-1;
			final int rookNewX = newX + -directionX;
			final int rookY = newY;
			newBoard[rookNewX][rookY] = newBoard[rookOldX][rookY];
			newBoard[rookOldX][rookY]= Piece.emptyId;
		} else if(move.isEnPassantCapture()) { // if en passant, make sure to take the pawn
			final EnPassantMove enPassantMove = (EnPassantMove) move;
			final byte[] pawnCapturePosition = enPassantMove.getPawnCapturePosition();
			newBoard[pawnCapturePosition[0]][pawnCapturePosition[1]] = Piece.emptyId;
			numMoves = 0;
		} else if(move.isPromotionMove()) { //if is promotion, replace pawn with promoted Piece
			newBoard[newX][newY] = (byte) (((PromotionMove) move).promotionId() * sideMoving);
		}
		//finish updating metaData
		//if a king has moved, update that in metadata
		final byte staticWhiteKingPosition = getKingPositionAsByte(1);
		final byte staticBlackKingPosition = getKingPositionAsByte(-1);
		if(Math.abs(pieceMoving) == Piece.kingId) {
			final byte whiteKingPosition, blackKingPosition;
			if(sideMoving == 1){
				whiteKingPosition = positionToByte(new byte[]{(byte) newX, (byte) newY});
				blackKingPosition = staticBlackKingPosition;
				newBoardMetaData = createMetaData((byte) 1, (byte) 1, (byte) 1,
						whiteKingPosition, blackKingPosition, numMoves, boolToByte(kingsRookMoved(-1)),
						boolToByte(kingsMoved(-1)), boolToByte(queensRookMoved(-1)));
			}else {
				whiteKingPosition = staticWhiteKingPosition;
				blackKingPosition = positionToByte(new byte[]{(byte) newX, (byte) newY});
				newBoardMetaData = createMetaData(boolToByte(kingsRookMoved(1)),
						boolToByte(kingsMoved(1)), boolToByte(queensRookMoved(1)),
						whiteKingPosition, blackKingPosition, numMoves, (byte) 1, (byte) 1, (byte) 1);
			}

		}
		//if a rook has moved, update that in metadata
		if(Math.abs(pieceMoving) == Piece.rookId && !kingsMoved(sideMoving)) {
			if(!kingsRookMoved(sideMoving) && oldX == 0){
				if(sideMoving == 1) {
					newBoardMetaData = createMetaData((byte) 1, (byte) 0, boolToByte(queensRookMoved(1)),
							staticWhiteKingPosition, staticBlackKingPosition, numMoves,
							boolToByte(kingsRookMoved(-1)), boolToByte(kingsMoved(-1)),
							boolToByte(queensRookMoved(-1)));
				} else {
					newBoardMetaData = createMetaData(boolToByte(kingsRookMoved(1)), (byte) 0,
							boolToByte(queensRookMoved(1)), staticWhiteKingPosition, staticBlackKingPosition,
							numMoves, (byte) 1, boolToByte(kingsMoved(-1)),
							boolToByte(queensRookMoved(-1)));
				}
			} else if(!queensRookMoved(sideMoving) && oldX == 7) {
				if(sideMoving == 1) {
					newBoardMetaData = createMetaData(boolToByte(kingsRookMoved(1)), (byte) 0, (byte) 1,
							staticWhiteKingPosition, staticBlackKingPosition, numMoves,
							boolToByte(kingsRookMoved(-1)), boolToByte(kingsMoved(-1)),
							boolToByte(queensRookMoved(-1)));
				} else {
					newBoardMetaData = createMetaData(boolToByte(kingsRookMoved(1)), (byte) 0,
							boolToByte(queensRookMoved(1)), staticWhiteKingPosition, staticBlackKingPosition,
							numMoves, boolToByte(kingsRookMoved(-1)), boolToByte(kingsMoved(-1)),
							(byte) 1);
				}
			}
		}
		//if the metadata hasnt otherwise been changed, set it to be the same, with numMoves updated
		if(newBoardMetaData == -1) {
			newBoardMetaData = createMetaData(boolToByte(kingsRookMoved(1)), boolToByte(kingsMoved(1)),
					boolToByte(queensRookMoved(1)), staticWhiteKingPosition, staticBlackKingPosition, numMoves,
					boolToByte(kingsRookMoved(-1)), boolToByte(kingsMoved(-1)), boolToByte(queensRookMoved(-1)));
		}

		return new Board(newBoard, newBoardMetaData);
	}

	private boolean canApplyMove(final ChessMove chessMove) {
		final Move move = chessMove.getMove();
		final byte[] positionOfPieceToMove = chessMove.getPositionOfPieceToMove();
		final byte pieceIdValidation = chessMove.getPieceId();

		final byte oldX = positionOfPieceToMove[0];
		final byte oldY = positionOfPieceToMove[1];
		final byte[] delta = move.getDeltaPosition();
		final byte directionX = Utility.getSign(delta[0]);
		final int newX = oldX + delta[0];
		final int newY = oldY + delta[1];
		if(!isWithinBoard(newX, newY)) {
			return false;
		}
		byte pieceValue = board[oldX][oldY];
		byte moveToValue = board[newX][newY];
		final byte sideMoving = Utility.getSign(pieceValue);
		if(pieceValue != pieceIdValidation){
			Logger.log("Did not find Piece ID: " + pieceIdValidation +
					" at position [" + oldX + " , " + oldY + "] instead found ID: " + pieceValue);
			return false;
		}
		//Cant take piece with same value as itself
		if(Utility.getSign(pieceValue) == Utility.getSign(moveToValue)) {
			return false;
		}
		//TODO: fix this
		if(move.isEnPassantCapture()){
			return false;
		}

		if(move.isCastle()) {
			if (kingsMoved(sideMoving)) {
				return false;
			}
			if (directionX == -1) {
				if (queensRookMoved(sideMoving)) {
					return false;
				}
			} else {
				if (kingsRookMoved(sideMoving)) {
					return false;
				}
			}
			//check if castling through, or out of check (not allowed). into check will be checked at the end
			byte xPosition = oldX;
			while (xPosition != newX) {
				if (isPositionInCheck(new byte[]{xPosition, oldY}, sideMoving)) {
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
		//can only double pawn move on starting square, cant double move through piece
		if(move.isDoublePawnMove()) {
			if(oldY != 1 && oldY != 6) {
				return false;
			}
			//if position moving through is not empty return false
			if(board[oldX][oldY + Utility.getSign(delta[1])] != Piece.emptyId) {
				return false;
			}
		}

		Board boardWithMove = blitheMove(chessMove);
		if(boardWithMove.isPositionInCheck(boardWithMove.getKingPosition(sideMoving), sideMoving)){
			return false;
		}

		return true;
	}

	private boolean isPositionInCheck(final byte[] position, final byte side){
		//check if knight can take position
		List<Move> knightMoves = PieceFactory.getPieceMoves(Piece.knightId);
		for(Move move : knightMoves) {
			final byte[] delta = move.getDeltaPosition();
			final byte[] knightPos = new byte[] {(byte) (position[0] + delta[0]), (byte) (position[1] + delta[1])};
			if(isWithinBoard(knightPos[0], knightPos[1])) {
				if(board[knightPos[0]][knightPos[1]] == -side*Piece.knightId){
					return true;//in check from a knight
				}
			}
		}
		//check if bishop, queen, or rook can take position
		for(byte piece : new byte[]{Piece.bishopId, Piece.rookId}){
			List<Move> moveList = PieceFactory.getPieceMoves(piece);
			for(Move move : moveList) {
				final byte[] trueDelta = new byte[]{Utility.getSign(move.getDeltaPosition()[0]), Utility.getSign(move.getDeltaPosition()[1])};
				byte[] currentPosition = new byte[]{position[0],position[1]};
				byte currentPiece = board[currentPosition[0]][currentPosition[1]];
				do {
					currentPosition[0] += trueDelta[0];
					currentPosition[1] += trueDelta[1];
					if(!isWithinBoard(currentPosition[0], currentPosition[1])){
						break;
					}
					currentPiece = board[currentPosition[0]][currentPosition[1]];
				} while(currentPiece == Piece.emptyId);
				//keep going on the current position until you get a non empty square, thats the first piece that it could hit
				if(currentPiece == -side*piece || currentPiece == -side*Piece.queenId){//if the first piece it could hit can move in the direction of the current piece, then position is in check
					return true;
				}
			}
		}
		//check if king can take position
		List<Move> kingMoves = PieceFactory.getPieceMoves(Piece.kingId);
		for(Move move : kingMoves) {
			if(move.isCastle()) {//ignore the castle when checking if position can be taken from a king, as a castle cant be a capture
				continue;
			}
			final byte[] delta = move.getDeltaPosition();
			final byte[] trueLocation = new byte[]{(byte) (position[0] + delta[0]), (byte) (position[1] + delta[1])};
			if(isWithinBoard(trueLocation[0], trueLocation[1])){
				if(board[trueLocation[0]][trueLocation[1]] == -side*Piece.kingId){
					return true;
				}
				if(delta[1] == side && delta[0] != 0){//if a forward diagonal capture, also check pawn
					if(board[trueLocation[0]][trueLocation[1]] == -side*Piece.pawnId){
						return true;
					}
				}
			}
		}

		return false;
	}

	public byte getPiece(final int x, final int y) {
		return board[x][y];
	}

	private boolean isWithinBoard(final int x, final int y) {
		if(x < 0 || x >= getBoardDimension()){
			return false;
		}
		return y >= 0 && y < getBoardDimension();
	}


	public int getBoardDimension() {
		return board.length;
	}

	public boolean kingsRookMoved(final int side) {
		if(side == 1) {
			return (boardMetaData & 1) == 1;
		} else {
			return ((boardMetaData >> 21) & 1) == 1;
		}
	}

	public boolean kingsMoved(final int side) {
		if(side == 1) {
			return ((boardMetaData >> 1) & 1) == 1;
		}else {
			return ((boardMetaData >> 22) & 1) == 1;
		}
	}

	public boolean queensRookMoved(final int side) {
		if(side == 1) {
			return ((boardMetaData >> 2) & 1) == 1;
		} else {
			return ((boardMetaData >> 23) & 1) == 1;
		}
	}

	public byte[] getKingPosition(final int side) {
		byte[] kingPosition = new byte[2];
		final int startingOffset = side == 1 ? 3 : 9;
		kingPosition[0] = (byte) ((boardMetaData >> startingOffset) & 7);
		kingPosition[1] = (byte) ((boardMetaData >> (startingOffset + 3)) & 7);
		return kingPosition;
	}

	public byte getMovesSinceBoardStateChange() {
		return (byte) ((boardMetaData >> 15) & 63);
	}

	private int createMetaData(final byte whiteKingsRookMoved, final byte whiteKingMoved, final byte whiteQueensRookMoved,
							  final byte whiteKingPosition, final byte blackKingPosition, final byte movesSinceBoardStateChange,
							  final byte blackKingsRookMoved, final byte blackKingMoved, final byte blackQueensRookMoved) {
		return  (whiteKingsRookMoved + (whiteKingMoved << 1) + (whiteQueensRookMoved << 2) + (whiteKingPosition << 3) + (blackKingPosition << 9)
				+ (movesSinceBoardStateChange << 15)) + (blackKingsRookMoved << 21) + (blackKingMoved << 22) + (blackQueensRookMoved << 23);
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

	private byte[][] deepCopyBoard(byte[][] board){
		byte[][] newBoard = new byte[board.length][];
		for(int i = 0; i < newBoard.length; i++){
			newBoard[i] = board[i].clone();
		}
		return newBoard;
	}
}
