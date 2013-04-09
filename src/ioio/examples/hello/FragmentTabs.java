package ioio.examples.hello;

import tabControl.TabManager;
import android.graphics.Point;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.widget.TabHost;
import android.widget.TabWidget;

public class FragmentTabs extends FragmentActivity {

	static private TabHost tabHost;
	private TabManager tabManager;
	private static Point screen_px;
	private static Point tab_px;
	private static float screen_density;
	
	@Override
	protected void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		Display display = getWindowManager().getDefaultDisplay();
		if (Build.VERSION.SDK_INT<13){
			int w = display.getWidth();
			int h = display.getHeight();
			screen_px = new Point(w,h);
		}
		else{
			screen_px = new Point();
			display.getSize(screen_px);
		}
		screen_density =this.getResources().getDisplayMetrics().density;

		Log.d("SCREEN_PX",screen_px.toString());
		
		setContentView(R.layout.tab_layout);
		tabHost = (TabHost) this.findViewById(android.R.id.tabhost);
		tabHost.setup();
		tabManager = new TabManager(this,tabHost,R.id.real_tabcontent);
		

		
		tabManager.addTab(
				tabHost.newTabSpec("Test").setIndicator("測試"),
				TestFragment.class,
				null
				);
		
		tabManager.addTab(
				tabHost.newTabSpec("Statistic").setIndicator("紀錄"),
				StatisticFragment.class,
				null
				);
		
		tabManager.addTab(
				tabHost.newTabSpec("Interaction").setIndicator("社交"),
				InteractionFragment.class,
				null
				);
		
		tabManager.addTab(
				tabHost.newTabSpec("History").setIndicator("歷程"),
				HistoryFragment.class,
				null
				);
		
		tabHost.setCurrentTab(0);
		
		DisplayMetrics dm = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(dm);
		int screenWidth = dm.widthPixels;
		
		
		TabWidget tabWidget = tabHost.getTabWidget();
		
		
		int count  = tabWidget.getChildCount();
		for (int i=0;i<count;++i)
			tabWidget.getChildTabViewAt(i).setMinimumWidth(screenWidth/count);
		LayoutParams widgetParam = tabWidget.getLayoutParams();
		widgetParam.height = (int)(screen_px.y*(100.0/1280.0));
		tab_px = new Point(widgetParam.width,widgetParam.height);
		Log.d("TAB PX",tab_px.toString());
	}
	
	static public Point getSize(){
		if (screen_px == null){
			Log.d("SCREEN_SIZE","NULL");
		}
		else{
			String str = screen_px.toString();
			Log.d("SCREEN_SIZE",str);
		}
		Point size = new Point();
		size.x = screen_px.x;
		size.y = screen_px.y;
		if (tab_px!=null&&tab_px.y>0)
			size.y -= tab_px.y;
		return size;
	}
	
	static public Point getTabSize(){
		if (tab_px == null){
			Log.d("TAB_SIZE","NULL");
		}
		else{
			String str = tab_px.toString();
			Log.d("TAB_SIZE",str);
		}
		return tab_px;
	}
	
	static public void changeTab(int pos){
		TabWidget tabWidget = tabHost.getTabWidget();
		int count  = tabWidget.getChildCount();
		if (pos>=0 && pos < count){
			tabHost.setCurrentTab(pos);
		}
	}
	
	static public void enableTab(boolean s){
		TabWidget tabWidget = tabHost.getTabWidget();
		int count  = tabWidget.getChildCount();
		for (int i=0;i<count;++i){
			tabWidget.getChildAt(i).setEnabled(s);
		}
	}
	
}
