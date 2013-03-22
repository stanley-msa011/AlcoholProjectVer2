package tabControl;

import java.util.HashMap;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.widget.TabHost;

public class TabManager implements TabHost .OnTabChangeListener{

	private final FragmentActivity activity;
	private final TabHost tabHost;
	private final int containerId;
	private final HashMap<String, TabInfo> tabs = new HashMap<String, TabInfo>();
	TabInfo lastTab;
	
	
	static final class TabInfo{
		private final String tag;
		private final Class<?> t_class;
		private final Bundle args;
		private Fragment fragment;
		
		TabInfo(String _tag, Class<?> _class, Bundle _args){
			tag = _tag;
			t_class = _class;
			args = _args;
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
			v.setMinimumWidth(0);
			v.setMinimumHeight(0);
			return v;
		}
		
	}
	
	public TabManager(FragmentActivity activity, TabHost tabHost, int containerId) {
        this.activity = activity;
        this.tabHost = tabHost;
        this.containerId = containerId;
        this.tabHost.setOnTabChangedListener(this);
    }
	
	public void addTab(TabHost.TabSpec tabSpec, Class<?> clss, Bundle args) {
        tabSpec.setContent(new DummyTabFactory(activity));
        String tag = tabSpec.getTag();

        TabInfo info = new TabInfo(tag, clss, args);

        info.fragment = activity.getSupportFragmentManager().findFragmentByTag(tag);
        if (info.fragment != null && !info.fragment.isDetached()) {
            FragmentTransaction ft = activity.getSupportFragmentManager().beginTransaction();
            ft.detach(info.fragment);
            ft.commit();
        }

        tabs.put(tag, info);
        tabHost.addTab(tabSpec);
    }
	
	@Override
	public void onTabChanged(String tabId) {
	     TabInfo newTab = tabs.get(tabId);

	        if (lastTab != newTab) {
	            FragmentTransaction ft = activity.getSupportFragmentManager().beginTransaction();
	            if (lastTab != null) {
	                if (lastTab.fragment != null) {
	                    ft.detach(lastTab.fragment);
	                }
	            }

	            if (newTab != null) {
	                newTab.fragment = Fragment.instantiate(activity, newTab.t_class.getName(), newTab.args);
	                ft.add(containerId, newTab.fragment, newTab.tag);
	                if (newTab.fragment == null) {
	                    ft.detach(lastTab.fragment);
	                } else {
	                    activity.getSupportFragmentManager().popBackStack();
	                    ft.replace(containerId, newTab.fragment);
	                    ft.attach(newTab.fragment);
	                }
	            }
	            
	            lastTab = newTab;
	            ft.commit();
	            activity.getSupportFragmentManager().executePendingTransactions();
	        }		
	}

}
