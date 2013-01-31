package game.statistic;

import ioio.examples.hello.R;

import java.util.ArrayList;
import java.util.HashMap;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

public class StatisticAdapter extends BaseAdapter {
	private ArrayList<HashMap<String,Object>> list;
	private LayoutInflater inflater;
	
	private static int[] COLOR = {0xFFFFCCCC,0xFFFF8888,0xFFFF4444,0xFFFF0000};
	
	public StatisticAdapter(ArrayList<HashMap<String,Object>> list, Context context){
		this.list = list;
		inflater  = LayoutInflater.from(context);
	}
	
	@Override
	public int getCount() {
		return list.size();
	}

	@Override
	public Object getItem(int position) {
		return list.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		vTag v_tag = null;
		if(convertView==null){
			convertView = inflater.inflate(R.layout.statistic_content, null);
			v_tag = new vTag(convertView);
			convertView.setTag(v_tag);
		}
		else
			v_tag = (vTag) convertView.getTag();
		
		HashMap<String, Object> item_info = list.get(position);
		
		boolean exist = (Boolean) item_info.get("exist");
		int idx= position / 7;
		if (exist)
			v_tag.block.setBackgroundColor(COLOR[idx]);
		else
			v_tag.block.setBackgroundColor(0x00000000);
		return convertView;
	}
	
	private class vTag{
		View block;
		public vTag(View convertView){
			block = (View) convertView.findViewById(R.id.static_content_block);
		}
	}
}
