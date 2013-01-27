package game.gallery;

import game.BackgroundImageHandler;
import game.GameState;
import game.TreeImageHandler;
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

public class GalleryIndexAdapter extends BaseAdapter {
	private ArrayList<HashMap<String,Object>> list;
	private HashMap<String,Bitmap> bitmap_map;
	private Context context;
	private LayoutInflater inflater;
	
	public GalleryIndexAdapter(ArrayList<HashMap<String,Object>> list, Context context){
		this.list = list;
		this.context = context;
		bitmap_map = new  HashMap<String,Bitmap>();
		inflater  = LayoutInflater.from(context);
	}
	
	public void Reset(ArrayList<HashMap<String,Object>> list){
		this.list = list;
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
			convertView = inflater.inflate(R.layout.game_history_index, null);
			v_tag = new vTag(convertView);
			convertView.setTag(v_tag);
		}
		else
			v_tag = (vTag) convertView.getTag();
		
		HashMap<String, Object> item_info = list.get(position);
		
		int stage = (Integer) item_info.get("stage");
		Bitmap cur_stage = bitmap_map.get("bg"+String.valueOf(stage));
		int tree = (Integer) item_info.get("tree");
		Bitmap cur_tree = bitmap_map.get("tree"+String.valueOf(tree));
		
		
		
		String date = (String) item_info.get("date");
		
		if (cur_stage == null){
			Bitmap tmp = BitmapFactory.decodeResource(context.getResources(), BackgroundImageHandler.getBackgroundImageDrawableId(stage));
			cur_stage = Bitmap.createScaledBitmap(tmp, 48, 79, true);
			tmp.recycle();
			bitmap_map.put("bg"+String.valueOf(stage),cur_stage);
		}
		
		if (cur_tree == null){
			Bitmap tmp = BitmapFactory.decodeResource(context.getResources(), TreeImageHandler.getTreeImageDrawableId(tree));
			cur_tree = Bitmap.createScaledBitmap(tmp, 48, 48, true);
			tmp.recycle();
			bitmap_map.put("tree"+String.valueOf(tree),cur_tree);
		}
		
		v_tag.bg.setImageBitmap(cur_stage);
		v_tag.tree.setImageBitmap(cur_tree);
		v_tag.date.setText(date);
		return convertView;
	}

	
	private class vTag{
		ImageView bg;
		ImageView tree;
		TextView date;
		
		
		public vTag(View convertView){
			bg = (ImageView) convertView.findViewById(R.id.gallery_index_background);
			tree = (ImageView) convertView.findViewById(R.id.gallery_index_tree);
			date = (TextView) convertView.findViewById(R.id.gallery_index_date);
		}
	}
	public void clearAll(){
		for (int i=0;i<=GameState.MAX_STAGE;++i){
			Bitmap bg = bitmap_map.get("bg"+String.valueOf(i));
			if (bg != null){
				bitmap_map.remove("bg"+String.valueOf(i));
				Bitmap tmp =bg;
				tmp.recycle();
				bg = null;
			}
		}
		for (int i=0;i<GameState.MAX_COINS[GameState.MAX_COINS.length-1];++i){
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
