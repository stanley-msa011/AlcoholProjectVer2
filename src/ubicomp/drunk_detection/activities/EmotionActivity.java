package ubicomp.drunk_detection.activities;

import statistic.ui.questionnaire.content.ConnectSocialInfo;
import ubicomp.drunk_detection.activities.R;
import ubicomp.drunk_detection.ui.CustomToast;
import ubicomp.drunk_detection.ui.Typefaces;
import data.database.QuestionDB;
import debug.clicklog.ClickLogId;
import debug.clicklog.ClickLoggerLog;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.util.TypedValue;
import android.view.Display;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.RelativeLayout.LayoutParams;

public class EmotionActivity extends Activity {

	private LayoutInflater inflater;
	
	private int textSize;
	private int iconMargin;
	private Typeface wordTypefaceBold;
	
	private RelativeLayout bgLayout;
	private LinearLayout mainLayout;
	private RelativeLayout callLayout;
	private RelativeLayout playLayout;
	private View shadowBg;
	private TextView callOK,callCancel,callHelp;
	private TextView playHelp;
	private ImageView playCancel,playPause;
	private Drawable playPauseDrawable, playPlayDrawable;
	
	private Activity activity;
	
	private MediaPlayer mediaPlayer;
	
	private ItemOnTouchListener onTouchListener;
	
	private static final int[] DRAWABLE_ID = {
		R.drawable.questionnaire_item_sol_1,
		R.drawable.questionnaire_item_sol_2,
		R.drawable.questionnaire_item_sol_3,
		R.drawable.questionnaire_item_sol_4,
		R.drawable.questionnaire_item_sol_5
	};
	
	private static final int TOTAL_BLOCK = 15;	
	private static String[] texts;
	
	private OnClickListener[] clickListeners = {
			new NormalSelectionOnClickListener(0),
			new NormalSelectionOnClickListener(1),
			new NormalSelectionOnClickListener(2),
			new HelpOnClickListener(3),
			new HelpOnClickListener(4)
	};
	
	private static final int TYPE_SOCIAL = 3, TYPE_FAMILY = 4;
	
	QuestionDB db;
	
	private Point screen;
	
	private int state = 0;
	
	private Toast endToast;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

	    getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
	    requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_emotion);
		
		Display display = getWindowManager().getDefaultDisplay();
		if (Build.VERSION.SDK_INT<13){
			@SuppressWarnings("deprecation")
			int w = display.getWidth();
			@SuppressWarnings("deprecation")
			int h = display.getHeight();
			screen = new Point(w,h);
		}
		else{
			screen = new Point();
			display.getSize(screen);
		}
		texts = getResources().getStringArray(R.array.emotionDIY_solution);
		
		this.activity = this;
		bgLayout = (RelativeLayout) this.findViewById(R.id.emotion_all_layout);
		mainLayout = (LinearLayout) this.findViewById(R.id.emotion_main_layout);
		inflater = LayoutInflater.from(this);
		shadowBg = new View(this);
		shadowBg.setBackgroundColor(0x99000000);
		callLayout = (RelativeLayout) inflater.inflate(R.layout.call_check_layout, null);
		playLayout = (RelativeLayout) inflater.inflate(R.layout.play_guide_layout, null);
		wordTypefaceBold = Typefaces.getWordTypefaceBold(this);
		setCallCheckBox();
		setPlayGuideBox();
		db = new QuestionDB(activity);
	}

	@Override
	protected void onResume(){
		super.onResume();
		enableBack = true;
		setQuestionStart();
	}
	
	
	@Override
	protected void onPause(){
		if (shadowBg!=null)
			bgLayout.removeView(shadowBg);
		if (callLayout!=null)
			bgLayout.removeView(callLayout);
		if (playLayout!=null)
			bgLayout.removeView(playLayout);
		if (mediaPlayer != null)
			mediaPlayer.release();
		int item_count = mainLayout.getChildCount();
		for (int i=0;i<item_count;++i)
			mainLayout.getChildAt(i).setEnabled(true);
		super.onPause();
	}
	
	private RelativeLayout.LayoutParams shadowParam;
	private RelativeLayout.LayoutParams boxParam;
	
	private void setCallCheckBox(){
		
		callOK = (TextView) callLayout.findViewById(R.id.call_ok_button);
		callCancel = (TextView) callLayout.findViewById(R.id.call_cancel_button);
		callHelp = (TextView) callLayout.findViewById(R.id.call_help);
		
		
		callHelp.setTextSize(TypedValue.COMPLEX_UNIT_PX, screen.x * 21/480);
		callHelp.setTypeface(wordTypefaceBold);
		RelativeLayout.LayoutParams hParam = (LayoutParams) callHelp.getLayoutParams();
		hParam.width = screen.x * 349/480;
		hParam.height = screen.x * 114/480;
		
		callOK.setTextSize(TypedValue.COMPLEX_UNIT_PX, screen.x * 21/480);
		callOK.setTypeface(wordTypefaceBold);
		callCancel.setTextSize(TypedValue.COMPLEX_UNIT_PX, screen.x * 21/480);
		callCancel.setTypeface(wordTypefaceBold);
		
		RelativeLayout.LayoutParams rParam = (LayoutParams) callOK.getLayoutParams();
		rParam.width = screen.x * 154/480;
		rParam.height = screen.x * 60/480;
		rParam.topMargin = screen.x * 5/480;
		rParam.rightMargin = screen.x * 15/480; 
		RelativeLayout.LayoutParams pParam = (LayoutParams) callCancel.getLayoutParams();
		pParam.width = screen.x * 154/480;
		pParam.height = screen.x * 60/480;
		pParam.topMargin = screen.x * 5/480;
		pParam.leftMargin = screen.x * 35/1480; 
	}
	
private void setPlayGuideBox(){
		
		playPause = (ImageView) playLayout.findViewById(R.id.play_pause_button);
		playCancel = (ImageView) playLayout.findViewById(R.id.play_cancel_button);
		playHelp = (TextView) playLayout.findViewById(R.id.play_help);
		
		playPauseDrawable = getResources().getDrawable(R.drawable.record_stop);
		playPlayDrawable = getResources().getDrawable(R.drawable.record_play);
		
		playHelp.setTextSize(TypedValue.COMPLEX_UNIT_PX, screen.x * 21/480);
		playHelp.setTypeface(wordTypefaceBold);
		RelativeLayout.LayoutParams hParam = (LayoutParams) playHelp.getLayoutParams();
		hParam.width = screen.x * 349/480;
		hParam.height = screen.x * 114/480;
		
		RelativeLayout.LayoutParams rParam = (LayoutParams) playPause.getLayoutParams();
		rParam.width = screen.x * 154/480;
		rParam.height = screen.x * 60/480;
		rParam.topMargin = screen.x * 5/480;
		rParam.rightMargin = screen.x * 15/480; 
		RelativeLayout.LayoutParams pParam = (LayoutParams) playCancel.getLayoutParams();
		pParam.width = screen.x * 154/480;
		pParam.height = screen.x * 60/480;
		pParam.topMargin = screen.x * 5/480;
		pParam.leftMargin = screen.x * 35/1480; 
	}
	
	private void setQuestionStart(){
		state = 0;
		
		mainLayout.removeAllViews();
		onTouchListener = new ItemOnTouchListener();
		
		textSize = screen.x * 24/480;
		iconMargin = screen.x * 33/480;
		View title = createTitleView();
		mainLayout.addView(title);
		LinearLayout.LayoutParams titleparam =(LinearLayout.LayoutParams) title.getLayoutParams();
		titleparam.height = screen.x*230/1080;
		
		View tv = createTextView(R.string.emotionDIY_help);
		mainLayout.addView(tv);
		
		for (int i=0;i<texts.length;++i){
			View v = createIconView(texts[i],DRAWABLE_ID[i],clickListeners[i]);
			mainLayout.addView(v);
		}
		
		int rest_block = TOTAL_BLOCK - mainLayout.getChildCount();
		for (int i=0;i<rest_block;++i){
			View v = createBlankView();
			mainLayout.addView(v);
		}
	}
	
	private void setQuestionCall(int type){
		state = 1;
		
		mainLayout.removeAllViews();
		
		View title = createTitleView();
		mainLayout.addView(title);
		LinearLayout.LayoutParams titleparam =(LinearLayout.LayoutParams) title.getLayoutParams();
		titleparam.height = screen.x*230/1080;
		
		View tv = createTextView(R.string.call_to);
		mainLayout.addView(tv);

		String[] names = new String[3];
		String[] calls = new String[3];
		
		if (type == TYPE_FAMILY){
			SharedPreferences sp= PreferenceManager.getDefaultSharedPreferences(this);
			names[0] = sp.getString("connect_n0", "");
			names[1] = sp.getString("connect_n1", "");
			names[2] = sp.getString("connect_n2", "");
			calls[0] = sp.getString("connect_p0", "");
			calls[1] = sp.getString("connect_p1", "");
			calls[2] = sp.getString("connect_p2", "");
		}else if (type == TYPE_SOCIAL){
			SharedPreferences sp= PreferenceManager.getDefaultSharedPreferences(this);
			int connectS0,connectS1,connectS2;
			connectS0 = sp.getInt("connect_s0", 0);
			connectS1 = sp.getInt("connect_s1", 1);
			connectS2 = sp.getInt("connect_s2", 2);
			names[0] = ConnectSocialInfo.NAME[connectS0];
			names[1] = ConnectSocialInfo.NAME[connectS1];
			names[2] = ConnectSocialInfo.NAME[connectS2];
			calls[0] = ConnectSocialInfo.PHONE[connectS0];
			calls[1] = ConnectSocialInfo.PHONE[connectS1];
			calls[2] = ConnectSocialInfo.PHONE[connectS2];
		}
		
		int counter = 0;
		for (int i=0;i<3;++i){
			OnClickListener listener = new CallCheckOnClickListener(type,names[i],calls[i]);
			String text = names[i];
			if (names[i].length()>0){
				View vv = createIconView(text,R.drawable.questionnaire_item_call,listener);
				mainLayout.addView(vv);
				++counter;
			}
		}
		if (counter == 0){
			mainLayout.removeAllViews();
			mainLayout.addView(title);
			titleparam =(LinearLayout.LayoutParams) title.getLayoutParams();
			titleparam.height = screen.x*230/1080;
			
			View tv2 = createTextView(R.string.emotion_connect_null);
			mainLayout.addView(tv2);
		}
		
		int rest_block = TOTAL_BLOCK - mainLayout.getChildCount();
		for (int i=0;i<rest_block;++i){
			View vv = createBlankView();
			mainLayout.addView(vv);
		}
	}
	
	private void setQuestionEnd(int selection){
		state = 1;
		
		mainLayout.removeAllViews();
		View title = createTitleView();
		mainLayout.addView(title);
		LinearLayout.LayoutParams titleparam =(LinearLayout.LayoutParams) title.getLayoutParams();
		titleparam.height = screen.x*230/1080;
		
		View tv;
		if (selection == 0)
			tv = createTextView(R.string.emotionDIY_help_case1);
		else if (selection == 1)
			tv = createTextView(R.string.emotionDIY_help_case2);
		else
			tv = createTextView(R.string.emotionDIY_help_case3);
		mainLayout.addView(tv);
		View pv = createIconView(R.string.emotionDIY_help_guide,R.drawable.questionnaire_item_play,new PlayGuideOnClickListener(selection));
		mainLayout.addView(pv);
		View vv = createIconView(R.string.try_to_do,R.drawable.questionnaire_item_ok,new EndOnClickListener(selection));
		mainLayout.addView(vv);
		
		int rest_block = TOTAL_BLOCK - mainLayout.getChildCount();
		for (int i=0;i<rest_block;++i){
			View v = createBlankView();
			mainLayout.addView(v);
		}
	}
	
private View createTextView(int textStr){
		
		LinearLayout layout = (LinearLayout) inflater.inflate(R.layout.question_select_item, null);
		TextView text = (TextView) layout.findViewById(R.id.question_description);
		text.setTextSize(TypedValue.COMPLEX_UNIT_PX,textSize );
		text.setTypeface(wordTypefaceBold);
		text.setTextColor(0xFF777777);
		text.setText(textStr);
		
		LinearLayout.LayoutParams tParam = (LinearLayout.LayoutParams)text.getLayoutParams();
		tParam.leftMargin = textSize;
		
		layout.setBackgroundResource(R.drawable.questionnaire_bar_question);
		
		return layout;
	}
	
	
	private View createIconView(String textStr, int DrawableId ,OnClickListener listener){
		
		LinearLayout layout = (LinearLayout) inflater.inflate(R.layout.question_select_item, null);
		TextView text = (TextView) layout.findViewById(R.id.question_description);
		text.setTextSize(TypedValue.COMPLEX_UNIT_PX,textSize );
		text.setTypeface(wordTypefaceBold);
		text.setTextColor(0xFF5c5c5c);
		text.setText(textStr);
		
		LinearLayout.LayoutParams tParam = (LinearLayout.LayoutParams)text.getLayoutParams();
		tParam.leftMargin = textSize;
		
		ImageView icon = (ImageView) layout.findViewById(R.id.question_icon);
		icon.setImageDrawable(getResources().getDrawable(DrawableId));
		LinearLayout.LayoutParams iParam =(LinearLayout.LayoutParams) icon.getLayoutParams();
		iParam.rightMargin = iconMargin;
		
		layout.setOnClickListener(listener);
		layout.setOnTouchListener(onTouchListener);
		layout.setBackgroundResource(R.drawable.questionnaire_bar_normal);
		
		return layout;
	}
	
	private View createIconView(int textStr, int DrawableId ,OnClickListener listener){
		
		LinearLayout layout = (LinearLayout) inflater.inflate(R.layout.question_select_item, null);
		TextView text = (TextView) layout.findViewById(R.id.question_description);
		text.setTextSize(TypedValue.COMPLEX_UNIT_PX,textSize );
		text.setTypeface(wordTypefaceBold);
		text.setTextColor(0xFF5c5c5c);
		text.setText(textStr);
		
		LinearLayout.LayoutParams tParam = (LinearLayout.LayoutParams)text.getLayoutParams();
		tParam.leftMargin = textSize;
		
		ImageView icon = (ImageView) layout.findViewById(R.id.question_icon);
		icon.setImageDrawable(getResources().getDrawable(DrawableId));
		LinearLayout.LayoutParams iParam =(LinearLayout.LayoutParams) icon.getLayoutParams();
		iParam.rightMargin = iconMargin;
		
		layout.setOnClickListener(listener);
		layout.setOnTouchListener(onTouchListener);
		layout.setBackgroundResource(R.drawable.questionnaire_bar_normal);
		
		return layout;
	}
	
	private View createTitleView(){
		
		RelativeLayout layout = (RelativeLayout) inflater.inflate(R.layout.titlebar, null);
		TextView text = (TextView) layout.findViewById(R.id.titlebar_text);
		text.setTextSize(TypedValue.COMPLEX_UNIT_PX,textSize );
		text.setTypeface(wordTypefaceBold);
		text.setText(R.string.emotionDIY_title);
		
		RelativeLayout.LayoutParams tParam = (RelativeLayout.LayoutParams)text.getLayoutParams();
		tParam.leftMargin = textSize;
		
		ImageView icon = (ImageView) layout.findViewById(R.id.titlebar_icon);
		RelativeLayout.LayoutParams iParam =(RelativeLayout.LayoutParams) icon.getLayoutParams();
		iParam.leftMargin = textSize;
		
		return layout;
	}
	
	private View createBlankView(){
		
		LinearLayout layout = (LinearLayout) inflater.inflate(R.layout.question_select_item, null);
		layout.setBackgroundResource(R.drawable.questionnaire_bar_normal);
		return layout;
	}
	
	private class EndOnClickListener implements View.OnClickListener{
		int in;
		
		EndOnClickListener(int in){
			this.in = in+1;
		}
		
		@Override
		public void onClick(View v) {
			
			ClickLoggerLog.Log(getBaseContext(), ClickLogId.EMOTIONDIY_SELECTION);
			boolean addAcc = db.insertEmotion(in,null);
			if (addAcc)
				CustomToast.generateToast(activity, R.string.emotionDIY_end_toast, 1, screen);
			else
				CustomToast.generateToast(activity, R.string.emotionDIY_end_toast, 0, screen);
			activity.finish();
		}
	}
	
	private class CallCheckOnClickListener  implements View.OnClickListener{

		private int in;
		private String name;
		private String call;
		
		CallCheckOnClickListener(int in,String name,String call){
			this.in = in;
			this.name = name;
			this.call = call;
		}
		
		@Override
		public void onClick(View v) {
			ClickLoggerLog.Log(getBaseContext(), ClickLogId.EMOTIONDIY_OPEN_CALL_BOX);
			int item_count = mainLayout.getChildCount();
			for (int i=0;i<item_count;++i)
				mainLayout.getChildAt(i).setEnabled(false);
			enableBack = false;
			
			bgLayout.addView(shadowBg);
			bgLayout.addView(callLayout);
			shadowParam = (LayoutParams) shadowBg.getLayoutParams();
			shadowParam.width = shadowParam.height = LayoutParams.MATCH_PARENT;
			
			boxParam = (LayoutParams) callLayout.getLayoutParams();
			boxParam.width = screen.x * 349/480;
			boxParam.height = screen.x * 189/480;
			boxParam.addRule(RelativeLayout.CENTER_IN_PARENT, RelativeLayout.TRUE);
			
			String call_check = getResources().getString(R.string.call_check_help);
			String question_sign = getResources().getString(R.string.question_sign);
			callHelp.setText(call_check+" "+name+" "+question_sign);
			callOK.setOnClickListener(new CallOnClickListener(in,name,call));
			callCancel.setOnClickListener(new CallCancelOnClickListener());
		}
		
	}
	
	private class PlayGuideOnClickListener  implements View.OnClickListener{

		private int type;
		
		public PlayGuideOnClickListener(int type){
			this.type =type;
		}
		
		@Override
		public void onClick(View v) {
			ClickLoggerLog.Log(getBaseContext(), ClickLogId.EMOTIONDIY_OPEN_PLAY_BOX);
			int item_count = mainLayout.getChildCount();
			for (int i=0;i<item_count;++i)
				mainLayout.getChildAt(i).setEnabled(false);
			enableBack = false;
			
			bgLayout.addView(shadowBg);
			bgLayout.addView(playLayout);
			shadowParam = (LayoutParams) shadowBg.getLayoutParams();
			shadowParam.width = shadowParam.height = LayoutParams.MATCH_PARENT;
			
			boxParam = (LayoutParams) playLayout.getLayoutParams();
			boxParam.width = screen.x * 349/480;
			boxParam.height = screen.x * 189/480;
			boxParam.addRule(RelativeLayout.CENTER_IN_PARENT, RelativeLayout.TRUE);
			
			playHelp.setText(R.string.emotion_box_playing);
			if (mediaPlayer!=null)
				mediaPlayer.release();
			switch (type){
			case 0:
				mediaPlayer = MediaPlayer.create(activity, R.raw.emotion1);
				break;
			case 1:
				mediaPlayer = MediaPlayer.create(activity, R.raw.emotion2);
				break;
			case 2:
				mediaPlayer = MediaPlayer.create(activity, R.raw.emotion3);
				break;
			default:
				mediaPlayer = MediaPlayer.create(activity, R.raw.emotion1);
			}
			mediaPlayer.start();
			mediaPlayer.setOnCompletionListener(new PlayOnCompletionListener());
			playPause.setOnClickListener(new PlayOnClickListener());
			playPause.setImageDrawable(playPauseDrawable);
			playCancel.setOnClickListener(new PlayCancelOnClickListener());
		}
		
	}
	
	
	private class PlayOnCompletionListener implements MediaPlayer.OnCompletionListener{

		@Override
		public void onCompletion(MediaPlayer mp) {
			playPause.setImageDrawable(playPlayDrawable);
			playHelp.setText(R.string.emotion_box_replay);
		}
		
	}
	
	private class PlayOnClickListener implements View.OnClickListener{
		@Override
		public void onClick(View arg0) {
			if (mediaPlayer == null)
				return;
			if (mediaPlayer.isPlaying()){
				ClickLoggerLog.Log(getBaseContext(), ClickLogId.EMOTIONDIY_PAUSE_AUDIO);
				mediaPlayer.pause();
				playPause.setImageDrawable(playPlayDrawable);
				playHelp.setText(R.string.emotion_box_pause);
			}
			else{
				ClickLoggerLog.Log(getBaseContext(), ClickLogId.EMOTIONDIY_PLAY_AUDIO);
				mediaPlayer.start();
				playPause.setImageDrawable(playPauseDrawable);
				playHelp.setText(R.string.emotion_box_playing);
			}
		}
	}
	
	private class PlayCancelOnClickListener implements View.OnClickListener{
		@Override
		public void onClick(View v) {
			ClickLoggerLog.Log(getBaseContext(), ClickLogId.EMOTIONDIY_CANCEL_AUDIO);
			if (mediaPlayer != null){
				mediaPlayer.release();
				mediaPlayer = null;
			}
			bgLayout.removeView(shadowBg);
			bgLayout.removeView(playLayout);
			int item_count = mainLayout.getChildCount();
			for (int i=0;i<item_count;++i)
				mainLayout.getChildAt(i).setEnabled(true);
			enableBack = true;
		}
		
	}
	
	
	private class CallCancelOnClickListener implements View.OnClickListener{
		@Override
		public void onClick(View v) {
			ClickLoggerLog.Log(getBaseContext(), ClickLogId.EMOTIONDIY_CANCEL_CALL);
			bgLayout.removeView(shadowBg);
			bgLayout.removeView(callLayout);
			int item_count = mainLayout.getChildCount();
			for (int i=0;i<item_count;++i)
				mainLayout.getChildAt(i).setEnabled(true);
			enableBack = true;
		}
		
	}
	
	private class CallOnClickListener implements View.OnClickListener{
		private int in;
		private String name;
		private String call;
		
		CallOnClickListener(int in,String name,String call){
			this.in = in+1;
			this.name = name;
			this.call = call;
		}
		
		@Override
		public void onClick(View v) {
			ClickLoggerLog.Log(getBaseContext(), ClickLogId.EMOTIONDIY_CALL);
			db.insertEmotion(in,name);
			Intent intentDial = new Intent("android.intent.action.CALL",Uri.parse("tel:"+call));
			activity.startActivity(intentDial);
			activity.finish();
		}
	}
	
	private class NormalSelectionOnClickListener implements View.OnClickListener{
		int in;
		NormalSelectionOnClickListener( int in){
			this.in = in;
		}
		
		@Override
		public void onClick(View v) {
			ClickLoggerLog.Log(getBaseContext(), ClickLogId.EMOTIONDIY_SELECTION);
			setQuestionEnd(in);
		}
	}
	
	private class HelpOnClickListener implements View.OnClickListener{
		int type;
		HelpOnClickListener( int type){
			this.type = type;
		}
		
		@Override
		public void onClick(View v) {
			ClickLoggerLog.Log(getBaseContext(), ClickLogId.EMOTIONDIY_SELECTION);
			setQuestionCall(type);
		}
	}
	
	
	private class ItemOnTouchListener implements View.OnTouchListener{

		private Rect rect;
		
		@Override
		public boolean onTouch(View v, MotionEvent event) {
			int e = event.getAction();
			switch(e){
				case MotionEvent.ACTION_MOVE:
					if(!rect.contains(v.getLeft() + (int) event.getX(), v.getTop() + (int) event.getY()))
						v.setBackgroundResource(R.drawable.questionnaire_bar_normal);
					break;
				case MotionEvent.ACTION_UP:
					v.setBackgroundResource(R.drawable.questionnaire_bar_normal);
					break;
				case MotionEvent.ACTION_DOWN:
					v.setBackgroundResource(R.drawable.questionnaire_bar_selected);
					rect = new Rect(v.getLeft(), v.getTop(), v.getRight(), v.getBottom());
					break;
			}
			return false;
		}
	}
	
	
	private boolean enableBack = true;
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event){
		if (keyCode == KeyEvent.KEYCODE_BACK){
			if (!enableBack)
				return false;
			ClickLoggerLog.Log(getBaseContext(), ClickLogId.EMOTIONDIY_RETURN_BUTTON);
			if (state == 0){
				if (endToast!=null)
					endToast.cancel();
				endToast = Toast.makeText(this, R.string.emotionDIY_toast, Toast.LENGTH_SHORT);
				endToast.show();
				--state;
			}else if (state == -1)
				return super.onKeyDown(keyCode, event);
			else{
				--state;
				if (state == 0)
					setQuestionStart();
			}
		}
		
		return false;
	}
}
