package test.data;

import java.io.IOException;

import new_database.HistoryDB;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;

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
		Log.d("DataUploader","start upload");
		HttpResponse httpResponse;
		Integer result = -1;
		Timer timer = new Timer();
		Thread thread = new Thread(timer);
		thread.start();
		try {
			Log.d("DataUploader","execute");
			httpResponse = httpClient.execute(httpPost);
			
			Log.d("DataUploader","getStatusCode");
			int httpStatusCode = httpResponse.getStatusLine().getStatusCode();
			Log.d("DataUploader","check status");
			if (httpStatusCode == HttpStatus.SC_OK)
				result = 1;
			else
				result = -1;
		} catch (ClientProtocolException e) {
			Log.d("DataUploader","fail upload 1");
		} catch (IOException e) {
			Log.d("DataUploader","fail upload 2");
			String err_msg = e.getMessage();
			Log.e("DataUploader",err_msg);
		} finally{
			httpClient.getConnectionManager().shutdown();
			Log.d("DataUploader","finalize");
		}
		return result;
	}

	@Override
	 protected void onPostExecute(Integer result) {
		if (result == -1){
			Log.d("UPLOADER","UPLOAD FAILED");
			//put ts to the uploader
			HistoryDB db = new HistoryDB(context);
			long _ts =Long.valueOf(ts);
			db.insertNotUploadedTS(_ts);
		}
		else{
			Log.d("UPLOADER","UPLOAD SUCCESS");
		}
    }
	
	public class Timer implements Runnable{

		@Override
		public void run() {
			Log.d("UPLOADER TIMER","RUN");
			try {
				Thread.sleep(16000);
				Log.d("UPLOADER TIMER","END");
				mTask.cancel(true);
			} catch (InterruptedException e) {
				Log.d("UPLOADER TIMER","EXCEPTION");
			}
		}
	}
	
}
