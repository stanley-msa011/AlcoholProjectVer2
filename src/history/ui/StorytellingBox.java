package history.ui;

import debug.clicklog.ClickLogId;
import debug.clicklog.ClickLoggerLog;

import ubicomp.drunk_detection.activities.FragmentTabs;
import ubicomp.drunk_detection.activities.R;
import ubicomp.drunk_detection.activities.StorytellingSharingActivity;
import ubicomp.drunk_detection.fragments.HistoryFragment;
import ubicomp.drunk_detection.ui.Typefaces;
import android.content.Context;
import android.content.Intent;
import android.graphics.Point;
import android.graphics.Typeface;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.TextView;

public class StorytellingBox {

	private HistoryFragment historyFragment;
	private Context context;
	private LayoutInflater inflater;
	private RelativeLayout boxLayout = null;
	private View background;
	private TextView help,cancel,ok;
	
	private RelativeLayout mainLayout;
	
	private Typeface wordTypefaceBold;
	
	private Point screen;
	
	private OKListener okListener = new OKListener();
	private EndListener endListener = new EndListener();
	
	public StorytellingBox(HistoryFragment historyFragment,RelativeLayout mainLayout){
		this.historyFragment = historyFragment;
		this.context = historyFragment.getActivity();
		this.inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		this.mainLayout = mainLayout;
		background = new FrameLayout(context);
		background.setBackgroundColor(0x99000000);
		screen = FragmentTabs.getSize();
		setting();
	}
	
	private void setting(){
		
		background.setVisibility(View.INVISIBLE);
		wordTypefaceBold = Typefaces.getWordTypefaceBold(context);
		boxLayout = (RelativeLayout) inflater.inflate(R.layout.storytelling_check_layout,null);
		boxLayout.setVisibility(View.INVISIBLE);
		help = (TextView) boxLayout.findViewById(R.id.storytelling_help);
		help.setTextSize(TypedValue.COMPLEX_UNIT_PX, screen.x * 21/480);
		help.setTypeface(wordTypefaceBold);
		mainLayout.addView(background);
		mainLayout.addView(boxLayout);
		
		RelativeLayout.LayoutParams bgParam = (LayoutParams) background.getLayoutParams();
		bgParam.width = bgParam.height = LayoutParams.MATCH_PARENT;
		
		RelativeLayout.LayoutParams param = (LayoutParams) boxLayout.getLayoutParams();
		param.width = screen.x * 349/480;
		param.height = screen.x * 189/480;
		param.addRule(RelativeLayout.CENTER_IN_PARENT, RelativeLayout.TRUE);
		
		cancel = (TextView) boxLayout.findViewById(R.id.storytelling_cancel_button);
		cancel.setTextSize(TypedValue.COMPLEX_UNIT_PX, screen.x * 21/480);
		cancel.setTypeface(wordTypefaceBold);
		ok = (TextView) boxLayout.findViewById(R.id.storytelling_ok_button);
		ok.setTextSize(TypedValue.COMPLEX_UNIT_PX, screen.x * 21/480);
		ok.setTypeface(wordTypefaceBold);
		
		ok.setOnClickListener(okListener);
		cancel.setOnClickListener(endListener);
		
		RelativeLayout.LayoutParams hParam = (LayoutParams) help.getLayoutParams();
		hParam.width = screen.x * 349/480;
		hParam.height = screen.x * 114/480;
		
		RelativeLayout.LayoutParams rParam = (LayoutParams) ok.getLayoutParams();
		rParam.width = screen.x * 154/480;
		rParam.height = screen.x * 60/480;
		rParam.topMargin = screen.x * 5/480;
		rParam.rightMargin = screen.x * 15/480; 
		RelativeLayout.LayoutParams pParam = (LayoutParams) cancel.getLayoutParams();
		pParam.width = screen.x * 154/480;
		pParam.height = screen.x * 60/480;
		pParam.topMargin = screen.x * 5/480;
		pParam.leftMargin = screen.x * 35/1480; 
		
	}
	
	
	public void clear(){
		if (background != null)
			mainLayout.removeView(background);
		
		if (boxLayout!=null)
			mainLayout.removeView(boxLayout);
		
		historyFragment.enablePage(true);
	}
	
	private class EndListener implements View.OnClickListener{
		@Override
		public void onClick(View v) {
			ClickLoggerLog.Log(context, ClickLogId.STORYTELLING_SHARE_CANCEL);
			background.setVisibility(View.INVISIBLE);
			boxLayout.setVisibility(View.INVISIBLE);
			historyFragment.enablePage(true);
		}
	}
	
	private class OKListener implements View.OnClickListener{
		@Override
		public void onClick(View v) {
			
			ClickLoggerLog.Log(context, ClickLogId.STORYTELLING_SHARE_OK);
			background.setVisibility(View.INVISIBLE);
			boxLayout.setVisibility(View.INVISIBLE);
			historyFragment.enablePage(true);
			Intent questionIntent = new Intent(context,StorytellingSharingActivity.class);
			context.startActivity(questionIntent);
			
		}
	}
	
	public void showMsgBox(){
		historyFragment.enablePage(false);
		background.setVisibility(View.VISIBLE);
		boxLayout.setVisibility(View.VISIBLE);
	}
	
}
