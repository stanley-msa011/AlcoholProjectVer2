package ubicomp.drunk_detection.ui;

import ubicomp.drunk_detection.activities.R;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.media.AudioManager;
import android.media.SoundPool;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.TextView;
import android.widget.Toast;

public class CustomToast {

	private static Toast toast = null;
	
	private static View layout = null;
	private static TextView toastText, toastCounter;
	private static ImageView toastImage;

	private static Drawable c1Drawable,c2Drawable, c3Drawable, okDrawable,failDrawable;
	
	private static SoundPool soundpool;
	private static int soundId;
	
	public static void settingSoundPool(Context context){
		soundpool = new SoundPool(1,AudioManager.STREAM_MUSIC,1);
		soundId = soundpool.load(context, R.raw.ding, 1);
	}
	
	
	public static void generateToast(Context context, int str_id, int counter){
		if (toast!=null){
			toast.cancel();
			toast = null;
		}
		toast = new Toast(context);
		
		if (layout==null){
			LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			layout = inflater.inflate(R.layout.custom_toast,null);
			toastText = (TextView) layout.findViewById(R.id.custom_toast_text);
			toastText.setTypeface(Typefaces.getWordTypefaceBold(context));
			int screen_x = ScreenSize.getScreenX(context);
			int titleSize = TextSize.smallTitleTextSize(context);
			toastText.setTextSize(TypedValue.COMPLEX_UNIT_PX,titleSize);
			toastCounter = (TextView) layout.findViewById(R.id.custom_toast_counter);
			toastCounter.setTypeface(Typefaces.getWordTypefaceBold(context));
			toastCounter.setTextSize(TypedValue.COMPLEX_UNIT_PX,titleSize);
			toastImage = (ImageView) layout.findViewById(R.id.custom_toast_main_picture);
			RelativeLayout.LayoutParams iParam = (LayoutParams) toastImage.getLayoutParams();
			iParam.topMargin = screen_x * 280/480;
			c1Drawable = context.getResources().getDrawable(R.drawable.toast_counter_1);
			c2Drawable = context.getResources().getDrawable(R.drawable.toast_counter_2);
			c3Drawable = context.getResources().getDrawable(R.drawable.toast_counter_3);
			okDrawable = context.getResources().getDrawable(R.drawable.statistic_show_pass);
			failDrawable = context.getResources().getDrawable(R.drawable.statistic_show_fail);
		}
		if (soundpool == null){
			soundpool = new SoundPool(1,AudioManager.STREAM_MUSIC,1);
			soundId = soundpool.load(context, R.raw.ding, 1);
		}
		toast.setView(layout);
		toast.setMargin(0, 0);
		toast.setGravity(Gravity.FILL, 0, 0);
		toast.setDuration(Toast.LENGTH_SHORT);
		toastText.setText(str_id);
		
		switch (counter){
		case 0:
			toastImage.setImageDrawable(okDrawable);
			toastCounter.setVisibility(View.INVISIBLE);
			break;
		case 1:
			toastImage.setImageDrawable(c1Drawable);
			toastCounter.setVisibility(View.VISIBLE);
			soundpool.play(soundId, 1.f, 1.f, 0, 0, 1.f);
			break;
		case 2:
			toastImage.setImageDrawable(c2Drawable);
			toastCounter.setVisibility(View.VISIBLE);
			soundpool.play(soundId, 1.f, 1.f, 0, 0, 1.f);
			break;
		case 3:
			toastImage.setImageDrawable(c3Drawable);
			toastCounter.setVisibility(View.VISIBLE);
			soundpool.play(soundId, 1.f, 1.f, 0, 0, 1.f);
			break;
		case -1:
			toastImage.setImageDrawable(failDrawable);
			toastCounter.setVisibility(View.INVISIBLE);
			break;
		}
		
		toast.show();
	}
	
}
