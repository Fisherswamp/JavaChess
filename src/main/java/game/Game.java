package main.java.game;

import main.java.game.moves.ChessMove;
import main.java.game.moves.Move;

import java.util.ArrayList;
import java.util.List;

public class Game {

	private final List<Board> boardStates;
	private final List<ChessMove> legalMoves;
	/* 1 for white, -1 for black */
	private byte turn;


	public Game() {
		this.boardStates = new ArrayList<>();
		this.legalMoves = new ArrayList<>();
		this.turn = 1;
		boardStates.add(new Board());
		legalMoves.addAll(getCurrentBoardState().getLegalMoves(this.turn));
	}

	public List<ChessMove> getLegalMoves(){
		return legalMoves;
	}

	public boolean move(final ChessMove move) {
		final Board newBoard = getCurrentBoardState().move(move, true);
		if(newBoard == null) {
			return  false;
		} else {
			turn *= -1;
			boardStates.add(newBoard);
			legalMoves.clear();
			legalMoves.addAll(getCurrentBoardState().getLegalMoves(this.turn));
			return true;
		}
	}

	public Board getCurrentBoardState() {
		return boardStates.get(boardStates.size() - 1);
	}

	public int getTurn(){
		return turn;
	}

	public int getNumMoves() {
		return boardStates.size() - 1;
	}

}
