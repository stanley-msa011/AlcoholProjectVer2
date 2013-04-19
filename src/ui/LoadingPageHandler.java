package ui;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Message;
import android.view.ViewGroup;
import android.widget.ImageView;

public class LoadingPageHandler extends Handler {

	ImageView loadingPage;
	ViewGroup layout;
	public  LoadingPageHandler(Drawable d,Context context){
		loadingPage = new ImageView(context);
		loadingPage.setImageDrawable(d);
	}
	
	public void setLayout(ViewGroup layout){
		if (layout!=null)
			layout.removeView(loadingPage);
		this.layout = layout;
	}
	public void handleMessage(Message msg){
		layout.removeView(loadingPage);
		if (msg.what == 1){
			layout.addView(loadingPage);
		}
	}
	
	
}
