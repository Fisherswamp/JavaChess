package main.java.game.evaluation;

public class Evaluation {

	private final double value;
	private final boolean isGameOver;
	private final int movesToMate;

	public Evaluation(final double value, final boolean isGameOver, final int movesToMate) {
		this.value = value;
		this.isGameOver = isGameOver;
		this.movesToMate = movesToMate;
	}

	public Evaluation(final double value) {
		this(value, false, -1);
	}

	public boolean isCheckmate() {
		return isGameOver && (value != 0);
	}

	public boolean isStalemate() {
		return isGameOver && (value == 0);
	}

	public double getValue() {
		return value;
	}

	public boolean isGameOver() {
		return isGameOver;
	}

	public int getMovesToMate() {
		return movesToMate;
	}

}
