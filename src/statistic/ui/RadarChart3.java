package statistic.ui;

import java.util.ArrayList;

import ubicomp.drunk_detection.activities.R;
import ubicomp.drunk_detection.ui.TextSize;
import ubicomp.drunk_detection.ui.Typefaces;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.Align;
import android.graphics.Paint.Style;
import android.graphics.Path;
import android.graphics.PointF;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.view.View;

@SuppressLint("ViewConstructor")
public class RadarChart3 extends View {

	private ArrayList<Double> scoreList;
	private ArrayList<String> labelList;
	private String title;
	private PointF topCorner,leftCorner,rightCorner, center;
	private PointF p0,p1,p2;
	
	private Paint chartLine, valueLine, valuePaint, titlePaint, labelPaint;
	
	private Typeface wordTypeface,wordTypefaceBold;
	private int titleSize,labelSize;
	
	public RadarChart3(Context context,ArrayList<Double> scoreList,ArrayList<String> labelList,String title) {
		super(context);
		this.scoreList = scoreList;
		this.labelList = labelList;
		this.title = title;
		this.setBackgroundResource(R.drawable.toast_small);
		wordTypefaceBold = Typefaces.getWordTypefaceBold(context);
		wordTypeface = Typefaces.getWordTypeface(context);
		titleSize = TextSize.largeTitleSize(context);
		labelSize = TextSize.normalTextSize(context);
		createPaints();
	}
	
	private void createPaints(){
		chartLine = new Paint();
		chartLine.setColor(0xFFababab);
		chartLine.setStyle(Style.STROKE);
		chartLine.setStrokeWidth(2);
		
		valueLine = new Paint();
		valueLine.setColor(0xFFF19700);
		valueLine.setStyle(Style.STROKE);
		valueLine.setStrokeWidth(3);
		
		valuePaint = new Paint();
		valuePaint.setColor(0xFFF19700);
		valuePaint.setStyle(Style.FILL);
		valuePaint.setAlpha(100);
		
		titlePaint = new Paint();
		titlePaint.setColor(0xFF000000);
		titlePaint.setTextSize(titleSize);
		titlePaint.setTypeface(wordTypefaceBold);
		titlePaint.setTextAlign(Align.LEFT);
		
		labelPaint = new Paint();
		labelPaint.setColor(0xFF000000);
		labelPaint.setTextSize(labelSize);
		labelPaint.setTypeface(wordTypeface);
		labelPaint.setTextAlign(Align.CENTER);
	}
	
	@SuppressLint("DrawAllocation")
	protected void onDraw(Canvas canvas){
		Rect area = new Rect(getLeft(),getTop(),getRight(),getBottom());
		//canvas.drawColor(0xFFFFEEEE);
		int limit = Math.min(area.width(), area.height());
		
		topCorner = new PointF(limit/2,limit/5);
		leftCorner = new PointF(limit/10,(int)(limit*0.89282));
		rightCorner = new PointF(limit*9/10,(int)(limit*0.89282));
		center = new PointF((topCorner.x+leftCorner.x+rightCorner.x)/3.f,(topCorner.y+leftCorner.y+rightCorner.y)/3.f);

		canvas.drawLine(topCorner.x, topCorner.y, leftCorner.x, leftCorner.y, chartLine);
		canvas.drawLine(leftCorner.x, leftCorner.y, rightCorner.x, rightCorner.y, chartLine);
		canvas.drawLine(rightCorner.x, rightCorner.y, topCorner.x, topCorner.y, chartLine);
		canvas.drawLine(center.x, center.y, leftCorner.x, leftCorner.y, chartLine);
		canvas.drawLine(center.x, center.y, rightCorner.x, rightCorner.y, chartLine);
		canvas.drawLine(center.x, center.y, topCorner.x, topCorner.y, chartLine);
		
		canvas.drawLine(topCorner.x/3+center.x*2/3, topCorner.y/3+center.y*2/3, leftCorner.x/3+center.x*2/3, leftCorner.y/3+center.y*2/3, chartLine);
		canvas.drawLine(topCorner.x*2/3+center.x/3, topCorner.y*2/3+center.y/3, leftCorner.x*2/3+center.x/3, leftCorner.y*2/3+center.y/3, chartLine);
		canvas.drawLine(leftCorner.x/3+center.x*2/3, leftCorner.y/3+center.y*2/3, rightCorner.x/3+center.x*2/3, rightCorner.y/3+center.y*2/3, chartLine);
		canvas.drawLine(leftCorner.x*2/3+center.x/3, leftCorner.y*2/3+center.y/3, rightCorner.x*2/3+center.x/3, rightCorner.y*2/3+center.y/3, chartLine);
		canvas.drawLine(rightCorner.x/3+center.x*2/3, rightCorner.y/3+center.y*2/3, topCorner.x/3+center.x*2/3, topCorner.y/3+center.y*2/3, chartLine);
		canvas.drawLine(rightCorner.x*2/3+center.x/3, rightCorner.y*2/3+center.y/3, topCorner.x*2/3+center.x/3, topCorner.y*2/3+center.y/3, chartLine);
		
		double s0,s1,s2;
		s0 = scoreList.get(0);
		s1 = scoreList.get(1);
		s2 = scoreList.get(2);
		
		p0 = new PointF((float)(topCorner.x*s0 + center.x*(1-s0)),(float)(topCorner.y*s0 + center.y*(1-s0)));
		p1 = new PointF((float)(leftCorner.x*s1 + center.x*(1-s1)),(float)(leftCorner.y*s1 + center.y*(1-s1)));
		p2 = new PointF((float)(rightCorner.x*s2 + center.x*(1-s2)),(float)(rightCorner.y*s2 + center.y*(1-s2)));
		
		canvas.drawLine(p0.x, p0.y, p1.x, p1.y, valueLine);
		canvas.drawLine(p1.x, p1.y, p2.x, p2.y, valueLine);
		canvas.drawLine(p2.x, p2.y, p0.x, p0.y, valueLine);
		
		Path path = new Path();
		path.moveTo(p0.x, p0.y);
		path.lineTo(p1.x, p1.y);
		path.lineTo(p2.x, p2.y);
		path.lineTo(p0.x, p0.y);
		
		canvas.drawPath(path, valuePaint);
		
		canvas.drawText(title, limit/10, limit/10, titlePaint);
		canvas.drawText(labelList.get(0),topCorner.x , topCorner.y-labelSize/3, labelPaint);
		canvas.drawText(labelList.get(1),leftCorner.x , leftCorner.y+labelSize, labelPaint);
		canvas.drawText(labelList.get(2),rightCorner.x , rightCorner.y+labelSize, labelPaint);
	}

}
