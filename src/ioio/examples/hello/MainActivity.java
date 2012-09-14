package ioio.examples.hello;

import ioio.lib.api.AnalogInput;
import ioio.lib.api.DigitalOutput;
import ioio.lib.api.exception.ConnectionLostException;
import ioio.lib.util.AbstractIOIOActivity;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.text.DecimalFormat;
import java.util.Calendar;


import android.os.Bundle;
import android.os.Environment;
import android.widget.TextView;
import android.widget.ToggleButton;

/**
 * This is the main activity of the HelloIOIO example application.
 * 
 * It displays a toggle button on the screen, which enables control of the
 * on-board LED. This example shows a very simple usage of the IOIO, by using
 * the {@link AbstractIOIOActivity} class. For a more advanced use case, see the
 * HelloIOIOPower example.
 */
public class MainActivity extends AbstractIOIOActivity {
	private final static String TAG = "IOIO";
	//private ToggleButton button_;

	private ToggleButton button_led_2;
	private ToggleButton button_led_4;
	

	private double num; 
	
	DecimalFormat nf;
	
	TextView row_value;

	TextView storage_state;

	private float value;

	String filename;
	File textfile;
	FileOutputStream stream;
	String path;
	OutputStreamWriter sensor_value;
	
	Calendar calendar;
	
	
	
	/**
	 * Called when the activity is first created. Here we normally initialize
	 * our GUI.
	 */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		//button_ = (ToggleButton) findViewById(R.id.button);
		button_led_2 = (ToggleButton) findViewById(R.id.button_led2);
		button_led_4 = (ToggleButton) findViewById(R.id.button_led4);
	
		num = 100.0;
		nf = new DecimalFormat("0.0000");
		row_value = (TextView)findViewById(R.id.row_value);
	
	
	
		storage_state = (TextView)findViewById(R.id.storage_state);
		
		//先取得sdcard目錄
		path = Environment.getExternalStorageDirectory().getPath();
		//利用File來設定目錄的名稱(alcohol_value)
		File dir = new File(path + "/alcohol_value");
		//先檢查該目錄是否存在
		if (!dir.exists()){
		    //若不存在則建立它
		    dir.mkdir();
		}
		
		
		

		
	}
	
	
	/**
	 * This is the thread on which all the IOIO activity happens. It will be run
	 * every time the application is resumed and aborted when it is paused. The
	 * method setup() will be called right after a connection with the IOIO has
	 * been established (which might happen several times!). Then, loop() will
	 * be called repetitively until the IOIO gets disconnected.
	 */
	
	
	
	class IOIOThread extends AbstractIOIOActivity.IOIOThread {
		/** The on-board LED. */
		//private DigitalOutput led_;
		private DigitalOutput led_2;
		private DigitalOutput led_4;

		
		private AnalogInput in_40;
		
		
		
		/**
		 * Called every time a connection with IOIO has been established.
		 * Typically used to open pins.
		 * 
		 * @throws ConnectionLostException
		 *             When IOIO connection is lost.
		 * 
		 * @see ioio.lib.util.AbstractIOIOActivity.IOIOThread#setup()
		 */
		@Override
		protected void setup() throws ConnectionLostException {
			//led_ = ioio_.openDigitalOutput(0, true);
			led_2 = ioio_.openDigitalOutput(2, true);
			led_4 = ioio_.openDigitalOutput(4, true);
		
			//in_3 = ioio_.openDigitalInput(3);
			in_40 = ioio_.openAnalogInput(40);
			
			
			runOnUiThread(new Runnable() { 
		        public void run() 
		        {			
		        	//取得外部儲存媒體的狀態
					String state = Environment.getExternalStorageState();
					//判斷狀態
					if (Environment.MEDIA_MOUNTED.equals(state)) {
						storage_state.setText("can written");
					} else if (Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)) {
						storage_state.setText("read only, can't written");
					} else {
						storage_state.setText("can't read and written");
					}
  
		        } 
		    });
			
			
			calendar = Calendar.getInstance(); 
			filename = calendar.get(Calendar.YEAR) +"_" +calendar.get(Calendar.MONTH)+ "_" + calendar.get(Calendar.DATE) 
						+ "_" + calendar.get(Calendar.HOUR_OF_DAY) + "_" + calendar.get(Calendar.MINUTE) 
						+ "_"+ calendar.get(Calendar.SECOND) + ".txt";
			
			textfile = new File(path + "/alcohol_value/" + filename);
						
			try {
				stream = new FileOutputStream(textfile);
				
				sensor_value = new OutputStreamWriter(stream, "US-ASCII");
				
			} catch (FileNotFoundException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			} catch (UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			
			
			
		
		}

		/**
		 * Called repetitively while the IOIO is connected.
		 * 
		 * @throws ConnectionLostException
		 *             When IOIO connection is lost.
		 * 
		 * @see ioio.lib.util.AbstractIOIOActivity.IOIOThread#loop()
		 */
		
		
		
		@Override
		protected void loop() throws ConnectionLostException {
			//led_.write(!button_.isChecked());
			//led_2.write(!button_led_2.isChecked());
			
			//led_4.write(!button_led_4.isChecked());
			

			
			
			try {
				value = in_40.read();
				
				calendar = Calendar.getInstance(); 
				
				if(!button_led_2.isChecked())
				{
					sensor_value.write(calendar.get(Calendar.HOUR_OF_DAY) + "_" + calendar.get(Calendar.MINUTE) 
							+ "_"+ calendar.get(Calendar.SECOND) + "_"+ calendar.get(Calendar.MILLISECOND) + "\t");
					
					sensor_value.write(String.valueOf(value) + "\t");
					
					sensor_value.write("\r\n");
				}
				else
				{
					sensor_value.close();
					
				}
				
				runOnUiThread(new Runnable() { 
			        public void run() 
			        {			
			        	row_value.setText("Row value: " + nf.format(value)+", button isChecked " + button_led_2.isChecked());
	  
			        	
			      
			        	
			        } 
			    });
				
				
			} catch (InterruptedException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			
			try {
				
				led_2.write(true);
				sleep(1000);
				led_2.write(false);
				sleep(1000);
				
			} catch (InterruptedException e) {
			}
		}
	}

	/**
	 * A method to create our IOIO thread.
	 * 
	 * @see ioio.lib.util.AbstractIOIOActivity#createIOIOThread()
	 */
	@Override
	protected AbstractIOIOActivity.IOIOThread createIOIOThread() {
		return new IOIOThread();
	}
}