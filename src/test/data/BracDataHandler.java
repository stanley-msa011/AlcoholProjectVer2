package test.data;

import history.BracGameHistory;
import history.DateBracGameHistory;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.InputStream;
import java.security.KeyStore;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Scanner;


import main.activities.R;
import main.activities.TestFragment;

import org.apache.http.HttpVersion;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.ContentBody;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.CoreProtocolPNames;

import database.HistoryDB;
import database.TimeBlock;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.provider.Settings.Secure;
import android.util.Log;

public class BracDataHandler {
	private String ts;
	private Context context;
	private String devId;
	private double avg_result = 0;
	private HistoryDB db;
	
	public BracDataHandler(String timestamp_string, TestFragment fragment){
		ts = timestamp_string;
		context = fragment.getActivity();
		db = new HistoryDB(fragment.getActivity());
		this.devId = Secure.getString(context.getContentResolver(), Secure.ANDROID_ID);
	}
	
	public static final int Nothing = 0; 
	public static final int ERROR = -1;
	public static final int SUCCESS = 1;
	public static final double THRESHOLD = 0.004;
	public static final double THRESHOLD2 = 0.25;
	
	private static final String SERVER_URL = "https://140.112.30.165/develop/drunk_detection/drunk_detect_upload.php";
	
	public int start(){
		
		File mainStorageDir;
		File textFile, geoFile, stateFile;
		File[] imageFiles = new File[3];
		int result = SUCCESS;
		
        if(Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED))
        	mainStorageDir = new File(Environment.getExternalStorageDirectory(), "drunk_detection");
        else
        	mainStorageDir = new File(context.getFilesDir(),"drunk_detection");
        
        textFile = new File(mainStorageDir.getPath() + File.separator + ts + File.separator + ts + ".txt");
        geoFile = new File(mainStorageDir.getPath() + File.separator + ts + File.separator + "geo.txt");
        
        imageFiles[0] = new File(mainStorageDir.getPath() + File.separator + ts + File.separator + "IMG_" + ts + "_1.jpg");
        imageFiles[1] = new File(mainStorageDir.getPath() + File.separator + ts + File.separator + "IMG_" + ts + "_2.jpg");
        imageFiles[2] = new File(mainStorageDir.getPath() + File.separator + ts + File.separator + "IMG_" + ts + "_3.jpg");
       	
        avg_result = parseTextFile(textFile);
        
        BracGameHistory history= db.getLatestBracGameHistory();
        
        history.brac = (float)avg_result;
        history.timestamp = Long.parseLong(ts);
        
        DateBracGameHistory prevHistory = db.getLatestBracGameHistory();
        int year,month,date,timeblock,hour;
        
    	Calendar cal = Calendar.getInstance();
    	cal.setTimeInMillis(history.timestamp*1000);
    	year = cal.get(Calendar.YEAR);
    	month = cal.get(Calendar.MONTH)+1;
    	date = cal.get(Calendar.DATE);
    	hour = cal.get(Calendar.HOUR_OF_DAY);
    	timeblock = TimeBlock.getTimeBlock(hour);
    
    	//check time block
    	boolean check_time_block = true;
    	if (check_time_block){
    		if (timeblock==-1);
    		else if (year == prevHistory.year && month == prevHistory.month && date == prevHistory.date && timeblock == prevHistory.timeblock);
    		else{
    			if (avg_result != ERROR){
    				if (avg_result > THRESHOLD)
    					history.changeLevel(0);
    				else
    					history.changeLevel(+1);
    			}
    			else{
    				result = ERROR;
    			}
    		}
    	}else{
			if (avg_result != ERROR){
				if (avg_result > THRESHOLD)
					history.changeLevel(0);
				else
					history.changeLevel(+1);
			}
    	}
        
        db.insertNewState(history);
        
       	stateFile = new File(mainStorageDir.getPath() + File.separator + ts + File.separator + "state.txt");
       	try {
       		BufferedWriter state_writer = new BufferedWriter(new FileWriter(stateFile));
       		state_writer.write(String.valueOf(history.level));
       		state_writer.flush();
       		state_writer.close();
		} catch (Exception e) {	
			e.printStackTrace();	
		}
       	
       	/*Connection to server*/
       	int server_connect = connectingToServer(textFile,geoFile,stateFile,imageFiles);
		if (server_connect == ERROR) // error happens when preparing files
			result = ERROR;
		if (result == ERROR){
			long _ts = Long.valueOf(ts);
			db.insertNotUploadedTS(_ts);
		}
		return result;
	}
	
	public double getResult(){
		return avg_result;
	}
	
	private double parseTextFile(File textFile){
		double avg = 0;
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
			for(int i = 0; i < valueArray.size(); ++i)
				avg += Double.parseDouble(valueArray.get(i));
			if (valueArray.size()==0)
				return ERROR;
			avg /= valueArray.size();
			
		} catch (FileNotFoundException e1) {
			return ERROR;
		}
        return avg;
	}
	
	
	private int connectingToServer(File textFile, File geoFile, File stateFile, File[] imageFiles){
		try {
			
			Log.d("DataHandler","Start Init");
			DefaultHttpClient httpClient = new DefaultHttpClient();
			
			KeyStore trustStore = KeyStore.getInstance(KeyStore.getDefaultType());
			Log.d("DataHandler","got key");
			InputStream instream = context.getResources().openRawResource(R.raw.alcohol_certificate);
			try{
				Log.d("DataHandler","new instream");
				trustStore.load(instream, null);
				Log.d("DataHandler","load");
			} finally{
				instream.close();
			}
			Log.d("DataHandler","Start ssl setting");
			SSLSocketFactory socketFactory = new SSLSocketFactory(trustStore);
			Scheme sch = new Scheme("https",socketFactory,443);
			
			httpClient.getConnectionManager().getSchemeRegistry().register(sch);
			
			Log.d("DataHandler","end ssl setting");
			
			HttpPost httpPost = new HttpPost(SERVER_URL);
			httpClient.getParams().setParameter(CoreProtocolPNames.PROTOCOL_VERSION, HttpVersion.HTTP_1_1);
			MultipartEntity mpEntity = new MultipartEntity();
			
			SharedPreferences sp= PreferenceManager.getDefaultSharedPreferences(context);
			String uid = sp.getString("uid", "");
			mpEntity.addPart("userData[]", new StringBody(uid));
			mpEntity.addPart("userData[]", new StringBody(ts));
			mpEntity.addPart("userData[]", new StringBody(devId));
			
			ContentBody cbFile = new FileBody(textFile, "application/octet-stream");
			mpEntity.addPart("userfile[]", cbFile);
			if (geoFile.exists()){
				ContentBody cbGeoFile = new FileBody(geoFile, "application/octet-stream");
				mpEntity.addPart("userfile[]", cbGeoFile);
			}
			if(stateFile.exists()){
				ContentBody cbStateFile = new FileBody(stateFile, "application/octet-stream");
				mpEntity.addPart("userfile[]", cbStateFile);
			}
			
			if (imageFiles[0].exists())
				mpEntity.addPart("userfile[]", new FileBody(imageFiles[0], "image/jpeg"));
			if (imageFiles[1].exists())
				mpEntity.addPart("userfile[]", new FileBody(imageFiles[1], "image/jpeg"));
			if (imageFiles[2].exists())
				mpEntity.addPart("userfile[]", new FileBody(imageFiles[2], "image/jpeg"));
			
			httpPost.setEntity(mpEntity);
			DataUploader uploader = new DataUploader(httpClient,httpPost,ts,context);
			Log.d("DataHandler","uploader execute");
			uploader.execute();
			
		} catch (Exception e) {
			return ERROR;
		} 
		return SUCCESS;
	}
}
