package ioio.examples.hello;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.TimeZone;

import game.BracGameState;
import game.GameDB;
import game.GameState;
import game.TreeImageHandler;
import game.gallery.GalleryIndexAdapter;
import android.os.Bundle;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.Spinner;

public class GalleryIndexActivity extends Activity {

	private GalleryIndexAdapter gi_adapter;
	private GameDB gDB;
	private ArrayAdapter<CharSequence> session_adapter;  
	private int cur_session = 0;
	
	
	private GridView galleryIdxGridView;
	private Spinner galleryIdxSpinner;
	private ArrayList<HashMap<String,Object>> gallery_idx_list = new ArrayList<HashMap<String,Object>>();
	
	private Context context;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		System.gc();
		setContentView(R.layout.activity_gallery_index);
	}

	@Override
	protected void onResume(){
		super.onResume();
		context  = this;
		gDB=new GameDB(this);
		galleryIdxGridView = (GridView) this.findViewById(R.id.Gallery_Index_List);
		galleryIdxSpinner = (Spinner) this.findViewById(R.id.gallery_index_session_spinner);
		session_adapter =ArrayAdapter.createFromResource(this, R.array.gallery_index_session, android.R.layout.simple_spinner_item);
		session_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item); 
		galleryIdxSpinner.setAdapter(session_adapter);  
		
		galleryIdxGridView.setOnItemClickListener(new GalleryIndexOnItemClickListener() );
		
		 gi_adapter = new GalleryIndexAdapter(gallery_idx_list,this);
		 
		 galleryIdxGridView.setAdapter(gi_adapter);
		
		 galleryIdxSpinner.setOnItemSelectedListener(new MonthOnItemSelectedListener());
		 galleryIdxSpinner.setSelection(cur_session);
		 
	}
	
	@Override
	protected void onPause(){
		super.onPause();
		cleanMemory();
	}
	
	private int setGalleryIndexList(int cur_session){
		gallery_idx_list.clear();
		BracGameState[] stateList = gDB.getAllStates(cur_session);
		if (stateList == null)
			return 0;
		
		int end = stateList.length;
		int i = 0;
		int page = 0;
		while ( i < end){ 
			HashMap<String,Object> item = new HashMap<String,Object>();
			int stage = stateList[i].stage;
			int coin = stateList[i].coin;

			long time = stateList[i].date*1000L;
		
			
			int tree = TreeImageHandler.getTreeImageIdx(stage, coin);
			
			Calendar cal_from = Calendar.getInstance();
			
			cal_from.setTimeInMillis(time);
			cal_from.set(Calendar.HOUR, 0);
			cal_from.set(Calendar.MINUTE, 0);
			cal_from.set(Calendar.SECOND, 0);
			cal_from.set(Calendar.MILLISECOND, 0);
			
			int month = cal_from.get(Calendar.MONTH)+1;
			int day = cal_from.get(Calendar.DATE);
			int year = cal_from.get(Calendar.YEAR);
			
			String DateString = month+"/"+day+"\n"+year;
			
			int next = end;
			for (int j=i+1;j<end;++j){
				long mili =  stateList[j].date*1000L;
				long diff_day = (mili - cal_from.getTimeInMillis()) / 86400000;
				if (diff_day  == 0){
					// in the same day
				}else{
					next = j;
					break;
				}
			}
			item.put("stage", stage);
			item.put("tree",tree);
			item.put("date", DateString);
			item.put("page", page);
			++page;
			gallery_idx_list.add(item);
			i = next;
		}
		return gallery_idx_list.size();
	}
	
	
	class GalleryIndexOnItemClickListener implements AdapterView.OnItemClickListener{

		@Override
		public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,	long arg3) {
			
			Intent newActivity;
			int target = (int) arg3;
			newActivity = new Intent(context, GalleryActivity.class);
			int page = (Integer) gallery_idx_list.get(target).get("page");
			newActivity.putExtra("PAGE", page);
			newActivity.putExtra("SESSION", cur_session);
			context.startActivity(newActivity);
		}
	}
	
	class MonthOnItemSelectedListener implements AdapterView.OnItemSelectedListener{

		@Override
		public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2,
				long arg3) {
			
			 cur_session = (int)arg3;
			 Log.d("INDEX",String.valueOf(cur_session));
			 setGalleryIndexList(cur_session-1);
			 gi_adapter.clearAll();
			 gi_adapter.Reset(gallery_idx_list);
			 gi_adapter.notifyDataSetChanged();
			
		}

		@Override
		public void onNothingSelected(AdapterView<?> arg0) {
			setGalleryIndexList(cur_session-1);
			 gi_adapter.clearAll();
			 gi_adapter.Reset(gallery_idx_list);
			 gi_adapter.notifyDataSetChanged();
		}
		
	}
	
	private void cleanMemory(){
		gi_adapter.notifyDataSetInvalidated();
		gi_adapter.clearAll();
		gallery_idx_list.clear();
		System.gc();
	}
	
}
