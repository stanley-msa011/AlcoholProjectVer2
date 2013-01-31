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

public class GalleryAdapter extends BaseAdapter {
	private ArrayList<HashMap<String,Object>> list;
	private HashMap<String,Bitmap> bitmap_map;
	private Context context;
	private LayoutInflater inflater;
	
	static int[] brac_result_image_id = {R.drawable.apple_good,R.drawable.apple_bad};
	
	public GalleryAdapter(ArrayList<HashMap<String,Object>> list, Context context){
		this.list = list;
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
			convertView = inflater.inflate(R.layout.game_history, null);
			v_tag = new vTag(convertView);
			convertView.setTag(v_tag);
		}
		else
			v_tag = (vTag) convertView.getTag();
		
		HashMap<String, Object> item_info = list.get(position);
		
		int stage = (Integer) item_info.get("stage");
		int tree = (Integer) item_info.get("pic");
		boolean brac = (Boolean) item_info.get("brac");
		Bitmap cur_brac = bitmap_map.get("brac"+String.valueOf(brac));
		Bitmap cur_stage = bitmap_map.get("bg"+String.valueOf(stage));
		Bitmap cur_tree = bitmap_map.get("tree"+String.valueOf(tree));
		
		
		String date = (String) item_info.get("date");
		
		if (cur_brac == null){
			int id;
			if (brac)
				id = brac_result_image_id [1];
			else
				id = brac_result_image_id [0];
			
			Bitmap tmp = BitmapFactory.decodeResource(context.getResources(),id );
			cur_brac = Bitmap.createScaledBitmap(tmp, 36, 36, true);
			tmp.recycle();
			bitmap_map.put("brac"+String.valueOf(brac),cur_brac);
		}
		
		if (cur_stage == null){
			Bitmap tmp = BitmapFactory.decodeResource(context.getResources(), BackgroundImageHandler.getBackgroundImageDrawableId(stage));
			cur_stage = Bitmap.createScaledBitmap(tmp, 192, 315, true);
			tmp.recycle();
			bitmap_map.put("bg"+String.valueOf(stage),cur_stage);
		}
		
		if (cur_tree == null){
			Bitmap tmp = BitmapFactory.decodeResource(context.getResources(), TreeImageHandler.getTreeImageDrawableId(tree));
			cur_tree = Bitmap.createScaledBitmap(tmp, 192, 192, true);
			tmp.recycle();
			bitmap_map.put("tree"+String.valueOf(tree),cur_tree);
		}
		v_tag.bg.setImageBitmap(cur_stage);
		v_tag.tree.setImageBitmap(cur_tree);
		v_tag.brac.setImageBitmap(cur_brac);
		v_tag.date.setText(date);
		
		return convertView;
	}
	
	private class vTag{
		ImageView bg;
		ImageView tree;
		TextView date;
		ImageView brac;
		public vTag(View convertView){
			bg = (ImageView) convertView.findViewById(R.id.gallery_bg);
			tree = (ImageView) convertView.findViewById(R.id.gallery_tree);
			date = (TextView) convertView.findViewById(R.id.gallery_date);
			brac = (ImageView) convertView.findViewById(R.id.gallery_result);
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
		
		Bitmap brac = bitmap_map.get("brac"+String.valueOf(true));
		if (brac != null){
			bitmap_map.remove("brac"+String.valueOf(true));
			Bitmap tmp =brac;
			tmp.recycle();
			brac = null;
		}
		brac = bitmap_map.get("brac"+String.valueOf(false));
		if (brac != null){
			bitmap_map.remove("brac"+String.valueOf(false));
			Bitmap tmp =brac;
			tmp.recycle();
			brac = null;
		}
		bitmap_map.clear();
	}
	
	public void clearSelected(int pos){
		boolean[] stage_selected_list = new boolean[GameState.MAX_STAGE+1];
		boolean[] tree_selected_list = new boolean[GameState.MAX_COINS[GameState.MAX_COINS.length-1]];
		
		for (int i = 0;i <stage_selected_list.length;++i)
			stage_selected_list[i] = true;
		
		for (int i = 0;i <tree_selected_list.length;++i)
			tree_selected_list[i] = true;
		
		for (int i = -2;i<3;++i){
			int s_pos = pos+i;
			if (s_pos <0 || s_pos >= list.size())
				continue;
			HashMap<String,Object> item= list.get(s_pos);
			int tree = (Integer) item.get("pic");
			int stage = (Integer) item.get("stage");
			stage_selected_list[stage] = false;
			tree_selected_list[tree] = false;
		}
		for (int i = 0;i <stage_selected_list.length;++i){
			if (stage_selected_list[i]){
				Bitmap bg = bitmap_map.get("bg"+String.valueOf(i));
				if (bg != null){
					bitmap_map.remove("bg"+String.valueOf(i));
					Bitmap tmp =bg;
					tmp.recycle();
					bg = null;
				}
			}
		}
		for (int i = 0;i <tree_selected_list.length;++i){
			if(tree_selected_list[i]){
				Bitmap tree = bitmap_map.get("tree"+String.valueOf(i));
				if (tree != null){
					bitmap_map.remove("tree"+String.valueOf(i));
					Bitmap tmp = tree;
					tmp.recycle();
					tree = null;
				}
			}
		}
	}
	
}
