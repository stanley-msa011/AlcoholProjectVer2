package ioio.examples.hello;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.HttpVersion;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.ContentBody;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.CoreProtocolPNames;

import android.app.Activity;
import android.os.Bundle;
import android.os.Environment;
import android.provider.Settings.Secure;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

public class ShowBracActivity extends Activity {
	
	private static final String TAG = "ShowBracActivity";
	private static final String SERVER_URL = "http://140.112.30.165:80/drunk_detect_upload.php";
	
	private boolean fileSent;
	private File fileSDCard;
	private File mainStorageDir;
	private File textFile;
	private File imageFile;
	private String timeStamp;
	
	public TextView bracValueTextView;
    public ImageView thumbsImageView;
    public TextView tvConnectStatus;
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.show_brac);
        
        //timeStamp 1347886007
        //zoe的機器
        bracValueTextView = (TextView)findViewById(R.id.barc_value);
        thumbsImageView = (ImageView)findViewById(R.id.imageView1);
        tvConnectStatus = (TextView) findViewById(R.id.tvConnectStatus);
        int numberToForsakeHead = 2;
        int numberToForsakeTail = 2;
        double thresholdBracValue = 0.01;
        
        fileSDCard = null;
        
        fileSent = false;
        
        //先make sure SDcard is ready
        if(Environment.getExternalStorageState().equals(Environment.MEDIA_REMOVED))
        {
           return;
        }
        else
        {
//            fileSDCard = Environment.getExternalStorageDirectory();
        	mainStorageDir = new File(Environment.getExternalStorageDirectory(), "drunk_detection");
            Log.i("tag", "in else");
        }
        
        //the activity to call this view should pass in a key-value pair by
        //--> intent.putExtra("timestamp",1234567890); 
        timeStamp = this.getIntent().getStringExtra("timestamp");
//        textFile = new File(fileSDCard.getParent() + File.separator + fileSDCard.getName() + File.separator + "drunk_detection" + File.separator + timeStamp + File.separator + timeStamp + ".txt");
        textFile = new File(mainStorageDir.getPath() + File.separator + timeStamp + File.separator + timeStamp + ".txt");
        tvConnectStatus.setText(textFile.getPath());
        //File textFile = new File(fileSDCard.getParent() + "/" + fileSDCard.getName() + "/drunk_detection/1348033520/1348033520.txt");
        
        //second way - Scanner
        try {
			Scanner s = new Scanner(textFile);
			int index = 0;
			List<String> valueArray = new ArrayList<String>();
			
			while(s.hasNext()){
				index++;
				String word = s.next();
				if(index % 2 == 0){
					valueArray.add(word);
					Log.i("nextword added = ",word);

				}
				
			}
			
			//這時候數值都存起來了 要去頭去尾。
			for(int i = 0; i < numberToForsakeHead ;++i ){
				valueArray.remove(0);
			}
			for(int i = 0; i < numberToForsakeTail ;++i){
				valueArray.remove(valueArray.size()-1);
			}
			
			//計算並且顯示 平均值
			double average = 0;
			for(int i = 0; i < valueArray.size(); ++i){
				
				average += Double.parseDouble(valueArray.get(i));
				
			}
			average = average/valueArray.size();
			bracValueTextView.setText(String.valueOf(average));
			
			//set thumbs up/down 的pic
			if(average < thresholdBracValue){
				thumbsImageView.setImageResource(R.drawable.thumbs_up);
			}
			else thumbsImageView.setImageResource(R.drawable.thumbs_down);
			
			
		} catch (FileNotFoundException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
        
        
        /*
        FileInputStream fstream = null;
        String readString = "";  
        
        try 
        {
            fstream = new FileInputStream(textFile);
            StringBuffer buffer = new StringBuffer();
            int readChar;
            
            while ((readChar = fstream.read()) != -1) 
            { 
                buffer.append((char) readChar); 
            } 
            
            fstream.close(); 
            readString = buffer.toString(); 
            Log.i("file content = ",readString);
            
        } 
        catch (Exception e) 
        {
            e.printStackTrace();
        }
        
        */
        
    }
    
    @Override
    public void onResume() {
    	super.onResume();
    	
    	if (!fileSent) {
    		String debug = "Nothing!";
			try {
    			HttpClient httpClient = new DefaultHttpClient();
    			HttpPost httpPost = new HttpPost(SERVER_URL);
    			
    			httpClient.getParams().setParameter(CoreProtocolPNames.PROTOCOL_VERSION, HttpVersion.HTTP_1_1);
    			
//    			FileEntity entity = new FileEntity(textFile, "application/octet-stream");
//    			entity.setChunked(true);
//    			httpPost.setEntity(entity);
    			
//    			List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
//    			nameValuePairs.add(new BasicNameValuePair("androiddeviceid", "123456789"));
//    			httpPost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
    			
//    			HttpResponse httpResponse = httpClient.execute(httpPost);
    			
    			String devId = Secure.getString(this.getContentResolver(), Secure.ANDROID_ID);
    			
    			MultipartEntity mpEntity = new MultipartEntity();
    			
    			mpEntity.addPart("userData[]", new StringBody(devId));
    			mpEntity.addPart("userData[]", new StringBody(timeStamp));
    			
    			ContentBody cbFile = new FileBody(textFile, "application/octet-stream");
    			mpEntity.addPart("userfile[]", cbFile);
    			
    			imageFile = new File(mainStorageDir.getPath() + File.separator + timeStamp + File.separator + "IMG_" + timeStamp + "_1.jpg");
    			mpEntity.addPart("userfile[]", new FileBody(imageFile, "image/jpeg"));
    			
    			imageFile = new File(mainStorageDir.getPath() + File.separator + timeStamp + File.separator + "IMG_" + timeStamp + "_2.jpg");
    			mpEntity.addPart("userfile[]", new FileBody(imageFile, "image/jpeg"));
    			
    			imageFile = new File(mainStorageDir.getPath() + File.separator + timeStamp + File.separator + "IMG_" + timeStamp + "_3.jpg");
    			mpEntity.addPart("userfile[]", new FileBody(imageFile, "image/jpeg"));
    			
    			httpPost.setEntity(mpEntity);
    			
    			//debug = "Executing...";
    			HttpResponse httpResponse = httpClient.execute(httpPost);
    			//debug = "Executed!";
    			
    			int httpStatusCode = httpResponse.getStatusLine().getStatusCode();
    			if (httpStatusCode == HttpStatus.SC_OK) {
    				tvConnectStatus.setText("File successfully sent by device " + devId + "!");
    			} else {
    				tvConnectStatus.setText("Failed to send file! HTTP Response: " + httpStatusCode);
    			}
    			
    			fileSent = true;
    		} catch (ClientProtocolException cpe) {
    			Log.d(TAG, "Client Protocol Exception: " + cpe.getMessage());
    			cpe.printStackTrace();
//    			tvConnectStatus.setText(debug + " Client Protocol Exception: " + cpe.getMessage());
    			tvConnectStatus.setText("Client Protocol Exception: " + cpe.getMessage());
    		} catch (IOException ioe) {
    			Log.d(TAG, "IOException: " + ioe.getMessage());
    			ioe.printStackTrace();
//    			tvConnectStatus.setText(debug + " IOException: " + ioe.getMessage());
    			tvConnectStatus.setText("IOException: " + ioe.getMessage());
    		}
    	}
    }
}