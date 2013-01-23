package game;

import java.io.IOException;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;

public class BracDataToServer implements Runnable {

	private HttpPost httpPost;
	private HttpClient httpClient;
	public BracDataToServer(HttpClient httpClient, HttpPost httpPost){
		this.httpClient = httpClient;
		this.httpPost = httpPost;
		result = -1;
	}
	public int result;
	@Override
	public void run() {
		
		
		HttpResponse httpResponse;
		try {
			httpResponse = httpClient.execute(httpPost);
			int httpStatusCode = httpResponse.getStatusLine().getStatusCode();
			if (httpStatusCode == HttpStatus.SC_OK)
				result = 1;
			else
				result = -1;
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
