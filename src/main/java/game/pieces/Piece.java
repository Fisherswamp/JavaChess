package main.java.game.pieces;

import main.java.game.moves.Move;

import java.util.List;

public class Piece {

	public static final byte emptyId = 0;
	public static final byte pawnId = 1;
	public static final byte knightId = 2;
	public static final byte bishopId = 3;
	public static final byte rookId = 4;
	public static final byte queenId = 5;
	public static final byte kingId = 6;
	/* Piece's unique id, to be used for board representation >0 values are white, < 0 values are black*/
	private final byte id;

	/* numerical value of piece ~Always Positive~ */
	private final int value;

	/* english name of piece */
	private final String name;
	/* List of Piece's legal moves*/
	private final List<Move> moves;

	public Piece(final byte id, final int pieceValue, final List<Move> moves, final String name) {
		this.id = id;
		this.value = pieceValue;
		this.moves = moves;
		this.name = name;
	}

	public List<Move> getMoves(){
		return moves;
	}

	public byte getId() {
		return id;
	}

	public String getName() {
		return name;
	}
}
