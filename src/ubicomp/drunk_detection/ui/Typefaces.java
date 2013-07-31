package ubicomp.drunk_detection.ui;

import android.content.Context;
import android.graphics.Typeface;

public class Typefaces {
	private static Typeface wordTypeface,digitTypeface,wordTypefaceBold,digitTypefaceBold;
	
	static public Typeface getDigitTypeface(Context context){
		if (digitTypeface == null)
			digitTypeface =Typeface.createFromAsset(context.getAssets(), "fonts/dinproregular.ttf");
		return digitTypeface;
	}
	
	static public Typeface getDigitTypefaceBold(Context context){
		if (digitTypefaceBold == null)
			digitTypefaceBold =Typeface.createFromAsset(context.getAssets(),  "fonts/dinpromedium.ttf");
		return digitTypefaceBold;
	}
	
	static public Typeface getWordTypeface(Context context){
		if (wordTypeface == null)
			wordTypeface =Typeface.createFromAsset(context.getAssets(), "fonts/DFLiHeiStd-W3.otf");
		return wordTypeface;
	}
	
	static public Typeface getWordTypefaceBold(Context context){
		if (wordTypefaceBold == null)
			wordTypefaceBold =Typeface.createFromAsset(context.getAssets(), "fonts/DFLiHeiStd-W5.otf");
		return wordTypefaceBold;
	}
	
	static public void initAll(Context context){
		getDigitTypeface(context);
		getDigitTypefaceBold(context);
		getWordTypeface(context);
		getWordTypefaceBold(context);
	}
}
