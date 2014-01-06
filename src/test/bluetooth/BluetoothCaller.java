package test.bluetooth;

import test.Tester;

public interface BluetoothCaller extends Tester{
	public void stopDueToInit();
	public void failBT();
	public void setPairMessage();
}
