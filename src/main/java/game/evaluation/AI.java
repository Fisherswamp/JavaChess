package main.java.game.evaluation;

import main.java.game.Board;
import main.java.game.Game;
import main.java.game.moves.ChessMove;

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
		final long startTime = System.nanoTime();
		final Board board = game.getCurrentBoardState();
		final int side = game.getTurn();
		ConcurrentSkipListMap<Evaluation, Queue<ChessMove>> bestMoves = new ConcurrentSkipListMap<>();
		game.getLegalMoves().parallelStream().forEach( chessMove -> {
			final Map<Evaluation, ChessMove> moveValue = minmax(board.move(chessMove, false), -side,4, 1, chessMove);
			moveValue.entrySet().forEach(evaluationChessMoveEntry -> {
				if (!bestMoves.containsKey(evaluationChessMoveEntry.getKey())) {
					bestMoves.put(evaluationChessMoveEntry.getKey(), new ConcurrentLinkedQueue<>());
				}
				bestMoves.get(evaluationChessMoveEntry.getKey()).add(evaluationChessMoveEntry.getValue());
			});
		});
		final Queue<ChessMove> bestMovesQueue = bestMoves.firstEntry().getValue();
		return bestMovesQueue.peek();
	}

	/**
	 *
	 * @param board
	 * @param side
	 * @return Returns a map with the best evaluation and all moves that fit
	 */
	private ConcurrentSkipListMap<Evaluation, ChessMove> minmax(final Board board, final int side, final int maxDepth, final int depth, final ChessMove firstMove) {
		final ConcurrentSkipListMap<Evaluation, ChessMove> map = new ConcurrentSkipListMap<>();
		if(depth >= maxDepth) {
			final Evaluation evaluation = BoardEvaluator.evaluateBoard(board, (byte) side, depth);
			map.put(evaluation, firstMove);
			return map;
		}
		board.getLegalMoves((byte) side).forEach(chessMove -> {
			final Board newBoard = board.move(chessMove, false);
			map.putAll(minmax(newBoard, -side, maxDepth, depth+1, firstMove));
		});

		final Map.Entry<Evaluation, ChessMove> bestMove = map.firstEntry();
		final ConcurrentSkipListMap<Evaluation, ChessMove> emptyMap = new ConcurrentSkipListMap<>();
		emptyMap.put(bestMove.getKey(), bestMove.getValue());
		return emptyMap;
	}

}
