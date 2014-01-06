package history.ui;

import data.database.AdditionalDB;
import data.uploader.DataUploader;
import debug.clicklog.ClickLogId;
import debug.clicklog.ClickLogger;
import ubicomp.drunk_detection.activities.R;
import ubicomp.drunk_detection.ui.CustomToast;
import ubicomp.drunk_detection.ui.EnablePage;
import ubicomp.drunk_detection.ui.ScreenSize;
import ubicomp.drunk_detection.ui.TextSize;
import ubicomp.drunk_detection.ui.Typefaces;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Point;
import android.graphics.Typeface;
import android.os.Build;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

@SuppressLint("InlinedApi")
public class QuoteMsgBox {

	
	private EnablePage enablePage;
	private Context context;
	private LayoutInflater inflater;
	private LinearLayout boxLayout = null;
	
	private RelativeLayout mainLayout;
	private View shadow;
	private TextView help,end;
	private Resources r;
	private Point screen;
	private Typeface wordTypefaceBold;
	private int page;
	private AdditionalDB aDB;
	private String[] learningArray;
	
	public QuoteMsgBox(Context context,EnablePage enablePage,RelativeLayout mainLayout){
		this.enablePage = enablePage;
		this.context = context;
		this.r = context.getResources();
		this.inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		this.mainLayout = mainLayout;
		shadow = new View(context);
		shadow.setBackgroundColor(0x99000000);
		screen = ScreenSize.getScreenSize(context);
		aDB=new AdditionalDB(context);
		learningArray = r.getStringArray(R.array.quote_learning);
		setting();
	}
	
	@SuppressWarnings("deprecation")
	private void setting(){
		
		wordTypefaceBold = Typefaces.getWordTypefaceBold(context);
		shadow.setVisibility(View.INVISIBLE);
		shadow.setKeepScreenOn(false);
		
		boxLayout = (LinearLayout) inflater.inflate(R.layout.quote_box_layout,null);
		boxLayout.setVisibility(View.INVISIBLE);
		
		help = (TextView) boxLayout.findViewById(R.id.quote_text);
		end = (TextView) boxLayout.findViewById(R.id.quote_enter);
		
		mainLayout.addView(shadow);
		mainLayout.addView(boxLayout);
		
		ViewGroup.LayoutParams bgParam = shadow.getLayoutParams();
		if (Build.VERSION.SDK_INT>=8)
			bgParam.width = bgParam.height = LayoutParams.MATCH_PARENT;
		else
			bgParam.width = bgParam.height = LayoutParams.FILL_PARENT;
		RelativeLayout.LayoutParams boxParam = (RelativeLayout.LayoutParams) boxLayout.getLayoutParams();
		boxParam.addRule(RelativeLayout.CENTER_IN_PARENT,RelativeLayout.TRUE);
		boxParam.width = screen.x * 348/480;
		
		
		int textSize = TextSize.normalTextSize(context);
		help.setTextSize(TypedValue.COMPLEX_UNIT_PX, textSize);
		help.setTypeface(wordTypefaceBold);
		RelativeLayout.LayoutParams hParam = (RelativeLayout.LayoutParams) help.getLayoutParams();
		hParam.leftMargin = screen.x * 40/480;
		
		end.setTextSize(TypedValue.COMPLEX_UNIT_PX, textSize);
		end.setTypeface(wordTypefaceBold);
		RelativeLayout.LayoutParams nParam = (RelativeLayout.LayoutParams) end.getLayoutParams();
		nParam.rightMargin = screen.x * 40/480;
		nParam.height = textSize*2;
		
		end.setOnClickListener(new EndListener());
	}
	
	public void clear(){
		if (shadow != null)
			mainLayout.removeView(shadow);
		if (boxLayout !=null)
			mainLayout.removeView(boxLayout);
	}
	
	
	public void openBox(int page){
		enablePage.enablePage(false);
		shadow.setVisibility(View.VISIBLE);
		shadow.setKeepScreenOn(true);
		boxLayout.setVisibility(View.VISIBLE);
		this.page =page;
		help.setText(learningArray[page]);
		return;
}
	
	private class EndListener implements View.OnClickListener{

		@Override
		public void onClick(View v) {
			closeBox();
			ClickLogger.Log(context, ClickLogId.STORYTELLING_QUOTE_END);
			boolean result =aDB.insertStorytellingFling(page);
			if(result)
				CustomToast.generateToast(context, R.string.bonus, 3);
			else
				CustomToast.generateToast(context, R.string.bonus, 0);
			DataUploader.upload(context);
		}
		
	}
	
	public void closeBox(){
		enablePage.enablePage(true);
		shadow.setVisibility(View.INVISIBLE);
		shadow.setKeepScreenOn(false);
		boxLayout.setVisibility(View.INVISIBLE);
	}
}
