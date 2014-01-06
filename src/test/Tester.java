package test;

public interface Tester {
	public static final int _GPS = 0;
	public static final int _BT = 1;
	public static final int _CAMERA = 2;
	public void updateInitState(int type);
	public void updateDoneState(int type);
}
