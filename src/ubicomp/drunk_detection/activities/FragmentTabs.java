package ubicomp.drunk_detection.activities;

import ubicomp.drunk_detection.activities.R;
import ubicomp.drunk_detection.fragments.HistoryFragment;
import ubicomp.drunk_detection.fragments.StatisticFragment;
import ubicomp.drunk_detection.fragments.TestFragment;
import ubicomp.drunk_detection.ui.CustomTab;
import ubicomp.drunk_detection.ui.LoadingDialogControl;
import ubicomp.drunk_detection.ui.CustomMenu;
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
import android.view.Display;
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
import debug.clicklog.ClickLoggerLog;

public class FragmentTabs extends FragmentActivity {
	
	private static boolean isWideScreen;
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
	
	private FragmentTabs fragmentTabs; 
	
	private LoadingPageHandler loadingPageHandler;
	
	private Thread t;
	
	private CustomMenu menu;
	
	@SuppressWarnings("deprecation")
	@Override
	protected void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.tab_layout);
		context = this;
		fragmentTabs = this;
		Typefaces.initAll(this);
		
		loading_page = (ImageView) this.findViewById(R.id.loading_page);
		
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
		if (screen_px.x > screen_px.y){
			int tmp = screen_px.x;
			screen_px.x = screen_px.y;
			screen_px.y = tmp;
		}

		isWideScreen = ((float)screen_px.y/(float)screen_px.x)>1.67F;
		
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
		//enableTab(true);
		
		SharedPreferences sp= PreferenceManager.getDefaultSharedPreferences(this);
		String uid = sp.getString("uid", "");
		//boolean first_setting = sp.getBoolean("first_setting", false);
		if (uid.length() == 0){
			loading_page.setVisibility(View.INVISIBLE);
			Intent newIntent = new Intent(this, DeveloperActivity.class);
			this.startActivity(newIntent);
			return;
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
	
	public static int getScreenWidth(){
		return screen_px.x;
	}
	
	public static Point getSize(){
		if (screen_px == null)
			return null;
		Point size = new Point();
		size.x = screen_px.x;
		size.y = screen_px.y;
		if (tab_px!=null&&tab_px.y>0)
			size.y -= tab_px.y;
		return size;
	}
	
	public static Point getTabSize(){
		if (tab_px != null)
			return tab_px;
		return null;
	}
	
	public static boolean isWideScreen(){
		return isWideScreen;
	}
	
	public static void changeTab(int pos){
		TabWidget tabWidget = tabHost.getTabWidget();
		int count  = tabWidget.getChildCount();
		if (pos>=0 && pos < count){
			tabHost.setCurrentTab(pos);
		}
	}
	
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
    	MenuInflater inflater = getMenuInflater();
	    inflater.inflate(R.menu.main_menu, menu);
    	return true;
    }
	
    public boolean onOptionsItemSelected(MenuItem item){
		int id = item.getItemId();
		Intent newIntent;
		switch(id){
			case R.id.menu_emotion_diy:
				newIntent = new Intent(this, EmotionActivity.class);
				ClickLoggerLog.Log(getBaseContext(), ClickLogId.MENU_EMOTIONDIY);
				this.startActivity(newIntent);
				return true;
			case R.id.menu_emotion_management:
				newIntent = new Intent(this, EmotionManageActivity.class);
				ClickLoggerLog.Log(getBaseContext(), ClickLogId.MENU_EMOTIONMANAGE);
				this.startActivity(newIntent);
				return true;
			case R.id.menu_about:
				newIntent = new Intent(this, AboutActivity.class);
				ClickLoggerLog.Log(getBaseContext(), ClickLogId.MENU_ABOUT);
				this.startActivity(newIntent);
				return true;
			default:
				return super.onOptionsItemSelected(item);
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
			ClickLoggerLog.Log(getBaseContext(), tab);
			
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
		if (menu == null)
			menu = new CustomMenu(this,this.getLayoutInflater());
		if (!menu.isShowing() && clickable)
			menu.showAtLocation(this.getWindow().getDecorView(), Gravity.BOTTOM,0, 0);
	}
	
	@Override
	public void closeOptionsMenu(){
		if (menu!=null && menu.isShowing())
			menu.dismiss();
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
			closeOptionsMenu();
			return true;
		}
		return super.onKeyUp(keyCode, event);
	}
	
}
