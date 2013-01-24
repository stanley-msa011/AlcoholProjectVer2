package game.interaction;

import ioio.examples.hello.R;

import java.util.ArrayList;
import java.util.HashMap;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class InteractiveAdapter extends BaseAdapter {

	private static int[] STAGE_BG_COLOR = {0x22FF0000,0x22FFFF00,0x2200FF00,0x220000FF};
	
	private ArrayList<HashMap<String,Object>> list;
	private HashMap<String,Bitmap> bitmap_map;
	private Context context;
	private LayoutInflater inflater;
	public InteractiveAdapter(ArrayList<HashMap<String,Object>> partner_list, Context context){
		list = partner_list;
		this.context = context;
		bitmap_map = new  HashMap<String,Bitmap>();
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
			convertView = inflater.inflate(R.layout.interactive_item, null);
			v_tag = new vTag(convertView);
			convertView.setTag(v_tag);
		}
		else
			v_tag = (vTag) convertView.getTag();
		
		HashMap<String, Object> item_info = list.get(position);
		

		
		Bitmap cur_tree = bitmap_map.get("tree"+String.valueOf(position));
		
		int stage = (Integer)item_info.get("stage");
		convertView.setBackgroundColor(STAGE_BG_COLOR [stage]);
		
		int tree = (Integer) item_info.get("pic");
		String code = (String) item_info.get("code_name");
		
		if (cur_tree == null){
			Bitmap tmp = BitmapFactory.decodeResource(context.getResources(), tree);
			cur_tree = Bitmap.createScaledBitmap(tmp, 72,72, true);//81,144
			tmp.recycle();
			bitmap_map.put("tree"+String.valueOf(position),cur_tree);
		}
		v_tag.tree.setImageBitmap(cur_tree);
		v_tag.code_name.setText(code);
		
		return convertView;
	}
	
	private class vTag{
		ImageView tree;
		TextView code_name;
		public vTag(View convertView){
			tree = (ImageView) convertView.findViewById(R.id.interactive_state);
			code_name = (TextView) convertView.findViewById(R.id.interactive_code);
		}
	}

	public void clearAll(){
		int len = list.size() + 1;
		for (int i=0;i<len;++i){
			Bitmap tree = bitmap_map.get("tree"+String.valueOf(i));
			if (tree != null){
				bitmap_map.remove("tree"+String.valueOf(i));
				Bitmap tmp = tree;
				tmp.recycle();
				tree = null;
			}
		}
		bitmap_map.clear();
	}
	
}
