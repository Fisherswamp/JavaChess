package game.moves;

public class EnPassantMove extends Move{

	private final byte[] pawnCapturePosition;

	public EnPassantMove(final byte[] deltaPosition, final byte[] pawnCapturePosition) {
		super(deltaPosition);
		setIsEnPassantCapture();
		this.pawnCapturePosition = pawnCapturePosition;
	}

	public byte[] getPawnCapturePosition() {
		return pawnCapturePosition;
	}
}
