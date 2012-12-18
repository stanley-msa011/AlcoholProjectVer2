package ioio.examples.hello;




import game.BackgroundHandler;
import game.GameDB;
import game.GameState;
import ioio.examples.hello.R;
import ioio.examples.hello.R.drawable;
import ioio.examples.hello.R.id;
import ioio.examples.hello.R.layout;

import java.util.ArrayList;
import java.util.HashMap;

import android.os.Bundle;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Gallery;
import android.widget.SimpleAdapter;
import android.widget.TextView;

public class GalleryActivity extends Activity {

	private SimpleAdapter gallery_adapter;
	private Gallery galleryListView;
	private GalleryActivity galleryActivity;
	private Button nextButton;
	private Button prevButton;
	private EditText curPage;
	private TextView maxPage;
	
	private int max_page;
	private int cur_page;
	private int first_show_view;
	
	private GameDB gDB;
	private BracDbAdapter bDb;
	
	private static final int TOTAL_VIEW_PAGE = 10;
	
	private static boolean lock = false;
	
	ArrayList<HashMap<String,Object>> gallery_list = new ArrayList<HashMap<String,Object>>();
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		System.gc();
		setContentView(R.layout.activity_gallery);
		galleryActivity = this;
		galleryListView = (Gallery) findViewById(R.id.gallery_list);
		
		
		cur_page = this.getIntent().getIntExtra("PAGE", -1);
		first_show_view = this.getIntent().getIntExtra("SHOW", -1);
		
		gDB=new GameDB(this);
		bDb = new BracDbAdapter(this);
		
		
		
		int adapter_len = getGalleryListSize();
		 init(adapter_len);
		
		 int start_pos = (cur_page-1)*TOTAL_VIEW_PAGE ;
		 int end_pos = start_pos+TOTAL_VIEW_PAGE  -1 ;
		 if (end_pos > adapter_len-1)
			 end_pos = adapter_len-1;
		 
		 setGalleryList(start_pos, end_pos);
		 
		 
		 gallery_adapter = new SimpleAdapter(
				this, 
				gallery_list,
				R.layout.game_history,
				new String[] { "pic","tree","coin0","coin1","coin2","coin3","date"},
				new int[] {R.id.gallery_background,R.id.gallery_tree,R.id.gallery_coin1,R.id.gallery_coin2,R.id.gallery_coin3,R.id.gallery_coin4,R.id.gallery_date});

		 if (adapter_len>0){
			galleryListView.setAdapter(gallery_adapter);
			int select_pos;
			if (first_show_view == -1)
				select_pos = end_pos-start_pos;
			else 
				select_pos = first_show_view;
			galleryListView.setSelection(select_pos);
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
		gallery_list.clear();
		gallery_adapter.notifyDataSetInvalidated();
		System.gc();
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
			int state = stateList[i].state;
			int coin = stateList[i].coin;

			int[] coins = new int[4];
			int j=0;
			for (;j<4;++j){
				if (j<coin)
					coins[j] = R.drawable.coin_n;
				else
					coins[j] = R.drawable.blank_img;
			}
			
			int bg_pic =  BackgroundHandler.getBackgroundDrawableId(state, coin);
			int tree_pic = BackgroundHandler.getTreeDrawableId(state);
			brac_test_list.moveToPosition(i);
			String date = brac_test_list.getString(1);
			
			item.put("pic",bg_pic);
			item.put("tree",tree_pic );
			item.put("coin0", coins[0]);
			item.put("coin1", coins[1]);
			item.put("coin2", coins[2]);
			item.put("coin3", coins[3]);
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
					gallery_list.clear();
					gallery_adapter.notifyDataSetInvalidated();
					System.gc();
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
					gallery_list.clear();
					gallery_adapter.notifyDataSetInvalidated();
					System.gc();
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
				gallery_list.clear();
				gallery_adapter.notifyDataSetInvalidated();
				Intent newActivity = new Intent(galleryActivity, GalleryActivity.class); 
				newActivity.putExtra("PAGE", set_page);
				galleryActivity.startActivity(newActivity);
				galleryActivity.finish();
			}
			return false;
		}
		
	}

}
