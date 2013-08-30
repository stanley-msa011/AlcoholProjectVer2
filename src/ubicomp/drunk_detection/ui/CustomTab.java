package ubicomp.drunk_detection.ui;

import ubicomp.drunk_detection.activities.FragmentTabs;
import ubicomp.drunk_detection.activities.R;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;

public class CustomTab {

	private Context context;
	private View view;
	private int iconId;
	private int iconOnId;
	private LayoutInflater inflater;
	
	private ImageView bg,icon;
	private Drawable onDrawable,offDrawable;
	private Drawable iconDrawable, iconOnDrawable;
	
	public CustomTab(Context context, int id, int onId){
		this.context = context;
		this.inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		this.iconId = id;
		this.iconOnId = onId;
		setting();
	}
	
	
	private void setting(){
		int width = FragmentTabs.getScreenWidth();
		view = inflater.inflate(R.layout.tab_icon_layout, null);
		bg = (ImageView) view.findViewById(R.id.tab_icon_bg);
		icon = (ImageView) view.findViewById(R.id.tab_icon_icon);
		iconDrawable = context.getResources().getDrawable(iconId);
		iconOnDrawable = context.getResources().getDrawable(iconOnId);
		RelativeLayout.LayoutParams bgParam = (RelativeLayout.LayoutParams)bg.getLayoutParams();
		bgParam.width = width/3;
		bgParam.height = width*209/1080;
		RelativeLayout.LayoutParams iconParam = (RelativeLayout.LayoutParams)icon.getLayoutParams();
		iconParam.width = width * 89/1080;
		iconParam.height = width*58/1080;
		icon.setImageDrawable(iconDrawable);
		onDrawable = context.getResources().getDrawable(R.drawable.tabs_down);
		offDrawable =context.getResources().getDrawable(R.drawable.tabs_selector);
	}
	
	public View getTab(){
		return view;
	}
	
	public void changeState(boolean selected){
		if (selected){
			bg.setImageDrawable(onDrawable);
			icon.setImageDrawable(iconOnDrawable);
		}else{
			bg.setImageDrawable(offDrawable);
			icon.setImageDrawable(iconDrawable);
		}
	}
	
	public void clear(){
		
		bg.setImageDrawable(null);
		icon.setImageDrawable(null);
	}
	
}
