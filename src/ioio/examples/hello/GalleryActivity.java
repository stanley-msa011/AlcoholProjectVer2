package ioio.examples.hello;



import game.BackgroundHandler;
import game.GameDB;
import game.GameState;

import java.util.ArrayList;
import java.util.HashMap;

import com.devsmart.android.ui.HorizontalListView;

import android.os.Bundle;
import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.util.Log;
import android.widget.SimpleAdapter;

public class GalleryActivity extends Activity {

	private SimpleAdapter gallery_adapter;
	private HorizontalListView galleryListView;
	//private ListView galleryListView;
	private Context galleryActivity;
	ArrayList<HashMap<String,Object>> gallery_list = new ArrayList<HashMap<String,Object>>();
	
	private final int[] treePics ={
		R.drawable.tree1,
		R.drawable.tree2,
		R.drawable.tree3,
		R.drawable.tree4,
		R.drawable.tree5,
		R.drawable.tree6,
		R.drawable.tree7
	};
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_gallery);
		galleryListView = (HorizontalListView) findViewById(R.id.gallery_list);
		galleryActivity = this;
		int adapter_len = setGalleryList();
		 gallery_adapter = new SimpleAdapter(
				this, 
				gallery_list,
				R.layout.game_history,
				new String[] { "pic","tree","coin0","coin1","coin2","coin3","date"},
				new int[] {R.id.gallery_background,R.id.gallery_tree,R.id.gallery_coin1,R.id.gallery_coin2,R.id.gallery_coin3,R.id.gallery_coin4,R.id.gallery_date});
		 Log.d("Gallery", "End Adapter");
		 if (adapter_len >0){
			galleryListView.setAdapter(gallery_adapter);
		}
	}

	private int setGalleryList(){
		GameDB gDB=new GameDB(this);
		BracDbAdapter bDb = new BracDbAdapter(galleryActivity);
		GameState[] stateList = gDB.getAllStates();
		bDb.open();
		Cursor brac_test_list =  bDb.fetchAllHistory();

		if (stateList == null)
			return 0;
		
		int brac_test_list_length = brac_test_list.getCount();
		
		Log.d("Gallery",String.valueOf(brac_test_list_length)+" "+String.valueOf(stateList.length));
		
		for (int i=0;i<stateList.length;++i){
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
			brac_test_list.moveToPosition(i);
			String date = brac_test_list.getString(1);
			
			item.put("pic",bg_pic);
			item.put("tree",treePics[state] );
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
	

}
