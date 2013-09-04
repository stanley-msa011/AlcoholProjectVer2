package test.data;


import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.InputStream;
import java.security.KeyStore;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.Scanner;


import ubicomp.drunk_detection.activities.R;

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

import ubicomp.drunk_detection.fragments.TestFragment;

import data.calculate.WeekNum;
import data.database.HistoryDB;
import data.database.StartDateCheck;
import data.info.AccumulatedHistoryState;
import data.info.DateBracDetectionState;
import data.uploader.DataUploader;
import data.uploader.ServerUrl;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.provider.Settings.Secure;
import android.util.Log;

public class BracDataHandler {
	protected String ts;
	protected Context context;
	private String devId;
	protected double avg_result = 0;
	protected HistoryDB db;
	private static String SERVER_URL;
	
	public BracDataHandler(String timestamp_string, TestFragment fragment){
		ts = timestamp_string;
		context = fragment.getActivity();
		db = new HistoryDB(fragment.getActivity());
		this.devId = Secure.getString(context.getContentResolver(), Secure.ANDROID_ID);
		SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
		SERVER_URL = ServerUrl.SERVER_URL_TEST(sp.getBoolean("developer", false));
	}
	
	public static final int Nothing = 0; 
	public static final int ERROR = -1;
	public static final int SUCCESS = 1;
	public static final double THRESHOLD = 0.03;
	public static final double THRESHOLD2 = 0.25;
	
	
	
	public int start(){
		
		File mainStorageDir;
		File textFile, geoFile, stateFile, questionFile;
		File[] imageFiles = new File[3];
		int result = SUCCESS;
		
        if(Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED))
        	mainStorageDir = new File(Environment.getExternalStorageDirectory(), "drunk_detection");
        else
        	mainStorageDir = new File(context.getFilesDir(),"drunk_detection");
        
        textFile = new File(mainStorageDir.getPath() + File.separator + ts + File.separator + ts + ".txt");
        geoFile = new File(mainStorageDir.getPath() + File.separator + ts + File.separator + "geo.txt");
        questionFile = new File(mainStorageDir.getPath() + File.separator + ts + File.separator + "question.txt");
        
        imageFiles[0] = new File(mainStorageDir.getPath() + File.separator + ts + File.separator + "IMG_" + ts + "_1.jpg");
        imageFiles[1] = new File(mainStorageDir.getPath() + File.separator + ts + File.separator + "IMG_" + ts + "_2.jpg");
        imageFiles[2] = new File(mainStorageDir.getPath() + File.separator + ts + File.separator + "IMG_" + ts + "_3.jpg");
       	
        avg_result = parseTextFile(textFile);
        
        int q_result = getQuestionResult(questionFile);
        int emotion = q_result/100;
        int desire = q_result%100;
        if (q_result == -1){
        	emotion = -1;
        	desire = -1;
        }
      
        AccumulatedHistoryState a_history = db.getLatestAccumulatedHistoryState();
        
        float brac = (float)avg_result;
        long timestamp = Long.parseLong(ts) * 1000L;
        int week = WeekNum.getWeek(context, timestamp);
        
        DateBracDetectionState detection= new DateBracDetectionState(week,timestamp,brac,emotion,desire);
        
        DateBracDetectionState prevDetection = db.getLatestBracDetection();
        
    
        boolean isAdd = false;
        
        if (StartDateCheck.check(context))
    	if (detection.year != prevDetection.year || detection.month != prevDetection.month || detection.day != prevDetection.day || detection.timeblock != prevDetection.timeblock){
    		if (avg_result >=0 && avg_result < THRESHOLD){
    				a_history.changeAcc(true, week, detection.timeblock);
    		}
    		else if (avg_result < 0)
    			result = ERROR;
    		else{
    			a_history.changeAcc(false, week, detection.timeblock);
    		}
    		isAdd = true;
    	}
    	
    	db.insertNewState(detection,a_history);
    	
    	SharedPreferences sp= PreferenceManager.getDefaultSharedPreferences(context);
		SharedPreferences.Editor editor = sp.edit();
    	if (avg_result  < THRESHOLD){
    		if (emotion <=3 || desire >= 5)
    			editor.putInt("latest_result", 1);
    		else
    			editor.putInt("latest_result", 0);
    	}else if (avg_result < THRESHOLD2){
    		editor.putInt("latest_result", 2);
    	}else{
    		editor.putInt("latest_result", 3);
    	}
    	editor.putBoolean("latest_result_add", isAdd);
    	editor.putBoolean("tested", true);
    	editor.putBoolean("hourly_alarm", false);
    	editor.commit();
        
       	stateFile = new File(mainStorageDir.getPath() + File.separator + ts + File.separator + "state.txt");
       	try {
       		BufferedWriter state_writer = new BufferedWriter(new FileWriter(stateFile));
       		String state_str = a_history.toString();
       		String used_str = db.getLatestUsedState().toString();
       		String output_str = state_str + used_str;
       		state_writer.write(output_str);
       		state_writer.flush();
       		state_writer.close();
		} catch (Exception e) {	
			Log.d("BrAC DATA HANDLER","FAIL TO WRITE");
		}
       	
       	/*Connection to server*/
       	int server_connect = connectingToServer(textFile,geoFile,stateFile,questionFile,imageFiles);
		if (server_connect == ERROR){ // error happens when preparing files
			result = ERROR;
		}
		return result;
	}
	
	public double getResult(){
		return avg_result;
	}
	
	protected double parseTextFile(File textFile){
		double median = 0;
        try {
			Scanner s = new Scanner(textFile);
			int index = 0;
			List<Double> valueArray2 = new ArrayList<Double>();
			
			while(s.hasNext()){
				index++;
				String word = s.next();
				if(index % 2 == 0){
					valueArray2.add(Double.valueOf(word));
				}
			}
			if (valueArray2.size()==0)
				return ERROR;
			Double[] values = valueArray2.toArray(new Double[valueArray2.size()]);
			Arrays.sort(values);
			median = values[(values.length-1)/2];
			
		} catch (FileNotFoundException e1) {
			Log.d("BrAC DATA HANDLER","FILE NOT FOUND");
			return ERROR;
		}
        return median;
	}
	
	protected int getQuestionResult(File textFile){
		int result = -1; 
        try {
			Scanner s = new Scanner(textFile);

			int emotion = 0;
			int desire = 0;
			
			if(s.hasNextInt())
				emotion = s.nextInt();
			if(s.hasNextInt())
				desire = s.nextInt();
			
			if (emotion == -1 || desire == -1)
				return -1;
			result = emotion * 100 + desire;
			
		} catch (FileNotFoundException e1) {
			return ERROR;
		}
        return result;
	}
	
	
	private int connectingToServer(File textFile, File geoFile, File stateFile, File questionFile, File[] imageFiles){
		try {
			
			DefaultHttpClient httpClient = new DefaultHttpClient();
			
			KeyStore trustStore = KeyStore.getInstance(KeyStore.getDefaultType());
			InputStream instream = context.getResources().openRawResource(R.raw.alcohol_certificate);
			try{
				trustStore.load(instream, null);
			} finally{
				instream.close();
			}
			SSLSocketFactory socketFactory = new SSLSocketFactory(trustStore);
			Scheme sch = new Scheme("https",socketFactory,443);
			
			httpClient.getConnectionManager().getSchemeRegistry().register(sch);
			
			HttpPost httpPost = new HttpPost(SERVER_URL);
			httpClient.getParams().setParameter(CoreProtocolPNames.PROTOCOL_VERSION, HttpVersion.HTTP_1_1);
			MultipartEntity mpEntity = new MultipartEntity();
			
			SharedPreferences sp= PreferenceManager.getDefaultSharedPreferences(context);
			String uid = sp.getString("uid", "");
			mpEntity.addPart("userData[]", new StringBody(uid));
			mpEntity.addPart("userData[]", new StringBody(ts));
			mpEntity.addPart("userData[]", new StringBody(devId));
			
			
			Calendar c = Calendar.getInstance();
			
		    int mYear = sp.getInt("sYear", c.get(Calendar.YEAR));
		    int mMonth = sp.getInt("sMonth", c.get(Calendar.MONTH));
		    int mDay = sp.getInt("sDate", c.get(Calendar.DATE));
		    
		    String joinDate = mYear+"-"+(mMonth+1)+"-"+mDay;
		    mpEntity.addPart("userData[]", new StringBody(joinDate));
			
		    PackageInfo pinfo;
			try {
				pinfo = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
				String versionName = pinfo.versionName;
				mpEntity.addPart("userData[]", new StringBody( versionName));
			} catch (NameNotFoundException e) {	}
		    
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
			if (questionFile.exists()){
				ContentBody cbQuestionFile = new FileBody(questionFile, "application/octet-stream");
				mpEntity.addPart("userfile[]", cbQuestionFile);
			}
			
			if (imageFiles[0].exists())
				mpEntity.addPart("userfile[]", new FileBody(imageFiles[0], "image/jpeg"));
			if (imageFiles[1].exists())
				mpEntity.addPart("userfile[]", new FileBody(imageFiles[1], "image/jpeg"));
			if (imageFiles[2].exists())
				mpEntity.addPart("userfile[]", new FileBody(imageFiles[2], "image/jpeg"));
			
			httpPost.setEntity(mpEntity);
			DataUploader uploader = new DataUploader(httpClient,httpPost,ts,context);
			uploader.execute();
			
		} catch (Exception e) {
			return ERROR;
		} 
		return SUCCESS;
	}
}
