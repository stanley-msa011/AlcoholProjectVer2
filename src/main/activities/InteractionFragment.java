package main.activities;

import database.HistoryDB;
import interaction.UserLevelCollector;
import history.InteractionHistory;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

public class InteractionFragment extends Fragment {

	private View view;
	
	private Bitmap[] iconsBmp;
	private Bitmap iconBgBmp, iconCoverBmp;
	private Bitmap bgBmp;
	private ScrollView mainView;
	private RelativeLayout main_layout;
	private final static int ICON_TYPES = 5;
	private int horizontalGap, verticalGap;
	
	private LayoutInflater inflater;
	private ImageLoadingTask imageLoadingTask;
	private NetworkLoadingTask networkLoadingTask;

	InteractionHistory[] historys_all;
	
	private HistoryDB db;
	
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    	view = inflater.inflate(R.layout.interaction_fragment, container,false);
    	this.inflater = inflater;
    	return view;
    }
    
    @Override
    public void onResume(){
		super.onResume();
		this.db = new HistoryDB(this.getActivity());
		Point screen = FragmentTabs.getSize();
		
		mainView = (ScrollView) view.findViewById(R.id.interaction_scroll_view);
		main_layout = (RelativeLayout) view.findViewById(R.id.interaction_layout);
		
		RelativeLayout.LayoutParams mainViewParam = (RelativeLayout.LayoutParams)mainView.getLayoutParams();
		mainViewParam.height = screen.y;
		
		horizontalGap = (int)(screen.x * 5.0/720.0);
		verticalGap = (int)(screen.x * 14.0/720.0);
		
		imageLoadingTask = new ImageLoadingTask();
		imageLoadingTask.execute();
	}
    
    public void onPause(){
    	clear();
    	super.onPause();
    }
    
    private void clear(){
    	Log.d("CLEAR","interaction onPause");
    	this.db = null;
    	main_layout.removeAllViews();
    	if (imageLoadingTask != null){
    		Log.d("RECYCLE","CANCEL1");
    		imageLoadingTask.cancel(true);
    		imageLoadingTask = null;
    	}
    	if (networkLoadingTask != null){
    		Log.d("RECYCLE","CANCEL2");
    		networkLoadingTask.cancel(true);
    		networkLoadingTask = null;
    	}
    	if (iconsBmp!=null)
    		for(int i=0;i<iconsBmp.length;++i){
    			if (iconsBmp[i]!=null && !iconsBmp[i].isRecycled()){
    				Log.d("RECYCLE","RECYCLE1");
    				iconsBmp[i].recycle();
    				iconsBmp[i] = null;
    			}
    		}
    	if (iconBgBmp!=null && !iconBgBmp.isRecycled()){
    		Log.d("RECYCLE","RECYCLE2");
    		iconBgBmp.recycle();
    		iconBgBmp = null;
    	}
    	if (iconCoverBmp!=null && !iconCoverBmp.isRecycled()){
    		Log.d("RECYCLE","RECYCLE3");
    		iconCoverBmp.recycle();
    		iconCoverBmp = null;
    	}
    	if (bgBmp!=null && !bgBmp.isRecycled()){
    		Log.d("RECYCLE","RECYCLE4");
    		mainView.setBackgroundDrawable(null);
    		bgBmp.recycle();
    		bgBmp = null;
    	}
    	System.gc();
    }
    
    private void setImages(InteractionHistory[] historys){
    	if (historys == null)
    		return;
    	
    	Point screen = FragmentTabs.getSize();
    	Typeface face=Typeface.createFromAsset(this.getActivity().getAssets(), "fonts/helvetica-lt-std-bold.otf");
    	int bgWidth = (int)(screen.x * 172.0/720.0);
    	int bgHeight =  (int)(screen.x * 192.0/720.0);
    	
    	int iconWidth =  (int)(bgWidth * 148.0/211.0);
    	int iconHeight = (int)(bgHeight * 126.0/236.0);
    	int iconLeftMargin = (int)(bgWidth * 23.0/211.0);
    	int iconTopMargin = (int)(bgHeight * 66.0/236.0);
    	
    	int iconCoverWidth =  (int)(bgWidth * 159.0/211.0);
    	int iconCoverHeight = (int)(bgHeight * 138.0/236.0);
    	int iconCoverLeftMargin = (int)(bgWidth * 17.0/211.0);
    	int iconCoverTopMargin = (int)(bgHeight * 60.0/236.0);
    	
    	int textSize =  (int)(bgHeight * 36.0/236.0);
    	int textLeftMargin = (int)(bgWidth * 23.0/211.0);
    	int textTopMargin = (int)(bgHeight * 18.0/236.0);
    	
    	int leftMargin = (int)(screen.x*7.0/720.0);
    	int topMargin = (int)(screen.x*72.0/720.0);
    	
    	for (int i=0;i<historys.length;++i){
    		View iconView = inflater.inflate(R.layout.interaction_icon, null);
    		ImageView icon = (ImageView) iconView.findViewById(R.id.interaction_icon);
    		ImageView iconBg = (ImageView) iconView.findViewById(R.id.interaction_icon_bg);
    		ImageView iconCover = (ImageView) iconView.findViewById(R.id.interaction_icon_cover);
    		TextView iconText = (TextView) iconView.findViewById(R.id.interaction_icon_text);
    		
    		RelativeLayout.LayoutParams iconBgParam = (RelativeLayout.LayoutParams)iconBg.getLayoutParams();
    		iconBgParam.width = bgWidth;
    		iconBgParam.height = bgHeight;
    		iconBgParam.leftMargin = 0;
    		iconBgParam.topMargin = 0;
    		
    		RelativeLayout.LayoutParams iconParam = (RelativeLayout.LayoutParams)icon.getLayoutParams();
    		iconParam.width = iconWidth;
    		iconParam.height = iconHeight;
    		iconParam.leftMargin = iconLeftMargin;
    		iconParam.topMargin = iconTopMargin;
    		
    		RelativeLayout.LayoutParams iconCoverParam = (RelativeLayout.LayoutParams)iconCover.getLayoutParams();
    		iconCoverParam.width = iconCoverWidth;
    		iconCoverParam.height =  iconCoverHeight;
    		iconCoverParam.leftMargin = iconCoverLeftMargin;
    		iconCoverParam.topMargin =  iconCoverTopMargin;
    		
    		iconText.setText(historys[i].uid);
    		iconText.setTextColor(0xFFF97306);
        	iconText.setTypeface(face);
        	iconText.setTextSize(TypedValue.COMPLEX_UNIT_PX,textSize);
        	
        	RelativeLayout.LayoutParams iconTextParam = (RelativeLayout.LayoutParams)iconText.getLayoutParams();
        	iconTextParam.leftMargin = textLeftMargin;
        	iconTextParam.topMargin = textTopMargin;
    		
    		int page = historys[i].level / 4;
    		icon.setImageBitmap(iconsBmp[page]);
    		iconBg.setImageBitmap(iconBgBmp);
    		iconCover.setImageBitmap(iconCoverBmp);
    		main_layout.addView(iconView);
    		
    		int h_pos = i%4;
    		int v_pos = i/4;
    		
    		RelativeLayout.LayoutParams iconViewParam = (RelativeLayout.LayoutParams)iconView.getLayoutParams();
    		iconViewParam.width = bgWidth;
    		iconViewParam.height = bgHeight;
    		iconViewParam.leftMargin = leftMargin + (bgWidth+horizontalGap)*h_pos ;
    		iconViewParam.topMargin = topMargin + (bgHeight+verticalGap)*v_pos ;
    		
    	}
    	
    }
    
    
    private class ImageLoadingTask extends AsyncTask<Void, Void, Void>{

		@Override
		protected Void doInBackground(Void... params) {
			iconsBmp = new Bitmap[ICON_TYPES];
			//Bitmap tmp = 
			iconsBmp[0] = BitmapFactory.decodeResource(view.getResources(), R.drawable.drunk_social_icon1);
			iconsBmp[1] = BitmapFactory.decodeResource(view.getResources(), R.drawable.drunk_social_icon2);
			iconsBmp[2] = BitmapFactory.decodeResource(view.getResources(), R.drawable.drunk_social_icon3);
			iconsBmp[3] = BitmapFactory.decodeResource(view.getResources(), R.drawable.drunk_social_icon4);
			iconsBmp[4] = BitmapFactory.decodeResource(view.getResources(), R.drawable.drunk_social_icon5);
			
			iconBgBmp = BitmapFactory.decodeResource(view.getResources(), R.drawable.drunk_social_iconbg);
			
			iconCoverBmp = BitmapFactory.decodeResource(view.getResources(), R.drawable.drunk_social_iconcover);
			
			bgBmp = BitmapFactory.decodeResource(view.getResources(), R.drawable.drunk_social_bg);
			
			return null;
		}
		@SuppressWarnings("deprecation")
		@Override
		 protected void onPostExecute(Void result) {
			
			BitmapDrawable bgDrawable = new BitmapDrawable(bgBmp);
			mainView.setBackgroundDrawable(bgDrawable);

			InteractionHistory[] historys = db.getAllUsersHistory();
			 historys_all = historys;
			 setImages(historys_all);
			 
			networkLoadingTask = new NetworkLoadingTask();
			networkLoadingTask.execute();
		}
		
		protected void onCancelled(){
			clear();
		}
    }
    
    
    private class NetworkLoadingTask extends AsyncTask<Void, Void, Void>{

    	private InteractionHistory[] historys;
    	private UserLevelCollector levelCollector;
    	
		@Override
		protected Void doInBackground(Void... params) {
			Log.d("NetworkLoadingTask","StartLoading");
			levelCollector = new UserLevelCollector(view.getContext());
			historys = levelCollector.update();
			return null;
		}
		@Override
		 protected void onPostExecute(Void result) {
			/*insert to db*/
			if (historys == null)
				return;
			for (int i=0;i<historys.length;++i)
				db.insertInteractionHistory(historys[i]);
			historys_all = db.getAllUsersHistory();
			setImages(historys_all);
		}
		protected void onCancelled(){
			clear();
		}
    }
    
}
