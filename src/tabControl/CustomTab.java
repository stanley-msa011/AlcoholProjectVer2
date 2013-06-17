package tabControl;

import ubicomp.drunk_detection.activities.FragmentTabs;
import ubicomp.drunk_detection.activities.R;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;

public class CustomTab {

	private Context context;
	private View view;
	private int iconId;
	private int iconOnId;
	//private String iconText; 
	private LayoutInflater inflater;
	
	private ImageView bg,icon;
	private Bitmap iconBmp,iconOnBmp;
	static private Bitmap onBmp, offBmp;
	//private TextView textView;
	
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
		
		if (onBmp == null || onBmp.isRecycled())
			onBmp = BitmapFactory.decodeResource(context.getResources(), R.drawable.tabs_down);
		
		offBmp = null;
		
		iconBmp = BitmapFactory.decodeResource(context.getResources(), iconId);
		iconOnBmp = BitmapFactory.decodeResource(context.getResources(), iconOnId);
		
		Point tab =FragmentTabs.getTabSize();
		
		int tabWidth = tab.x / 3;
		RelativeLayout.LayoutParams bgParam = (LayoutParams) bg.getLayoutParams();
		bgParam.width = tab.x / 3;
		bgParam.height = tab.y;
		
		RelativeLayout.LayoutParams iconParam = (LayoutParams) icon.getLayoutParams();
		iconParam.width = tabWidth * 77/360;
		iconParam.height =tab.y *57/211;
		
		icon.setImageBitmap(iconBmp);
		
	}
	
	public View getTab(){
		return view;
	}
	
	public void changeState(boolean selected){
		if (selected){
			bg.setImageBitmap(onBmp);
			icon.setImageBitmap(iconOnBmp);
		}else{
			bg.setImageBitmap(offBmp);
			icon.setImageBitmap(iconBmp);
		}
	}
	
	public void clear(){
		bg.setImageBitmap(null);
		if (onBmp != null && !onBmp.isRecycled()){
			onBmp.recycle();
			onBmp = null;
		}
		
		if (offBmp != null && !offBmp.isRecycled()){
			offBmp.recycle();
			offBmp = null;
		}
		icon.setImageBitmap(null);
		if (iconBmp!=null && !iconBmp.isRecycled()){
			iconBmp.recycle();
			iconBmp = null;
		}
		if (iconOnBmp!=null && !iconOnBmp.isRecycled()){
			iconOnBmp.recycle();
			iconOnBmp = null;
		}
	}
	
	
	
	
}
