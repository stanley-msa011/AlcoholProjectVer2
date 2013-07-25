package test.bluetooth;

import android.os.Bundle;
import android.os.Message;
import android.util.Log;
import test.camera.CameraRunHandler;
import test.file.BracValueDebugHandler;
import test.file.BracValueFileHandler;
import ubicomp.drunk_detection.activities.TestFragment;

public class BluetoothDebugModeNormal extends Bluetooth {

	protected BracValueDebugHandler bracDebugHandler;
	
	public BluetoothDebugModeNormal(TestFragment testFragment,
			CameraRunHandler cameraRunHandler,
			BracValueFileHandler bracFileHandler,
			BracValueDebugHandler bracDebugHandler) {
		super(testFragment, cameraRunHandler, bracFileHandler, bracDebugHandler);
		this.bracDebugHandler = bracDebugHandler;
	}

	private static int READ_A0 = 10;
	private static int READ_A1 = 11;
	@Override
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
					testFragment.showDebug("read before start");
					continue;
				}
				long time = System.currentTimeMillis();
				long time_gap = time - first_start_time;
				if (first_start_time == -1){
					first_start_time = time;
				}
				else if (time_gap > MAX_TEST_TIME){
					Log.d("BT","TIME OUT");
					end =-1; 
					throw new Exception("time out");
				}
				
				for (int i=0;i<bytes;++i){
					if ((char)temp[i]=='a'){
						end = sendMsgToApp(msg);
						sendDebugMsg(msg);
						msg="a";
						read_type = READ_A0;
					}else if ((char)temp[i]=='c'){
						end = sendMsgToApp(msg);
						sendDebugMsg(msg);
						msg="c";
						read_type = READ_A1;
					}else if ((char)temp[i]=='m'){
						end = sendMsgToApp(msg);
						sendDebugMsg(msg);
						msg="m";
						read_type = READ_PRESSURE;
					}else if ((char)temp[i]=='v'){
						end = sendMsgToApp(msg);
						sendDebugMsg(msg);
						msg = "v";
						read_type = READ_VOLTAGE;
					}else if (read_type!= READ_NULL){
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
			close();
			testFragment.showDebug("Close by exception or timeout" );
			if(!success){
					cameraRunHandler.sendEmptyMessage(1);
			}
		}
	}
	
	@Override
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
	protected String debugMsg = "";
	
	protected StringBuilder debugMsgBuilder = new StringBuilder();
	
	protected void sendDebugMsg(String msg){
		if (msg == "")
			return;
		if (msg.charAt(0) == 'm'){
			debugMsgBuilder.append(',');
			debugMsgBuilder.append(msg.substring(1, msg.length()-1));
			debugMsgBuilder.append("\n");
		}
		else if (msg.charAt(0) == 'a'){
			long timestamp = System.currentTimeMillis();
			debugMsgBuilder.append(timestamp);
			debugMsgBuilder.append(',');
			debugMsgBuilder.append(msg.substring(1,msg.length()-1));
			return;
		}else if (msg.charAt(0)=='v'){
			debugMsgBuilder.append(',');
			debugMsgBuilder.append(msg.substring(1,msg.length()-1));
			return;
		}
		else
			return;
		
		Message message = new Message();
		Bundle data = new Bundle();
		String output = debugMsgBuilder.toString();
		data.putString("ALCOHOL_DEBUG", output);
		debugMsgBuilder = new StringBuilder();
		message.setData(data);
		Log.d("DEBUG","DEBUG = "+output);
		bracDebugHandler.sendMessage(message);
	}
	
	private String temp_pressure;
	private float temp_brac;
	
	protected int sendMsgToApp(String msg){
		synchronized(lock){
			if (msg=="");
			else if (msg.charAt(0)=='a'){
				if (isPeak){
					long timeStamp = System.currentTimeMillis()/1000L;
					
					float alcohol = Float.valueOf(msg.substring(1));
					String output = timeStamp+"\t"+temp_pressure+"\t"+alcohol;
					testFragment.showDebug("time: "+timeStamp);
					testFragment.showDebug("a: "+alcohol);
					if (start_recorder){
						temp_brac = alcohol;
						write_to_file(output);
					}
				}
			}else if (msg.charAt(0)=='m'){
				if (prev_pressure == 0.f){
					prev_pressure = Float.valueOf(msg.substring(1));
					prev_prev_pressure = Float.valueOf(msg.substring(1));
					testFragment.showDebug("first_pressure: "+ prev_pressure);
				}
				else {
					prev_prev_pressure = prev_pressure;
					prev_pressure = now_pressure;
					temp_pressure = msg.substring(1,msg.length()-1);
					now_pressure = Float.valueOf(temp_pressure);
					testFragment.showDebug("set_pressure: "+ now_pressure);
					float diff = now_pressure - prev_pressure;
					
					long time = System.currentTimeMillis();
					if(prev_pressure < prev_prev_pressure && prev_pressure < now_pressure && !isPeak){
						local_min = prev_pressure;
					}
					if(local_min > 1 && now_pressure > local_min + PRESSURE_DIFF_MIN && !isPeak){
						testFragment.showDebug("P_PeakStart" );
						isPeak = true;
						start_time = time;
					}
					
					testFragment.showDebug("P_diff: "+diff );
					if (diff > -PRESSURE_DIFF_MIN /2&& isPeak){
							testFragment.showDebug("P_Peak" );
							end_time = time;
							duration += (end_time-start_time);
							start_time = end_time;
							
							float value = temp_brac;
							
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
							}else if (image_count == 1 && duration > IMAGE_MILLIS_1){
								cameraRunHandler.sendEmptyMessage(0);
								++image_count;
							}else if (image_count == 2 && duration > IMAGE_MILLIS_2){
								cameraRunHandler.sendEmptyMessage(0);
								++image_count;
							}else if (image_count == 3 && duration >MAX_DURATION_MILLIS ){
								testFragment.showDebug("End of Blowing" );
								show_in_UI(value,6);
								success = true;
								return -1;
							}
					}else if (diff <-PRESSURE_DIFF_MIN/2 ){
						testFragment.showDebug("P_PeakEnd" );
						isPeak = false;
						start_time = end_time = 0;
					}
				}
			}else if (msg.charAt(0) == 'v'){
				if (isPeak){
					float voltage = Float.valueOf(msg.substring(1));
					String output = "\t"+voltage+"\n";
					testFragment.showDebug("v: "+voltage);
					if (start_recorder)
						write_to_file(output);
				}
			}
		}
		return 0;
	}
	
	
	
}
