package main.java.game.moves;

public class ExclusiveCaptureMove extends Move{

	public ExclusiveCaptureMove(final byte[] deltaPosition) {
		super(deltaPosition);
		setExclusiveCaptureMove();
	}

}
