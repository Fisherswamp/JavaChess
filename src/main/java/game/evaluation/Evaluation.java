package main.java.game.evaluation;

public class Evaluation implements Comparable<Evaluation>{

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
	//if bigger than other, return positive number, if same return 0, else return negative
	@Override
	public int compareTo(final Evaluation other) {
		if(value == other.getValue()) {
			if(isCheckmate() && other.isCheckmate()) {
				return  -(movesToMate - other.movesToMate);
			}
		}
		return (int) (value - other.getValue());
	}
}
