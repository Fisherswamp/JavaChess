package main.debug;

public class Utility {

	public static byte getSign(byte number) {
		return (byte) Math.signum(number);
	}

	public static byte boolToByte(boolean bool) {
		return (byte) (bool ? 1 : 0);
	}

}
