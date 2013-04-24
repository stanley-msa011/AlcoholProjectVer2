package main.activities;

import tabControl.CustomTab;
import tabControl.TabManager;
import test.data.Reuploader;
import ui.LoadingPageHandler;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Message;
import android.os.Parcel;
import android.os.Parcelable;
import android.preference.PreferenceManager;
import android.support.v4.app.FragmentActivity;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.widget.TabHost;
import android.widget.TabHost.TabSpec;
import android.widget.TabWidget;

public class FragmentTabs extends FragmentActivity {

	static private TabHost tabHost;
	private TabManager tabManager;
	private static Point screen_px;
	private static Point tab_px;
	static private TabSpec[] tabs;
	static private CustomTab[] customTabs;
	
	private static final String[] tabName ={"Test","Record","History","Question"}; 
	private static final int[] iconId ={R.drawable.tab_test,R.drawable.tab_record,R.drawable.tab_history,R.drawable.tab_social}; 
	private static final String[] iconText ={"測試","紀錄","人生新頁","問卷"}; 
	private static final String[] iconTextEng ={"Test","Record","History","Questionaire"}; 
	
	public static Bitmap loadingBmp;
	
	@SuppressWarnings("deprecation")
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

		tab_px = new Point(screen_px.x,(int)(screen_px.y*(110.0/1280.0)));
		
		setContentView(R.layout.tab_layout);
		tabHost = (TabHost) this.findViewById(android.R.id.tabhost);
		tabHost.setup();
		tabManager = new TabManager(this,tabHost,R.id.real_tabcontent);
		

		if (tabs==null)
			tabs = new TabSpec[4];
		if (customTabs==null)
			customTabs = new CustomTab[4];
		
		for (int i=0;i<4;++i){
			if (Lang.eng)
				customTabs[i] = new CustomTab(this,iconId[i],iconTextEng[i]);
			else
			customTabs[i] = new CustomTab(this,iconId[i],iconText[i]);
			tabs[i] = tabHost.newTabSpec(tabName[i]).setIndicator(customTabs[i].getTab());
			
		}
		
		tabManager.addTab(
				tabs[0],
				TestFragment.class,
				null
				);
		
		tabManager.addTab(
				tabs[1],
				StatisticFragment.class,
				null
				);
		
		tabManager.addTab(
				tabs[2],
				HistoryFragment.class,
				null
				);
		
		tabManager.addTab(
				tabs[3],
				SocialFragment.class,
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
		widgetParam.width = tab_px.x;
		widgetParam.height = tab_px.y;
		
		Log.d("TAB PX",tab_px.toString());
		
	}
	
	protected void onResume(){
		super.onResume();
		
		SharedPreferences sp= PreferenceManager.getDefaultSharedPreferences(this);
		String uid = sp.getString("uid", "");
		if (uid.length() == 0){
			Intent newIntent = new Intent(this, PreSettingActivity.class);
			this.startActivityForResult(newIntent, 1);
			return;
		}
		Reuploader.reuploader(this);
	}
	
	protected void onStop(){
		Log.d("TABS","ONSTOP");
		if (loadingBmp!=null && !loadingBmp.isRecycled())
			loadingBmp.recycle();
		super.onStop();
	}
	
	protected void onPause(){
		Reuploader.cancel();
		Log.d("tabs","onPause");
		super.onPause();
		Log.d("tabs","onPauseEnd");
	}

	protected void onDestory(){
		Log.d("tabs","onDestory");
		super.onDestroy();
		Log.d("tabs","onDestory end");
	}
	
	
	public void setTabState(String tabId){
		for (int i=0;i<4;++i){
			if (tabId.equals(tabName[i]))
				customTabs[i].changeState(true);
			else
				customTabs[i].changeState(false);
		}
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
	
	public void onActivityResult(int requestCode, int resultCode, Intent data){
		if (requestCode==1){
			finish();
		}
	}
	
}
