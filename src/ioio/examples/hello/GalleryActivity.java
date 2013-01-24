package ioio.examples.hello;




import game.TreeImageHandler;
import game.GameDB;
import game.GameState;
import game.gallery.GalleryAdapter;
import ioio.examples.hello.R;

import java.util.ArrayList;
import java.util.HashMap;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
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
	private int first_show_view;
	
	private GameDB gDB;
	private BracDbAdapter bDb;
	
	private static final int TOTAL_VIEW_PAGE = 10;
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
		
		cur_page = this.getIntent().getIntExtra("PAGE", -1);
		first_show_view = this.getIntent().getIntExtra("SHOW", -1);
		
		gDB=new GameDB(this);
		bDb = new BracDbAdapter(this);
		
		stopAnimeSetting();
		anime1.setOnClickListener(new AnimeFrameClickListener());
		
		int adapter_len = getGalleryListSize();
		 init(adapter_len);
		
		 int start_pos = (cur_page-1)*TOTAL_VIEW_PAGE ;
		 int end_pos = start_pos+TOTAL_VIEW_PAGE  -1 ;
		 if (end_pos > adapter_len-1)
			 end_pos = adapter_len-1;
		 
		 setGalleryList(start_pos, end_pos);
		 
		 i_adapter = new GalleryAdapter(gallery_list,galleryActivity);
		 if (adapter_len>0){
			 galleryListView.setAdapter(i_adapter);
			int select_pos;
			if (first_show_view == -1)
				select_pos = end_pos-start_pos;
			else 
				select_pos = first_show_view;
			galleryListView.setSelection(select_pos);
			animeListener = new AnimeListener();
			galleryListView.setOnItemLongClickListener(animeListener);
			galleryListView.setOnItemSelectedListener(new GallerySelectedListener());
		}
	}
	
	private void init(int len){
		nextButton = (Button)findViewById(R.id.gallery_next);
		prevButton = (Button)findViewById(R.id.gallery_prev);
		curPage = (EditText)findViewById(R.id.gallery_cur_page);
		maxPage = (TextView)findViewById(R.id.gallery_page_max);
		
		if (len == 0)//# state == 0
			max_page=cur_page=1;
		else{//# state >0
			max_page = len/TOTAL_VIEW_PAGE;
			if (len%TOTAL_VIEW_PAGE >0)
				++max_page;
			if (cur_page == -1)
				cur_page = max_page;
		}
		
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
	
	private int getGalleryListSize(){
		GameState[] stateList = gDB.getAllStates();
		if (stateList == null)
			return 0;
		return stateList.length;
	}
	
	private int setGalleryList(int start, int end){
		GameState[] stateList = gDB.getAllStates();
		bDb.open();
		Cursor brac_test_list =  bDb.fetchAllHistory();

		if (stateList == null)
			return 0;
		
		for (int i=start;i<=end;++i){
			HashMap<String,Object> item = new HashMap<String,Object>();
			int stage = stateList[i].stage;
			int coin = stateList[i].coin;

			int bg_pic =  TreeImageHandler.getTreeImageDrawableId(stage, coin);
			brac_test_list.moveToPosition(i);
			String date = brac_test_list.getString(1);
			
			item.put("stage", stage);
			item.put("pic",bg_pic);
			item.put("date", date);
			gallery_list.add(item);
		}
		bDb.close();
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
					newActivity.putExtra("PAGE", cur_page+1);
					newActivity.putExtra("SHOW", 0);
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
					newActivity.putExtra("PAGE", cur_page-1);
					newActivity.putExtra("SHOW", -1);
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
				newActivity.putExtra("PAGE", set_page);
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
			idx =start + TOTAL_VIEW_PAGE*(cur_page-1);
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
}
