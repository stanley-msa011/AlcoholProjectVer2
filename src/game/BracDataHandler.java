package game;

import ioio.examples.hello.BracDbAdapter;

import java.io.File;
import java.io.FileNotFoundException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Scanner;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.HttpVersion;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.ContentBody;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.CoreProtocolPNames;

import android.content.Context;
import android.os.Environment;
import android.provider.Settings.Secure;
import android.util.Log;


public class BracDataHandler {
	String ts;
	Context activity_context;
	String TAG = "BRACDATAHANDLER";
	
	public BracDataHandler(String timestamp_string, Context activity){
		ts = timestamp_string;
		activity_context = activity;
	}
	
	public static final int HaveAlcohol = 11;
	public static final int NoAlcohol = 10; 
	public static final int ERROR = -1;
	public static final int SUCCESS = 1;
	private static final double THRESHOLD = 0.01;
	
	private static final String SERVER_URL = "http://140.112.30.165:80/drunk_detect_upload.php";
	
	public int start(){
		
		File mainStorageDir;
		File textFile;
		File[] imageFiles = new File[3];

        if(Environment.getExternalStorageState().equals(Environment.MEDIA_REMOVED))
           return ERROR;
        else
        	mainStorageDir = new File(Environment.getExternalStorageDirectory(), "drunk_detection");
        
        textFile = new File(mainStorageDir.getPath() + File.separator + ts + File.separator + ts + ".txt");
        imageFiles[0] = new File(mainStorageDir.getPath() + File.separator + ts + File.separator + "IMG_" + ts + "_1.jpg");
        imageFiles[1] = new File(mainStorageDir.getPath() + File.separator + ts + File.separator + "IMG_" + ts + "_2.jpg");
        imageFiles[2] = new File(mainStorageDir.getPath() + File.separator + ts + File.separator + "IMG_" + ts + "_3.jpg");
       	
        double avg_result = parseTextFile(textFile);
       	int result = 0;
       	
       	if (avg_result == ERROR)
       		return ERROR;
       	if (avg_result < THRESHOLD)
			result = NoAlcohol;
       	else
       		result = HaveAlcohol;
       	

       	int server_connect = connectingToServer(textFile,imageFiles);
		if (server_connect == ERROR)
			return ERROR;
     	
		saveToDB(avg_result);
       	
		return result;
		
	}
	
	private double parseTextFile(File textFile){
        int numberToForsakeHead = 2;
        int numberToForsakeTail = 2;
		double avg_result = 0;
        try {
			Scanner s = new Scanner(textFile);
			int index = 0;
			List<String> valueArray = new ArrayList<String>();
			
			while(s.hasNext()){
				index++;
				String word = s.next();
				if(index % 2 == 0)
					valueArray.add(word);
			}
			if (valueArray.size()>=5){
				for(int i = 0; i < numberToForsakeHead ;++i )
					valueArray.remove(0);
				for(int i = 0; i < numberToForsakeTail ;++i)
					valueArray.remove(valueArray.size()-1);
			}
			for(int i = 0; i < valueArray.size(); ++i)
				avg_result += Double.parseDouble(valueArray.get(i));
			if (valueArray.size()==0)
				return ERROR;
			avg_result /= valueArray.size();

			
		} catch (FileNotFoundException e1) {
			return ERROR;
		}
        return avg_result;
	}
	
	private int connectingToServer(File textFile,File[] imageFiles){
		try {
			HttpClient httpClient = new DefaultHttpClient();
			HttpPost httpPost = new HttpPost(SERVER_URL);
			
			httpClient.getParams().setParameter(CoreProtocolPNames.PROTOCOL_VERSION, HttpVersion.HTTP_1_1);
			Log.e(TAG, "http setParams");
			
			String devId = Secure.getString(activity_context.getContentResolver(), Secure.ANDROID_ID);
			
			Log.e(TAG, "get devID");
			
			MultipartEntity mpEntity = new MultipartEntity();
			
			mpEntity.addPart("userData[]", new StringBody(devId));
			mpEntity.addPart("userData[]", new StringBody(ts));
			
			Log.e(TAG, "add part #1");
			
			ContentBody cbFile = new FileBody(textFile, "application/octet-stream");
			mpEntity.addPart("userfile[]", cbFile);
			
			Log.e(TAG, "add part #2");
			
			mpEntity.addPart("userfile[]", new FileBody(imageFiles[0], "image/jpeg"));
			
			mpEntity.addPart("userfile[]", new FileBody(imageFiles[1], "image/jpeg"));
			
			mpEntity.addPart("userfile[]", new FileBody(imageFiles[2], "image/jpeg"));
			
			Log.e(TAG, "add part #3");
			
			httpPost.setEntity(mpEntity);
			
			Log.e(TAG, "set entity");
			
			BracDataToServer BDT = new BracDataToServer(httpClient,httpPost);
			Thread thread = new Thread(BDT);
			thread.start();
			thread.join();
			if (BDT.result==-1)
				return ERROR;
			/*
			HttpResponse httpResponse = httpClient.execute(httpPost);
			
			Log.e(TAG, "get response");
			
			int httpStatusCode = httpResponse.getStatusLine().getStatusCode();
			
			Log.e(TAG, "get http status");
			
			if (httpStatusCode == HttpStatus.SC_OK) {} 
			else
				return ERROR;
			*/
			
		} catch (Exception e) {
			Log.e(TAG,e.toString());
			return ERROR;
		} 
		return SUCCESS;
	}
	
	private void saveToDB(double avg_value){
		Date time = new Date(Long.parseLong(ts)*1000L);
		String dateStamp = new SimpleDateFormat("MM/dd/yyyy\nkk:mm",Locale.TAIWAN).format(time);
		
		DecimalFormat df = new DecimalFormat("0.000");
		String strAvg = df.format(avg_value);

	    BracDbAdapter mBracDbAdapter = new BracDbAdapter(activity_context);
        mBracDbAdapter.open();
	    mBracDbAdapter.createEntry(dateStamp, strAvg);
		mBracDbAdapter.close();
	}
	

}
