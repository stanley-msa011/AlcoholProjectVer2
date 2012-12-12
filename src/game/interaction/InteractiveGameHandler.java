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
	public InteractiveGameHandler(GameActivity ga){
		this.ga = ga;
		fake_update();
		init();
	}
	
	private void init(){
		gGallery = (Gallery) ga.findViewById(R.id.interactive_game_bar);
		SimpleAdapter adapter =  setAdapter();
		if (adapter == null)
			gGallery.setVisibility(View.INVISIBLE);
		else{
			gGallery.setVisibility(View.VISIBLE);
			gGallery.setAdapter(adapter);
			int position = (adapter.getCount())/2;
			gGallery.setSelection(position);
			gGallery.setOnItemClickListener(new InteractiveOnItemClickListener());
		}
		pop = new InteractiveGamePopupWindowHandler(ga,this);
	}
	
	private final static String code_names = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
	
	private SimpleAdapter setAdapter(){
		InteractiveGameDB igDB = new InteractiveGameDB(ga);
		InteractiveGameState[] stateList = igDB.getStates();
		if (stateList.length == 0)
			return null;
		
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
		
		SimpleAdapter adapter = new SimpleAdapter(
					ga, 
					parterner_list,
					R.layout.interactive_item,
					new String[] { "pic","tree","code_name"},
					new int[] {R.id.interactive_state,R.id.interactive_tree,R.id.interactive_code});
		return adapter;
	}
	
	private void fake_update(){
		InteractiveGameDB igDB = new InteractiveGameDB(ga);
		InteractiveGameState states[] = new 	InteractiveGameState[5];
		states[0] = new InteractiveGameState(3,4,"Abcde");
		states[1] = new InteractiveGameState(4,2,"Bcdef");
		states[2] = new InteractiveGameState(5,3,"Cdefg");
		states[3] = new InteractiveGameState(6,1,"Defgh");
		states[4] = new InteractiveGameState(0,2,"Ehijk");
		igDB.updateState(states);
	}
	
	public void update(){
		
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
	
	public void send_cheers(String pid){
		
	}
}
