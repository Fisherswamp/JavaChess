package main.java.game.evaluation;

import main.java.game.Board;
import main.java.game.Game;
import main.java.game.moves.ChessMove;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ConcurrentSkipListMap;
import java.util.concurrent.ConcurrentSkipListSet;

public class AI {

	private final Game game;
	private final long totalTime;
	private long remainingTime;
	private final boolean timePerTurn;
	private final long timeIncrement;

	public AI(final Game game, final long totalTime, final boolean timePerTurn, final long timeIncrement) {
		this.game = game;
		this.totalTime = totalTime;
		this.remainingTime = totalTime;
		this.timePerTurn = timePerTurn;
		this.timeIncrement = timeIncrement;
	}

	public ChessMove findBestMove() {
		System.out.printf("Finding best move...\n");
		return findBestMoves().firstEntry().getValue().peek();
	}

	public ConcurrentSkipListMap<Evaluation, Queue<ChessMove>> findBestMoves() {
		final long startTime = System.nanoTime();
		final Board board = game.getCurrentBoardState();
		final int side = game.getTurn();
		ConcurrentSkipListMap<Evaluation, Queue<ChessMove>> bestMoves = new ConcurrentSkipListMap<>();
		game.getLegalMoves().parallelStream().forEach( chessMove -> {
			System.out.printf("%s: %s\n", Thread.currentThread().getName(), chessMove.toString());
			final Evaluation moveValue = minmax(board.move(chessMove, false), -side,0, 1);
			if (!bestMoves.containsKey(moveValue)) {
				bestMoves.put(moveValue, new ConcurrentLinkedQueue<>());
			}
			bestMoves.get(moveValue).add(chessMove);
		});
		System.out.printf("Found best move in %.4f seconds\n",(System.nanoTime()-startTime)/1_000_000_000f );
		return bestMoves;
	}

	/**
	 *
	 * @param board
	 * @param side
	 * @return Returns a map with the best evaluation and all moves that fit
	 */
	private Evaluation minmax(final Board board, final int side, final int maxDepth, final int depth) {
		if(depth >= maxDepth) {
			return BoardEvaluator.evaluateBoard(board, (byte) side, depth);
		}
		List<Evaluation> bestEvaluation = new ArrayList<>();
		bestEvaluation.add(new Evaluation(Double.MIN_VALUE));
		board.getLegalMoves((byte) side).forEach(chessMove -> {
			final Board newBoard = board.move(chessMove, false);
			final Evaluation evaluation = minmax(newBoard, -side, maxDepth, depth+1);
			if(evaluation.compareTo(bestEvaluation.get(0)) > 0) {
				bestEvaluation.clear();
				bestEvaluation.add(evaluation);
			}
		});
		return bestEvaluation.get(0);
	}

}
