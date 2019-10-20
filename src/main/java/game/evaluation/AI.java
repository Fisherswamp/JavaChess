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
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

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

	public ChessMove findBestMove(final long maxTime) {
		final int sideMaximizing = game.getTurn();
		if(sideMaximizing == 1) {
			return findBestMoves(maxTime).lastEntry().getValue().peek();
		} else {
			return findBestMoves(maxTime).firstEntry().getValue().peek();
		}
	}

	public ConcurrentSkipListMap<Evaluation, Queue<ChessMove>> findBestMoves(final long maxTime) {
		final long startTime = System.nanoTime();
		final Board board = game.getCurrentBoardState();
		final int sideMaximizing = game.getTurn();

		ConcurrentSkipListMap<Evaluation, Queue<ChessMove>> deepestEvaluation = new ConcurrentSkipListMap<>();
		final AtomicInteger maxDepth = new AtomicInteger(1);
		while (System.nanoTime()-startTime < maxTime) {
			final ConcurrentSkipListMap<Evaluation, Queue<ChessMove>> bestMoves = new ConcurrentSkipListMap<>();
			game.getLegalMoves().parallelStream().forEach(chessMove -> {
				//System.out.printf("%s: %s\n", Thread.currentThread().getName(), chessMove.toString());
				final Evaluation moveValue = minmax(board.move(chessMove, false), -sideMaximizing,
						1, maxDepth.get(), startTime, maxTime, Double.MIN_VALUE, Double.MAX_VALUE);//TODO: change this to max and min
				if(moveValue != null) {//if movevalue is null, we want to finish asap
					if (!bestMoves.containsKey(moveValue)) {
						bestMoves.put(moveValue, new ConcurrentLinkedQueue<>());
					}
					bestMoves.get(moveValue).add(chessMove);
				}
			});
			if(System.nanoTime()-startTime < maxTime) {
				deepestEvaluation = bestMoves;
				maxDepth.incrementAndGet();
			}
		}
		System.out.printf("Found best move in %.4f seconds at depth %d\n",(System.nanoTime()-startTime)/1_000_000_000f, maxDepth.get()-1);
		return deepestEvaluation;
	}

	/**
	 *
	 * @param board
	 * @param side
	 * @return Returns a map with the best evaluation and all moves that fit
	 */
	private Evaluation minmax(final Board board, final int side, final int depth, final int maxDepth,
							  final long startTime, final long maxTime, double alpha, double beta) {
		if(System.nanoTime()-startTime >= maxTime) {
			return null;
		}
		if(depth >= maxDepth) {
			return BoardEvaluator.evaluateBoard(board, (byte) side, depth);
		}
		final boolean isMaximizing = side == 1;
		Evaluation bestChoice = isMaximizing ? new Evaluation(Double.MIN_VALUE) : new Evaluation(Double.MAX_VALUE);
		for(ChessMove chessMove : board.getLegalMoves((byte) side)) {
			final Board newBoard = board.move(chessMove, false);
			final Evaluation evaluation = minmax(newBoard, -side, depth+1, maxDepth, startTime, maxTime, alpha, beta);
			if(evaluation == null) {
				return null;
			}
			final boolean betterChoice = isMaximizing ? evaluation.compareTo(bestChoice) > 0 : evaluation.compareTo(bestChoice) < 0;
			if(betterChoice) {
				bestChoice = evaluation;
			}
			if(isMaximizing) {
				alpha = Math.max(alpha, evaluation.getValue());
				if(beta <= alpha) {
					break;
				}
			} else {
				beta = Math.min(beta, evaluation.getValue());
				if(beta <= alpha) {
					break;
				}
			}
		}
		return bestChoice;
	}

}
