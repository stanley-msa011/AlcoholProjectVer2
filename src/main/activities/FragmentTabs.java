package main.activities;

import java.util.Calendar;

import tabControl.CustomTab;
import test.data.Reuploader;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;import android.content.SharedPreferences;
import android.graphics.Point;
import android.os.Build;
import android.os.Bundle;
import android.os.SystemClock;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.widget.TabHost;
import android.widget.TabHost.TabSpec;
import android.widget.TabWidget;

public class FragmentTabs extends FragmentActivity {

	static private TabHost tabHost;

	private static Context context;
	
	private static boolean enableTabs;
	private static Point screen_px;
	private static Point tab_px;
	static private TabSpec[] tabs;
	static private CustomTab[] customTabs;
	
	private static final String[] tabName ={"Test","Record","History"}; 
	private static final int[] iconId ={R.drawable.tabs_test,R.drawable.tabs_statistic,R.drawable.tabs_history}; 
	private static final String[] iconText ={"測試","紀錄","人生新頁"}; 
	
	private Fragment[] fragments;
	private android.support.v4.app.FragmentTransaction ft;
	private android.support.v4.app.FragmentManager fm;
	TabChangeListener tabChangeListener;
	
	@SuppressWarnings("deprecation")
	@Override
	protected void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		context = this;
        
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

		tab_px = new Point(screen_px.x,(int)(screen_px.y*(104.0/1280.0)));
		
		setContentView(R.layout.tab_layout);
		tabHost = (TabHost) this.findViewById(android.R.id.tabhost);
		tabHost.setup();
		
		if (tabs==null)
			tabs = new TabSpec[3];
		if (customTabs==null)
			customTabs = new CustomTab[3];
		
		
		for (int i=0;i<3;++i){
			customTabs[i] = new CustomTab(this,iconId[i],iconText[i]);
			tabs[i] = tabHost.newTabSpec(tabName[i]).setIndicator(customTabs[i].getTab());
			tabs[i].setContent(new DummyTabFactory(this));
			tabHost.addTab(tabs[i]);
		}
		fm =  getSupportFragmentManager();
		fragments = new Fragment[3];
		tabHost.setOnTabChangedListener(new TabChangeListener());
		enableTabs = true;
		
		tabHost.setCurrentTab(2);
		tabHost.setCurrentTab(0);
		
		DisplayMetrics dm = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(dm);
		int screenWidth = dm.widthPixels;
		
		
		TabWidget tabWidget = tabHost.getTabWidget();
		tabWidget.setBackgroundResource(R.drawable.tabs_background);
		
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
		enableTabs = true;
		
		SharedPreferences sp= PreferenceManager.getDefaultSharedPreferences(this);
		String uid = sp.getString("uid", "");
		if (uid.length() == 0){
			Intent newIntent = new Intent(this, PreSettingActivity.class);
			this.startActivity(newIntent);
			this.finish();
			return;
		}
		Reuploader.reuploader(this);
	}
	
	protected void onStop(){
		Log.d("TABS","ONSTOP");
		context = null;
		super.onStop();
	}
	
	
	protected void onPause(){
		Reuploader.cancel();
		Log.d("tabs","onPause");
		super.onPause();
		Log.d("tabs","onPauseEnd");
	}
	
	public void setTabState(String tabId){
		for (int i=0;i<3;++i){
			if (tabId.equals(tabName[i])){
				customTabs[i].changeState(true);
			}
			else{
				customTabs[i].changeState(false);
			}
		}
	}
	
	static public int getScreenWidth(){
		return screen_px.x;
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
		if (tab_px != null)
			return tab_px;
		return null;
	}
	
	static public void changeTab(int pos){
		TabWidget tabWidget = tabHost.getTabWidget();
		int count  = tabWidget.getChildCount();
		if (pos>=0 && pos < count){
			tabHost.setCurrentTab(pos);
		}
	}
	
	public void onActivityResult(int requestCode, int resultCode, Intent data){
		if (requestCode==1){
			finish();
		}
	}
	
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
    	menu.add(0, 0, 0, "Debug");
    	menu.add(0, 1, 1, "Normal");
    	menu.add(0, 2, 1, "Setting");
    	return super.onCreateOptionsMenu(menu);
    }
	
    public boolean onOptionsItemSelected(MenuItem item){
    	SharedPreferences sp= PreferenceManager.getDefaultSharedPreferences(this);
    	SharedPreferences.Editor editor = sp.edit();
		int id = item.getItemId();
		if (id == 0){
			editor.putBoolean("debug", true);
			editor.commit();
		}else if (id == 1){
			editor.putBoolean("debug", false);
			editor.commit();
		}else if (id == 2){
			Intent newIntent = new Intent(this, PreSettingActivity.class);
			this.startActivity(newIntent);
			this.finish();
		}
		
		return false;
    }
    
    public class TabChangeListener implements TabHost .OnTabChangeListener{

    	private String lastTabId;
    	
    	public TabChangeListener(){
    		 lastTabId = "null";
    	}
    	
		@Override
		public void onTabChanged(String tabId) {
			if (!enableTabs)
				return;
			if (lastTabId.equals(tabId))
				return;
			
			ft = fm.beginTransaction();
			for (int i=0;i<fragments.length;++i){
					if (fragments[i]!=null)
						ft.detach(fragments[i]);
			}
			for (int i=0;i<tabName.length;++i){
				if (tabId.equals(tabName[i])){
					if (fragments[i]== null){
						if (i==0)
							fragments[i] = new TestFragment();
						else if (i==1)
							fragments[i] = new StatisticFragment();
						else if (i==2)
							fragments[i] = new HistoryFragment2();
						ft.add(R.id.real_tabcontent,fragments[i],tabName[i] );
					}else{
						ft.attach(fragments[i]);
					}
					break;
				}
			}
			lastTabId = tabId;
			setTabState(tabId);
			ft.commit();
		}
    	
    }
	static class DummyTabFactory implements TabHost.TabContentFactory{
		private final Context context;

		public DummyTabFactory(Context context){
			this.context = context;
		}
		@Override
		public View createTabContent(String tag) {
			View v = new View(context);
			return v;
		}
		
	}
	static public void enableTab(boolean enable){
		enableTabs = enable;
	}
	
	static public Context getContext(){
		if (context !=null)
			return context;
		return null;
	}
	
}
