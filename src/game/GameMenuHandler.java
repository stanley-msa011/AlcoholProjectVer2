package game;

import ioio.examples.hello.BracHistoryActivity;
import ioio.examples.hello.GalleryActivity;
import ioio.examples.hello.GameActivity;
import ioio.examples.hello.MainActivity;
import ioio.examples.hello.PrefSettingActivity;
import ioio.examples.hello.R;
import ioio.examples.hello.TestActivity;

import java.util.ArrayList;
import java.util.HashMap;


import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import android.preference.PreferenceManager;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class GameMenuHandler {
	ArrayList<HashMap<String,Object>> game_list = new ArrayList<HashMap<String,Object>>();
	private SimpleAdapter game_adapter;
	private ListView game_list_view;
	private GameActivity ga;
	private boolean isShowingMenu = false;
	private Context context;
	public static final int REQUEST_TEST = GameActivity.REQUEST_TEST;
	
	private static final int[] menuPics=new int[]{
		 R.drawable.blow_function,R.drawable.history2_function,
		 R.drawable.history_function,R.drawable.setting_function
	};
	
	public GameMenuHandler(GameActivity ga){
		this.ga = ga;
		context = this.ga;
		initList();
	}
	
	private void initList(){
		game_list_view = (ListView) ga.findViewById(R.id.game_menu_list);
		for (int i=0;i<4;++i){
			HashMap<String,Object> item = new HashMap<String,Object>();
			item.put("pic", menuPics[i]);
			game_list.add(item);
		}
		game_adapter = new SimpleAdapter(
				ga,
				game_list,
				R.layout.game_menu,
				new String[] { "pic"},
				new int[] { R.id.game_menu_icon } );
		game_list_view.setAdapter(game_adapter);
		game_list_view.setVisibility(View.INVISIBLE);
		game_list_view.setBackgroundColor(0xAAAAFFAA);
		game_list_view.setOnItemClickListener(new GameMenuOnClickListener());
	}
	
	public void changeMenuVisibility(){
	if (isShowingMenu == false){
			game_list_view.setVisibility(View.VISIBLE);
			isShowingMenu = true;
		}
		else{
			game_list_view.setVisibility(View.INVISIBLE);
			isShowingMenu = false;
		}
	
	}
	
	private class GameMenuOnClickListener implements AdapterView.OnItemClickListener{
		@Override
		public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
			int list_id = (int)arg3;
			Intent newActivity;
			
			changeMenuVisibility();
			
			switch (list_id){
				case 0:	//MainActivity
					SharedPreferences sp= PreferenceManager.getDefaultSharedPreferences(ga);
			        boolean fake = sp.getBoolean("use_fake_data", false);
			        if (fake)
			        	newActivity = new Intent(context, TestActivity.class);
			        else
			        	newActivity = new Intent(context, MainActivity.class);
					ga.startActivityForResult(newActivity, REQUEST_TEST);  
					break;
				case 1: //Dummy (Record of TreeGame)
					newActivity = new Intent(context, GalleryActivity.class); 
					newActivity.putExtra("PAGE", -1);
					ga.startActivity(newActivity);  
					break;
				case 2: //BracListActivity
					newActivity = new Intent(context, BracHistoryActivity.class);     
	                ga.startActivity(newActivity);
					break;
				case 3: //Dummy (Setting)
					newActivity = new Intent(context, PrefSettingActivity.class);     
	                ga.startActivity(newActivity);
					break;
				default:
					break;
			}
		}
	}
}
