package ubicomp.drunk_detection.fragments;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;

import test.data.BracDataHandler;
import test.ui.NotificationBox;
import ubicomp.drunk_detection.activities.FacebookActivity;
import ubicomp.drunk_detection.activities.FragmentTabs;
import ubicomp.drunk_detection.activities.R;
import ubicomp.drunk_detection.config.Config;
import ubicomp.drunk_detection.ui.CustomTypefaceSpan;
import ubicomp.drunk_detection.ui.EnablePage;
import ubicomp.drunk_detection.ui.LoadingDialogControl;
import ubicomp.drunk_detection.ui.ScreenSize;
import ubicomp.drunk_detection.ui.TextSize;
import ubicomp.drunk_detection.ui.Typefaces;

import data.calculate.WeekNum;
import data.database.AdditionalDB;
import data.database.AudioDB;
import data.database.HistoryDB;
import data.info.AccumulatedHistoryState;
import data.info.BarInfo;
import data.info.DateBracDetectionState;
import data.info.DateValue;
import debug.clicklog.ClickLogId;
import debug.clicklog.ClickLogger;
import history.ui.ChartView;
import history.ui.PageAnimationCaller;
import history.ui.HistoryStorytelling;
import history.ui.AudioRecordBox;
import history.ui.PageAnimationTaskVertical;
import history.ui.PageAnimationTaskVerticalFling;
import history.ui.PageWidgetVertical;
import history.ui.QuoteMsgBox;
import history.ui.AudioRecordBoxCallee;
import history.ui.StorytellingBox;
import history.ui.RecorderCaller;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlarmManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.PointF;
import android.graphics.Typeface;
import android.graphics.Paint.Align;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.text.Spannable;
import android.text.SpannableString;
import android.util.TypedValue;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.FrameLayout;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.ScrollView;
import android.widget.TextView;

public class HistoryFragment extends Fragment implements AudioRecordBoxCallee,EnablePage, PageAnimationCaller, RecorderCaller {

	private View view;

	private Activity activity;

	private RelativeLayout pageLayout;
	private RelativeLayout chartLayout;
	private RelativeLayout chartAreaLayout;
	private RelativeLayout stageLayout;
	private HistoryDB hdb;
	private AudioDB adb;
	private AdditionalDB addDb;
	private PageWidgetVertical pageWidget;
	private PageAnimationTaskVertical pageAnimationTask;
	private PageAnimationTaskVerticalFling pageAnimationTask2;

	private HorizontalScrollView scrollView;

	private Point screen;
	private ArrayList<DateBracDetectionState> historys;;
	private ArrayList<BarInfo> bars;
	private ArrayList<Boolean> hasAudio;

	private int NUM_OF_BARS;
	private int page_week;
	
	private ChartView chart;
	private ChartTitleView chartTitle;
	private ChartYAxisView chartYAxis;
	private ChartLabelView chartLabel;
	private int chartWidth;
	private int chartHeight;

	private GestureDetector gDetector;
	private GestureListener gListener;
	private PageTouchListener gtListener;

	private boolean isAnimation = false;

	private int page_width, page_height, bg_x;

	private Bitmap cur_bg_bmp, next_bg_bmp;

	private PointF from, to;
	private HistoryFragment historyFragment;

	
	private LoadingHandler loadHandler;

	private Typeface wordTypefaceBold, digitTypeface, digitTypefaceBold;

	private DecimalFormat format;
	private TextView quoteText;
	private TextView stageMessageText, stageMessage, stageRateText;

	private Calendar from_cal;
	private Calendar to_cal;

	private int max_week;
	private boolean chartTouchable = true;

	private AudioRecordBox recordBox;
	private StorytellingBox storytellingBox;

	private Drawable chartBg1Drawable, chartBg2Drawable, chartBg3Drawable, chartBg4Drawable;

	private AccumulatedHistoryState[] page_states;

	private static String[] QUOTE_STR;

	private static final int MAX_PAGE_WEEK = 11;

	private String doneStr;

	private ScrollToHandler scrollToHandler;

	private ImageView storytellingButton, fbButton;
	private StorytellingOnClickListener storytellingOnClickListener;

	private int received_msg = 0;
	private boolean read_arg = false;

	private AlphaAnimation shareAnimation;

	private static final long READING_PAGE_TIME = Config.READING_PAGE_TIME;

	private static final int LONG_FLING_LIMIT = Config.LONG_FLING_LIMIT;

	private ScrollView quoteScrollView;
	private RelativeLayout quoteHiddenLayout;
	private TextView quoteHiddenText;
	private ImageView quoteNextButton;
	private QuoteScrollListener quoteScrollListener;

	private QuoteScrollHandler quoteScrollHandler;
	private ScrollHandler scrollHandler;
	private Thread infiniteThread;

	private QuoteMsgBox quoteMsgBox;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.activity = this.getActivity();
		hdb = new HistoryDB(activity);
		adb = new AudioDB(activity);
		addDb = new AdditionalDB(activity);
		from_cal = Calendar.getInstance();

		wordTypefaceBold = Typefaces.getDigitTypefaceBold(activity);
		digitTypeface = Typefaces.getDigitTypeface(activity);
		digitTypefaceBold = Typefaces.getWordTypefaceBold(activity);

		SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(activity);
		int mYear = sp.getInt("sYear", from_cal.get(Calendar.YEAR));
		int mMonth = sp.getInt("sMonth", from_cal.get(Calendar.MONTH));
		int mDay = sp.getInt("sDate", from_cal.get(Calendar.DATE));
		from_cal.set(mYear, mMonth, mDay, 0, 0, 0);
		from_cal.set(Calendar.MILLISECOND, 0);
		doneStr = getResources().getString(R.string.done);
		QUOTE_STR = getResources().getStringArray(R.array.quote_message);

		format = new DecimalFormat();
		format.setMaximumIntegerDigits(3);
		format.setMinimumIntegerDigits(1);
		format.setMinimumFractionDigits(0);
		format.setMaximumFractionDigits(0);

		gListener = new GestureListener();
		gDetector = new GestureDetector(getActivity(), gListener);
		gtListener = new PageTouchListener();

		scrollToHandler = new ScrollToHandler();
		quoteScrollHandler = new QuoteScrollHandler();
		scrollHandler = new ScrollHandler();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		this.historyFragment = this;
		view = inflater.inflate(R.layout.history_fragment, container, false);

		received_msg = 0;
		if (!read_arg) {
			Bundle data = getArguments();
			if (data != null) {
				received_msg = data.getInt("action");
			}
			read_arg = true;
		}

		screen = ScreenSize.getScreenSize(getActivity());
		if (screen == null) {
			if (activity != null)
				activity.finish();
			else
				return view;
		}
		bg_x = screen.x;
		page_width = bg_x;
		page_height = screen.y - bg_x * 574 / 1080;
		from = new PointF(page_width, page_height);
		to = new PointF(page_width / 2, -page_height);

		pageLayout = (RelativeLayout) view.findViewById(R.id.history_book_layout);
		chartLayout = (RelativeLayout) view.findViewById(R.id.history_content_layout);
		scrollView = (HorizontalScrollView) view.findViewById(R.id.history_scroll_view);
		chartAreaLayout = (RelativeLayout) view.findViewById(R.id.history_chart_area_layout);
		quoteHiddenLayout = (RelativeLayout) view.findViewById(R.id.history_quote_hidden_layout);
		quoteHiddenText = (TextView) view.findViewById(R.id.history_quote_hidden_text);
		quoteNextButton = (ImageView) view.findViewById(R.id.history_quote_hidden_button);
		quoteScrollView = (ScrollView) view.findViewById(R.id.history_quote_scroll_view);
		fbButton = (ImageView) view.findViewById(R.id.history_fb_button);

		int textSize = TextSize.normalTextSize(activity);
		stageLayout = (RelativeLayout) view.findViewById(R.id.history_stage_message_layout);
		stageMessage = (TextView) view.findViewById(R.id.history_stage);
		stageMessage.setTypeface(wordTypefaceBold);

		stageMessageText = (TextView) view.findViewById(R.id.history_stage_message);
		stageMessageText.setTextSize(TypedValue.COMPLEX_UNIT_PX, TextSize.mlargeTitleSize(activity));
		stageMessageText.setTypeface(digitTypefaceBold);

		stageRateText = (TextView) view.findViewById(R.id.history_stage_rate);
		stageRateText.setTextSize(TypedValue.COMPLEX_UNIT_PX, textSize);

		quoteText = (TextView) view.findViewById(R.id.history_quote);
		quoteText.setTextSize(TypedValue.COMPLEX_UNIT_PX, textSize);
		quoteText.setTypeface(wordTypefaceBold);

		quoteHiddenText.setTextSize(TypedValue.COMPLEX_UNIT_PX, textSize);
		quoteHiddenText.setTypeface(wordTypefaceBold);

		storytellingButton = (ImageView) view.findViewById(R.id.history_storytelling_button);
		storytellingOnClickListener = new StorytellingOnClickListener();
		storytellingButton.setOnClickListener(storytellingOnClickListener);

		fbButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				ClickLogger.Log(activity, ClickLogId.STORYTELLING_FB_SHARE_BUTTON_CLICK);
				Intent intent = new Intent(getActivity(), FacebookActivity.class);
				intent.putExtra("image_week", page_week);
				intent.putExtra("image_score", page_states[page_week].getScore());
				getActivity().startActivity(intent);
			}
		});

		LayoutParams fbParam = (LayoutParams) fbButton.getLayoutParams();
		fbParam.rightMargin = bg_x * 10 / 480;

		LayoutParams sParam = (LayoutParams) stageLayout.getLayoutParams();
		sParam.leftMargin = bg_x * 10 / 480;
		sParam.topMargin = bg_x * 60 / 480;
		sParam.width = bg_x * 70 / 480;

		int quoteTopMargin = page_height * 428 / 509;

		LayoutParams rParam = (LayoutParams) stageRateText.getLayoutParams();
		rParam.leftMargin = bg_x * 20 / 480;
		rParam.topMargin = quoteTopMargin;
		rParam.width = bg_x * 70 / 480;

		LayoutParams qParam = (LayoutParams) quoteScrollView.getLayoutParams();
		qParam.topMargin = quoteTopMargin;
		qParam.height = bg_x * 64 / 480;
		quoteScrollListener = new QuoteScrollListener();
		quoteScrollView.setOnTouchListener(quoteScrollListener);

		LinearLayout.LayoutParams qTextParam = (LinearLayout.LayoutParams) quoteText.getLayoutParams();
		qTextParam.height = bg_x * 64 / 480;

		LinearLayout.LayoutParams qHiddenParam = (LinearLayout.LayoutParams) quoteHiddenLayout.getLayoutParams();
		qHiddenParam.height = bg_x * 64 / 480;
		quoteHiddenLayout.setPadding(0, 0, bg_x * 30 / 480, 0);

		LayoutParams qNextButtonParam = (LayoutParams) quoteNextButton.getLayoutParams();
		qNextButtonParam.leftMargin = bg_x * 20 / 480;

		return view;
	}

	public void onResume() {
		super.onResume();
		RelativeLayout r = (RelativeLayout) view;
		recordBox = new AudioRecordBox(this.getActivity(),this, r);
		storytellingBox = new StorytellingBox(this.getActivity(),this, r);
		if (loadHandler == null)
			loadHandler = new LoadingHandler();
		loadHandler.sendEmptyMessage(0);
	}

	public void onPause() {
		if (recordBox != null) {
			recordBox.OnPause();
			recordBox.clear();
		}
		if (storytellingBox != null)
			storytellingBox.clear();
		if (scrollToHandler != null)
			scrollToHandler.removeMessages(0);
		historys.clear();
		bars.clear();
		hasAudio.clear();
		if (storytellingButton != null) {
			if (Build.VERSION.SDK_INT >= 8 && shareAnimation != null)
				shareAnimation.cancel();
			storytellingButton.setAnimation(null);
		}
		if (quoteScrollHandler != null)
			quoteScrollHandler.removeMessages(0);
		if (infiniteThread != null && !infiniteThread.isInterrupted()) {
			infiniteThread.interrupt();
			try {
				infiniteThread.join();
			} catch (InterruptedException e) {
			} finally {
				infiniteThread = null;
			}
		}
		if (scrollHandler != null) {
			scrollHandler.removeMessages(0);
			scrollHandler.removeMessages(1);
		}
		if (quoteMsgBox != null)
			quoteMsgBox.closeBox();
		quoteScrollView.scrollTo(0, 0);
		clear();
		super.onPause();
	}

	private void clear() {
		pageLayout.removeView(pageWidget);
		chartLayout.removeView(chart);
		chartAreaLayout.removeView(chartYAxis);
		chartAreaLayout.removeView(chartTitle);
		chartAreaLayout.removeView(chartLabel);

		if (pageWidget!=null)
			pageWidget.setBitmaps(null, null);

		if (loadHandler != null)
			loadHandler.removeMessages(0);

		if (pageAnimationTask != null && !pageAnimationTask.isCancelled()) {
			pageAnimationTask.cancel(true);
			pageAnimationTask = null;
		}
		if (pageAnimationTask2 != null && !pageAnimationTask2.isCancelled()) {
			pageAnimationTask2.cancel(true);
			pageAnimationTask2 = null;
		}

		if (cur_bg_bmp != null && !cur_bg_bmp.isRecycled()) {
			cur_bg_bmp.recycle();
			cur_bg_bmp = null;
		}
		if (next_bg_bmp != null && !next_bg_bmp.isRecycled()) {
			next_bg_bmp.recycle();
			next_bg_bmp = null;
		}
		if (pageWidget != null) {
			pageWidget.destroyDrawingCache();
			pageWidget.clear();
			pageWidget = null;
		}
		System.gc();
	}

	@SuppressWarnings("deprecation")
	private void initView() {

		scrollView.setSmoothScrollingEnabled(true);
		pageWidget = new PageWidgetVertical(activity, page_width, page_height);

		Resources r = activity.getResources();

		int chart_height = screen.x * 564 / 1080;

		if (chartBg1Drawable == null)
			chartBg1Drawable = r.getDrawable(R.drawable.chart_bg1);
		if (chartBg2Drawable == null)
			chartBg2Drawable = r.getDrawable(R.drawable.chart_bg2);
		if (chartBg3Drawable == null)
			chartBg3Drawable = r.getDrawable(R.drawable.chart_bg3);
		if (chartBg4Drawable == null)
			chartBg4Drawable = r.getDrawable(R.drawable.chart_bg4);

		pageLayout.addView(pageWidget);
		LayoutParams param = (LayoutParams) pageWidget.getLayoutParams();
		param.width = page_width;
		param.height = page_height;

		// Set chart
		RelativeLayout.LayoutParams scrollParam = (RelativeLayout.LayoutParams) scrollView.getLayoutParams();
		scrollParam.width = screen.x;
		scrollParam.height = chart_height;

		FrameLayout.LayoutParams clParam = (FrameLayout.LayoutParams) chartLayout.getLayoutParams();
		clParam.width = screen.x;
		clParam.height = chart_height;

		settingBars();
		checkHasRecorder();

		chartHeight = chart_height;

		chart = new ChartView(activity.getBaseContext(), bars, page_week, hasAudio, chartHeight, scrollView,historyFragment);

		chartWidth = chart.getChartWidth();
		if (chartWidth < screen.x)
			chartWidth = screen.x;

		chartLayout.addView(chart);

		RelativeLayout.LayoutParams chartParam = (RelativeLayout.LayoutParams) chart.getLayoutParams();
		chartParam.width = chartWidth;
		chartParam.height = chart_height;

		chartLayout.invalidate();
		chartLayout.updateViewLayout(chart, chartParam);

		chartYAxis = new ChartYAxisView(activity);
		chartAreaLayout.addView(chartYAxis);
		RelativeLayout.LayoutParams chartYParam = (RelativeLayout.LayoutParams) chartYAxis.getLayoutParams();
		chartYParam.width = screen.x * 94 / 1080;
		chartYParam.height = chartParam.height;

		chartTitle = new ChartTitleView(activity);
		chartAreaLayout.addView(chartTitle);
		RelativeLayout.LayoutParams chartTitleParam = (RelativeLayout.LayoutParams) chartTitle.getLayoutParams();
		chartTitleParam.width = screen.x;
		chartTitleParam.height = screen.x * 100 / 1080;
		chartTitleParam.topMargin = screen.x * 35 / 1080;

		chartLabel = new ChartLabelView(activity);
		chartAreaLayout.addView(chartLabel, 0);
		RelativeLayout.LayoutParams chartLabelParam = (RelativeLayout.LayoutParams) chartLabel.getLayoutParams();
		chartLabelParam.width = screen.x * 540 / 1080;
		chartLabelParam.height = screen.x * 90 / 1080;
		chartLabelParam.topMargin = screen.x * 130 / 1080;
		chartLabelParam.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);

		if (Build.VERSION.SDK_INT >= 16) {
			if (chart_type == 0)
				chartAreaLayout.setBackground(chartBg1Drawable);
			else if (chart_type == 1)
				chartAreaLayout.setBackground(chartBg2Drawable);
			else if (chart_type == 2)
				chartAreaLayout.setBackground(chartBg3Drawable);
			else
				chartAreaLayout.setBackground(chartBg4Drawable);
		} else {
			if (chart_type == 0)
				chartAreaLayout.setBackgroundDrawable(chartBg1Drawable);
			else if (chart_type == 1)
				chartAreaLayout.setBackgroundDrawable(chartBg2Drawable);
			else if (chart_type == 2)
				chartAreaLayout.setBackgroundDrawable(chartBg3Drawable);
			else
				chartAreaLayout.setBackgroundDrawable(chartBg4Drawable);
		}
		pageWidget.setOnTouchListener(gtListener);
		storytellingButton.setVisibility(View.VISIBLE);
		storytellingButton.bringToFront();
		fbButton.setVisibility(View.VISIBLE);
		fbButton.bringToFront();
	}

	private void setStorytellingTexts() {

		AccumulatedHistoryState curAH = page_states[page_week];
		float progress = curAH.getProgress();

		String stageText = String.valueOf(page_week + 1);

		stageMessageText.setText(stageText);

		String progress_str = format.format(progress) + "%\n";
		Spannable p_str = new SpannableString(progress_str + doneStr);
		p_str.setSpan(new CustomTypefaceSpan("c1", digitTypefaceBold, 0xFFE79100), 0, progress_str.length(),
				Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
		p_str.setSpan(new CustomTypefaceSpan("c2", wordTypefaceBold, 0xFF717071), progress_str.length(),
				progress_str.length() + doneStr.length(), Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
		stageRateText.setText(p_str);
		quoteText.setText(QUOTE_STR[page_week]);

	}

	@Override
	public void endAnimation() {
		setStorytellingTexts();
		setStageVisible(true);
		FragmentTabs.enableTabAndClick(true);
		isAnimation = false;
		chart.invalidate();
		if (received_msg == NotificationBox.TYPE_STORYTELLING_RECORDING) {
			received_msg = 0;
			Calendar cal = Calendar.getInstance();
			DateValue dv = new DateValue(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH),
					cal.get(Calendar.DAY_OF_MONTH));
			recordBox.showRecordBox(dv, bars.size() - 1);
		}
	}

	@Override
	public void endAnimation(int tt) {
		setStorytellingTexts();
		setStageVisible(true);
		pageWidget.setOnTouchListener(gtListener);
		FragmentTabs.enableTabAndClick(true);
		isAnimation = false;
		chart.invalidate();
		quoteScrollListener.setEnable(true);
		if (infiniteThread != null && !infiniteThread.isInterrupted()) {
			infiniteThread.interrupt();
			try {
				infiniteThread.join();
			} catch (InterruptedException e) {
			} finally {
				infiniteThread = null;
			}
		}
		if (scrollHandler != null) {
			scrollHandler.removeMessages(0);
			scrollHandler.removeMessages(1);
		}
		quoteScrollView.scrollTo(0, 0);

		if (received_msg == NotificationBox.TYPE_STORYTELLING_RECORDING) {
			received_msg = 0;
			Calendar cal = Calendar.getInstance();
			DateValue dv = new DateValue(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH),
					cal.get(Calendar.DAY_OF_MONTH));
			recordBox.showRecordBox(dv, bars.size() - 1);
		}
	}

	@Override
	public void resetPage(int change) {
		if (cur_bg_bmp != null && !cur_bg_bmp.isRecycled()) {
			cur_bg_bmp.recycle();
			cur_bg_bmp = null;
		}
		if (next_bg_bmp != null && !next_bg_bmp.isRecycled()) {
			next_bg_bmp.recycle();
			next_bg_bmp = null;
		}
		if (change > 0) {
			++page_week;
			if (page_week > max_week)
				page_week = max_week;
		} else if (change < 0) {
			--page_week;
			if (page_week < 0)
				page_week = 0;
		}
		AccumulatedHistoryState AH = page_states[page_week];
		Bitmap tmp = BitmapFactory.decodeResource(activity.getResources(),
				HistoryStorytelling.getPage(AH.getScore(), AH.week));
		cur_bg_bmp = Bitmap.createScaledBitmap(tmp, screen.x, page_height, true);
		tmp.recycle();
		next_bg_bmp = Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888);
		pageWidget.setBitmaps(cur_bg_bmp, next_bg_bmp);

		int scroll_value = chart.getScrollValue(page_week);
		if (scroll_value < 0)
			scroll_value = 0;
		Message msg = new Message();
		Bundle data = new Bundle();
		data.putInt("pos", scroll_value);
		msg.setData(data);
		msg.what = 0;
		scrollToHandler.sendMessage(msg);
	}

	@SuppressLint("HandlerLeak")
	private class ScrollToHandler extends Handler {
		public void handleMessage(Message msg) {

			chart.setPageWeek(page_week);
			int pos = msg.getData().getInt("pos", 0);
			scrollView.smoothScrollTo(pos, 0);
		}
	}

	@SuppressLint("HandlerLeak")
	private class LoadingHandler extends Handler {

		public void handleMessage(Message msg) {
			isAnimation = false;

			page_states = hdb.getAccumulatedHistoryStateByWeek();
			page_week = page_states.length - 1;
			if (page_week > MAX_PAGE_WEEK)
				page_week = MAX_PAGE_WEEK;
			max_week = page_week;

			historys = new ArrayList<DateBracDetectionState>();
			bars = new ArrayList<BarInfo>();
			hasAudio = new ArrayList<Boolean>();

			DateBracDetectionState[] h = hdb.getAllHistory();
			if (h != null)
				for (int i = 0; i < h.length; ++i)
					historys.add(h[i]);

			to_cal = Calendar.getInstance();
			if (from_cal.before(to_cal)) {
				long millis = to_cal.getTimeInMillis() - from_cal.getTimeInMillis();
				NUM_OF_BARS = (int) (millis / AlarmManager.INTERVAL_DAY) + 1;
			} else
				NUM_OF_BARS = 0;

			initView();
			recordBox.setImage();
			endAnimation();

			startAnim();
			LoadingDialogControl.dismiss();

			if (received_msg == NotificationBox.TYPE_STORYTELLING_SHARING) {
				shareAnimation = new AlphaAnimation(1.0F, 0.0F);
				shareAnimation.setDuration(200);
				shareAnimation.setRepeatCount(Animation.INFINITE);
				shareAnimation.setRepeatMode(Animation.REVERSE);
				storytellingButton.setAnimation(shareAnimation);
				received_msg = 0;
			}

			RelativeLayout r = (RelativeLayout) view;
			quoteMsgBox = new QuoteMsgBox(historyFragment.getActivity(),historyFragment, r);

		}
	}

	private void startAnim() {

		if (page_week == 0) {
			resetPage(0);
			return;
		}
		isAnimation = true;
		FragmentTabs.enableTabAndClick(false);
		int[] aBgs = HistoryStorytelling.getAnimationBgs(page_states);
		int startIdx = page_week - 1;
		setStageVisible(false);
		pageAnimationTask = new PageAnimationTaskVertical(pageWidget, from, to, aBgs, historyFragment, startIdx);
		if (Build.VERSION.SDK_INT >= 11)
			pageAnimationTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, (Void[]) null);
		else
			pageAnimationTask.execute();
	}

	public void setStageVisible(boolean visible) {
		if (visible) {
			storytellingButton.setVisibility(View.VISIBLE);
			stageMessageText.setVisibility(View.VISIBLE);
			stageMessage.setVisibility(View.VISIBLE);
			stageRateText.setVisibility(View.VISIBLE);
			quoteText.setVisibility(View.VISIBLE);
			fbButton.setVisibility(View.VISIBLE);
		} else {
			storytellingButton.setVisibility(View.INVISIBLE);
			stageMessageText.setVisibility(View.INVISIBLE);
			stageMessage.setVisibility(View.INVISIBLE);
			stageRateText.setVisibility(View.INVISIBLE);
			quoteText.setVisibility(View.INVISIBLE);
			fbButton.setVisibility(View.INVISIBLE);
		}
	}

	private class PageTouchListener implements View.OnTouchListener {
		@Override
		public boolean onTouch(View v, MotionEvent event) {
			return gDetector.onTouchEvent(event);
		}
	}

	private void quoteScroll(int next_page) {
		hideSpecialQuote();
		Message msg = new Message();
		msg.what = 0;
		Bundle data = new Bundle();
		data.putInt("time", next_page);
		msg.setData(data);
		quoteScrollHandler.sendMessageDelayed(msg, READING_PAGE_TIME);
	}

	private class GestureListener extends GestureDetector.SimpleOnGestureListener {

		@Override
		public boolean onDown(MotionEvent e1) {
			return true;
		}

		@Override
		public boolean onScroll(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
			return true;
		}

		@Override
		public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
			final int FLING_THRESHOLD = 5;
			if (Math.abs(velocityX) > 1.2 * Math.abs(velocityY))
				return true;
			if (isAnimation)
				return true;

			float y1 = e1.getY();
			float y2 = e2.getY();
			if (y1 - y2 > FLING_THRESHOLD) {// UP
				int[] aBgs = HistoryStorytelling.getAnimationBgs(page_states);
				int pageIdx = page_week;
				if (pageIdx == max_week) {
					isAnimation = false;
					pageWidget.setOnTouchListener(gtListener);
					FragmentTabs.enableTabAndClick(true);
					return true;
				} else {
					isAnimation = true;
					pageWidget.setOnTouchListener(null);
					FragmentTabs.enableTabAndClick(false);
				}
				quoteScroll(page_week + 1);
				ClickLogger.Log(getActivity(), ClickLogId.STORYTELLING_FLING_UP);
				setStageVisible(false);
				pageAnimationTask2 = new PageAnimationTaskVerticalFling(pageWidget, from, to, aBgs, historyFragment,
						pageIdx, 1);
				if (Build.VERSION.SDK_INT >= 11)
					pageAnimationTask2.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, (Void[]) null);
				else
					pageAnimationTask2.execute();
			} else if (y2 - y1 > FLING_THRESHOLD) {// DOWN
				int[] aBgs = HistoryStorytelling.getAnimationBgs(page_states);
				int pageIdx = page_week;
				if (pageIdx == 0) {
					isAnimation = false;
					pageWidget.setOnTouchListener(gtListener);
					FragmentTabs.enableTabAndClick(true);
					return true;
				} else {
					isAnimation = true;
					pageWidget.setOnTouchListener(null);
					FragmentTabs.enableTabAndClick(false);
				}
				quoteScroll(page_week - 1);
				ClickLogger.Log(getActivity(), ClickLogId.STORYTELLING_FLING_DOWN);
				setStageVisible(false);
				pageAnimationTask2 = new PageAnimationTaskVerticalFling(pageWidget, from, to, aBgs, historyFragment,
						pageIdx, 0);
				if (Build.VERSION.SDK_INT >= 11)
					pageAnimationTask2.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, (Void[]) null);
				else
					pageAnimationTask2.execute();
			}
			return true;
		}
	}

	private static final long DAY_MILLIS = AlarmManager.INTERVAL_DAY;

	public void settingBars() {

		bars.clear();

		if (NUM_OF_BARS == 0)
			return;

		long from_t = from_cal.getTimeInMillis();
		Calendar ccal = Calendar.getInstance();
		ccal.setTimeInMillis(from_cal.getTimeInMillis());

		for (int i = 0; i < NUM_OF_BARS; ++i) {

			int count = 0;
			int q_count = 0;
			float e_sum = 0;
			float d_sum = 0;
			float b_sum = 0;
			boolean drink = false;

			float emotion, desire, brac;

			int pos = 0;

			int bar_week = -1;

			for (int j = pos; j < historys.size(); ++j) {
				DateBracDetectionState h = historys.get(j);
				if (h.timestamp >= from_t && h.timestamp < from_t + DAY_MILLIS) {
					e_sum += (h.emotion > 0) ? h.emotion : 0;
					d_sum += (h.desire > 0) ? h.desire : 0;
					if (h.emotion > 0)
						++q_count;
					b_sum += h.brac;
					if (!(h.brac < BracDataHandler.THRESHOLD))
						drink = true;
					++count;
					bar_week = h.week;
				} else if (h.timestamp >= from_t + DAY_MILLIS) {
					pos = j;
					break;
				}
			}

			boolean hasData = true;
			if (count == 0) {
				hasData = false;
				brac = 0F;
				bar_week = WeekNum.getWeek(this.getActivity(), from_t);
			} else {
				brac = b_sum / count;
			}
			if (q_count == 0) {
				emotion = desire = 0F;
			} else {
				emotion = e_sum / q_count;
				desire = d_sum / q_count;
			}

			int mYear = ccal.get(Calendar.YEAR);
			int mMonth = ccal.get(Calendar.MONTH);
			int mDate = ccal.get(Calendar.DAY_OF_MONTH);

			DateValue dv = new DateValue(mYear, mMonth, mDate);

			BarInfo barInfo = new BarInfo(emotion, desire, brac, bar_week, hasData, dv, drink);

			bars.add(barInfo);

			from_t += DAY_MILLIS;
			ccal.add(Calendar.DATE, 1);
		}

	}

	private int chart_type = 0;

	private class ChartTitleView extends View {

		private Paint text_paint_large = new Paint();
		private Paint text_paint_large_2 = new Paint();
		private String[] title_str = new String[4];

		public ChartTitleView(Context context) {
			super(context);
			text_paint_large.setColor(0xFFAAAAAA);
			int textSize = TextSize.normalTextSize(activity);
			text_paint_large.setTextSize(textSize);
			text_paint_large.setTextAlign(Align.LEFT);
			text_paint_large.setTypeface(wordTypefaceBold);
			text_paint_large_2.setColor(0xFFf09600);
			text_paint_large_2.setTextSize(textSize);
			text_paint_large_2.setTextAlign(Align.LEFT);
			text_paint_large_2.setTypeface(wordTypefaceBold);
			title_str[0] = getResources().getString(R.string.emotion);
			title_str[1] = getResources().getString(R.string.craving);
			title_str[2] = getResources().getString(R.string.brac_result);
			title_str[3] = getResources().getString(R.string.total_result);
		}

		@SuppressWarnings("deprecation")
		@Override
		public boolean onTouchEvent(MotionEvent event) {
			if (!chartTouchable)
				return true;
			int x = (int) event.getX();

			if (event.getAction() == MotionEvent.ACTION_DOWN) {
				if (x < screen.x * 310 / 1080) {
					chart_type = 0;
					if (Build.VERSION.SDK_INT >= 16)
						chartAreaLayout.setBackground(chartBg1Drawable);
					else
						chartAreaLayout.setBackgroundDrawable(chartBg1Drawable);
					ClickLogger.Log(getActivity(), ClickLogId.STORYTELLING_CHART_TYPE0);
				} else if (x < screen.x * 590 / 1080) {
					chart_type = 1;
					if (Build.VERSION.SDK_INT >= 16)
						chartAreaLayout.setBackground(chartBg2Drawable);
					else
						chartAreaLayout.setBackgroundDrawable(chartBg2Drawable);
					ClickLogger.Log(getActivity(), ClickLogId.STORYTELLING_CHART_TYPE1);
				} else if (x < screen.x * 870 / 1080) {
					chart_type = 2;
					if (Build.VERSION.SDK_INT >= 16)
						chartAreaLayout.setBackground(chartBg3Drawable);
					else
						chartAreaLayout.setBackgroundDrawable(chartBg3Drawable);
					ClickLogger.Log(getActivity(), ClickLogId.STORYTELLING_CHART_TYPE2);
				} else {
					chart_type = 3;
					if (Build.VERSION.SDK_INT >= 16)
						chartAreaLayout.setBackground(chartBg4Drawable);
					else
						chartAreaLayout.setBackgroundDrawable(chartBg4Drawable);
					ClickLogger.Log(getActivity(), ClickLogId.STORYTELLING_CHART_TYPE3);
				}

				invalidate();
				chart.setChartType(chart_type);
				chartYAxis.invalidate();
				chartLabel.invalidate();
			}
			return true;
		}

		@Override
		protected void onDraw(Canvas canvas) {
			super.onDraw(canvas);
			canvas.drawText(title_str[0], screen.x * 30F / 1080F, screen.x * 70F / 1080F, text_paint_large);
			canvas.drawText(title_str[1], screen.x * 310F / 1080F, screen.x * 70F / 1080F, text_paint_large);
			canvas.drawText(title_str[2], screen.x * 590F / 1080F, screen.x * 70F / 1080F, text_paint_large);
			canvas.drawText(title_str[3], screen.x * 870F / 1080F, screen.x * 70F / 1080F, text_paint_large);
			switch (chart_type) {
			case 0:
				canvas.drawText(title_str[0], screen.x * 30F / 1080F, screen.x * 70F / 1080F, text_paint_large_2);
				break;
			case 1:
				canvas.drawText(title_str[1], screen.x * 310F / 1080F, screen.x * 70F / 1080F, text_paint_large_2);
				break;
			case 2:
				canvas.drawText(title_str[2], screen.x * 590F / 1080F, screen.x * 70F / 1080F, text_paint_large_2);
				break;
			case 3:
				canvas.drawText(title_str[3], screen.x * 870F / 1080F, screen.x * 70F / 1080F, text_paint_large_2);
				break;
			}
		}
	}

	private class ChartYAxisView extends View {

		private Paint axis_paint = new Paint();
		private Paint text_paint_small = new Paint();
		private String high;

		public ChartYAxisView(Context context) {
			super(context);
			int textSize = TextSize.mTextSize(context);
			text_paint_small.setColor(0xFF3c3b3b);
			text_paint_small.setTextAlign(Align.CENTER);
			text_paint_small.setTextSize(textSize);
			text_paint_small.setTypeface(digitTypeface);
			axis_paint.setColor(0xFF000000);
			axis_paint.setStrokeWidth(screen.x * 7 / 1080);
			high = getResources().getString(R.string.high);
		}

		@Override
		protected void onDraw(Canvas canvas) {
			super.onDraw(canvas);

			int bar_width = screen.x * 36 / 1080;
			
			int bar_bottom = screen.x * 90 / 1080;
			int max_height = (chartHeight - bar_bottom) * 4 / 10;
			int _bottom = chartHeight - bar_bottom;

			// Draw Y axis label
			canvas.drawText("0", 3 * bar_width / 2, _bottom, text_paint_small);
			String maxLabel;
			if (chart_type == 0)
				maxLabel = "5";
			else if (chart_type == 1)
				maxLabel = "10";
			else if (chart_type == 2)
				maxLabel = "0.5";
			else
				maxLabel = high;
			canvas.drawText(maxLabel, 3 * bar_width / 2, _bottom - max_height, text_paint_small);
		}
	}

	private class ChartLabelView extends View {

		private Paint emotion_paint = new Paint();
		private Paint desire_paint = new Paint();
		private Paint brac_paint = new Paint();
		private Paint text_paint = new Paint();
		private Paint paint_pass = new Paint();
		private Paint paint_fail = new Paint();

		private String[] type_str = new String[3];
		private String[] pass_str = new String[2];

		public ChartLabelView(Context context) {
			super(context);
			text_paint.setColor(0xFFAAAAAA);
			text_paint.setTextAlign(Align.CENTER);
			text_paint.setTextSize(TextSize.mTextSize(context));
			text_paint.setTypeface(wordTypefaceBold);

			emotion_paint.setColor(0xFF2dc7b3);
			desire_paint.setColor(0xFFf19700);
			brac_paint.setColor(0xFFFFFFFF);

			paint_pass.setColor(0xFF5bdebe);
			paint_fail.setColor(0xFFf09600);

			type_str[0] = getResources().getString(R.string.emotion_short);
			type_str[1] = getResources().getString(R.string.craving_short);
			type_str[2] = getResources().getString(R.string.brac_result_short);
			pass_str[0] = getResources().getString(R.string.test_pass);
			pass_str[1] = getResources().getString(R.string.test_fail);
		}

		@Override
		protected void onDraw(Canvas canvas) {
			super.onDraw(canvas);
			int base = screen.x * 44 / 1080;
			int gap = screen.x * 10 / 1080;
			int top = screen.x * 20 / 1080;
			int top2 = screen.x * 55 / 1080;
			int from = 0;
			int line_len = base * 5 / 2;
			if (chart_type == 3) {
				canvas.drawRect(from, top, from + base, base + top, emotion_paint);
				from += base + gap;
				canvas.drawText(type_str[0], from + line_len / 2, top2, text_paint);
				from += line_len + gap;

				canvas.drawRect(from, top, from + base, base + top, desire_paint);
				from += base + gap;
				canvas.drawText(type_str[1], from + line_len / 2, top2, text_paint);
				from += line_len + gap;

				canvas.drawRect(from, top, from + base, base + top, brac_paint);
				from += base + gap;
				canvas.drawText(type_str[2], from + line_len * 3 / 4, top2, text_paint);
			} else {
				// only two labels
				from += base + gap;
				from += line_len + gap;

				canvas.drawRect(from, top, from + base, base + top, paint_pass);
				from += base + gap;
				canvas.drawText(pass_str[0], from + line_len / 2, top2, text_paint);
				from += line_len + gap;

				canvas.drawRect(from, top, from + base, base + top, paint_fail);
				from += base + gap;
				canvas.drawText(pass_str[1], from + line_len * 3 / 4, top2, text_paint);
			}
		}
	}

	@Override
	public void enablePage(boolean enable) {
		chartTouchable = enable;
		if (pageWidget != null)
			pageWidget.setEnabled(enable);
		if (scrollView != null)
			scrollView.setEnabled(enable);
		if (chart != null)
			chart.setEnabled(enable);
		FragmentTabs.enableTabAndClick(enable);
		storytellingButton.setEnabled(enable);
		fbButton.setEnabled(enable);
	}

	private void checkHasRecorder() {
		hasAudio.clear();
		for (int i = 0; i < bars.size(); ++i) {
			if (adb.hasAudio(bars.get(i).dv))
				hasAudio.add(true);
			else
				hasAudio.add(false);
		}
		if (chart != null)
			chart.invalidate();
	}

	public void updateHasRecorder(int idx) {
		if (idx >= 0 && idx < bars.size())
			hasAudio.set(idx, adb.hasAudio(bars.get(idx).dv));
	}

	private class StorytellingOnClickListener implements View.OnClickListener {
		@Override
		public void onClick(View v) {
			ClickLogger.Log(getActivity(), ClickLogId.STORYTELLING_SHARE_BUTTON);
			storytellingBox.showMsgBox();
			if (storytellingButton != null) {
				if (Build.VERSION.SDK_INT >= 8 && shareAnimation != null)
					shareAnimation.cancel();
				storytellingButton.setAnimation(null);
			}
		}
	}

	private class QuoteScrollListener implements View.OnTouchListener {
		private boolean enable = true;

		public void setEnable(final boolean enable) {
			this.enable = enable;
		}

		@Override
		public boolean onTouch(View v, MotionEvent event) {
			if (enable)
				return gDetector.onTouchEvent(event);
			else
				return true;
		}
	}

	@SuppressLint("HandlerLeak")
	private class QuoteScrollHandler extends Handler {
		public void handleMessage(Message msg) {
			int time = msg.getData().getInt("time");
			if (time == page_week) {
				addLongFlingTime(getActivity());
				int limit = LONG_FLING_LIMIT;
				if (addDb.getLatestStorytellingFling().ts == 0)
					limit /= 2;
				int cur_time = getLongFlingTime(getActivity());
				if (cur_time >= limit) {
					infiniteThread = new InfiniteScroll();
					infiniteThread.start();
					resetLongFlingTime(getActivity());

					View.OnClickListener listener = new QuoteOnClickListener(page_week);
					quoteHiddenLayout.setOnClickListener(listener);
				}
			}
		}
	}

	private class InfiniteScroll extends Thread {
		@Override
		public void run() {
			while (true) {
				scrollHandler.removeMessages(0);
				scrollHandler.removeMessages(1);
				scrollHandler.sendEmptyMessage(1);
				if (isInterrupted())
					break;
				try {
					Thread.sleep(5000);
				} catch (InterruptedException e) {
					break;
				}
				if (isInterrupted())
					break;
				scrollHandler.removeMessages(0);
				scrollHandler.removeMessages(1);
				scrollHandler.sendEmptyMessage(0);
				if (isInterrupted())
					break;
				try {
					Thread.sleep(3000);
				} catch (InterruptedException e) {
					break;
				}
				if (isInterrupted())
					break;
			}
		}
	}

	@SuppressLint("HandlerLeak")
	private class ScrollHandler extends Handler {
		public void handleMessage(Message msg) {
			if (msg.what == 0) {// up
				quoteScrollView.smoothScrollTo(0, 0);
				quoteScrollListener.setEnable(true);
			} else if (msg.what == 1) {// down
				quoteScrollView.smoothScrollTo(0, quoteScrollView.getBottom());
				quoteScrollListener.setEnable(false);
			}
		}
	}

	private void addLongFlingTime(Context context) {
		SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
		int time = sp.getInt("LongFlingTime", 0);
		if (time < LONG_FLING_LIMIT) {
			SharedPreferences.Editor edit = sp.edit();
			edit.putInt("LongFlingTime", (time + 1));
			edit.commit();
		}
	}

	private int getLongFlingTime(Context context) {
		SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
		int time = sp.getInt("LongFlingTime", 0);
		return time;
	}

	private void resetLongFlingTime(Context context) {

		SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
		SharedPreferences.Editor edit = sp.edit();
		edit.putInt("LongFlingTime", 0);
		edit.commit();
	}

	private class QuoteOnClickListener implements View.OnClickListener {

		private int page;

		public QuoteOnClickListener(int page) {
			this.page = page;
		}

		@Override
		public void onClick(View v) {
			hideSpecialQuote();
			quoteMsgBox.openBox(page);
			ClickLogger.Log(getActivity(), ClickLogId.STORYTELLING_QUOTE_CLICK);
		}

	}

	private void hideSpecialQuote() {
		quoteScrollHandler.removeMessages(0);
		if (infiniteThread != null && !infiniteThread.isInterrupted()) {
			infiniteThread.interrupt();
			try {
				infiniteThread.join();
			} catch (InterruptedException e) {
			} finally {
				infiniteThread = null;
			}
		}
		if (scrollHandler != null) {
			scrollHandler.removeMessages(0);
			scrollHandler.removeMessages(1);
		}
		quoteScrollView.scrollTo(0, 0);
		quoteHiddenLayout.setOnClickListener(null);
	}
	
	@Override
	public void showRecordBox(DateValue tdv,int selected_button){
		recordBox.showRecordBox(tdv, selected_button);
	}
}
