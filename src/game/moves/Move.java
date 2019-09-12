package game.moves;

import game.Board;
import jdk.jshell.spi.ExecutionControl;

public class Move {

	public final static byte infinity = 64;

	/* Change in position. If x or y is +-infinity then that means move infinitely in that direction */
	private final byte[] deltaPosition;

	private final boolean isCastle;
	private boolean isEnPassantCapture;
	private boolean isDoublePawnMove;
	private boolean isExclusiveCaptureMove;
	private boolean isPromotionMove;

	public Move(final byte[] deltaPosition){
		this(deltaPosition, false);
	}

	public Move(final byte[] deltaPosition, final boolean isCastle) {
		this.deltaPosition = deltaPosition;
		this.isCastle = isCastle;
		this.isEnPassantCapture = false;
		this.isDoublePawnMove = false;
		this.isExclusiveCaptureMove = false;
		this.isPromotionMove = false;
	}

	public byte[] getDeltaPosition() {
		return deltaPosition;
	}

	void setIsEnPassantCapture() {
		this.isEnPassantCapture = true;
	}

	void setIsDoublePawnMove() {
		this.isDoublePawnMove = true;
	}

	void setExclusiveCaptureMove() {
		this.isExclusiveCaptureMove = true;
	}

	void setIsPromotionMove() {
		this.isPromotionMove = true;
	}

	public boolean isCastle() {
		return isCastle;
	}

	public boolean isEnPassantCapture() {
		return isEnPassantCapture;
	}

	public boolean isDoublePawnMove() {
		return isDoublePawnMove;
	}

	public boolean isExclusiveCaptureMove() {
		return isExclusiveCaptureMove;
	}

	public boolean isPromotionMove() {
		return isPromotionMove;
	}


}
