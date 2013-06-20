package test.bluetooth;



import java.io.InputStream;
import java.util.Iterator;
import java.util.Set;
import java.util.UUID;


import test.camera.CameraRunHandler;
import test.file.BracValueDebugHandler;
import test.file.BracValueFileHandler;
import ubicomp.drunk_detection.activities.TestFragment;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.os.Message;
import android.preference.PreferenceManager;
import android.util.Log;

public class Bluetooth {

	private BluetoothAdapter btAdapter;
	
	private static final UUID uuid=  UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
	private static String DEVICE_NAME = "BOLUTEK";
	private static String DEVICE_NAME2 = "AEGIN";
	private BluetoothDevice sensor;
	private BluetoothSocket socket;
	
	private InputStream in;
	private Context context;
	
	private float local_min;
	//private float local_max;
	private float prev_prev_pressure;
	private float prev_pressure;
	private float now_pressure;
	private boolean isPeak = false;
	private final static float PRESSURE_DIFF_MIN = 300.f;
	private final static float PRESSURE_DIFF_MAX = 20000.f;
	private final static long IMAGE_MILLIS_0 = 500;
	private final static long IMAGE_MILLIS_1 = 2500;
	private final static long MAX_DURATION_MILLIS = 5000;
	
	private final static long MILLIS_1 = 1000;
	private final static long MILLIS_2 = 2000;
	private final static long MILLIS_3 = 3000;
	private final static long MILLIS_4 = 4000;
	private final static long MILLIS_5 = 5000;
	
	private long start_time;
	private long end_time;
	private long first_start_time;
	private long duration = 0;
	
	private boolean start;
	
	private boolean success;
	
	private final static int READ_NULL = 0;
	private final static int READ_ALCOHOL = 1;
	private final static int READ_PRESSURE = 2;
	
	private Object lock = new Object();
	private BTUIHandler btUIHandler;
	
	private int image_count;
	
	private CameraRunHandler cameraRunHandler;
	private BracValueFileHandler bracFileHandler;
	private BracValueDebugHandler bracDebugHandler;
	
	private TestFragment testFragment;
	
	private int count;
	private float sum;
	
	private SharedPreferences sp;
	private SharedPreferences.Editor sp_editor;
	
	public Bluetooth(TestFragment testFragment, CameraRunHandler cameraRunHandler,BracValueFileHandler bracFileHandler, BracValueDebugHandler bracDebugHandler){
		this.testFragment = testFragment;
		this.context = testFragment.getActivity();
		this.cameraRunHandler = cameraRunHandler;
		this.bracFileHandler = bracFileHandler;
		this.bracDebugHandler = bracDebugHandler;
		btAdapter =  BluetoothAdapter.getDefaultAdapter();
		if (btAdapter == null)
			Log.e("BT","NOT SUPPORT BT");
		local_min = 0.f;
		//local_max = 0.f;
		prev_prev_pressure = 0.f;
		prev_pressure = 0.f;
		now_pressure = 0.f;
		success = false;
		btUIHandler=new BTUIHandler(testFragment);
		start = false;
		sum = 0;
		count = 0;
		sp= PreferenceManager.getDefaultSharedPreferences(context);
		sp_editor = sp.edit();
	}
	
	public void enableAdapter(){
		if (!btAdapter.isEnabled()){
			btAdapter.enable();
			int state = btAdapter.getState();
			while (state!=BluetoothAdapter.STATE_ON){
				try { Thread.sleep(100);} catch (InterruptedException e) {}
				state =  btAdapter.getState();
			}
		}
	}
	
	public void pair(){
		sensor = null;
		Set<BluetoothDevice> devices = btAdapter.getBondedDevices();
		Iterator<BluetoothDevice> iter = devices.iterator();
		while (iter.hasNext()){
			BluetoothDevice device = iter.next();
			if (device.getName().equals(DEVICE_NAME)||device.getName().equals(DEVICE_NAME2)){
				sensor = device;
				break;
			}
		}
		if (sensor == null){
			IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
			BroadcastReceiver receiver = new btReceiver();
			context.registerReceiver(receiver, filter);
			btAdapter.startDiscovery();
		}
	}
	
	private class btReceiver extends BroadcastReceiver{
		@Override
		public void onReceive(Context context, Intent intent) {
			if (BluetoothDevice.ACTION_FOUND.equals(intent.getAction())){
				BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
				if (device == null)
					return;
				if (device.getName().equals(DEVICE_NAME)||device.getName().equals(DEVICE_NAME2)){ // add DEVICE_NAME2?
					btAdapter.cancelDiscovery();
					sensor = device;
				}
			}
		}
	}
	
	public int connect(){
		if (sensor == null)
			return -1;
		try {
			
			if (Build.VERSION.SDK_INT<11){
				socket = sensor.createRfcommSocketToServiceRecord(uuid);
				Log.d("BT","createSocket (<11)");
			}
			else{
				socket = sensor.createRfcommSocketToServiceRecord(uuid);
				Log.d("BT","createSocket (>=11)");
			}
			btAdapter.cancelDiscovery();
			socket.connect();
			Log.d("BT","createSocket connect");
		} catch (Exception e) {
			Log.e("BT","FAIL TO CONNECT TO THE SENSOR");
			Log.e("BT",e.toString());
			String s = e.toString();
			sp_editor.putString("BT_CONNECTION_ERROR", s);
			sp_editor.putBoolean("HAS_BT_CONNECTION_ERROR", true);
			sp_editor.commit();
			close();
			return -1;
		}
		return 1;
	}
	
	public void start(){
		start = true;
	}
	
	
	public void read(){
		
		int end=0;
		byte[] temp = new byte[1024];
		int bytes;
		String msg = "";
		isPeak=false;
		success = false;
		local_min = 0;
		//local_max = 0;
		now_pressure = 0;
		prev_prev_pressure = 0;
		prev_pressure = 0;
		int read_type = READ_NULL;
		duration = 0;
		first_start_time = -1;
		image_count  =0;
		sum = 0;
		count = 0;
		try {
			in = socket.getInputStream();
			bytes =in.read(temp);
			while(bytes>0){
				if (!start){
					bytes =in.read(temp);
					continue;
				}
				//Log.d("BT","READ");
				long time = System.currentTimeMillis();
				if (first_start_time == -1){
					first_start_time = time;
					Log.d("ERIC", "start");
				}
				else if (time - first_start_time > 15000){
					Log.d("BT","TIME OUT");
					end =-1; 
					throw new Exception("time out");
				}
				for (int i=0;i<bytes;++i){
					if ((char)temp[i]=='a'){
						end = sendMsgToApp(msg);
						sendDebugMsg(msg);
						msg="a";
						read_type = READ_ALCOHOL;
					}
					else if ((char)temp[i]=='m'){
						end = sendMsgToApp(msg);
						sendDebugMsg(msg);
						msg="m";
						read_type = READ_PRESSURE;
					}
					else if ( (char)temp[i]=='b'){
						throw new Exception("NO BETTARY");
					}
					else if (read_type!= READ_NULL){
							msg += (char)temp[i];
					}
				}
				if (end == -1){
					break;
				}
				bytes =in.read(temp);
			}
			close();
		} catch (Exception e) {
			Log.e("BT","FAIL TO READ DATA FROM THE SENSOR");
			close();
			testFragment.showDebug("Close by exception or timeout" );
			if(!success){
					cameraRunHandler.sendEmptyMessage(1);
			}
		}
	}
	
	private String debugMsg = "";
	
	private void sendDebugMsg(String msg){
		if (msg == "")
			return;
		
		if (msg.charAt(0) == 'm'){
			String output = ","+msg.substring(1,msg.length()-1);
			debugMsg = debugMsg+output+"\n";
		}
		else if (msg.charAt(0) == 'a'){
			long timestamp = System.currentTimeMillis();
			debugMsg = timestamp+","+msg.substring(1,msg.length()-1);
			return;
		}else
			return;
		
		Message message = new Message();
		Bundle data = new Bundle();
		data.putString("ALCOHOL_DEBUG", debugMsg);
		message.setData(data);
		bracDebugHandler.sendMessage(message);
	}
	
	private int sendMsgToApp(String msg){
		synchronized(lock){
			if (msg=="");
				//Do nothing
			else if (msg.charAt(0)=='a'){
				if (isPeak){
					long timeStamp = System.currentTimeMillis()/1000L;
					
					float alcohol = Float.valueOf(msg.substring(1));
					String output = timeStamp+"\t"+alcohol+"\n";
					testFragment.showDebug("time: "+timeStamp);
					testFragment.showDebug("alcohol: "+alcohol);
					
					sum+=alcohol;
					++count;
					/*write to the file*/
					write_to_file(output);
				}
			}
			else if (msg.charAt(0)=='m'){
				
				//Log.d("BT","READ-M");
				if (prev_pressure == 0.f){
					prev_pressure = Float.valueOf(msg.substring(1));
					prev_prev_pressure = Float.valueOf(msg.substring(1));
					Log.d("ERIC", "first pressure: "+prev_pressure);
					testFragment.showDebug("set_pressure: "+ prev_pressure);
				}
				else {
					prev_prev_pressure = prev_pressure;
					prev_pressure = now_pressure;
					now_pressure = Float.valueOf(msg.substring(1));
					testFragment.showDebug("set_pressure: "+ now_pressure);
					Log.d("ERIC", "set_pressure: "+ now_pressure);
					float diff = now_pressure - prev_pressure;
					
					long time = System.currentTimeMillis();
					if(prev_pressure < prev_prev_pressure && prev_pressure < now_pressure && !isPeak){
						local_min = prev_pressure;
						Log.d("ERIC", "set local min = " + prev_pressure);
					}
					else if(prev_pressure > prev_prev_pressure && prev_pressure > now_pressure){
						//local_max = prev_pressure;
						Log.d("ERIC", "set local max = " + prev_pressure);
					}
					if(local_min > 1 && now_pressure > local_min + 1000 && !isPeak){
						testFragment.showDebug("P_PeakStart" );
						Log.d("ERIC", "P_PeakStart");
						isPeak = true;
						start_time = time;
					}
					
					testFragment.showDebug("P_diff: "+diff );
					Log.d("ERIC", "P_diff: "+diff);
					if ( diff>PRESSURE_DIFF_MIN  && diff <PRESSURE_DIFF_MAX  && !isPeak){
					}else if (diff > -PRESSURE_DIFF_MIN/2){
						if (isPeak){
							testFragment.showDebug("P_Peak" );
							Log.d("ERIC","P_Peak");
							end_time = time;
							duration += (end_time-start_time);
							start_time = end_time;
							
							float value;
							if (count == 0)
								value = 0;
							else
								value = sum/count;
							
							if (duration > MILLIS_5){
								testFragment.showDebug("End of Blowing" );
								Log.d("ERIC","End of Blowing");
								show_in_UI(value,5);
							}else if (duration > MILLIS_4){
								show_in_UI(value,4);
							}else if (duration > MILLIS_3){
								show_in_UI(value,3);
							}else if (duration > MILLIS_2){
								show_in_UI(value,2);
							}else if (duration > MILLIS_1){
								show_in_UI(value,1);
							}
							
							if (image_count == 0 && duration > IMAGE_MILLIS_0){
								cameraRunHandler.sendEmptyMessage(0);
								++image_count;
							}
							else if (image_count == 1 && duration > IMAGE_MILLIS_1){
								cameraRunHandler.sendEmptyMessage(0);
								++image_count;
							}
							else if (image_count == 2 && duration >MAX_DURATION_MILLIS ){
								cameraRunHandler.sendEmptyMessage(0);
								++image_count;
								show_in_UI(value,6);
								success = true;
								return -1;
							}
							
						}
					}else if (diff <-PRESSURE_DIFF_MIN/2 ){
						testFragment.showDebug("P_PeakEnd" );
						Log.d("ERIC","P_PeakEnd");
						isPeak = false;
						start_time = end_time = 0;
					}
				}
			}
		}
		return 0;
	}
	
	public void close(){
		try {
			socket.close();
		} catch (Exception e) {
			Log.e("BT","FAIL TO CLOSE THE SENSOR");
		}
		
		try {
			if (in != null)
				in.close();
		} catch (Exception e) {
			Log.e("BT","FAIL TO CLOSE THE SENSOR INPUTSTREAM");
		}
		if (bracFileHandler!= null)
			bracFileHandler.close();
		if (bracDebugHandler !=null)
			bracDebugHandler.close();
	}
	
	private void write_to_file(String str){
		Message msg = new Message();
		Bundle data = new Bundle();
		data.putString("ALCOHOL", str);
		msg.setData(data);
		bracFileHandler.sendMessage(msg);
	}
	/*
	private void show_in_UI(int time){
		Message msg = new Message();
		Bundle data = new Bundle();
		data.putInt("TIME", time);
		msg.setData(data);
		msg.what = 0;
		btUIHandler.sendMessage(msg);
	}
	*/
	private void show_in_UI(float value,int time){
		Message msg = new Message();
		Bundle data = new Bundle();
		data.putFloat("value", value);
		data.putInt("TIME", time);
		msg.setData(data);
		msg.what = 2;
		btUIHandler.sendMessage(msg);
	}
	
}
