package ioio.examples.hello;

import game.BracGameState;
import game.GameDB;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

import android.os.Bundle;
import android.app.Activity;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;

public class BracHistoryActivity extends Activity {

	private GameDB gdb ;
	private SimpleAdapter brac_adapter;
	private ListView brac_list_view;
	private ArrayList<HashMap<String,Object>> brac_list;
	
	static private final double[] limit = {0.05, 0.09, 0.25, 0.40, 0.60};
	static private final int[]	bg_setting = {
		R.drawable.bar01,R.drawable.bar02,R.drawable.bar03,R.drawable.bar04,R.drawable.bar05,R.drawable.bar06
	};
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		System.gc();
		setContentView(R.layout.activity_brac_history);
		brac_list_view = (ListView)findViewById(R.id.brac_history_listview);
		
		gdb = new GameDB(this);
		BracGameState[] list = gdb.getAllStates();
		fillData(list);
		brac_adapter = new SimpleAdapter(
					this, 
					brac_list,
					R.layout.brac_history_content,
					new String[] { "date","brac","bg"},
					new int[] {R.id.brac_date,R.id.brac_value,R.id.brac_background});
		brac_list_view.setAdapter(brac_adapter);

	}
	
	protected void onStop(){
		super.onStop();
		brac_list.clear();
		brac_adapter.notifyDataSetInvalidated();
	}
	
	private void fillData(BracGameState[] gs){
		int len = gs.length;
		brac_list = new ArrayList<HashMap<String,Object>>();
		for (int i=0;i<len;++i){
			HashMap<String,Object> item = new HashMap<String,Object>();
			long date = gs[i].date;
			Date time = new Date(date*1000L);
			String _date = new SimpleDateFormat("MM/dd/yyyy\nkk:mm",Locale.TAIWAN).format(time);
			item.put("date",_date);
			DecimalFormat df = new DecimalFormat("0.000");
			String _brac = df.format(gs[i].brac);
			item.put("brac", _brac);
			int _bg = getBg(_brac);
			item.put("bg", _bg);
			brac_list.add(item);
		}
	}
	
	private int getBg(String _brac){
		double brac_v = Double.valueOf(_brac);
		if (brac_v < limit[0])
			return bg_setting[0];
		else if (brac_v < limit[1])
			return bg_setting[1];
		else if (brac_v < limit[2])
			return bg_setting[2];
		else if (brac_v < limit[3])
			return bg_setting[3];
		else if (brac_v < limit[4])
			return bg_setting[4];
		else 
			return bg_setting[5];
	}
}
