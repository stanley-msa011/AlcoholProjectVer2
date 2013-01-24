package game;

import ioio.examples.hello.BracDbAdapter;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Scanner;

import org.apache.http.HttpVersion;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.ContentBody;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.CoreProtocolPNames;

import database.Reuploader;

import android.content.Context;
import android.os.Environment;
import android.provider.Settings.Secure;

public class BracDataHandler {
	private String ts;
	private Context activity_context;
	private TreeGame treeGame;
	private GameDB gDB;
	private String devId;
	private Reuploader reuploader;
	
	
	public BracDataHandler(String timestamp_string, Context activity, TreeGame treeGame,GameDB gDB){
		ts = timestamp_string;
		activity_context = activity;
		this.treeGame = treeGame;
		this.gDB = gDB;
		this.devId = Secure.getString(activity_context.getContentResolver(), Secure.ANDROID_ID);
		reuploader = new Reuploader(activity);
	}
	
	private BracDataHandler(String timestamp_string, String id){
		ts = timestamp_string;
		devId = id;
	};
	
	
	public static final int HaveAlcohol = 11;
	public static final int NoAlcohol = 10; 
	public static final int Nothing = 0; 
	public static final int ERROR = -1;
	public static final int SUCCESS = 1;
	private static final double THRESHOLD = 0.09;
	
	private static final String SERVER_URL = "http://140.112.30.165:80/develop/drunk_detect_upload.php";
	
	public int start(){
		
		File mainStorageDir;
		File textFile, geoFile, stateFile;
		File[] imageFiles = new File[3];

        if(Environment.getExternalStorageState().equals(Environment.MEDIA_REMOVED))
           return ERROR;
        else
        	mainStorageDir = new File(Environment.getExternalStorageDirectory(), "drunk_detection");
        
        textFile = new File(mainStorageDir.getPath() + File.separator + ts + File.separator + ts + ".txt");
        geoFile = new File(mainStorageDir.getPath() + File.separator + ts + File.separator + "geo.txt");
        
        imageFiles[0] = new File(mainStorageDir.getPath() + File.separator + ts + File.separator + "IMG_" + ts + "_1.jpg");
        imageFiles[1] = new File(mainStorageDir.getPath() + File.separator + ts + File.separator + "IMG_" + ts + "_2.jpg");
        imageFiles[2] = new File(mainStorageDir.getPath() + File.separator + ts + File.separator + "IMG_" + ts + "_3.jpg");
       	
        double avg_result = parseTextFile(textFile);
       	int result = 0;
       	
       	if (avg_result == ERROR)
       		return ERROR;
       	if (avg_result < THRESHOLD){
			result = NoAlcohol;
			treeGame.getCoin();
       	}
       	else{
       		result = HaveAlcohol;
       		treeGame.loseCoin();//Do nothing now
       	}
       	gDB.updateState(treeGame.getGameState());
       	
       	stateFile = new File(mainStorageDir.getPath() + File.separator + ts + File.separator + "state.txt");
       	try {
       		BufferedWriter state_writer = new BufferedWriter(new FileWriter(stateFile));
       		state_writer.write(treeGame.toString());
       		state_writer.flush();
       		state_writer.close();
		} catch (Exception e) {	
			e.printStackTrace();	
		}
       	
       	/*Connection to server*/
       	int server_connect = connectingToServer(textFile,geoFile,stateFile,imageFiles);
		if (server_connect == ERROR)
			reuploader.storeTS(ts);
		
		saveToDB(avg_result);
       	
		return result;
		
	}
	
	private double parseTextFile(File textFile){
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
	
	public static int connectingToServer(File textFile, File geoFile, File stateFile, File[] imageFiles,boolean hasGeoFile,boolean hasStateFile,String ts,String Id){
		BracDataHandler bdh = new BracDataHandler(ts,Id);
		return bdh.connectingToServer(textFile, geoFile, stateFile, imageFiles);
	}
	
	
	private int connectingToServer(File textFile, File geoFile, File stateFile, File[] imageFiles){
		try {
			HttpClient httpClient = new DefaultHttpClient();
			HttpPost httpPost = new HttpPost(SERVER_URL);
			
			httpClient.getParams().setParameter(CoreProtocolPNames.PROTOCOL_VERSION, HttpVersion.HTTP_1_1);
			
			MultipartEntity mpEntity = new MultipartEntity();
			
			mpEntity.addPart("userData[]", new StringBody(devId));
			mpEntity.addPart("userData[]", new StringBody(ts));
			
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
			
			BracDataToServer BDT = new BracDataToServer(httpClient,httpPost);
			Thread thread = new Thread(BDT);
			thread.start();
			thread.join(5000);
			if (BDT.result==-1)
				return ERROR;
			
		} catch (Exception e) {
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
