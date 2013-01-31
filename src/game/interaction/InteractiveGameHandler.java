package game.interaction;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.HttpVersion;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.CoreProtocolPNames;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.provider.Settings.Secure;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Gallery;
import game.TreeImageHandler;
import ioio.examples.hello.GameActivity;
import ioio.examples.hello.R;

public class InteractiveGameHandler {
	private GameActivity ga;
	private Gallery gGallery;
	private ArrayList<HashMap<String,Object>> partner_list = new ArrayList<HashMap<String,Object>>();
	private InteractiveGamePopupWindowHandler pop;
	private InteractiveGameDB igDB;
	private InteractiveAdapter i_adapter;
	private int cur_pos = -1;
	
	static private final String myCode = "Me";
	
	public InteractiveGameHandler(GameActivity ga){
		this.ga = ga;
		init();
	}
	
	private void init(){
		gGallery = (Gallery) ga.findViewById(R.id.interactive_game_bar);
		igDB = new InteractiveGameDB(ga);
		gGallery.setOnItemClickListener(new InteractiveOnItemClickListener());
		gGallery.setOnItemSelectedListener(new InteractiveOnItemSelectedListener());
		pop = new InteractiveGamePopupWindowHandler(ga,this);
	}
	
	private void setAdapter(){
		partner_list.clear();
		InteractiveGameState[] stateList = igDB.getStates();
		if (stateList.length == 0){
				i_adapter = null;
				return;
		}
		for (int i=0;i<stateList.length;++i){
			HashMap<String,Object> item = new HashMap<String,Object>();
			int bg_stage = stateList[i].stage;
			int tree_pic =  TreeImageHandler.getTreeImageDrawableId(stateList[i].stage, stateList[i].coin);
			item.put("stage",bg_stage);
			item.put("pic",tree_pic);
			item.put("pid", stateList[i].PID);
			if (stateList[i].PID.equals(Secure.getString(ga.getContentResolver(), Secure.ANDROID_ID)))
				item.put("code_name",myCode);
			else
				item.put("code_name",stateList[i].name );
			partner_list.add(item);
		}
		i_adapter = new InteractiveAdapter(partner_list,ga);
	}
	
	private static final String SERVER_URL = "http://140.112.30.165/develop/drunk_detection/userStates.php";
	
	public void update(){
		try {
			 DefaultHttpClient httpClient = new DefaultHttpClient();
			HttpPost httpPost = new HttpPost(SERVER_URL);
			httpClient.getParams().setParameter(CoreProtocolPNames.PROTOCOL_VERSION, HttpVersion.HTTP_1_1);
			MultipartEntity mpEntity = new MultipartEntity();
			String devId = Secure.getString(ga.getContentResolver(), Secure.ANDROID_ID);
			mpEntity.addPart("userData[]", new StringBody(devId));
			httpPost.setEntity(mpEntity);
			GetStateHandler handler = new GetStateHandler(httpClient,httpPost);
			Thread thread = new Thread(handler);
			thread.start();
			thread.join(3000);
			update_adapter();
			//i_adapter.notifyDataSetChanged();
		} catch (Exception e) {
			e.printStackTrace();	
			return;
		} 
	}
	
	public void notifyUpdate(){
		i_adapter.notifyDataSetChanged();
	}
	
	public String getCodeNameByPID(String pid){
		if (pid.equals(Secure.getString(ga.getContentResolver(), Secure.ANDROID_ID)))
			return myCode;
		String c_name = igDB. getCodeName(pid);
		if (c_name == null)
			return "???";
		else
			return c_name;
	}
	
	private class GetStateHandler implements Runnable{
		private HttpPost httpPost;
		private DefaultHttpClient httpClient;
		public int result;
		private ResponseHandler< String> responseHandler;
		
		public GetStateHandler(DefaultHttpClient httpClient, HttpPost httpPost){
			this.httpClient = httpClient;
			this.httpPost = httpPost;
			responseHandler=new BasicResponseHandler();
			result = -1;
		}
		@Override
		public void run() {
			HttpResponse httpResponse;
			try {
				result = -1;
				httpResponse = httpClient.execute(httpPost);
				String responseString = responseHandler.handleResponse(httpResponse);
				int httpStatusCode = httpResponse.getStatusLine().getStatusCode();
				if (httpStatusCode == HttpStatus.SC_OK)
					result = 1;
				else
					result = -1;
				InteractiveGameState[] states = parseResponse(responseString);
				if (states != null){
					igDB.updateState(states);
					result = 2;
				}
				
			} catch (Exception e) {	e.printStackTrace();}
		}
		
	}
	
	private InteractiveGameState[] parseResponse(String response){
		response = response.substring(2, response.length()-2);
		String[] tmp = response.split("]," );
		if (tmp.length==0)
			return null;
					
		InteractiveGameState[] states = new InteractiveGameState[tmp.length];
		for (int i=0;i<tmp.length;++i){
			if (tmp[i].charAt(0)=='[')
				tmp[i]=tmp[i].substring(1,tmp[i].length());
			String[] items = tmp[i].split(",");
			String pid = items[0].substring(1, items[0].length()-1);
			int stage;
			if (items[1].equals("null"))
				stage = 0;
			else
				stage= Integer.valueOf(items[1].substring(1,items[1].length()-1));
			int coin;
			if (items[2].equals("null"))
				coin = 0;
			else
				coin = Integer.valueOf(items[2].substring(1,items[2].length()-1));
			String name = items[3].substring(1,items[3].length()-1);
			states[i] = new InteractiveGameState(stage,coin,pid,name);
		}
		return states;
	}
	
	
	public void clear(){
		if (i_adapter != null){
			i_adapter.notifyDataSetInvalidated();
			i_adapter.clearAll();
		}
		if (partner_list != null)
			partner_list.clear();
			
	}
	
	private void update_adapter(){
		setAdapter();
		if (i_adapter == null)
			gGallery.setVisibility(View.INVISIBLE);
		else{
			gGallery.setVisibility(View.VISIBLE);
			gGallery.setAdapter(i_adapter);
			if (cur_pos == -1)
				cur_pos= (i_adapter.getCount())/2;
			gGallery.setSelection(cur_pos);
			gGallery.refreshDrawableState();
		}
	}
	
	private class InteractiveOnItemClickListener implements AdapterView.OnItemClickListener{

		@Override
		public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,long arg3) {
			int idx = (int)arg3;
			HashMap<String,Object> item =partner_list.get(idx);
			String pid = (String) item.get("pid");
			String code_name = (String) item.get("code_name");
			pop.showPopWindow(code_name, pid);
		}
		
	}
	
	private class InteractiveOnItemSelectedListener implements AdapterView.OnItemSelectedListener{

		@Override
		public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2,
				long arg3) {
			cur_pos = arg2;
		}

		@Override
		public void onNothingSelected(AdapterView<?> arg0) {
		}
		
	}
	
	private static final String GCM_SERVER_URL = "http://140.112.30.165:80/drunk_detection/GCM/encourage.php";
	
	public void send_cheers(String pid){
		try {
			DefaultHttpClient httpClient = new DefaultHttpClient();
			String devId = Secure.getString(ga.getContentResolver(), Secure.ANDROID_ID);
			String url = GCM_SERVER_URL+"?sender="+devId+"&receiver="+pid;
			HttpPost httpPost = new HttpPost(url);
			httpClient.getParams().setParameter(CoreProtocolPNames.PROTOCOL_VERSION, HttpVersion.HTTP_1_1);
			SendCheers cheer= new SendCheers(httpClient,httpPost);
			Thread thread = new Thread(cheer);
			thread.start();
			thread.join(3000);
			
			if (cheer.result==-1)
				Log.d("GCM","Send fail");
			else
				Log.d("GCM","Send Success");
			
		} catch (Exception e) {
			e.printStackTrace();	
			return;
		} 
	}
	public class SendCheers implements Runnable {

		private HttpPost httpPost;
		private HttpClient httpClient;
		public SendCheers(HttpClient httpClient, HttpPost httpPost){
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
	

	
}
