package main.game;

import main.game.moves.ChessMove;

import java.util.ArrayList;
import java.util.List;

public class Game {

	private final List<Board> boardStates;
	private byte turn;

	public Game() {
		this.boardStates = new ArrayList<>();
		this.turn = 1;
		boardStates.add(new Board());
	}

	public List<ChessMove> getLegalMoves(){
		return getCurrentBoardState().getLegalMoves(this.turn);
	}

	public boolean move(final ChessMove move) {
		final Board newBoard = getCurrentBoardState().move(move, true);
		if(newBoard == null) {
			return  false;
		} else {
			turn *= -1;
			boardStates.add(newBoard);
			return true;
		}
	}

	public Board getCurrentBoardState() {
		return boardStates.get(boardStates.size() - 1);
	}

}
