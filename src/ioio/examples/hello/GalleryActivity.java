package ioio.examples.hello;




import game.BracDataHandler;
import game.BracGameState;
import game.TreeImageHandler;
import game.GameDB;
import game.GameState;
import game.gallery.GalleryAdapter;
import ioio.examples.hello.R;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Gallery;
import android.widget.ImageView;
import android.widget.TextView;

public class GalleryActivity extends Activity {

	private GalleryAdapter i_adapter;
	private Gallery galleryListView;
	private GalleryActivity galleryActivity;
	private Button nextButton;
	private Button prevButton;
	private EditText curPage;
	private TextView maxPage;
	
	private ImageView anime1;
	
	private int max_page;
	private int cur_page;
	private int cur_session;
	
	private int start_pos,end_pos;
	
	private GameDB gDB;
	
	private AnimeListener animeListener;
	
	
	private static boolean lock = false;
	
	ArrayList<HashMap<String,Object>> gallery_list = new ArrayList<HashMap<String,Object>>();
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		System.gc();
		setContentView(R.layout.activity_gallery);
		galleryActivity = this;
		galleryListView = (Gallery) findViewById(R.id.gallery_list);
		anime1 = (ImageView) findViewById(R.id.gallery_anime1);
		gDB=new GameDB(this);
		
		
		int cur = this.getIntent().getIntExtra("PAGE", 0);
		cur_session = this.getIntent().getIntExtra("SESSION", 0);
		init(cur);
		
		stopAnimeSetting();
		anime1.setOnClickListener(new AnimeFrameClickListener());
		
		 setGalleryList(start_pos, end_pos);
		 
		 i_adapter = new GalleryAdapter(gallery_list,galleryActivity);
		 if (start_pos<=end_pos){
			 galleryListView.setAdapter(i_adapter);
			galleryListView.setSelection(0);
			animeListener = new AnimeListener();
			galleryListView.setOnItemLongClickListener(animeListener);
			galleryListView.setOnItemSelectedListener(new GallerySelectedListener());
		}
	}
	
	private void init(int cur){
		nextButton = (Button)findViewById(R.id.gallery_next);
		prevButton = (Button)findViewById(R.id.gallery_prev);
		curPage = (EditText)findViewById(R.id.gallery_cur_page);
		maxPage = (TextView)findViewById(R.id.gallery_page_max);
		
		int[] info = getGalleryPage(cur);
		
		max_page = info[0];
		cur_page = info[1];
		start_pos = info[2];
		end_pos = info[3];
		
		maxPage.setText(String.valueOf(max_page));
		curPage.setText(String.valueOf(cur_page));
		
		nextButton.setEnabled(false);
		nextButton.setClickable(false);
		prevButton.setEnabled(false);
		prevButton.setClickable(false);
		if (cur_page < max_page){
			nextButton.setEnabled(true);
			nextButton.setClickable(true);
		}
		if (cur_page > 1){
			prevButton.setEnabled(true);
			prevButton.setClickable(true);
		}
		
		nextButton.setOnClickListener(new NextOnClickListener());
		prevButton.setOnClickListener(new PrevOnClickListener());
		curPage.setOnEditorActionListener(new CurPageListener());
	}
	
	protected void onStop(){
		super.onStop();
		cleanMemory();
		TreeImageHandler.cleanBitmaps();
	}
	
	private int setGalleryList(int start, int end){
		BracGameState[] stateList = gDB.getAllStates( cur_session-1);

		if (stateList == null)
			return 0;
		
		for (int i=start;i<=end;++i){
			HashMap<String,Object> item = new HashMap<String,Object>();
			int stage = stateList[i].stage;
			int coin = stateList[i].coin;

			int tree_pic =  TreeImageHandler.getTreeImageIdx(stage, coin);
			long _date = stateList[i].date;
			Date time = new Date(_date*1000L);
			String date = new SimpleDateFormat("MM/dd/yyyy\nkk:mm",Locale.TAIWAN).format(time);
			float brac = stateList[i].brac;
			if (brac > BracDataHandler.THRESHOLD)
				item.put("brac", true);
			else
				item.put("brac", false);
			
			item.put("stage", stage);
			item.put("pic",tree_pic);
			item.put("date", date);
			gallery_list.add(item);
		}
		return stateList.length;
	}
	
	
	
	private class NextOnClickListener implements View.OnClickListener
	{
		@Override
		public void onClick(View v) {
			if(!lock){
				lock = true;
				if (nextButton.isClickable() && nextButton.isEnabled()){
					nextButton.setEnabled(false);
					nextButton.setClickable(false);
					cleanMemory();
					Intent newActivity = new Intent(galleryActivity, GalleryActivity.class); 
					newActivity.putExtra("PAGE", cur_page);
					newActivity.putExtra("SESSION",  cur_session);
					galleryActivity.startActivity(newActivity);
				}
				lock = false;
			}
		}
		
	}
	private class PrevOnClickListener implements View.OnClickListener
	{
		@Override
		public void onClick(View v) {
			if(!lock){
				lock = true;
				if (prevButton.isClickable() && prevButton.isEnabled()){
					prevButton.setEnabled(false);
					prevButton.setClickable(false);
					cleanMemory();
					Intent newActivity = new Intent(galleryActivity, GalleryActivity.class); 
					newActivity.putExtra("PAGE", cur_page-2);
					newActivity.putExtra("SESSION",  cur_session);
					galleryActivity.startActivity(newActivity);
					galleryActivity.finish();
				}
				lock = false;
			}
		}
		
	}
	
	private class CurPageListener implements TextView.OnEditorActionListener{

		@Override
		public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
			int set_page = Integer.parseInt(curPage.getText().toString());
			if (set_page <= max_page && set_page >=1){
				cleanMemory();
				Intent newActivity = new Intent(galleryActivity, GalleryActivity.class); 
				newActivity.putExtra("PAGE", set_page-1);
				newActivity.putExtra("SESSION",  cur_session);
				galleryActivity.startActivity(newActivity);
				galleryActivity.finish();
			}
			else
				curPage.setText(String.valueOf(cur_page));
			return false;
		}
		
	}

	
	private class AnimeFrameClickListener implements View.OnClickListener{
		@Override
		public void onClick(View v) {
			stopAnimeSetting();
		}
	}
	
	private class GallerySelectedListener implements AdapterView.OnItemSelectedListener{

		@Override
		public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2,
				long arg3) {
			i_adapter.clearSelected((int)arg3);
			System.gc();
			stopAnimeSetting();
		}

		@Override
		public void onNothingSelected(AdapterView<?> arg0) {
			stopAnimeSetting();
		}
		
	}
	
	
	private class AnimeListener implements AdapterView.OnItemLongClickListener{
		private AnimationDrawable animation;
		private int start;
		private GameState[] states;
		private int idx;
		private Resources r;
		
		
		 AnimeListener(){
			 r = galleryActivity.getResources();
		 }
		 
		@SuppressWarnings("deprecation")
		@Override
		public boolean onItemLongClick(AdapterView<?> arg0, View arg1,int arg2, long arg3) {
			anime1.setClickable(true);
			anime1.clearAnimation();
			anime1.setVisibility(View.VISIBLE);
			start = (int) arg3;
			states = gDB.getAllStates();
			idx =start_pos+start;;
			 animation = new AnimationDrawable();
			 anime1.setImageDrawable(animation);
			for (;idx<states.length;++idx){
				int state = states[idx].stage;
				int coin = states[idx].coin;
				Drawable d = new BitmapDrawable(TreeImageHandler.getTreeImageBitmap(state, coin, r));
				animation.addFrame(d,300);
			}
			animation.start();
			return false;
		}
	}
	
	private void stopAnimeSetting(){
		anime1.setClickable(false);
		anime1.clearAnimation();
		anime1.setVisibility(View.INVISIBLE);
	}
	
	private void cleanMemory(){
		i_adapter.notifyDataSetInvalidated();
		i_adapter.clearAll();
		gallery_list.clear();
		System.gc();
	}
	
	//@return {total_page,cur_page,start_pos,end_pos}
	private int[] getGalleryPage(int cur){
		Log.d("GALLERY",String.valueOf(cur));
		BracGameState[] stateList = gDB.getAllStates(cur_session-1);

		if (stateList == null)
			return new int[]{1,1,-1,-1};
		
		int total_page = 0;
		int[] return_value = new int[4];
		
		int end = stateList.length;
		int i = 0;
		while ( i < end){ 
			long time = stateList[i].date*1000L;
			Calendar cal_from = Calendar.getInstance();
			cal_from.setTimeInMillis(time);
			cal_from.set(Calendar.HOUR, 0);
			cal_from.set(Calendar.MINUTE, 0);
			cal_from.set(Calendar.SECOND, 0);
			cal_from.set(Calendar.MILLISECOND, 0);
			
			int next = end;
			
			for (int j=i;j<end;++j){
				long mili =  stateList[j].date*1000L;
				long diff_day = (mili - cal_from.getTimeInMillis()) / 86400000;
				Log.d("Gallery diff",String.valueOf(diff_day));
				if (diff_day  == 0){
					if (j == end-1){//last
						if (cur == total_page){
							return_value[1] = cur+1;
							return_value[2] = i;
							return_value[3] = j;
						}
						++total_page;
					}
				}else{
					next = j;
					if (cur == total_page){
						Log.d("GALLERY",String.valueOf(cur)+"/"+String.valueOf(total_page)+" "+String.valueOf(i)+"/"+String.valueOf(j-1));
						return_value[1] = cur+1;
						return_value[2] = i;
						return_value[3] = j-1;
					}
					++total_page;
					break;
				}
			}
			i = next;
		}
		return_value[0] = total_page;
		return return_value;
	}
	
	
}
