package data.uploader;



import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;

import data.database.HistoryDB;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

public class DataUploader extends AsyncTask<Void, Void, Integer> {

	private HttpPost httpPost;
	private HttpClient httpClient;
	private String ts;
	private AsyncTask<Void, Void, Integer> mTask;
	private Context context;
	
	public DataUploader(HttpClient httpClient, HttpPost httpPost,String ts,Context context){
		mTask = this;
		this.ts = ts;
		this.httpClient = httpClient;
		this.httpPost = httpPost;
		this.context = context;
	}
	
	@Override
	protected Integer doInBackground(Void... arg0) {
		HttpResponse httpResponse;
		Integer result = -1;
		Timer timer = new Timer();
		Thread thread = new Thread(timer);
		thread.start();
		try {
			httpResponse = httpClient.execute(httpPost);
			int httpStatusCode = httpResponse.getStatusLine().getStatusCode();
			if (httpStatusCode == HttpStatus.SC_OK)
				result = 1;
			else
				result = -1;
		} catch (Exception e) {
			Log.d("DATA UPLOADER","EXCEPTION:"+e.toString());
		} finally{
			httpClient.getConnectionManager().shutdown();
		}
		return result;
	}

	@Override
	 protected void onPostExecute(Integer result) {
		if (result == -1){
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
