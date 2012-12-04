package ioio.examples.hello;



import game.GameDB;
import game.GameState;

import java.util.ArrayList;
import java.util.HashMap;

import com.devsmart.android.ui.HorizontalListView;

import android.os.Bundle;
import android.app.Activity;
import android.widget.SimpleAdapter;

public class GalleryActivity extends Activity {

	private SimpleAdapter gallery_adapter;
	private HorizontalListView galleryListView;
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
	
	private final int MAX_WEATHER_CHANGE_STATE = 10;
	
	private final int[] weatherPicsGood = {
			R.drawable.w10,R.drawable.w11,R.drawable.w12,R.drawable.w13,R.drawable.w14,
			R.drawable.w15,R.drawable.w16,R.drawable.w17,R.drawable.w18,R.drawable.w19
	};
	
	private final int[] weatherPicsBad = {
			R.drawable.w05,R.drawable.w06,R.drawable.w07,R.drawable.w08,R.drawable.w09,
			R.drawable.w00,R.drawable.w01,R.drawable.w02,R.drawable.w03,R.drawable.w04
	};
	// 00 ~ 04 thunder
	// 05 ~ 09 rain
	// 10 ~ 14 cloud
	// 15 ~ 19 sun
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_gallery);
		galleryListView = (HorizontalListView) findViewById(R.id.gallery_list);

		int adapter_len = setGalleryList();
		
		 gallery_adapter = new SimpleAdapter(
				this, 
				gallery_list,
				R.layout.game_history,
				new String[] { "pic","tree","coin0","coin1","coin2","coin3"},
				new int[] {R.id.gallery_background,R.id.gallery_tree,R.id.gallery_coin1,R.id.gallery_coin2,R.id.gallery_coin3,R.id.gallery_coin4});
		if (adapter_len >0)
			galleryListView.setAdapter(gallery_adapter);
	}

	private int setGalleryList(){
		GameDB gDB=new GameDB(this);
		GameState[] stateList = gDB.getAllStates();
		if (stateList == null)
			return 0;
		int weather_count = -1;
		boolean weather_direct = true;
		
		int prev_state=3, prev_coin=0;
		boolean prev_direct=false;
		
		for (int i=0;i<stateList.length;++i){
			HashMap<String,Object> item = new HashMap<String,Object>();
			
			int state = stateList[i].state;
			int coin = stateList[i].coin;
			
			if (state > prev_state) //add state
				weather_direct = true;
			else if (state == prev_state && coin > prev_coin)//add coin 
				weather_direct = true;
			else if (state == GameState.MAX_STATE && coin == GameState.MAX_COINS) // Best
				weather_direct = true;
			else
				weather_direct = false;
			
			if (weather_direct != prev_direct)
				weather_count = 0;
			else
				++weather_count;
			
			if (weather_count == MAX_WEATHER_CHANGE_STATE)
				--weather_count;
			
			prev_state = state;
			prev_coin = coin;
			prev_direct = weather_direct;
			
			int bg_pic=0;
			
			if (weather_direct)
				bg_pic = weatherPicsGood[weather_count];
			else
				bg_pic = weatherPicsBad[weather_count];
			
			int[] coins = new int[4];
			int j=0;
			for (;j<4;++j){
				if (j<coin)
					coins[j] = R.drawable.coin_n;
				else
					coins[j] = R.drawable.blank_img;
			}
			
			item.put("pic",bg_pic);
			item.put("tree",treePics[state] );
			item.put("coin0", coins[0]);
			item.put("coin1", coins[1]);
			item.put("coin2", coins[2]);
			item.put("coin3", coins[3]);
			gallery_list.add(item);
		}
		return stateList.length;
	}
	

}
