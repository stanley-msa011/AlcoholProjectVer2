package test.bluetooth;

import android.os.Bundle;
import android.os.Message;
import android.util.Log;
import test.camera.CameraRunHandler;
import test.data.BracValueDebugHandler;
import test.data.BracValueFileHandler;
import ubicomp.drunk_detection.fragments.TestFragment;

public class BluetoothDebugModeNormal extends Bluetooth {

	protected BracValueDebugHandler bracDebugHandler;
	protected static float PRESSURE_DIFF_MIN_RANGE  = 50f;
	protected static float PRESSURE_DIFF_MIN =100.f;
	
	
	private String temp_pressure;
	
	public BluetoothDebugModeNormal(TestFragment testFragment,
			CameraRunHandler cameraRunHandler,
			BracValueFileHandler bracFileHandler,
			BracValueDebugHandler bracDebugHandler) {
		super(testFragment, cameraRunHandler, bracFileHandler, bracDebugHandler);
		this.bracDebugHandler = bracDebugHandler;
	}

	@Override
	public void start(){
		testFragment.showDebug("bluetooth start the test");
		start = true;
	}
	
	@Override
	public void read(){
		
		boolean end=false;
		byte[] temp = new byte[256];
		int bytes = 0;
		String msg = "";
		isPeak=false;
		absolute_min = MAX_PRESSURE;
		now_pressure = 0;
		int read_type = READ_NULL;
		duration = 0;
		temp_duration = 0;
		first_start_time = -1;
		image_count  =0;
		show_value = 0.f;
		zero_start_time = zero_end_time = zero_duration = 0;
		start_recorder = false;
		try {
			in = socket.getInputStream();
			testFragment.showDebug("bluetooth start to read");
			if (in.available() > 0)
				bytes =in.read(temp);
			else
				zero_start_time = System.currentTimeMillis();
			
			while(bytes>=0){
				long time = System.currentTimeMillis();
				long time_gap = time - first_start_time;
				if (first_start_time == -1)
					first_start_time = time;
				else if (time_gap > MAX_TEST_TIME)
					throw new Exception("timeout");
				
				for (int i=0;i<bytes;++i){
					if ((char)temp[i]=='a'){
						end = sendMsgToApp(msg);
						sendDebugMsg(msg);
						msg="a";
						read_type = READ_ALCOHOL;
					}else if ((char)temp[i]=='m'){
						end = sendMsgToApp(msg);
						sendDebugMsg(msg);
						msg="m";
						read_type = READ_PRESSURE;
					}else if ( (char)temp[i]=='b'){
							throw new Exception("NO BETTARY");
					}else if ((char)temp[i]=='v'){
						end = sendMsgToApp(msg);
						sendDebugMsg(msg);
						msg = "v";
						read_type = READ_VOLTAGE;
					}else if (read_type!= READ_NULL){
							msg += (char)temp[i];
					}
				}
				if (end)
					break;
				if (in.available() > 0){
					bytes =in.read(temp);
					zero_start_time = System.currentTimeMillis();
				}else{
					bytes = 0;
					if (zero_start_time == 0)
						zero_start_time = System.currentTimeMillis();
					zero_end_time = System.currentTimeMillis();
					zero_duration += ( zero_end_time - zero_start_time);
					zero_start_time = zero_end_time;
					if (zero_duration > MAX_ZERO_DURATION)
						throw new Exception("NO BETTARY");
					Thread.sleep(50);
				}
				
			}
			close();
		} catch (Exception e) {
			Log.e("BT","FAIL TO READ DATA FROM THE SENSOR: " +e.toString());
			close();
			if (e.getMessage()!=null && e.getMessage().equals("TIMEOUT")){
				testFragment.showDebug("Close by timeout" );
				cameraRunHandler.sendEmptyMessage(3);
			}else{
				testFragment.showDebug("Close by exception" );
				cameraRunHandler.sendEmptyMessage(2);
			}
		}
	}
	
	@Override
	public void close(){
		try {
			if (socket != null && socket.isConnected()){
				sendEnd();
				socket.close();
			}
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
		bracDebugHandler.sendMessage(message);
	}
	
	@Override
	protected boolean sendMsgToApp(String msg){
		synchronized(lock){
			if (msg=="");
				//Do nothing
			else if (msg.charAt(0)=='a'){
				if (isPeak){
					long timeStamp = System.currentTimeMillis()/1000L;
					float alcohol = Float.valueOf(msg.substring(1));
					String output = timeStamp+"\t"+temp_pressure+"\t"+alcohol;
					testFragment.showDebug("time: "+timeStamp);
					testFragment.showDebug("alcohol: "+alcohol);
					if (start_recorder){
						show_value = alcohol;
						write_to_file(output);
					}
				}
			}else if (msg.charAt(0)=='m'){

				temp_pressure = msg.substring(1,msg.length()-1);
				now_pressure = Float.valueOf(temp_pressure);
				
				long time = System.currentTimeMillis();
				
				if(!start&&now_pressure < absolute_min){
					absolute_min = now_pressure;
					testFragment.showDebug("absolute min = "+absolute_min );
				}
					
				if (!start){
					testFragment.showDebug("read before start testing" );
					return false;
				}
					
				float diff_limit = PRESSURE_DIFF_MIN_RANGE * (5000.f - temp_duration)/5000.f + PRESSURE_DIFF_MIN;
				
				testFragment.showDebug("p: "+ now_pressure + " min: "+absolute_min+" l:"+diff_limit);
				
				if(now_pressure > absolute_min + diff_limit && !isPeak){
					testFragment.showDebug("Peak start" );
					isPeak = true;
					start_time = time;
					temp_duration = 0;
				}else if (now_pressure > absolute_min + diff_limit && isPeak){
					testFragment.showDebug("is Peak" );
					end_time = time;
					duration += (end_time-start_time);
					temp_duration += (end_time-start_time);
					start_time = end_time;
							
					if (duration > MILLIS_5)
						show_in_UI(show_value,5);
					else if (duration > MILLIS_4)
						show_in_UI(show_value,4);
					else if (duration > MILLIS_3)
						show_in_UI(show_value,3);
					else if (duration > MILLIS_2)
						show_in_UI(show_value,2);
					else if (duration > MILLIS_1)
						show_in_UI(show_value,1);
					
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
						testFragment.showDebug("test end" );
						show_in_UI(show_value,6);
						return true;
					}
				}else if (isPeak ){
					testFragment.showDebug("Peak end" );
					isPeak = false;
					start_time = end_time = 0;
				}
			}else if (msg.charAt(0) == 'v'){
				if (isPeak){
					float voltage = Float.valueOf(msg.substring(1));
					String output = "\t"+voltage+"\n";
					testFragment.showDebug("voltage: "+voltage);
					if (start_recorder)
						write_to_file(output);
				}
			}
		}
		return false;
	}
	
}
