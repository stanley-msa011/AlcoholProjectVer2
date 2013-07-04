package tabControl;

import ubicomp.drunk_detection.activities.R;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;

public class CustomTab {

	private Context context;
	private View view;
	private int iconId;
	private int iconOnId;
	private LayoutInflater inflater;
	
	private ImageView bg,icon;
	private Drawable onDrawable;
	private Drawable iconDrawable, iconOnDrawable;
	
	public CustomTab(Context context, int id, int onId, String text){
		this.context = context;
		this.inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		this.iconId = id;
		this.iconOnId = onId;
		setting();
	}
	
	
	private void setting(){
		view = inflater.inflate(R.layout.tab_icon_layout, null);
		bg = (ImageView) view.findViewById(R.id.tab_icon_bg);
		icon = (ImageView) view.findViewById(R.id.tab_icon_icon);
		iconDrawable = context.getResources().getDrawable(iconId);
		iconOnDrawable = context.getResources().getDrawable(iconOnId);
		icon.setImageDrawable(iconDrawable);
		onDrawable = context.getResources().getDrawable(R.drawable.tabs_down);
	}
	
	public View getTab(){
		return view;
	}
	
	public void changeState(boolean selected){
		if (selected){
			bg.setImageDrawable(onDrawable);
			icon.setImageDrawable(iconOnDrawable);
		}else{
			bg.setImageDrawable(null);
			icon.setImageDrawable(iconDrawable);
		}
	}
	
	public void clear(){
		
		bg.setImageDrawable(null);
		icon.setImageDrawable(null);
	}
	
}
