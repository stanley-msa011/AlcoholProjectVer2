package game.interaction;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

public class MsgService extends Service {
	static Object lock  = new Object();
	static Object lock2 = new Object();
	static String msg;
	static boolean hasMsg = false;
	static public void setMsg(String message){
		synchronized(lock){
			msg = message;
			hasMsg = true;
		}
	}
	static public boolean isHavingMsg(){
		synchronized(lock){
			return hasMsg;
		}
	}
	static public String getMsg(){
		synchronized(lock){
			hasMsg = false;
			return msg;
		}
	}
	
	static boolean hasNotify = false;
	static public void setNotify(){
		synchronized(lock2){
			hasNotify = true;
		}
	}
	
	static public void releaseNotify(){
		synchronized(lock2){
			hasNotify = false;;
		}
	}
	
	static public boolean getNotify(){
		synchronized(lock2){
			return hasNotify;
		}
	}
	@Override
	public IBinder onBind(Intent arg0) {
		// TODO Auto-generated method stub
		return null;
	}
}
