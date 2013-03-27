package test.bluetooth;


import ioio.examples.hello.TestFragment;

import java.io.InputStream;
import java.util.Iterator;
import java.util.Set;
import java.util.UUID;

import test.camera.CameraRunHandler;
import test.file.BracValueFileHandler;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Bundle;
import android.os.Message;
import android.util.Log;

public class Bluetooth {

	private BluetoothAdapter btAdapter;
	
	private static final UUID uuid=  UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
	private static String DEVICE_NAME = "BOLUTEK";
	private BluetoothDevice sensor;
	private BluetoothSocket socket;
	
	private InputStream in;
	private Context context;
	
	private int counter = 0;
	private float prev_pressure;
	private float now_pressure;
	private boolean isPeak = false;
	private final static float PRESSURE_DIFF_MIN = 300.f;
	private final static float PRESSURE_DIFF_MAX = 20000.f;
	private final static long IMAGE_MILLIS_0 = 500;
	private final static long IMAGE_MILLIS_1 = 2500;
	private final static long MAX_DURATION_MILLIS = 5000;
	private long start_time;
	private long end_time;
	private long duration = 0;
	
	private final static int READ_NULL = 0;
	private final static int READ_ALCOHOL = 1;
	private final static int READ_PRESSURE = 2;
	
	private Object lock = new Object();
	private BTUIHandler btUIHandler;
	
	private double sum;
	
	private int image_count;
	
	private CameraRunHandler cameraRunHandler;
	private BracValueFileHandler bracFileHandler;
	
	
	public Bluetooth(TestFragment testFragment, CameraRunHandler cameraRunHandler,BracValueFileHandler bracFileHandler){
		this.context = testFragment.getActivity();
		this.cameraRunHandler = cameraRunHandler;
		this.bracFileHandler = bracFileHandler;
		btAdapter =  BluetoothAdapter.getDefaultAdapter();
		if (btAdapter == null)
			Log.e("BT","NOT SUPPORT BT");
		prev_pressure = 0.f;
		now_pressure = 0.f;
		btUIHandler=new BTUIHandler(testFragment);
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
			if (device.getName().equals(DEVICE_NAME)){
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
				if (device.getName().equals(DEVICE_NAME)){
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
				socket = sensor.createInsecureRfcommSocketToServiceRecord(uuid);
			socket.connect();
		} catch (Exception e) {
			Log.e("BT","FAIL TO CONNECT THE SENSOR");
			close();
			return -1;
		}
		return 1;
	}
	
	public void read(){
		
		byte[] temp = new byte[1024];
		int bytes;
		String msg = "";
		isPeak=false;
		now_pressure = 0;
		prev_pressure = 0;
		int read_type = READ_NULL;
		duration = 0;
		sum = 0;
		counter = 0;
		image_count  =0;
		try {
			in = socket.getInputStream();
			bytes =in.read(temp);
			while(bytes>0){
				//Log.d("BT","READ");
				for (int i=0;i<bytes;++i){
					if ((char)temp[i]=='a'){
						sendMsgToApp(msg);
						msg="a";
						read_type = READ_ALCOHOL;
					}
					else if ((char)temp[i]=='m'){
						sendMsgToApp(msg);
						msg="m";
						read_type = READ_PRESSURE;
					}
					else if (read_type!= READ_NULL){
							msg += (char)temp[i];
					}
				}
				bytes =in.read(temp);
			}
		} catch (Exception e) {
			Log.e("BT","FAIL TO READ DATA FROM THE SENSOR");
			close();
		}
	}
	
	private void sendMsgToApp(String msg){
		synchronized(lock){
			if (msg=="");
				//Do nothing
			else if (msg.charAt(0)=='a'){
				if (isPeak){
					long timeStamp = System.currentTimeMillis()/1000L;
					
					float alcohol = Float.valueOf(msg.substring(1));
					String output = timeStamp+" "+alcohol+"\n";
					counter++;
					sum+=alcohol;
					/*write to the file*/
					write_to_file(output);
					show_in_UI(output);
				}
			}
			else if (msg.charAt(0)=='m'){
				//Log.d("BT","READ-M");
				if (prev_pressure == 0.f){
					prev_pressure = Float.valueOf(msg.substring(1));
					//first_pressure;
				}
				else {
					prev_pressure = now_pressure;
					now_pressure = Float.valueOf(msg.substring(1));
					float diff = now_pressure - prev_pressure;
					String diff_str = "<"+diff+">:"+now_pressure+"/"+prev_pressure;
					Log.d("Pressure",diff_str);
					if ( diff>PRESSURE_DIFF_MIN  && diff <PRESSURE_DIFF_MAX  && !isPeak){
						isPeak = true;
						start_time = System.currentTimeMillis();
					}else if ( diff < PRESSURE_DIFF_MIN && diff > -PRESSURE_DIFF_MIN){
						if (isPeak){
							end_time = System.currentTimeMillis();
							duration += (end_time-start_time);
							start_time = end_time;
							
							if (image_count == 0 && duration > IMAGE_MILLIS_0){
								cameraRunHandler.sendEmptyMessage(0);
								++image_count;
							}
							else if (image_count == 1 && duration > IMAGE_MILLIS_1){
								cameraRunHandler.sendEmptyMessage(0);
								++image_count;
							}else if (image_count == 2 && duration >MAX_DURATION_MILLIS ){
								cameraRunHandler.sendEmptyMessage(0);
								++image_count;
								close();
								String output = "<"+sum/counter+">";
								show_in_UI(output);
							}
							
						}
					}else if (diff <-PRESSURE_DIFF_MIN ){
						isPeak = false;
						start_time = end_time = 0;
					}
				}
			}
		}
	}
	
	public void close(){
		try {
			if (in != null)
				in.close();
		} catch (Exception e) {
			Log.e("BT","FAIL TO CLOSE THE SENSOR INPUTSTREAM");
		}
		try {
			socket.close();
		} catch (Exception e) {
			Log.e("BT","FAIL TO CLOSE THE SENSOR");
		}
		if (bracFileHandler!= null)
			bracFileHandler.close();
	}
	
	private void write_to_file(String str){
		Message msg = new Message();
		Bundle data = new Bundle();
		data.putString("ALCOHOL", str);
		msg.setData(data);
		bracFileHandler.sendMessage(msg);
	}
	private void show_in_UI(String str){
		Message msg = new Message();
		Bundle data = new Bundle();
		data.putString("ALCOHOL", str);
		msg.setData(data);
		btUIHandler.sendMessage(msg);
	}
}