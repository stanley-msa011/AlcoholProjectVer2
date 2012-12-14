package game;

import ioio.examples.hello.GameActivity;
import ioio.examples.hello.R;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;

public class GamePopupWindowHandler {

	private GameActivity ga;
	private Button ok_button;
	private TextView popText;
	private PopupWindow popupWindow;
	private ImageView bg;
	private View v_pop;
	
	public GamePopupWindowHandler(GameActivity ga){
		this.ga = ga;
		this.bg = (ImageView)ga.findViewById(R.id.background);
		initPopWindow();
	}
	
	private void initPopWindow(){
		 Context mContext = ga;   
		 LayoutInflater mLayoutInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		 v_pop = mLayoutInflater.inflate(R.layout.game_pop_window, null);
		 popupWindow = new PopupWindow(v_pop,400,400);
		 ok_button = (Button)v_pop.findViewById(R.id.game_pop_ok_button);
		 ok_button.setOnClickListener(new PopWindowOnClickListener());
		 popText = (TextView)v_pop.findViewById(R.id.game_pop_text);
	}
	private class PopWindowOnClickListener implements View.OnClickListener{
		public void onClick(View v) {
			popupWindow.setFocusable(false);
		    popupWindow.dismiss();
		}
	}
	public void showPopWindow(int test_result){
        int result = test_result;
        Drawable good_apple = ga.getResources().getDrawable(R.drawable.apple_good);
        Drawable bad_apple = ga.getResources().getDrawable(R.drawable.apple_bad);
        if (result == BracDataHandler.ERROR){
        	popText.setText("ERROR");
        	popupWindow.setBackgroundDrawable(bad_apple);
        }
		else if (result == BracDataHandler.HaveAlcohol){
			popText.setText("BAD");
			popupWindow.setBackgroundDrawable(bad_apple);
		}
		else if (result == BracDataHandler.NoAlcohol){
			popText.setText("GOOD");
			popupWindow.setBackgroundDrawable(good_apple);
		}
        popupWindow.setOutsideTouchable(false);
        popupWindow.setOnDismissListener(new PopWindowOnDismissListener(test_result));
        bg.post(new showPopWindowThread());
	}
	
	private class PopWindowOnDismissListener implements PopupWindow.OnDismissListener{
		private int test_result;
		PopWindowOnDismissListener(int test_result){
			this.test_result = test_result;
		}
		public void onDismiss() {
			closePopWindow(test_result);
		}
	}
	private void closePopWindow(int test_result){
        int result = test_result;
        if (result == BracDataHandler.ERROR);
		else if (result == BracDataHandler.HaveAlcohol)
			ga.loseCoin();
		else if (result == BracDataHandler.NoAlcohol)
			ga.getCoin();
	}
	private class showPopWindowThread implements Runnable{
		@Override
		public void run() {
				popupWindow.showAtLocation( bg, Gravity.CENTER, 0, 0);
				popupWindow.setFocusable(true);
		        popupWindow.update();
		}

	}

		

}
