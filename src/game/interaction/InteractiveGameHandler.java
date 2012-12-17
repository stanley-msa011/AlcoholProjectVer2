package game.interaction;

import java.util.ArrayList;
import java.util.HashMap;

import android.view.View;
import android.widget.AdapterView;
import android.widget.Gallery;
import android.widget.SimpleAdapter;
import game.BackgroundHandler;
import ioio.examples.hello.GameActivity;
import ioio.examples.hello.R;

public class InteractiveGameHandler {
	private GameActivity ga;
	private Gallery gGallery;
	private ArrayList<HashMap<String,Object>> parterner_list = new ArrayList<HashMap<String,Object>>();
	private InteractiveGamePopupWindowHandler pop;
	private InteractiveGameDB igDB = new InteractiveGameDB(ga);
	private SimpleAdapter adapter;
	private int cur_pos = -1;
	
	
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
	
	private final static String code_names = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
	
	private void setAdapter(){
		parterner_list.clear();
		InteractiveGameState[] stateList = igDB.getStates();
		if (stateList.length == 0){
				adapter = null;
				return;
		}
		for (int i=0;i<stateList.length;++i){
			HashMap<String,Object> item = new HashMap<String,Object>();
			int bg_pic =  BackgroundHandler.getBackgroundDrawableId(stateList[i].state, stateList[i].coin);
			int tree_pic = BackgroundHandler.getTreeDrawableId(stateList[i].state);
			
			item.put("pic",bg_pic);
			item.put("tree",tree_pic );
			item.put("pid", stateList[i].PID);
			item.put("code_name",code_names.substring(i, i+1) );
			parterner_list.add(item);
		}
		
		adapter = new SimpleAdapter(
					ga, 
					parterner_list,
					R.layout.interactive_item,
					new String[] { "pic","tree","code_name"},
					new int[] {R.id.interactive_state,R.id.interactive_tree,R.id.interactive_code});
	}
	
	private void fake_update(){
		InteractiveGameState states[] = new 	InteractiveGameState[5];
		states[0] = new InteractiveGameState(3,4,"Abcde");
		states[1] = new InteractiveGameState(4,2,"Bcdef");
		states[2] = new InteractiveGameState(5,3,"Cdefg");
		states[3] = new InteractiveGameState(6,1,"Defgh");
		states[4] = new InteractiveGameState(0,2,"Ehijk");
		igDB.updateState(states);
		update_adapter();
	}
	
	public void update(){
		fake_update();
		update_adapter();
	}
	
	public void clear(){
		parterner_list.clear();
		adapter.notifyDataSetInvalidated();
	}
	
	private void update_adapter(){
		setAdapter();
		if (adapter == null)
			gGallery.setVisibility(View.INVISIBLE);
		else{
			gGallery.setVisibility(View.VISIBLE);
			gGallery.setAdapter(adapter);
			if (cur_pos == -1)
				cur_pos= (adapter.getCount())/2;
			gGallery.setSelection(cur_pos);
			gGallery.refreshDrawableState();
		}
	}
	
	private class InteractiveOnItemClickListener implements AdapterView.OnItemClickListener{

		@Override
		public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,long arg3) {
			int idx = (int)arg3;
			HashMap<String,Object> item =parterner_list.get(idx);
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
	
	public void send_cheers(String pid){
		
	}
}
