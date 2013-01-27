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
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Spinner;

public class GalleryIndexActivity extends Activity {

	private static final String[] spinner_content={"全部","一月","二月","三月","四月","五月","六月","七月","八月","九月","十月","十一月","十二月"};  
	
	private GalleryIndexAdapter gi_adapter;
	private GameDB gDB;
	private ArrayAdapter<CharSequence> month_adapter;  
	private int cur_month = 0;
	
	ListView galleryIdxListView;
	Spinner galleryIdxSpinner;
	ArrayList<HashMap<String,Object>> gallery_idx_list = new ArrayList<HashMap<String,Object>>();
	ArrayList<HashMap<String,Object>> show_gallery_idx_list = new ArrayList<HashMap<String,Object>>();
	
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
		galleryIdxListView = (ListView) this.findViewById(R.id.Gallery_Index_List);
		galleryIdxSpinner = (Spinner) this.findViewById(R.id.gallery_index_month_spinner);
		month_adapter =ArrayAdapter.createFromResource(this, R.array.gallery_index_month, android.R.layout.simple_spinner_item);
		month_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item); 
		galleryIdxSpinner.setAdapter(month_adapter);  
		
		galleryIdxListView.setOnItemClickListener(new GalleryIndexOnItemClickListener() );
		//int adapter_len = setGalleryIndexList();
		setGalleryIndexList();
		//gi_adapter = new GalleryIndexAdapter(gallery_idx_list,this);
		 //if (adapter_len>0)
		//	 galleryIdxListView.setAdapter(gi_adapter);
		 
		 //setShowIndexList(0);
		 gi_adapter = new GalleryIndexAdapter(show_gallery_idx_list,this);
		 galleryIdxListView.setAdapter(gi_adapter);
		
		 galleryIdxSpinner.setOnItemSelectedListener(new MonthOnItemSelectedListener());
		 galleryIdxSpinner.setSelection(cur_month);
		 
	}
	
	@Override
	protected void onPause(){
		super.onPause();
		cleanMemory();
	}
	
	private int setGalleryIndexList(){
		BracGameState[] stateList = gDB.getAllStates();

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
			
			/*
			int hour = cal_from.get(Calendar.HOUR);
			int min = cal_from.get(Calendar.MINUTE);
			int sec = cal_from.get(Calendar.SECOND);
			*/
			String DateString = month+"/"+day+"/"+year;//+"\n"+hour+":"+min+":"+sec;
			
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
	
	private int setShowIndexList(int mon){
		int len = gallery_idx_list.size();
		show_gallery_idx_list.clear();
		if (mon == 0){
				show_gallery_idx_list.addAll(gallery_idx_list);
				return len;
		}
		
		for (int i=0;i<len;++i){
			HashMap<String,Object> item = gallery_idx_list.get(i);
			String date = (String) item.get("date");
			String[] dates = date.split("/");
			int month = Integer.valueOf(dates[0]);
			if (month == mon)
				show_gallery_idx_list.add(item);
		}
		return show_gallery_idx_list.size();
	}
	
	class GalleryIndexOnItemClickListener implements AdapterView.OnItemClickListener{

		@Override
		public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,	long arg3) {
			
			Intent newActivity;
			int target = (int) arg3;
			newActivity = new Intent(context, GalleryActivity.class);
			int page = (Integer) show_gallery_idx_list.get(target).get("page");
			newActivity.putExtra("PAGE", page);
			context.startActivity(newActivity);
		}
	}
	
	class MonthOnItemSelectedListener implements AdapterView.OnItemSelectedListener{

		@Override
		public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2,
				long arg3) {
			
			 int selected = (int)arg3;
			 cur_month = selected;
			 
			 setShowIndexList(cur_month);
			 gi_adapter.clearAll();
			 gi_adapter.Reset(show_gallery_idx_list);
			 gi_adapter.notifyDataSetChanged();
			
		}

		@Override
		public void onNothingSelected(AdapterView<?> arg0) {
			setShowIndexList(cur_month);
			 gi_adapter.clearAll();
			 gi_adapter.Reset(show_gallery_idx_list);
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
