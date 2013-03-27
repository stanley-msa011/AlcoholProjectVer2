package test.data;

import ioio.examples.hello.R;
import ioio.examples.hello.TestFragment;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.security.KeyStore;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import javax.net.ssl.HostnameVerifier;

import org.apache.http.HttpVersion;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.params.HttpClientParams;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.params.ConnManagerParams;
import org.apache.http.conn.params.ConnPerRoute;
import org.apache.http.conn.params.ConnPerRouteBean;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.conn.ssl.X509HostnameVerifier;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.ContentBody;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.SingleClientConnManager;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.CoreProtocolPNames;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;

import android.content.Context;
import android.os.Environment;
import android.provider.Settings.Secure;
import android.util.Log;

public class BracDataHandler {
	private String ts;
	private Context context;
	private String devId;
	private double avg_result = 0;
	
	public BracDataHandler(String timestamp_string, TestFragment fragment){
		ts = timestamp_string;
		context = fragment.getActivity();
		this.devId = Secure.getString(context.getContentResolver(), Secure.ANDROID_ID);
	}
	
	public static final int Nothing = 0; 
	public static final int ERROR = -1;
	public static final int SUCCESS = 1;
	public static final double THRESHOLD = 0.09;
	
	private static final String SERVER_URL = "https://140.112.30.165/develop/drunk_detect_upload.php";
	
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
        if (avg_result != ERROR){
        	if (avg_result > THRESHOLD){
        		//change state
        	}else{
        		//change
        	}
        }
        else{
        	result = ERROR;
        }
       	
       	stateFile = new File(mainStorageDir.getPath() + File.separator + ts + File.separator + "state.txt");
       	
       	/*Connection to server*/
       	int server_connect = connectingToServer(textFile,geoFile,stateFile,imageFiles);
		if (server_connect == ERROR) // error happens when preparing files
			result = ERROR;
		if (result == ERROR){
			//put ts into Reuploader
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
			DataUploader uploader = new DataUploader(httpClient,httpPost,ts);
			Log.d("DataHandler","uploader execute");
			uploader.execute();
			
		} catch (Exception e) {
			return ERROR;
		} 
		return SUCCESS;
	}
}
