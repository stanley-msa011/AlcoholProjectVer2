package test.bluetooth;



import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Iterator;
import java.util.Set;
import java.util.UUID;


import test.camera.CameraRunHandler;
import test.data.BracValueDebugHandler;
import test.data.BracValueFileHandler;
import ubicomp.drunk_detection.fragments.TestFragment;

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

	protected BluetoothAdapter btAdapter;
	
	protected static final UUID uuid=  UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
	protected static String DEVICE_NAME = "BOLUTEK";
	protected static String DEVICE_NAME2 = "AEGIN";
	protected static String DEVICE_NAME_ALL = "apple";
	protected BluetoothDevice sensor;
	protected BluetoothSocket socket;
	
	protected InputStream in;
	protected Context context;
	
	protected float local_min;
	protected float prev_prev_pressure;
	protected float prev_pressure;
	protected float now_pressure;
	protected boolean isPeak = false;
	protected final static float PRESSURE_DIFF_MIN = 100.f;
	protected final static float PRESSURE_DIFF_MAX = 20000.f;
	protected final static long IMAGE_MILLIS_0 = 500;
	protected final static long IMAGE_MILLIS_1 = 2500;
	protected final static long IMAGE_MILLIS_2 = 4900;
	protected final static long MAX_DURATION_MILLIS = 5000;
	
	protected final static long MILLIS_1 = 1000;
	protected final static long MILLIS_2 = 2000;
	protected final static long MILLIS_3 = 3000;
	protected final static long MILLIS_4 = 4000;
	protected final static long MILLIS_5 = 5000;
	
	protected final static long START_MILLIS = 2000;
	protected final static long MAX_TEST_TIME = 15000;
	
	protected long start_time;
	protected long end_time;
	protected long first_start_time;
	protected long duration = 0;
	
	protected boolean start;
	
	protected boolean success;
	
	protected final static int READ_NULL = 0;
	protected final static int READ_ALCOHOL = 1;
	protected final static int READ_PRESSURE = 2;
	protected final static int READ_VOLTAGE = 3;
	
	protected Object lock = new Object();
	protected BTUIHandler btUIHandler;
	
	protected int image_count;
	
	protected CameraRunHandler cameraRunHandler;
	protected BracValueFileHandler bracFileHandler;
	
	protected TestFragment testFragment;
	
	protected int count;
	protected float sum;
	
	protected SharedPreferences sp;
	protected SharedPreferences.Editor sp_editor;
	
	protected boolean start_recorder = false;
	
	public Bluetooth(TestFragment testFragment, CameraRunHandler cameraRunHandler,BracValueFileHandler bracFileHandler, BracValueDebugHandler bracDebugHandler){
		this.testFragment = testFragment;
		this.context = testFragment.getActivity();
		this.cameraRunHandler = cameraRunHandler;
		this.bracFileHandler = bracFileHandler;
		btAdapter =  BluetoothAdapter.getDefaultAdapter();
		if (btAdapter == null)
			Log.e("BT","NOT SUPPORT BT");
		local_min = 0.f;
		prev_prev_pressure = 0.f;
		prev_pressure = 0.f;
		now_pressure = 0.f;
		btUIHandler=new BTUIHandler(testFragment);
		start = false;
		sum = 0;
		count = 0;
		start_recorder = false;
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
			if (device.getName().equals(DEVICE_NAME)||device.getName().equals(DEVICE_NAME2)||device.getName().contains(DEVICE_NAME_ALL)){
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
	
	protected class btReceiver extends BroadcastReceiver{
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
			if (Build.VERSION.SDK_INT<11)
				socket = sensor.createRfcommSocketToServiceRecord(uuid);
			else
				socket = sensor.createRfcommSocketToServiceRecord(uuid);
			btAdapter.cancelDiscovery();
			socket.connect();
		} catch (Exception e) {
			Log.e("BT","FAIL TO CONNECT TO THE SENSOR");
			Log.e("BT",e.toString());
			close();
			return -1;
		}
		return 1;
	}
	
	public void start(){
		start = true;
	}
	
	
	private final static byte[] sendStartMessage = {'y','y','y'};
	private final static byte[] sendEndMessage = {'z','z','z'};
	
	private OutputStream out;
	
	public boolean sendStart(){
		try {
			Log.d("BT","SEND START");
			out = socket.getOutputStream();
			out.write(sendStartMessage);
			return true;
		} catch (IOException e) {
			Log.d("BT","SEND START FAIL "+ e.toString());
			close();
			cameraRunHandler.sendEmptyMessage(1);
			return false;
		}
	}
	
	public boolean sendEnd(){
		try {
			Log.d("BT","SEND END");
			out.write(sendEndMessage);
			return true;
		} catch (IOException e) {
			Log.d("BT","SEND END FAIL "+ e.toString());
			close();
			return false;
		}
	}
	
	public void read(){
		
		int end=0;
		byte[] temp = new byte[512];
		int bytes;
		String msg = "";
		isPeak=false;
		success = false;
		local_min = 0;
		now_pressure = 0;
		prev_prev_pressure = 0;
		prev_pressure = 0;
		int read_type = READ_NULL;
		duration = 0;
		first_start_time = -1;
		image_count  =0;
		sum = 0;
		count = 0;
		start_recorder = false;
		try {
			in = socket.getInputStream();
			bytes =in.read(temp);
			while(bytes>0){
				if (!start){
					bytes =in.read(temp);
					continue;
				}
				long time = System.currentTimeMillis();
				long time_gap = time - first_start_time;
				if (first_start_time == -1)
					first_start_time = time;
				else if (time_gap > MAX_TEST_TIME){
					end =-1; 
					throw new Exception("time out");
				}
				
				for (int i=0;i<bytes;++i){
					if ((char)temp[i]=='a'){
						end = sendMsgToApp(msg);
						msg="a";
						read_type = READ_ALCOHOL;
					}
					else if ((char)temp[i]=='m'){
						end = sendMsgToApp(msg);
						msg="m";
						read_type = READ_PRESSURE;
					}
					else if ( (char)temp[i]=='b'){
						throw new Exception("NO BETTARY");
					}else if ((char)temp[i]=='v'){
						end = sendMsgToApp(msg);
						msg = "v";
						read_type = READ_VOLTAGE;
					}else if (read_type!= READ_NULL)
							msg += (char)temp[i];
				}
				if (end == -1){
					sendEnd();
					break;
				}
				bytes =in.read(temp);
			}
			close();
		} catch (Exception e) {
			Log.e("BT","FAIL TO READ DATA FROM THE SENSOR");
			sendEnd();
			close();
			if(!success)
					cameraRunHandler.sendEmptyMessage(1);
		}
	}
	protected int sendMsgToApp(String msg){
		synchronized(lock){
			if (msg=="");
				//Do nothing
			else if (msg.charAt(0)=='a'){
				if (isPeak){
					long timeStamp = System.currentTimeMillis()/1000L;
					
					float alcohol = Float.valueOf(msg.substring(1));
					String output = timeStamp+"\t"+alcohol+"\n";
					if (start_recorder){
						sum+=alcohol;
						++count;
						write_to_file(output);
					}
				}
			}
			else if (msg.charAt(0)=='m'){
				
				if (prev_pressure == 0.f){
					prev_pressure = Float.valueOf(msg.substring(1));
					prev_prev_pressure = Float.valueOf(msg.substring(1));
				}
				else {
					prev_prev_pressure = prev_pressure;
					prev_pressure = now_pressure;
					now_pressure = Float.valueOf(msg.substring(1));
					float diff = now_pressure - prev_pressure;
					
					long time = System.currentTimeMillis();
					if(prev_pressure < prev_prev_pressure && prev_pressure < now_pressure && !isPeak){
						local_min = prev_pressure;
					}
					if(local_min > 1 && now_pressure > local_min +PRESSURE_DIFF_MIN && !isPeak){
						isPeak = true;
						start_time = time;
					}
					
					if (diff > -PRESSURE_DIFF_MIN/2 && isPeak){
							end_time = time;
							duration += (end_time-start_time);
							start_time = end_time;
							
							float value;
							if (count == 0)
								value = 0;
							else
								value = sum/count;
							
							if (duration > MILLIS_5)
								show_in_UI(value,5);
							else if (duration > MILLIS_4)
								show_in_UI(value,4);
							else if (duration > MILLIS_3)
								show_in_UI(value,3);
							else if (duration > MILLIS_2)
								show_in_UI(value,2);
							else if (duration > MILLIS_1)
								show_in_UI(value,1);
							
							if (duration >= START_MILLIS)
								start_recorder = true;
							
							if (image_count == 0 && duration > IMAGE_MILLIS_0){
								cameraRunHandler.sendEmptyMessage(0);
								++image_count;
							}
							else if (image_count == 1 && duration > IMAGE_MILLIS_1){
								cameraRunHandler.sendEmptyMessage(0);
								++image_count;
							}else if (image_count == 2 && duration > IMAGE_MILLIS_2){
								cameraRunHandler.sendEmptyMessage(0);
								++image_count;
							}else if (image_count == 3 && duration >MAX_DURATION_MILLIS ){
								show_in_UI(value,6);
								success = true;
								return -1;
							}
							
					}else if (diff <-PRESSURE_DIFF_MIN /2){
						isPeak = false;
						start_time = end_time = 0;
					}
				}
			}else if (msg.charAt(0) == 'v'){	}
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
		try {
			if (out != null)
				out.close();
		} catch (Exception e) {
			Log.e("BT","FAIL TO CLOSE THE SENSOR OUTPUTSTREAM");
		}
		if (bracFileHandler!= null)
			bracFileHandler.close();
	}
	
	protected void write_to_file(String str){
		Message msg = new Message();
		Bundle data = new Bundle();
		data.putString("ALCOHOL", str);
		msg.setData(data);
		bracFileHandler.sendMessage(msg);
	}
	protected void show_in_UI(float value,int time){
		Message msg = new Message();
		Bundle data = new Bundle();
		data.putFloat("value", value);
		data.putInt("TIME", time);
		msg.setData(data);
		msg.what = 2;
		btUIHandler.sendMessage(msg);
	}
	
}
