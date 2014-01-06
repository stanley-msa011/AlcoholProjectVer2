package ubicomp.drunk_detection.activities;

import statistic.ui.questionnaire.content.ConnectSocialInfo;
import ubicomp.drunk_detection.activities.R;
import ubicomp.drunk_detection.check.LockCheck;
import ubicomp.drunk_detection.ui.CustomToast;
import ubicomp.drunk_detection.ui.CustomToastSmall;
import ubicomp.drunk_detection.ui.ScreenSize;
import ubicomp.drunk_detection.ui.TextSize;
import ubicomp.drunk_detection.ui.Typefaces;
import data.database.QuestionDB;
import debug.clicklog.ClickLogId;
import debug.clicklog.ClickLogger;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.util.TypedValue;
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
import android.widget.RelativeLayout.LayoutParams;

public class EmotionActivity extends Activity {

	private LayoutInflater inflater;
	
	private int titleSize;
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
	
	private Thread media_thread;
	private String playString;
	private MediaUpdateHandler media_handler;
	
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
			new RecreationOnClickListener(),
			new HelpOnClickListener(3),
			new HelpOnClickListener(4)
	};
	
	private static final int TYPE_SOCIAL = 3, TYPE_FAMILY = 4;
	
	QuestionDB db;
	
	private Point screen;
	
	private int state = 0;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

	    getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
	    requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_emotion);
		
		screen = ScreenSize.getScreenSize(this);
		texts = getResources().getStringArray(R.array.emotionDIY_solution);
		
		this.activity = this;
		bgLayout = (RelativeLayout) this.findViewById(R.id.emotion_all_layout);
		mainLayout = (LinearLayout) this.findViewById(R.id.emotion_main_layout);
		inflater = LayoutInflater.from(this);
		shadowBg = new View(this);
		shadowBg.setKeepScreenOn(true);
		shadowBg.setBackgroundColor(0x99000000);
		callLayout = (RelativeLayout) inflater.inflate(R.layout.call_check_layout, null);
		playLayout = (RelativeLayout) inflater.inflate(R.layout.play_guide_layout, null);
		wordTypefaceBold = Typefaces.getWordTypefaceBold(this);
		setCallCheckBox();
		setPlayGuideBox();
		db = new QuestionDB(activity);
		playString = getString(R.string.emotion_box_playing);
		media_handler = new MediaUpdateHandler();
		
		SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
		SharedPreferences.Editor edit = sp.edit();
		edit.putLong("LatestEmotionDIYTime", System.currentTimeMillis());
		edit.commit();
		
	}

	@Override
	protected void onResume(){
		super.onResume();
		if (LockCheck.check(getBaseContext())){
			Intent lock_intent = new Intent(this,LockedActivity.class);
			this.startActivity(lock_intent);
			this.finish();
			return;
		}
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
		if (media_thread != null && !media_thread.isInterrupted())
			media_thread.interrupt();
		if (media_handler != null)
			media_handler.removeMessages(0);
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
		
		int textSize =TextSize.normalTextSize(activity);
		
		callHelp.setTextSize(TypedValue.COMPLEX_UNIT_PX, textSize);
		callHelp.setTypeface(wordTypefaceBold);
		RelativeLayout.LayoutParams hParam = (LayoutParams) callHelp.getLayoutParams();
		hParam.width = screen.x * 349/480;
		hParam.height = screen.x * 114/480;
		
		callOK.setTextSize(TypedValue.COMPLEX_UNIT_PX, textSize);
		callOK.setTypeface(wordTypefaceBold);
		callCancel.setTextSize(TypedValue.COMPLEX_UNIT_PX, textSize);
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
		
	int textSize =TextSize.normalTextSize(activity);
	
		playPause = (ImageView) playLayout.findViewById(R.id.play_pause_button);
		playCancel = (ImageView) playLayout.findViewById(R.id.play_cancel_button);
		playHelp = (TextView) playLayout.findViewById(R.id.play_help);
		
		playPauseDrawable = getResources().getDrawable(R.drawable.record_stop);
		playPlayDrawable = getResources().getDrawable(R.drawable.record_play);
		
		playHelp.setTextSize(TypedValue.COMPLEX_UNIT_PX,textSize);
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
		
		titleSize = TextSize.smallTitleTextSize(activity);
		iconMargin = screen.x * 33/480;
		View title = createTitleView();
		mainLayout.addView(title);
		LinearLayout.LayoutParams titleparam =(LinearLayout.LayoutParams) title.getLayoutParams();
		titleparam.height = screen.x*245/1080;
		
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
		titleparam.height = screen.x*245/1080;
		
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
			titleparam.height = screen.x*245/1080;
			
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
		titleparam.height = screen.x*245/1080;
		
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
	
	private void setRecreationEnd(String selected){
		state = 2;
		
		mainLayout.removeAllViews();
		View title = createTitleView();
		mainLayout.addView(title);
		LinearLayout.LayoutParams titleparam =(LinearLayout.LayoutParams) title.getLayoutParams();
		titleparam.height = screen.x*245/1080;
		
		String text=  getString(R.string.emotionDIY_help_case4) +selected;
		View tv;
			tv = createTextView(text);
		mainLayout.addView(tv);
		View vv = createIconView(R.string.try_to_do,R.drawable.questionnaire_item_ok,new EndOnClickListener(2,selected));
		mainLayout.addView(vv);
		
		int rest_block = TOTAL_BLOCK - mainLayout.getChildCount();
		for (int i=0;i<rest_block;++i){
			View v = createBlankView();
			mainLayout.addView(v);
		}
	}
	
	private void setQuestionRecreation(){
		state = 1;
		
		mainLayout.removeAllViews();
		View title = createTitleView();
		mainLayout.addView(title);
		LinearLayout.LayoutParams titleparam =(LinearLayout.LayoutParams) title.getLayoutParams();
		titleparam.height = screen.x*245/1080;
		
		SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(activity);
		
		String[] recreation = new String[5];
		
		recreation[0] = sp.getString("recreation0",getString(R.string.default_recreation_1));
		recreation[1] = sp.getString("recreation1", getString(R.string.default_recreation_2));
		recreation[2] = sp.getString("recreation2", getString(R.string.default_recreation_3));
		recreation[3] = sp.getString("recreation3", "");
		recreation[4] = sp.getString("recreation4", "");
		
		if (recreation[0].length() == 0)
			recreation[0] = getString(R.string.default_recreation_1);
		
		if (recreation[1].length() == 0)
			recreation[1] = getString(R.string.default_recreation_2);
		
		if (recreation[2].length() == 0)
			recreation[2] = getString(R.string.default_recreation_3);
		
		boolean[] has_value = {
				recreation[0].length()>0,
				recreation[1].length()>0,
				recreation[2].length()>0,
				recreation[3].length()>0,
				recreation[4].length()>0
		};
		
		boolean exist = false;
		
		for (int i=0;i<has_value.length;++i)
			exist|=has_value[i];
		
		View tv;
		if (exist)
			tv = createTextView(R.string.emotionDIY_help_case3);
		else
			tv = createTextView(R.string.emotionDIY_help_case3_2);
		mainLayout.addView(tv);
		
		for (int i=0;i<has_value.length;++i){
			if (has_value[i]){
				View v = createIconView(recreation[i],0,new RecreationSelectionOnClickListener(recreation[i]));
				mainLayout.addView(v);
			}
		}
		
		
		int rest_block = TOTAL_BLOCK - mainLayout.getChildCount();
		for (int i=0;i<rest_block;++i){
			View v = createBlankView();
			mainLayout.addView(v);
		}
	}
	
	private View createTextView(int textStr){
		
		LinearLayout layout = (LinearLayout) inflater.inflate(R.layout.question_select_item, null);
		TextView text = (TextView) layout.findViewById(R.id.question_description);
		text.setTextSize(TypedValue.COMPLEX_UNIT_PX,titleSize );
		text.setTypeface(wordTypefaceBold);
		text.setTextColor(0xFF777777);
		text.setText(textStr);
		
		LinearLayout.LayoutParams tParam = (LinearLayout.LayoutParams)text.getLayoutParams();
		tParam.leftMargin = titleSize;
		
		layout.setBackgroundResource(R.drawable.questionnaire_bar_question);
		
		return layout;
	}
	
private View createTextView(String textStr){
		
		LinearLayout layout = (LinearLayout) inflater.inflate(R.layout.question_select_item, null);
		TextView text = (TextView) layout.findViewById(R.id.question_description);
		text.setTextSize(TypedValue.COMPLEX_UNIT_PX,titleSize );
		text.setTypeface(wordTypefaceBold);
		text.setTextColor(0xFF777777);
		text.setText(textStr);
		
		LinearLayout.LayoutParams tParam = (LinearLayout.LayoutParams)text.getLayoutParams();
		tParam.leftMargin = titleSize;
		
		layout.setBackgroundResource(R.drawable.questionnaire_bar_question);
		
		return layout;
	}
	
	private View createIconView(String textStr, int DrawableId ,OnClickListener listener){
		
		LinearLayout layout = (LinearLayout) inflater.inflate(R.layout.question_select_item, null);
		TextView text = (TextView) layout.findViewById(R.id.question_description);
		text.setTextSize(TypedValue.COMPLEX_UNIT_PX,titleSize );
		text.setTypeface(wordTypefaceBold);
		text.setTextColor(0xFF5c5c5c);
		text.setText(textStr);
		
		LinearLayout.LayoutParams tParam = (LinearLayout.LayoutParams)text.getLayoutParams();
		tParam.leftMargin = titleSize;
		
		ImageView icon = (ImageView) layout.findViewById(R.id.question_icon);
		if (DrawableId>0)
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
		text.setTextSize(TypedValue.COMPLEX_UNIT_PX,titleSize );
		text.setTypeface(wordTypefaceBold);
		text.setTextColor(0xFF5c5c5c);
		text.setText(textStr);
		
		LinearLayout.LayoutParams tParam = (LinearLayout.LayoutParams)text.getLayoutParams();
		tParam.leftMargin = titleSize;
		
		ImageView icon = (ImageView) layout.findViewById(R.id.question_icon);
		if (DrawableId>0)
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
		text.setTextSize(TypedValue.COMPLEX_UNIT_PX,titleSize );
		text.setTypeface(wordTypefaceBold);
		text.setText(R.string.emotionDIY_title);
		
		RelativeLayout.LayoutParams tParam = (RelativeLayout.LayoutParams)text.getLayoutParams();
		tParam.leftMargin = titleSize;
		
		ImageView icon = (ImageView) layout.findViewById(R.id.titlebar_icon);
		RelativeLayout.LayoutParams iParam =(RelativeLayout.LayoutParams) icon.getLayoutParams();
		iParam.leftMargin = titleSize;
		
		return layout;
	}
	
	private View createBlankView(){
		
		LinearLayout layout = (LinearLayout) inflater.inflate(R.layout.question_select_item, null);
		layout.setBackgroundResource(R.drawable.questionnaire_bar_normal);
		return layout;
	}
	
	private class EndOnClickListener implements View.OnClickListener{
		int in;
		String selected = null;
		EndOnClickListener(int in){
			this.in = in+1;
		}
		
		EndOnClickListener(int in,String selected){
			this.in = in+1;
			this.selected = selected;
		}
		
		@Override
		public void onClick(View v) {
			
			ClickLogger.Log(getBaseContext(), ClickLogId.EMOTIONDIY_SELECTION);
			boolean addAcc = db.insertEmotion(in,selected);
			if (addAcc)
				CustomToast.generateToast(activity, R.string.emotionDIY_end_toast, 1);
			else
				CustomToast.generateToast(activity, R.string.emotionDIY_end_toast, 0);
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
		
		@SuppressLint("InlinedApi")
		@SuppressWarnings("deprecation")
		@Override
		public void onClick(View v) {
			ClickLogger.Log(getBaseContext(), ClickLogId.EMOTIONDIY_OPEN_CALL_BOX);
			int item_count = mainLayout.getChildCount();
			for (int i=0;i<item_count;++i)
				mainLayout.getChildAt(i).setEnabled(false);
			enableBack = false;
			
			bgLayout.addView(shadowBg);
			bgLayout.addView(callLayout);
			shadowParam = (LayoutParams) shadowBg.getLayoutParams();
			if (Build.VERSION.SDK_INT >=8)
				shadowParam.width = shadowParam.height = LayoutParams.MATCH_PARENT;
			else
				shadowParam.width = shadowParam.height = LayoutParams.FILL_PARENT;
			
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
	
	@SuppressLint("InlinedApi")
	private class PlayGuideOnClickListener  implements View.OnClickListener{

		private int type;
		
		public PlayGuideOnClickListener(int type){
			this.type =type;
		}
		
		@SuppressWarnings("deprecation")
		@Override
		public void onClick(View v) {
			ClickLogger.Log(getBaseContext(), ClickLogId.EMOTIONDIY_OPEN_PLAY_BOX);
			int item_count = mainLayout.getChildCount();
			for (int i=0;i<item_count;++i)
				mainLayout.getChildAt(i).setEnabled(false);
			enableBack = false;
			
			bgLayout.addView(shadowBg);
			bgLayout.addView(playLayout);
			shadowParam = (LayoutParams) shadowBg.getLayoutParams();
			if (Build.VERSION.SDK_INT >= 8)
				shadowParam.width = shadowParam.height = LayoutParams.MATCH_PARENT;
			else
				shadowParam.width = shadowParam.height = LayoutParams.FILL_PARENT;
			
			boxParam = (LayoutParams) playLayout.getLayoutParams();
			boxParam.width = screen.x * 349/480;
			boxParam.height = screen.x * 189/480;
			boxParam.addRule(RelativeLayout.CENTER_IN_PARENT, RelativeLayout.TRUE);
			
			playHelp.setText(R.string.emotion_box_playing);
			if (media_thread != null && !media_thread.isInterrupted())
				media_thread.interrupt();
			if (media_handler != null)
				media_handler.removeMessages(0);
			if (mediaPlayer!=null)
				mediaPlayer.release();
			switch (type){
			case 0:
				mediaPlayer = MediaPlayer.create(activity, R.raw.emotion1);
				break;
			case 1:
				mediaPlayer = MediaPlayer.create(activity, R.raw.emotion2);
				break;
			default:
				mediaPlayer = MediaPlayer.create(activity, R.raw.emotion1);
			}
			media_thread = new Thread(new MediaRunnable());
			media_thread.start();
			mediaPlayer.start();
			mediaPlayer.setOnCompletionListener(new PlayOnCompletionListener());
			playPause.setOnClickListener(new PlayOnClickListener());
			playPause.setImageDrawable(playPauseDrawable);
			playCancel.setOnClickListener(new PlayCancelOnClickListener());
		}
	}
	
	
	private class RecreationSelectionOnClickListener  implements View.OnClickListener{

		private String recreation;
		
		public RecreationSelectionOnClickListener(String recreation){
			this.recreation = recreation;
		}
		
		@Override
		public void onClick(View v) {
			ClickLogger.Log(getBaseContext(), ClickLogId.EMOTIONDIY_SELECTION);
			setRecreationEnd(recreation);
		}
		
	}
	
	private class PlayOnCompletionListener implements MediaPlayer.OnCompletionListener{

		@Override
		public void onCompletion(MediaPlayer mp) {
			if (media_thread != null && !media_thread.isInterrupted())
				media_thread.interrupt();
			if (media_handler != null)
				media_handler.removeMessages(0);
			playPause.setImageDrawable(playPlayDrawable);
			playHelp.setText(R.string.emotion_box_replay);
		}
		
	}
	
	private class PlayOnClickListener implements View.OnClickListener{
		@Override
		public void onClick(View arg0) {
			if (media_thread != null && !media_thread.isInterrupted())
				media_thread.interrupt();
			if (media_handler != null)
				media_handler.removeMessages(0);
			if (mediaPlayer == null)
				return;
			if (mediaPlayer.isPlaying()){
				ClickLogger.Log(getBaseContext(), ClickLogId.EMOTIONDIY_PAUSE_AUDIO);
				mediaPlayer.pause();
				playPause.setImageDrawable(playPlayDrawable);
				playHelp.setText(R.string.emotion_box_pause);
			}
			else{
				ClickLogger.Log(getBaseContext(), ClickLogId.EMOTIONDIY_PLAY_AUDIO);
				media_thread = new Thread(new MediaRunnable());
				media_thread.start();
				mediaPlayer.start();
				playPause.setImageDrawable(playPauseDrawable);
				playHelp.setText(R.string.emotion_box_playing);
			}
		}
	}
	
	private class PlayCancelOnClickListener implements View.OnClickListener{
		@Override
		public void onClick(View v) {
			ClickLogger.Log(getBaseContext(), ClickLogId.EMOTIONDIY_CANCEL_AUDIO);
			if (media_thread != null && !media_thread.isInterrupted())
				media_thread.interrupt();
			if (media_handler != null)
				media_handler.removeMessages(0);
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
			ClickLogger.Log(getBaseContext(), ClickLogId.EMOTIONDIY_CANCEL_CALL);
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
			ClickLogger.Log(getBaseContext(), ClickLogId.EMOTIONDIY_CALL);
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
			ClickLogger.Log(getBaseContext(), ClickLogId.EMOTIONDIY_SELECTION);
			setQuestionEnd(in);
		}
	}
	
	private class RecreationOnClickListener implements View.OnClickListener{
		@Override
		public void onClick(View v) {
			ClickLogger.Log(getBaseContext(), ClickLogId.EMOTIONDIY_SELECTION);
			setQuestionRecreation();
		}
	}
	
	private class HelpOnClickListener implements View.OnClickListener{
		int type;
		HelpOnClickListener( int type){
			this.type = type;
		}
		
		@Override
		public void onClick(View v) {
			ClickLogger.Log(getBaseContext(), ClickLogId.EMOTIONDIY_SELECTION);
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
			ClickLogger.Log(getBaseContext(), ClickLogId.EMOTIONDIY_RETURN_BUTTON);
			if (state == 0){
				CustomToastSmall.generateToast(this, R.string.emotionDIY_toast);
				--state;
			}else if (state == -1)
				return super.onKeyDown(keyCode, event);
			else{
				--state;
				if (state == 0)
					setQuestionStart();
				else if (state ==1)
					setQuestionRecreation();
			}
		}
		return false;
	}
	
	private class MediaRunnable implements Runnable{
		@Override
		public void run() {
			
			try {
				while(true){
					Thread.sleep(1000);
					media_handler.sendEmptyMessage(0);
				}
			} catch (InterruptedException e) {}
		}
	}
	
	@SuppressLint("HandlerLeak")
	private class MediaUpdateHandler extends Handler{
		public void handleMessage(Message msg){
			try{
				playHelp.setText(playString+"("+getFormattedTime(mediaPlayer.getCurrentPosition())+"/"+getFormattedTime(mediaPlayer.getDuration())+")");
			}catch(Exception e){}
		}
	}
	
	private static String getFormattedTime(long time){
		time = time/1000L;
		long min = time/60L;
		long sec = time%60L;
		if (sec<10)
			return min+":0"+sec;
		else
			return min+":"+sec;
	}
}
