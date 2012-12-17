package ioio.examples.hello;

import java.util.ArrayList;
import java.util.HashMap;

import android.os.Bundle;
import android.app.Activity;
import android.database.Cursor;
import android.util.Log;
import android.widget.ListView;
import android.widget.SimpleAdapter;

public class BracHistoryActivity extends Activity {

	private BracDbAdapter mBracDbAdapter;
	private SimpleAdapter brac_adapter;
	private ListView brac_list_view;
	private ArrayList<HashMap<String,Object>> brac_list;
	
	static private final double[] limit = {0.05, 0.15, 0.25, 0.40, 0.60};
	static private final int[]	bg_setting = {
		R.drawable.bar01,R.drawable.bar02,R.drawable.bar03,R.drawable.bar04,R.drawable.bar05,R.drawable.bar06
	};
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		System.gc();
		setContentView(R.layout.activity_brac_history);
		brac_list_view = (ListView)findViewById(R.id.brac_history_listview);
		mBracDbAdapter = new BracDbAdapter(this);
		mBracDbAdapter.open();
		brac_list = fillData();
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
		Log.d("Brac","OnStop");
		brac_list.clear();
		brac_adapter.notifyDataSetInvalidated();
	}
	
	private ArrayList<HashMap<String,Object>> fillData() {
        Cursor cursor = mBracDbAdapter.fetchAllHistory();
        
        cursor.moveToFirst();
        
        int number_history = cursor.getCount();
        ArrayList<HashMap<String,Object>> brac_list = new ArrayList<HashMap<String,Object>>();
        
        for (int i=0;i<number_history;++i){
        	HashMap<String,Object> item = new HashMap<String,Object>();
        	cursor.moveToPosition(i);
        	String _date = cursor.getString(1);
        	String _brac = cursor.getString(2);
        	int _bg = getBg(_brac);
        	
			item.put("date",_date);
			item.put("brac",_brac );
			item.put("bg", _bg);
        	brac_list.add(item);
        }
        mBracDbAdapter.close();
		return brac_list;
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
