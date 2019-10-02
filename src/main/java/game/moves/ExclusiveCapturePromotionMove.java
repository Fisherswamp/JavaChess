package main.java.game.moves;

public class ExclusiveCapturePromotionMove extends PromotionMove {

	public ExclusiveCapturePromotionMove(final byte[] deltaPosition, final byte promotionId) {
		super(deltaPosition, promotionId);
		setExclusiveCaptureMove();
	}

}
