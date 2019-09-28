package main.game.moves;

public class DoublePawnMove extends Move{

	public DoublePawnMove(final byte[] deltaPosition) {
		super(deltaPosition);
		setIsDoublePawnMove();
	}

}
