package test.game;

import main.java.game.Board;
import main.java.game.Game;
import main.java.game.evaluation.AI;
import main.java.game.moves.ChessMove;

import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

public class BoardTest {
	public static void main(String[] args) {
		final int numIterations = 1;
		final int maxDepthCheck = 8;
		for(int i = 1; i <= maxDepthCheck; i++) {
			double averageComputationTime = 0;
			for (int j = 0; j < numIterations; j++) {
				final Game game = new Game();
				final AI ai = new AI(game, 0, true, 0);
				final long startTime = System.nanoTime();
				ai.findBestMoveDepth(i);
				averageComputationTime += (System.nanoTime()-startTime);
			}
			averageComputationTime /= numIterations;
			System.out.printf("Average time at depth %d: %.4f\n", i, averageComputationTime/1_000_000_000L);
		}

	}

}
