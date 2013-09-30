package ubicomp.drunk_detection.ui;

import debug.clicklog.ClickLogId;
import debug.clicklog.ClickLogger;
import ubicomp.drunk_detection.activities.AboutActivity;
import ubicomp.drunk_detection.activities.EmotionActivity;
import ubicomp.drunk_detection.activities.EmotionManageActivity;
import ubicomp.drunk_detection.activities.R;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.PopupWindow;
import android.widget.TextView;

@SuppressLint({ "ViewConstructor", "InlinedApi" })
public class CustomMenu extends PopupWindow {

	private View menu0,menu1,menu2;
	private TextView m0,m1,m2;
	private Context context;
	
	private Typeface wordTypeface;
	
	@SuppressWarnings("deprecation")
	public CustomMenu(Context context,LayoutInflater inflater) {
		if (inflater == null)
			inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View menu=inflater.inflate(R.layout.menu, null);
		this.context = context;
		this.setContentView(menu);
		this.setFocusable(false);
		this.setOutsideTouchable(true);
		if (Build.VERSION.SDK_INT >= 8)
			this.setWidth(WindowManager.LayoutParams.MATCH_PARENT);
		else
			this.setWidth(WindowManager.LayoutParams.FILL_PARENT);
		
		this.setHeight(WindowManager.LayoutParams.WRAP_CONTENT);
		this.setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
		menu0 = menu.findViewById(R.id.menu_0);
		menu0.setOnClickListener(new MenuOnClickListener());
		menu1= menu.findViewById(R.id.menu_1);
		menu1.setOnClickListener(new MenuOnClickListener());
		menu2= menu.findViewById(R.id.menu_2);
		menu2.setOnClickListener(new MenuOnClickListener());
		
		wordTypeface = Typefaces.getWordTypeface(context);
		
		int x = ScreenSize.getScreenX(context);
		
		m0 = (TextView) menu.findViewById(R.id.menu_text_0);
		m0.setTypeface(wordTypeface);
		m0.setTextSize(TypedValue.COMPLEX_UNIT_PX, x*24/480);
		m0.getLayoutParams().height = x*209/1080;
		m1 = (TextView) menu.findViewById(R.id.menu_text_1);
		m1.setTypeface(wordTypeface);
		m1.setTextSize(TypedValue.COMPLEX_UNIT_PX, x*24/480);
		m1.getLayoutParams().height = x*209/1080;
		m2 = (TextView) menu.findViewById(R.id.menu_text_2);
		m2.setTypeface(wordTypeface);
		m2.setTextSize(TypedValue.COMPLEX_UNIT_PX, x*24/480);
		m2.getLayoutParams().height = x*209/1080;
	}
	
	
	private class MenuOnClickListener implements View.OnClickListener{
		@Override
		public void onClick(View v) {
			Intent intent;
			switch (v.getId()){
				case R.id.menu_0:
					intent = new Intent(context, EmotionActivity.class);
					ClickLogger.Log(context, ClickLogId.MENU_EMOTIONDIY);
					break;
				case R.id.menu_1:
					intent = new Intent(context, EmotionManageActivity.class);
					ClickLogger.Log(context, ClickLogId.MENU_EMOTIONMANAGE);
					break;
				case R.id.menu_2:
					intent = new Intent(context,AboutActivity.class);
					ClickLogger.Log(context, ClickLogId.MENU_ABOUT);
					break;
				default:
					intent = null;
			}
			if (intent !=null)
				context.startActivity(intent);
		}
	}
	
}
