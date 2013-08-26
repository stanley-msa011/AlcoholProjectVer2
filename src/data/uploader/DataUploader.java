package data.uploader;



import network.NetworkCheck;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.BasicResponseHandler;

import data.database.HistoryDB;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

public class DataUploader extends AsyncTask<Void, Void,Boolean> {

	private HttpPost httpPost;
	private HttpClient httpClient;
	private String ts;
	private AsyncTask<Void, Void, Boolean> mTask;
	private Context context;
	
	public DataUploader(HttpClient httpClient, HttpPost httpPost,String ts,Context context){
		if(!NetworkCheck.networkCheck(context))
			return;
		mTask = this;
		this.ts = ts;
		this.httpClient = httpClient;
		this.httpPost = httpPost;
		this.context = context;
	}
	
	@Override
	protected Boolean doInBackground(Void... arg0) {
		
		Log.d("UPLOADER","START");
		HttpResponse httpResponse;
		ResponseHandler <String> res=new BasicResponseHandler();  
		Boolean result = false;
		Timer timer = new Timer();
		Thread thread = new Thread(timer);
		thread.start();
		try {
			httpResponse = httpClient.execute(httpPost);
			int httpStatusCode = httpResponse.getStatusLine().getStatusCode();
			result = (httpStatusCode == HttpStatus.SC_OK);
			if (result){
				String response = res.handleResponse(httpResponse).toString();
				Log.d("UPLOADER","response = " + response);
				result &= (response.equals("10111") || response.equals("11111"));
			}
			Log.d("UPLOADER","result = "+result);
		} catch (Exception e) {
			Log.d("DATA UPLOADER","EXCEPTION:"+e.toString());
		} finally{
			httpClient.getConnectionManager().shutdown();
		}
		return result;
	}

	@Override
	 protected void onPostExecute(Boolean result) {
		if (!result.booleanValue()){
			Log.d("DATA UPLOADER","UPLOAD FAILED");
		}
		else{
			HistoryDB db = new HistoryDB(context);
			long _ts =Long.valueOf(ts)*1000L;
			db.updateDetectionUploaded(_ts);
		}
    }
	
	public class Timer implements Runnable{
		@Override
		public void run() {
			try {
				Thread.sleep(16000);
				mTask.cancel(true);
			} catch (InterruptedException e) {}
		}
	}
	
}
