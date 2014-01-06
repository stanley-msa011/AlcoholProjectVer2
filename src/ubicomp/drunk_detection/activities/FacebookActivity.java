package ubicomp.drunk_detection.activities;

import history.facebook.bmp.BitmapGenerator;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import org.json.JSONObject;

import ubicomp.drunk_detection.ui.CustomToast;
import ubicomp.drunk_detection.ui.CustomToastSmall;
import ubicomp.drunk_detection.ui.LoadingDialogControl;
import ubicomp.drunk_detection.ui.ScreenSize;
import ubicomp.drunk_detection.ui.TextSize;
import ubicomp.drunk_detection.ui.Typefaces;

import com.facebook.FacebookRequestError;
import com.facebook.Request;
import com.facebook.Response;
import com.facebook.Session;
import com.facebook.SessionState;
import com.facebook.UiLifecycleHelper;
import com.facebook.model.GraphObject;
import com.facebook.widget.LoginButton;

import data.database.AdditionalDB;
import data.info.FacebookInfo;
import debug.clicklog.ClickLogId;
import debug.clicklog.ClickLogger;

import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.RelativeLayout.LayoutParams;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.Typeface;

public class FacebookActivity extends Activity {

	private static final String TAG = "FACEBOOK";

	private RelativeLayout mainLayout, loginLayout,callLayout;
	private FrameLayout bgLayout;
	private View bgShadow;
	private ScrollView inputScrollview;
	private LinearLayout inputLayout;
	private Activity activity;
	private LoginButton authButton;

	private LayoutInflater inflater;

	private static final List<String> PERMISSIONS = Arrays.asList("publish_actions");

	private TextView titleText, loginText;
	private RelativeLayout titleLayout;
	private ImageView logo, image;
	private Point screen;
	private Typeface wordTypeface, wordTypefaceBold;
	private int textSize;
	private SharedPreferences sp;

	private int image_week, image_score;

	private EditText texts;

	private View shareButton, inputMessage, guideMessage;

	private Bitmap state_bmp;

	private UiLifecycleHelper uiHelper;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_facebook);

		Bundle data = this.getIntent().getExtras();
		image_week = data.getInt("image_week", 0);
		image_score = data.getInt("image_score", 0);

		activity = this;
		inflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		sp = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
		wordTypeface = Typefaces.getWordTypeface(this);
		wordTypefaceBold = Typefaces.getWordTypefaceBold(this);

		bgLayout = (FrameLayout) this.findViewById(R.id.fb_main_layout);
		titleLayout = (RelativeLayout) this.findViewById(R.id.fb_title_layout);
		titleText = (TextView) this.findViewById(R.id.fb_title);
		logo = (ImageView) this.findViewById(R.id.fb_logo);
		image = (ImageView) this.findViewById(R.id.fb_input_image);
		mainLayout = (RelativeLayout) this.findViewById(R.id.facebook_main);
		inputLayout = (LinearLayout) this.findViewById(R.id.fb_input_layout);
		loginLayout = (RelativeLayout) this.findViewById(R.id.fb_login_layout);
		inputScrollview = (ScrollView) this.findViewById(R.id.facebook_scrollview);
		loginText = (TextView) this.findViewById(R.id.fb_login_message);

		bgShadow = new View(activity);
		bgShadow.setBackgroundColor(0x99000000);
		
		screen = ScreenSize.getScreenSize(this);

		int titleSize = TextSize.smallTitleTextSize(activity);
		textSize = TextSize.normalTextSize(activity);

		RelativeLayout.LayoutParams logoParam = (RelativeLayout.LayoutParams) logo
				.getLayoutParams();
		logoParam.leftMargin = screen.x * 26 / 480;

		RelativeLayout.LayoutParams tParam = (RelativeLayout.LayoutParams) titleText
				.getLayoutParams();
		tParam.leftMargin = screen.x * 27 / 480;
		titleText.setTextSize(TypedValue.COMPLEX_UNIT_PX, titleSize);
		titleText.setTypeface(wordTypefaceBold);

		loginText.setTextSize(TypedValue.COMPLEX_UNIT_PX, textSize);
		loginText.setTypeface(wordTypeface);

		LinearLayout.LayoutParams ttParam = (LinearLayout.LayoutParams) titleLayout
				.getLayoutParams();
		ttParam.height = screen.x * 245 / 1080;

		authButton = (LoginButton) this.findViewById(R.id.authButton);
		authButton.setReadPermissions(Arrays.asList("basic_info"));
		authButton.setText(R.string.fb_login);
		authButton.setTypeface(wordTypefaceBold);
		authButton.setTextSize(TypedValue.COMPLEX_UNIT_PX, TextSize.smallTitleTextSize(activity));

		state_bmp = BitmapGenerator.generateBitmap(activity, image_week, image_score);

		LinearLayout.LayoutParams imageParam = (android.widget.LinearLayout.LayoutParams) image
				.getLayoutParams();
		imageParam.width = screen.x;
		imageParam.height = screen.x * 597 / 567;
		image.setImageBitmap(state_bmp);

		guideMessage = createTextView(R.string.fb_message);
		inputLayout.addView(guideMessage);

		inputMessage = createEditView();
		inputLayout.addView(inputMessage);

		shareButton = createIconView(R.string.fb_share, R.drawable.questionnaire_item_ok,
		/*		new OnClickListener() {
					@Override
					public void onClick(View v) {
						//publishStory();
					}
				}
				*/
				new SendOnClickListener()
		);

		inputLayout.addView(shareButton);

		uiHelper = new UiLifecycleHelper(this, callback);
		uiHelper.onCreate(savedInstanceState);

		Session.openActiveSession(this, true, callback);
	}

	@Override
	protected void onResume() {
		super.onResume();
		enablePage(true);
		uiHelper.onResume();
	}
	
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		uiHelper.onActivityResult(requestCode, resultCode, data);
		Session.getActiveSession().onActivityResult(this, requestCode, resultCode, data);
	}

	@Override
	public void onPause() {
		super.onPause();
		if (bgShadow!=null && bgShadow.getParent()!=null)
			bgLayout.removeView(bgShadow);
		if (callLayout!=null && callLayout.getParent()!=null)
			bgLayout.removeView(callLayout);
		enablePage(true);
		uiHelper.onPause();
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		if (state_bmp != null && !state_bmp.isRecycled()) {
			state_bmp.recycle();
		}
		uiHelper.onDestroy();

	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		uiHelper.onSaveInstanceState(outState);
	}

	@Override
	public void onStop() {
		super.onStop();
		uiHelper.onStop();
	}

	private Session.StatusCallback callback = new Session.StatusCallback() {
		@Override
		public void call(Session session, SessionState state, Exception exception) {
			onSessionStateChange(session, state, exception);
		}
	};

	private void onSessionStateChange(Session session, SessionState state, Exception exception) {
		Log.d(TAG, state.name() + " " + state.toString());
		if (state.isOpened()) {
			inputScrollview.setVisibility(View.VISIBLE);
			loginLayout.setVisibility(View.INVISIBLE);
			//authButton.setText(R.string.fb_logout);
		} else if (state.isClosed()) {
			inputScrollview.setVisibility(View.INVISIBLE);
			loginLayout.setVisibility(View.VISIBLE);
			//authButton.setText(R.string.fb_login);
		}
	}

	private boolean isSubsetOf(Collection<String> subset, Collection<String> superset) {
		for (String string : subset) {
			if (!superset.contains(string)) {
				return false;
			}
		}
		return true;
	}

	private void publishStory() {
		Session session = Session.getActiveSession();

		if (session != null) {
			// Check for publish permissions
			List<String> permissions = session.getPermissions();
			if (!isSubsetOf(PERMISSIONS, permissions)) {
				Session.NewPermissionsRequest newPermissionsRequest = new Session.NewPermissionsRequest(
						this, PERMISSIONS);
				session.requestNewPublishPermissions(newPermissionsRequest);
				return;
			}

			LoadingDialogControl.show(activity, 1);

			Request.Callback callback = new Request.Callback() {
				@Override
				public void onCompleted(Response response) {
					boolean result = false;
					if (response != null) {
						GraphObject gobj = response.getGraphObject();
						if (gobj == null) {
							Log.d(TAG, "upload failed");
							CustomToastSmall.generateToast(activity, R.string.fb_fail_toast);
						} else {
							JSONObject graphResponse = gobj.getInnerJSONObject();
							try {
								graphResponse.getString("id");
							} catch (Exception e) {
								Log.i(TAG, "upload exception" + e.getMessage());
							}
							FacebookRequestError error = response.getError();
							if (error == null) {
								result = true;
							}
						}
					}

					String text_msg = null;
					if (sp.getBoolean("FacebookText", false)) {
						if (texts != null && texts.getText() != null)
							text_msg = texts.getText().toString();
						else
							text_msg = "";
					}
					FacebookInfo info;

					if (result) {
						Log.d(TAG, "upload success");
						info = new FacebookInfo(System.currentTimeMillis(), image_week,
								image_score, text_msg, true);
						CustomToast.generateToast(activity, R.string.fb_success_toast, 0);

					} else {
						Log.d(TAG, "upload failed");
						info = new FacebookInfo(System.currentTimeMillis(), image_week,
								image_score, text_msg, false);
						CustomToastSmall.generateToast(activity, R.string.fb_fail_toast);
					}
					AdditionalDB adb = new AdditionalDB(activity);
					adb.insertFacebook(info);
					LoadingDialogControl.dismiss();
					if (result)
						finish();
				}
			};

			Request request = Request.newUploadPhotoRequest(session, state_bmp, callback);
			Bundle params = request.getParameters();
			if (texts != null && texts.getText().length() > 0)
				params.putString("name", texts.getText() + "\n" + getString(R.string.app_name)
						+ " " + getString(R.string.homepage));
			else
				params.putString("name", getString(R.string.app_name) + " "
						+ getString(R.string.homepage));

			request.setParameters(params);
			request.executeAsync();

		}

	}

	private View createTextView(int textStr) {

		LinearLayout layout = (LinearLayout) inflater.inflate(R.layout.question_select_item, null);
		TextView text = (TextView) layout.findViewById(R.id.question_description);
		text.setTextSize(TypedValue.COMPLEX_UNIT_PX, textSize);
		text.setTypeface(wordTypefaceBold);
		text.setTextColor(0xFF777777);
		text.setText(textStr);

		LinearLayout.LayoutParams tParam = (LinearLayout.LayoutParams) text.getLayoutParams();
		tParam.leftMargin = textSize;

		layout.setBackgroundResource(R.drawable.questionnaire_bar_question);

		return layout;
	}

	private View createEditView() {

		RelativeLayout layout = new RelativeLayout(mainLayout.getContext());

		EditText edit = new EditText(activity);
		edit.setTextColor(0xFF000000);
		edit.setLines(1);
		edit.setMaxLines(10);
		edit.setHint(R.string.emotion_manage_input);
		edit.setTextSize(TypedValue.COMPLEX_UNIT_PX, textSize * 4 / 3);
		edit.setTypeface(wordTypefaceBold);
		edit.setLineSpacing(0.f, 1.2f);
		edit.setId(0x1999);
		layout.addView(edit);
		edit.setWidth(screen.x);
		RelativeLayout.LayoutParams param = (RelativeLayout.LayoutParams) edit.getLayoutParams();
		param.addRule(RelativeLayout.CENTER_IN_PARENT);

		texts = edit;
		layout.setBackgroundResource(R.drawable.questionnaire_bar_normal);
		return layout;
	}

	private View createIconView(int textStr, int DrawableId, OnClickListener listener) {

		LinearLayout layout = (LinearLayout) inflater.inflate(R.layout.question_select_item, null);
		TextView text = (TextView) layout.findViewById(R.id.question_description);
		text.setTextSize(TypedValue.COMPLEX_UNIT_PX, textSize);
		text.setTypeface(wordTypefaceBold);
		text.setTextColor(0xFF5c5c5c);
		text.setText(textStr);

		LinearLayout.LayoutParams tParam = (LinearLayout.LayoutParams) text.getLayoutParams();
		tParam.leftMargin = textSize;

		ImageView icon = (ImageView) layout.findViewById(R.id.question_icon);
		if (DrawableId > 0)
			icon.setImageDrawable(getResources().getDrawable(DrawableId));
		LinearLayout.LayoutParams iParam = (LinearLayout.LayoutParams) icon.getLayoutParams();
		iParam.rightMargin = screen.x * 33 / 480;

		layout.setOnClickListener(listener);
		layout.setOnTouchListener(onTouchListener);
		layout.setBackgroundResource(R.drawable.questionnaire_bar_normal);

		return layout;
	}

	private final OnTouchListener onTouchListener = new ItemOnTouchListener();

	private class ItemOnTouchListener implements View.OnTouchListener {

		private Rect rect;

		@Override
		public boolean onTouch(View v, MotionEvent event) {
			int e = event.getAction();
			switch (e) {
			case MotionEvent.ACTION_MOVE:
				if (!rect.contains(v.getLeft() + (int) event.getX(),
						v.getTop() + (int) event.getY()))
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

	@SuppressLint("InlinedApi")
	private class SendOnClickListener implements View.OnClickListener {

		@SuppressWarnings("deprecation")
		@Override
		public void onClick(View v) {
			ClickLogger.Log(activity, ClickLogId.FB_SHARE_BUTTON);
			enablePage(false);
			bgLayout.addView(bgShadow);
			FrameLayout.LayoutParams shadowParam = (FrameLayout.LayoutParams) bgShadow.getLayoutParams();
			if (Build.VERSION.SDK_INT>=8)
				shadowParam.width = shadowParam.height = LayoutParams.MATCH_PARENT;
			else
				shadowParam.width = shadowParam.height = LayoutParams.FILL_PARENT;
			if (callLayout == null){
				callLayout = (RelativeLayout) inflater.inflate(R.layout.call_check_layout, null);
				TextView callOK = (TextView) callLayout.findViewById(R.id.call_ok_button);
				TextView callCancel = (TextView) callLayout.findViewById(R.id.call_cancel_button);
				TextView callHelp = (TextView) callLayout.findViewById(R.id.call_help);
				int textSize = TextSize.normalTextSize(activity);
			
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
				
				callHelp.setText(R.string.fb_check);
				callOK.setOnClickListener(new CallOnClickListener());
				callCancel.setOnClickListener(new CallCancelOnClickListener());
			}
			
			bgLayout.addView(callLayout);
			FrameLayout.LayoutParams boxParam = (FrameLayout.LayoutParams) callLayout.getLayoutParams();
			boxParam.width = screen.x * 349/480;
			boxParam.height = screen.x * 189/480;
			boxParam.gravity=Gravity.CENTER;
			
		}
	}

	private class CallOnClickListener implements View.OnClickListener {
		@Override
		public void onClick(View v) {
			ClickLogger.Log(activity, ClickLogId.FB_SHARE_CHECK_BUTTON);
			publishStory();
			bgLayout.removeView(bgShadow);
			bgLayout.removeView(callLayout);
			enablePage(true);
		}
	}
	private class CallCancelOnClickListener implements View.OnClickListener {
		@Override
		public void onClick(View v) {
			ClickLogger.Log(activity, ClickLogId.FB_SHARE_CANCEL_BUTTON);
			bgLayout.removeView(bgShadow);
			bgLayout.removeView(callLayout);
			enablePage(true);
		}
	}
	
	private void enablePage(boolean enable){
		authButton.setEnabled(enable);
		shareButton.setEnabled(enable);
		texts.setEnabled(enable);
	}
}
