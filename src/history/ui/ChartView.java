package history.ui;

import java.util.ArrayList;

import ubicomp.drunk_detection.activities.R;
import ubicomp.drunk_detection.fragments.HistoryFragment;
import ubicomp.drunk_detection.ui.ScreenSize;
import ubicomp.drunk_detection.ui.TextSize;
import data.info.BarInfo;
import data.info.DateValue;
import debug.clicklog.ClickLogId;
import debug.clicklog.ClickLogger;
import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.graphics.Paint.Align;
import android.graphics.Paint.Style;
import android.view.MotionEvent;
import android.view.View;
import android.widget.HorizontalScrollView;

public class ChartView extends View {

	private Paint paint_pass = new Paint();
	private Paint paint_fail = new Paint();
	private Paint paint_none = new Paint();

	private Paint paint_highlight = new Paint();
	private Paint circle_paint_stroke = new Paint();
	private Paint text_paint_large = new Paint();
	private Paint text_paint_small = new Paint();
	private Paint text_paint_button = new Paint();
	private Paint focus_paint_len = new Paint();
	private Paint line_paint = new Paint();
	private Paint axis_paint = new Paint();
	private Paint record_paint = new Paint();
	private Paint no_record_paint = new Paint();

	private Paint emotion_paint = new Paint();
	private Paint desire_paint = new Paint();
	private Paint brac_paint = new Paint();

	private Paint emotion_paint_bg = new Paint();
	private Paint desire_paint_bg = new Paint();
	private Paint brac_paint_bg = new Paint();

	private int RADIUS;
	private int RADIUS_SQUARE;
	private int BUTTON_RADIUS;
	private int BUTTON_RADIUS_SQUARE;
	private int BUTTON_GAPS;

	private int curX = -1, curY = -1;

	private ArrayList<Point> circle_centers = new ArrayList<Point>();
	private ArrayList<Point> selected_centers = new ArrayList<Point>();
	private ArrayList<Point> button_centers = new ArrayList<Point>();

	private float top_touch;

	private int bar_width, bar_gap, 	chart_width, circle_radius, bar_bottom, bar_left;
	
	private static Bitmap chartCircleBmp, chartPlayBmp;
	
	private ArrayList<DateValue> selected_dates= new ArrayList<DateValue>();;
	private ArrayList<Integer> selected_idx =new ArrayList<Integer>();;
	private ArrayList<BarInfo> bars;
	private ArrayList<Boolean> hasAudio;
	
	private int chartType = 0;
	private int pageWeek = 0;
	
	private Point screen;
	
	private int pageHeight = 0;
	
	private boolean onButton = false;
	private int buttonNum = -1;
	private HorizontalScrollView scrollView;
	private HistoryFragment fragment;
	
	public ChartView(Context context,ArrayList<BarInfo> bars,int pageWeek,ArrayList<Boolean> hasAudio,int pageHeight, HorizontalScrollView scrollView,HistoryFragment fragment) {
		super(context);

		this.fragment = fragment;
		screen = ScreenSize.getScreenSize(getContext());
		
		if (chartCircleBmp == null || chartCircleBmp.isRecycled())
			chartCircleBmp = BitmapFactory.decodeResource(getResources(), R.drawable.chart_circle);
		if (chartPlayBmp == null || chartPlayBmp.isRecycled())
			chartPlayBmp = BitmapFactory.decodeResource(getResources(), R.drawable.chart_play);
		
		paint_pass.setColor(0xFF5bdebe);
		paint_fail.setColor(0xFFf09600);
		paint_none.setColor(0xFFc9c9ca);

		record_paint.setColor(0xFFff6f61);
		no_record_paint.setColor(0xFF858585);

		paint_highlight.setColor(0x33FFFFFF);

		circle_paint_stroke.setColor(0xFFFF0000);
		circle_paint_stroke.setStyle(Style.STROKE);
		circle_paint_stroke.setStrokeWidth(screen.x * 7 / 1080);

		int smallTextSize = TextSize.smallTextSize(context);

		text_paint_large.setColor(0xFFFFFFFF);
		text_paint_large.setTextSize(smallTextSize);
		text_paint_large.setTextAlign(Align.LEFT);

		text_paint_button.setColor(0xFFFFFFFF);
		text_paint_button.setTextSize(smallTextSize);
		text_paint_button.setTextAlign(Align.CENTER);

		text_paint_small.setColor(0xFF908f90);
		text_paint_small.setTextAlign(Align.CENTER);
		text_paint_small.setTextSize(TextSize.mTextSize(context));

		focus_paint_len.setColor(0x44FFFFFF);

		axis_paint.setColor(0xFF5f5f5f);
		axis_paint.setStrokeWidth(screen.x * 8 / 1080);

		line_paint.setColor(0xFFFFFFFF);
		line_paint.setStrokeWidth(screen.x * 6 / 1080);

		emotion_paint.setColor(0xFF2dc7b3);
		emotion_paint.setStrokeWidth(screen.x * 4 / 480);
		desire_paint.setColor(0xFFf19700);
		desire_paint.setStrokeWidth(screen.x * 4 / 480);
		brac_paint.setColor(0xFFFFFFFF);
		brac_paint.setStrokeWidth(screen.x * 4 / 480);

		emotion_paint_bg.setColor(0x772dc7b3);
		desire_paint_bg.setColor(0x77f19700);
		brac_paint_bg.setColor(0x77FFFFFF);

		

		bar_width = screen.x * 36 / 1080;
		bar_gap = screen.x * 12 / 1080;
		chart_width = screen.x;
		circle_radius = bar_width / 3;
		bar_bottom = screen.x * 90 / 1080;
		bar_left = screen.x * 94 / 1080;

		RADIUS = bar_width * 9 / 5;
		RADIUS_SQUARE = RADIUS * RADIUS;
		BUTTON_RADIUS = chartCircleBmp.getWidth() / 2;
		BUTTON_RADIUS_SQUARE = BUTTON_RADIUS * BUTTON_RADIUS;
		BUTTON_GAPS = BUTTON_RADIUS * 9 / 2;

		top_touch = screen.x * 180 / 1080;
		
		this.chartType = 0;
		this.bars = bars;
		this.pageWeek = pageWeek;
		this.hasAudio = hasAudio;
		this.pageHeight = pageHeight;
		this.scrollView = scrollView;
	}

	

	@Override
	public boolean onTouchEvent(MotionEvent event) {

		if (!isEnabled())
			return true;

		int action = event.getAction();
		if (action == MotionEvent.ACTION_DOWN) {
			int x = (int) event.getX();
			int y = (int) event.getY();

			onButton = false;
			buttonNum = -1;
			for (int i = 0; i < button_centers.size(); ++i) {
				Point c = button_centers.get(i);
				int distance_square = (c.x - x) * (c.x - x) + (c.y - y) * (c.y - y);
				if (distance_square < BUTTON_RADIUS_SQUARE * 2.25F) {
					onButton = true;
					buttonNum = i;
					break;
				}
			}
			if (!onButton) {
				float ty = event.getY();
				if (ty >= top_touch) {
					curX = (int) event.getX();
					curY = (int) event.getY();
					ClickLogger.Log(getContext(), ClickLogId.STORYTELLING_CHART_TOUCH);
				}
			}
		} else if (action == MotionEvent.ACTION_UP && onButton && buttonNum >= 0
				&& buttonNum < selected_dates.size()) {
			int x = (int) event.getX();
			int y = (int) event.getY();

			Point c = button_centers.get(buttonNum);
			int distance_square = (c.x - x) * (c.x - x) + (c.y - y) * (c.y - y);
			if (distance_square < BUTTON_RADIUS_SQUARE * 2.25F) {
				DateValue tdv = selected_dates.get(buttonNum);
				ClickLogger.Log(getContext(), ClickLogId.STORYTELLING_CHART_BUTTON + tdv.toClickValue());
				fragment.showRecordBox(tdv,selected_idx.get(buttonNum));
			}
			onButton = false;
			buttonNum = -1;
		}
		invalidate();
		return true;
	}

	@SuppressLint("DrawAllocation")
	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);

		circle_centers.clear();
		selected_centers.clear();
		selected_idx.clear();
		button_centers.clear();
		selected_dates.clear();

		if (chartType < 3) {
			drawBarChart(canvas);
			drawButtons(canvas);
		} else
			drawLineChart(canvas);
	}

	private void drawLineChart(Canvas canvas) {
		int max_height = (pageHeight - bar_bottom) * 4 / 10;
		int left = bar_left;
		int small_radius = circle_radius / 2;

		int _bottom = pageHeight - bar_bottom;

		canvas.drawLine(left, _bottom, chart_width, _bottom, emotion_paint);

		if (bars.size() == 0)
			return;

		Point prev_e_center = null;
		Point prev_d_center = null;
		Point prev_b_center = null;

		for (int i = 0; i < bars.size(); ++i) {
			BarInfo bar = bars.get(i);

			float e_height, d_height, b_height;
			e_height = bar.emotion / 5 * max_height;
			d_height = bar.desire / 10 * max_height;
			b_height = bar.brac / 0.3F * max_height;
			if (b_height > max_height)
				b_height = max_height;

			int e_top = _bottom - (int) e_height;
			int d_top = _bottom - (int) d_height;
			int b_top = _bottom - (int) b_height;
			if (!bar.hasData)
				e_top = d_top = b_top = _bottom;

			// Draw X axis Label
			if (i % 7 == 0) {
				String str = (bar.dv.month + 1) + "/" + bar.dv.date;
				canvas.drawLine(left, _bottom, left, _bottom - max_height, axis_paint);
				canvas.drawText(str, left + small_radius, _bottom + bar_width * 2, text_paint_small);
			}
			// Draw bars & annotation_circles
			Point e_center = new Point(left + small_radius, e_top - bar_gap - small_radius);
			Point d_center = new Point(left + small_radius, d_top - bar_gap - small_radius);
			Point b_center = new Point(left + small_radius, b_top - bar_gap - small_radius);

			if (prev_e_center != null && prev_d_center != null && prev_b_center != null) {

				Path path_e = new Path();
				path_e.moveTo(prev_e_center.x, _bottom);
				path_e.lineTo(prev_e_center.x, prev_e_center.y);
				path_e.lineTo(e_center.x, e_center.y);
				path_e.lineTo(e_center.x, _bottom);

				Path path_d = new Path();
				path_d.moveTo(prev_d_center.x, _bottom);
				path_d.lineTo(prev_d_center.x, prev_d_center.y);
				path_d.lineTo(d_center.x, d_center.y);
				path_d.lineTo(d_center.x, _bottom);
				path_d.lineTo(prev_d_center.x, _bottom);

				Path path_b = new Path();
				path_b.moveTo(prev_d_center.x, _bottom);
				path_b.lineTo(prev_b_center.x, prev_b_center.y);
				path_b.lineTo(b_center.x, b_center.y);
				path_b.lineTo(b_center.x, _bottom);
				path_b.lineTo(prev_b_center.x, _bottom);

				canvas.drawPath(path_e, emotion_paint_bg);
				canvas.drawPath(path_d, desire_paint_bg);
				canvas.drawPath(path_b, brac_paint_bg);

				canvas.drawLine(prev_e_center.x, prev_e_center.y, e_center.x, e_center.y, emotion_paint);
				canvas.drawLine(prev_d_center.x, prev_d_center.y, d_center.x, d_center.y, desire_paint);
				canvas.drawLine(prev_b_center.x, prev_b_center.y, b_center.x, b_center.y, brac_paint);

			} else {
				canvas.drawLine(e_center.x, _bottom, e_center.x, e_center.y, emotion_paint);
				canvas.drawLine(d_center.x, _bottom, d_center.x, d_center.y, desire_paint);
				canvas.drawLine(b_center.x, _bottom, b_center.x, b_center.y, brac_paint);
			}
			prev_e_center = e_center;
			prev_d_center = d_center;
			prev_b_center = b_center;

			// draw highlights
			if (bar.week == pageWeek)
				canvas.drawRect(left, _bottom - max_height - bar_width - circle_radius, left + bar_width + bar_gap,
						_bottom, paint_highlight);

			if (i == bars.size() - 1) {
				canvas.drawLine(e_center.x, _bottom, e_center.x, e_center.y, emotion_paint);
				canvas.drawLine(d_center.x, _bottom, d_center.x, d_center.y, desire_paint);
				canvas.drawLine(b_center.x, _bottom, b_center.x, b_center.y, brac_paint);
			}
			left += (bar_width + bar_gap);
		}

		if (curX > 0 && curY > 0)
			canvas.drawCircle(curX, curY, RADIUS, focus_paint_len);
	}

	private void drawBarChart(Canvas canvas) {
		int max_height = (pageHeight - bar_bottom) * 4 / 10;
		int left = bar_left;

		if (bars.size() == 0)
			return;

		int playW = chartPlayBmp.getWidth() / 2;
		int playH = chartPlayBmp.getHeight() / 2;

		int bar_half = bar_width / 2;
		for (int i = 0; i < bars.size(); ++i) {

			float height = 0;
			BarInfo bar = bars.get(i);

			if (chartType == 0)
				height = bar.emotion / 5 * max_height;
			else if (chartType == 1)
				height = bar.desire / 10 * max_height;
			else if (chartType == 2) {
				height = bar.brac / 0.3F * max_height;
				if (height > max_height)
					height = max_height;
			}

			// Draw bars & annotation_circles & highlights
			int right = left + bar_width;
			int _bottom = pageHeight - bar_bottom;
			int _top = _bottom - (int) height;

			// Draw bars & annotation_circles
			Point center = new Point(left + bar_half, _top - bar_gap - circle_radius);

			boolean hasAudioData = hasAudio.get(i);
			;
			if (!hasAudioData)
				canvas.drawCircle(center.x, center.y, circle_radius, no_record_paint);
			else
				canvas.drawBitmap(chartPlayBmp, center.x - playW, center.y - playH, null);

			if (!bar.hasData)
				;
			else if (bar.drink)
				canvas.drawRect(left, _top, right, _bottom, paint_fail);
			else
				canvas.drawRect(left, _top, right, _bottom, paint_pass);

			circle_centers.add(center);

			// draw highlights
			if (bar.week == pageWeek)
				canvas.drawRect(left, _bottom - max_height - bar_width - circle_radius, right + bar_gap, _bottom,
						paint_highlight);

			// Draw X axis Label
			if (i % 7 == 0) {
				String str = (bar.dv.month + 1) + "/" + bar.dv.date;
				canvas.drawText(str, left + circle_radius, _bottom + bar_width * 2, text_paint_small);
			}
			left += (bar_width + bar_gap);
		}
	}

	private void drawButtons(Canvas canvas) {
		// Draw buttons
		if (curX > 0 && curY > 0) {

			// Draw focus area
			canvas.drawCircle(curX, curY, RADIUS, focus_paint_len);

			for (int i = 0; i < circle_centers.size(); ++i) {
				Point c = circle_centers.get(i);
				int distance_square = (curX - c.x) * (curX - c.x) + (curY - c.y) * (curY - c.y);
				if (distance_square < RADIUS_SQUARE) {
					DateValue d = bars.get(i).dv;
					selected_centers.add(c);
					selected_dates.add(d);
					selected_idx.add(i);
				}
			}

			int b_center_x = screen.x * 100 / 1080 + scrollView.getScrollX();
			int b_center_y = screen.x * 170 / 1080;
			int b_center_x_bak = b_center_x;
			// Draw lines
			for (int i = 0; i < selected_centers.size(); ++i) {
				Point from = selected_centers.get(i);
				Point to = new Point(b_center_x, b_center_y);
				button_centers.add(to);
				canvas.drawLine(from.x, from.y, to.x, to.y, line_paint);
				b_center_x += BUTTON_GAPS;
			}
			// Draw buttons
			b_center_x = b_center_x_bak;
			for (int i = 0; i < selected_centers.size(); ++i) {
				Point to = new Point(b_center_x, b_center_y);
				
				canvas.drawBitmap(chartCircleBmp, to.x - BUTTON_RADIUS, to.y - BUTTON_RADIUS, null);
				DateValue d = selected_dates.get(i);
				String str = (d.month + 1) + "/" + d.date;
				canvas.drawText(str, to.x, to.y + BUTTON_RADIUS / 3, text_paint_button);
				b_center_x += BUTTON_GAPS;
			}
		}
	}
	
	public void setChartType(int chartType){
		this.chartType = chartType;
		invalidate();
	}
	
	public void setPageWeek(int pageWeek){
		this.pageWeek = pageWeek;
		invalidate();
	}
	public int getChartWidth(){
		return bar_left * 3 / 2 + (bar_width + bar_gap) * bars.size();
	}
	public int getScrollValue(int page_week){
		return bar_left + (screen.x * 48 / 1080) * 7 * (page_week - 1);
	}
}