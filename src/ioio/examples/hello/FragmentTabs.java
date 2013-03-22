package ioio.examples.hello;

import tabControl.TabManager;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.DisplayMetrics;
import android.widget.TabHost;
import android.widget.TabWidget;

public class FragmentTabs extends FragmentActivity {

	static private TabHost tabHost;
	private TabManager tabManager;
	@Override
	protected void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.tab_layout);
		tabHost = (TabHost) this.findViewById(android.R.id.tabhost);
		tabHost.setup();
		tabManager = new TabManager(this,tabHost,R.id.real_tabcontent);
		
		
		tabManager.addTab(
				tabHost.newTabSpec("Statistic").setIndicator("紀錄"),
				StatisticFragment.class,
				null
				);
		tabManager.addTab(
				tabHost.newTabSpec("Test").setIndicator("測試"),
				TestFragment.class,
				null
				);
		
		tabHost.setCurrentTab(1);
		
		DisplayMetrics dm = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(dm);
		int screenWidth = dm.widthPixels;
		
		TabWidget tabWidget = tabHost.getTabWidget();
		int count  = tabWidget.getChildCount();
		for (int i=0;i<count;++i)
			tabWidget.getChildTabViewAt(i).setMinimumWidth(screenWidth/count);
	}
	
	static public void changeTab(int pos){
		TabWidget tabWidget = tabHost.getTabWidget();
		int count  = tabWidget.getChildCount();
		if (pos>=0 && pos < count){
			tabHost.setCurrentTab(pos);
		}
	}
	
}
