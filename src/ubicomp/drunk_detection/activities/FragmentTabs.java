package ubicomp.drunk_detection.activities;

import java.util.Calendar;

import ubicomp.drunk_detection.activities.R;
import ubicomp.drunk_detection.fragments.HistoryFragment;
import ubicomp.drunk_detection.fragments.StatisticFragment;
import ubicomp.drunk_detection.fragments.TestFragment;
import ubicomp.drunk_detection.ui.CustomTab;
import ubicomp.drunk_detection.ui.CustomToast;
import ubicomp.drunk_detection.ui.LoadingDialogControl;
import ubicomp.drunk_detection.ui.CustomMenu;
import ubicomp.drunk_detection.ui.ScreenSize;
import ubicomp.drunk_detection.ui.Typefaces;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Point;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TabHost;
import android.widget.TabHost.TabSpec;
import android.widget.TabWidget;
import data.uploader.ClickLogUploader;
import data.uploader.Reuploader;
import debug.clicklog.ClickLogId;
import debug.clicklog.ClickLogger;

public class FragmentTabs extends FragmentActivity {
	
	static private TabHost tabHost;

	private static Context context;
	
	private static Point screen_px;
	private static Point tab_px;
	static private TabSpec[] tabs;
	static private CustomTab[] customTabs;
	
	private static final String[] tabName ={"Test","Record","History"}; 
	private static final int[] iconId ={R.drawable.tabs_test_selector,R.drawable.tabs_statistic_selector,R.drawable.tabs_history_selector}; 
	private static final int[] iconOnId ={R.drawable.tabs_test_on,R.drawable.tabs_statistic_on,R.drawable.tabs_history_on}; 
	
	static private Fragment[] fragments;
	private android.support.v4.app.FragmentTransaction ft;
	private android.support.v4.app.FragmentManager fm;
	TabChangeListener tabChangeListener;
	
	private  FragmentTabs fragmentTabs; 
	
	private LoadingPageHandler loadingPageHandler;
	
	private Thread t;
	
	private CustomMenu menu;
	
	private static int notify_action = 0;
	
	@Override
	protected void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.tab_layout);
		
		context = this;
		fragmentTabs = this;
		Typefaces.initAll(this);
		
		CustomToast.settingSoundPool(getBaseContext());
		
		loading_page = (ImageView) this.findViewById(R.id.loading_page);
		
		screen_px = ScreenSize.getScreenSize(getContext());
		
		tab_px = new Point(screen_px.x,screen_px.x*209/1080);
		
		tabHost = (TabHost) this.findViewById(android.R.id.tabhost);
		tabHost.setup();
		
		if (tabs==null)
			tabs = new TabSpec[3];
		if (customTabs==null)
			customTabs = new CustomTab[3];
		
		for (int i=0;i<3;++i){
			customTabs[i] = new CustomTab(this,iconId[i],iconOnId [i]);
			tabs[i] = tabHost.newTabSpec(tabName[i]).setIndicator(customTabs[i].getTab());
			tabs[i].setContent(new DummyTabFactory(this));
			tabHost.addTab(tabs[i]);
		}
		fm =  getSupportFragmentManager();
		fragments = new Fragment[3];
		tabHost.setOnTabChangedListener(new TabChangeListener());
		
		tabHost.setCurrentTab(1);
		tabHost.setCurrentTab(0);
		
		loadingPageHandler = new LoadingPageHandler();
		t = new Thread(new TimerRunnable());
		t.start();
		
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
		
		 enableTabAndClick(false);
	}
	
	@Override
	protected void onStart(){
		Reuploader.reuploader(this);
		ClickLogUploader.upload(this);
		Intent a_intent = new Intent(this,RegularCheckService.class);
		this.startService(a_intent);
		super.onStart();
	}
	protected void onResume(){
		super.onResume();
		
		SharedPreferences sp= PreferenceManager.getDefaultSharedPreferences(this);
		String uid = sp.getString("uid", "");
		if (uid.length() == 0){
			defaultSetting();
		}
	}
	
	protected void onStop(){
		context = null;
		super.onStop();
	}
	
	
	protected void onPause(){
		closeOptionsMenu();
		Reuploader.cancel();
		super.onPause();
	}
	
	public void setTabState(String tabId){
		for (int i=0;i<3;++i){
			if (tabId.equals(tabName[i]))
				customTabs[i].changeState(true);
			else
				customTabs[i].changeState(false);
		}
	}
	
	public static void changeTab(int pos){
		notify_action = 0;
		TabWidget tabWidget = tabHost.getTabWidget();
		int count  = tabWidget.getChildCount();
		if (pos>=0 && pos < count){
			tabHost.setCurrentTab(pos);
		}
	}
	
	public static void changeTab(int pos, int action){
		if (pos == 2){
			notify_action = action;
			tabHost.setCurrentTab(pos);
		}
	}
	
    public class TabChangeListener implements TabHost .OnTabChangeListener{

    	private String lastTabId;
    	
    	public TabChangeListener(){
    		 lastTabId = "null";
    	}
    	
		@Override
		public void onTabChanged(String tabId) {

			if (lastTabId.equals(tabId))
				return;
			
			LoadingDialogControl.show(fragmentTabs);
			
			long tab = -1;
			if (tabId.equals(tabName[0]))
				tab = ClickLogId.TAB_TEST;
			else if (tabId.equals(tabName[1]))
				tab = ClickLogId.TAB_STATISTIC;
			else if (tabId.equals(tabName[2]))
				tab = ClickLogId.TAB_STORYTELLING;
			ClickLogger.Log(getBaseContext(), tab);
			
			ft = fm.beginTransaction();
			
			for (int i=0;i<fragments.length;++i){
					if (fragments[i]!=null)
						ft.detach(fragments[i]);
			}
			for (int i=0;i<tabName.length;++i){
				if (tabId.equals(tabName[i])){
					if (fragments[i]== null){
						if (i==0){
							if (fragments[i] != null)
								ft.remove(fragments[i]);
							fragments[i] = new TestFragment();
						}
						else if (i==1){
							if (fragments[i] != null)
								ft.remove(fragments[i]);
							fragments[i] = new StatisticFragment();
						}else if (i==2){
							if (fragments[i] != null)
								ft.remove(fragments[i]);
							fragments[i] = new HistoryFragment();
							if (notify_action != 0){
								Bundle data = new Bundle();
								data.putInt("action", notify_action);
								fragments[i].setArguments(data);
								notify_action = 0;
							}
						}
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
	
	public static void enableTabAndClick(boolean enable){
		enableTab(enable);
		setClickable(enable);
	}
	
	private static void enableTab(boolean enable){
		if (tabHost==null || tabHost.getTabWidget()==null)
			return;
		
		int count = tabHost.getTabWidget().getChildCount();
		for (int i=0;i<count;++i){
			tabHost.getTabWidget().getChildAt(i).setClickable(enable);
		}
	}
	
	private static void setClickable(boolean enable){
		clickable = enable;
	}
	
	public static boolean getClickable(){
		return clickable;
	}
	
	public static Context getContext(){
		if (context !=null)
			return context;
		return null;
	}
	
	private ImageView loading_page;
	
	private class TimerRunnable implements Runnable{
		@Override
		public void run() {
			try {
				Thread.sleep(3000);
				loadingPageHandler.sendEmptyMessage(0);
			} catch (InterruptedException e) {}
		}
	}
	
	@SuppressLint("HandlerLeak")
	private class LoadingPageHandler extends Handler{
		public void handleMessage(Message msg){
			if(msg.what == 0){
				loading_page.setVisibility(View.INVISIBLE);
				enableTabAndClick(true);
			}
			else
				loading_page.setVisibility(View.VISIBLE);
		}
	}
	
	
	private static boolean clickable = false;
	
	private boolean doubleClickState = false;
	
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		if (!clickable)
			return super.onTouchEvent(event);
		if (event.getAction() ==MotionEvent.ACTION_DOWN){
			if (!doubleClickState){
				doubleClickState = true;
				new Thread(new BackgroundDoubleOnTouchRunnable()).start();
			}
			else{
				openOptionsMenu();
				doubleClickState = false;
			}
			return false;
		}
		return super.onTouchEvent(event);
	}
	
	private class BackgroundDoubleOnTouchRunnable implements Runnable{
		@Override
		public void run() {
			try {
				Thread.sleep(300);
			} catch (InterruptedException e) {
			}finally{
				doubleClickState = false;
			}
		}
	}
	
	
	@Override
	public void openOptionsMenu(){
		if (Build.VERSION.SDK_INT<14){
			super.openOptionsMenu();
			return;
		}
		if (menu == null)
			menu = new CustomMenu(this,getLayoutInflater());
		if (!menu.isShowing() && clickable)
			menu.showAtLocation(this.getWindow().getDecorView(), Gravity.BOTTOM,0, 0);
	}
	
	@Override
	public void closeOptionsMenu(){
		if (Build.VERSION.SDK_INT<14){
			super.closeOptionsMenu();
			return;
		}
		if (menu!=null && menu.isShowing())
			menu.dismiss();
	}
	
	
	@Override
    public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.main_menu, menu);
		return true;
    }
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item){
		int id = item.getItemId();
		Intent newIntent;
		switch(id){
			case R.id.menu_emotion_diy:
				newIntent = new Intent(this, EmotionActivity.class);
				ClickLogger.Log(getBaseContext(), ClickLogId.MENU_EMOTIONDIY);
				this.startActivity(newIntent);
				return true;
			case R.id.menu_emotion_management:
				newIntent = new Intent(this, EmotionManageActivity.class);
				ClickLogger.Log(getBaseContext(), ClickLogId.MENU_EMOTIONMANAGE);
				this.startActivity(newIntent);
				return true;
			case R.id.menu_about:
				newIntent = new Intent(this, AboutActivity.class);
				ClickLogger.Log(getBaseContext(), ClickLogId.MENU_ABOUT);
				this.startActivity(newIntent);
				return true;
			default:
				return super.onOptionsItemSelected(item);
			}
	   }
	
	@Override
	public boolean onKeyUp(int keyCode, KeyEvent event){
		if (keyCode == KeyEvent.KEYCODE_MENU){
			if (menu!=null && menu.isShowing())
				closeOptionsMenu();
			else
				openOptionsMenu();
			return true;
		}else if (keyCode == KeyEvent.KEYCODE_BACK ){
			if (menu != null && menu.isShowing()){
				closeOptionsMenu();
				return true;
			}else{
				if (clickable){
					Context context = FragmentTabs.getContext();
					if (context != null)
						ClickLogger.Log(context, ClickLogId.MAIN_ACTIVITY_EXIT);
					return super.onKeyUp(keyCode, event);
				}else
					return true;
			}
		}
		return super.onKeyUp(keyCode, event);
	}
	
	private void defaultSetting(){
		SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
		SharedPreferences.Editor editor = sp.edit();
		editor.putString("uid", "sober_default_test");
		editor.putBoolean("developer", false);
		editor.putString("goal_good", getString(R.string.default_goal_good));
		editor.putInt("goal_money", 50000);
		editor.putInt("drink_cost", 200);
		Calendar cal = Calendar.getInstance();
		editor.putInt("sYear",cal.get(Calendar.YEAR));
		editor.putInt("sMonth",cal.get(Calendar.MONTH));
		editor.putInt("sDate", cal.get(Calendar.DAY_OF_MONTH));
		editor.putString("connect_n0", "");
		editor.putString("connect_n1", "");
		editor.putString("connect_n2", "");
		editor.putString("connect_p0", "");
		editor.putString("connect_p1", "");
		editor.putString("connect_p2", "");
		editor.putString("recreation0", getString(R.string.default_recreation_1));
		editor.putString("recreation1", getString(R.string.default_recreation_2));
		editor.putString("recreation2", getString(R.string.default_recreation_3));
		editor.putString("recreation3", "");
		editor.putString("recreation4", "");
		editor.putInt("connect_s0", 1);
		editor.putInt("connect_s1", 2);
		editor.putInt("connect_s2", 3);
		editor.putBoolean("upload_audio", false);
		editor.putBoolean("show_saving", true);
		editor.commit();
	}
	
	
}
