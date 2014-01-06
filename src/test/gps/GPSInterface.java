package test.gps;

public interface GPSInterface{
	public void runGPS();
	public void setKeepMsgBox(boolean keepMsgBox);
	public boolean isKeepMsgBox();
	public void callGPSActivity();
	public void startGPS(boolean enable);
}
