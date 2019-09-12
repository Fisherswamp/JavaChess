package debug;

public class Utility {

	public static byte getSign(byte number) {
		return (number >> 7) == -1 ? (byte) -1 : 1;
	}

	public static byte boolToByte(boolean bool) {
		return (byte) (bool ? 1 : 0);
	}

}
