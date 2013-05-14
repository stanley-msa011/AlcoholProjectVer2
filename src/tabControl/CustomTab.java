package tabControl;

import main.activities.FragmentTabs;
import main.activities.R;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.TextView;

public class CustomTab {

	private Context context;
	private View view;
	private int iconId;
	//private String iconText; 
	private LayoutInflater inflater;
	
	private ImageView bg,icon;
	private Bitmap iconBmp;
	static private Bitmap onBmp, offBmp;
	//private TextView textView;
	
	public CustomTab(Context context, int id, String text){
		this.context = context;
		this.inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		this.iconId = id;
		//this.iconText = text;
		setting();
	}
	
	
	private void setting(){
		view = inflater.inflate(R.layout.tab_icon_layout, null);
		bg = (ImageView) view.findViewById(R.id.tab_icon_bg);
		icon = (ImageView) view.findViewById(R.id.tab_icon_icon);
		//textView = (TextView) view.findViewById(R.id.tab_icon_text);
		
		
		if (onBmp == null || onBmp.isRecycled())
			onBmp = BitmapFactory.decodeResource(context.getResources(), R.drawable.tabs_down);
		
		offBmp = null;
		//if (offBmp == null || offBmp.isRecycled())
		//	offBmp = BitmapFactory.decodeResource(context.getResources(), R.drawable.tab_off);
		
		iconBmp = BitmapFactory.decodeResource(context.getResources(), iconId);
		
		Point tab =FragmentTabs.getTabSize();
		
		int tabWidth = tab.x / 3;
		RelativeLayout.LayoutParams bgParam = (LayoutParams) bg.getLayoutParams();
		bgParam.width = tab.x / 3;
		bgParam.height = tab.y;
		
		RelativeLayout.LayoutParams iconParam = (LayoutParams) icon.getLayoutParams();
		iconParam.width = (int)(tabWidth * 70.0/180.0);
		iconParam.height = (int)(tab.y * 80.0/110.0);
		
		icon.setImageBitmap(iconBmp);
		
		//textView.setText(iconText);
		//int textSize = (int) (bgParam.height*0.2);
		//textView.setTextSize(TypedValue.COMPLEX_UNIT_PX, textSize);
	}
	
	public View getTab(){
		return view;
	}
	
	public void changeState(boolean selected){
		if (selected){
			bg.setImageBitmap(onBmp);
			//textView.setTextColor(0xFF413D3C);
		}else{
			bg.setImageBitmap(offBmp);
			//textView.setTextColor(0xFFE5E5E5);
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
	}
	
	
	
	
}
