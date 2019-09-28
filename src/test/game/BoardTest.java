package test.game;

import main.game.Board;
import main.game.Game;
import main.game.moves.ChessMove;
import main.ui.TextUi;

import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

public class BoardTest {
	public static void main(String[] args) {
		int numIterations = 1000;
		//System.out.printf("Average run time in Parallel: %.6f seconds\n", getAverageRunTimeParallel(numIterations));
		//System.out.printf("Average run time in Series: %.6f seconds\n", getAverageRunTimeSeries(numIterations));
		System.out.printf("Moves at depth 2: " + getNumMovesAtDepth(2));
	}

	public static long getNumMovesAtDepth(final int depth) {
		final Game game = new Game();
		final Board currentBoard = game.getCurrentBoardState();
		if(depth < 0) {
			return 0;
		}
		if(depth == 0) {
			return currentBoard.getLegalMoves((byte) 1).size();
		}
		final AtomicLong numMoves = new AtomicLong(0l);
		currentBoard.getLegalMoves((byte) 1).parallelStream().forEach(chessMove -> {
			final Board board = currentBoard.move(chessMove, false);
			numMoves.addAndGet(getNumMovesAtDepth(depth - 1, (byte) -1, board));
		});
		return numMoves.get();
	}

	private static long getNumMovesAtDepth(final int depth, final byte side, final Board board) {
		if(depth <= 0) {
			return board.getLegalMoves(side).size();
		}

		final AtomicLong numMoves = new AtomicLong(0l);
		board.getLegalMoves(side).forEach(chessMove -> {
			final Board newBoard = board.move(chessMove, false);
			numMoves.addAndGet(getNumMovesAtDepth(depth - 1, (byte) (side * -1), newBoard ));
		});
		return numMoves.get();
	}

	public static double getAverageRunTimeParallel(final int numIterations) {
		double averageTime = 0;
		for(int i = 0; i < numIterations; i++) {
			long startTime = System.nanoTime();
			Game game = new Game();
			List<ChessMove> legalMoves = game.getLegalMoves();
			byte side = (byte) game.getTurn();
			Board prevBoardState = game.getCurrentBoardState();
			legalMoves.parallelStream().forEach(chessMove -> {
				Board board = prevBoardState.move(chessMove, false);
				byte newSide = (byte) -side;
				board.getLegalMoves(newSide).forEach(chessMove1 -> {
					Board secondBoard = board.move(chessMove1, false);
					secondBoard.getLegalMoves(side).forEach(chessMove2 -> {
						Board thirdBoard = secondBoard.move(chessMove2, false);
					});
				});
			});
			long endTime = System.nanoTime();
			averageTime += (endTime-startTime);
		}
		averageTime = (averageTime / numIterations) / 1_000_000_000.0;
		return averageTime;
	}

	public static double getAverageRunTimeSeries(int numIterations) {
		double averageTime = 0;
		for(int i = 0; i < numIterations; i++) {
			long startTime = System.nanoTime();
			Game game = new Game();
			List<ChessMove> legalMoves = game.getLegalMoves();
			byte side = (byte) game.getTurn();
			legalMoves.forEach(chessMove -> {
				Board board = game.getCurrentBoardState().move(chessMove, false);
				byte newSide = (byte) -side;
				board.getLegalMoves(newSide).forEach(chessMove1 -> {
					Board secondBoard = board.move(chessMove1, false);
					secondBoard.getLegalMoves(side).forEach(chessMove2 -> {
						Board thirdBoard = secondBoard.move(chessMove2, false);
					});
				});
			});
			long endTime = System.nanoTime();
			averageTime += (endTime-startTime);
		}
		averageTime = (averageTime / numIterations) / 1_000_000_000.0;
		return averageTime;
	}
}
