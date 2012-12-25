package game.interaction;

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

public class InteractivePopupWindowHandler {

	private GameActivity ga;
	private Button ok_button;
	private TextView popText;
	private PopupWindow popupWindow;
	private ImageView bg;
	private View v_pop;
    private Drawable smile;
    private showPopWindowThread showThread;
	public InteractivePopupWindowHandler(GameActivity ga){
		this.ga = ga;
		this.bg = (ImageView)ga.findViewById(R.id.background);
	    smile = ga.getResources().getDrawable(R.drawable.smile);
		initPopWindow();
	}
	
	private void initPopWindow(){
		 Context mContext = ga;   
		 LayoutInflater mLayoutInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		 v_pop = mLayoutInflater.inflate(R.layout.game_pop_window, null);
		 popupWindow = new PopupWindow(v_pop,400,400);
		 ok_button = (Button)v_pop.findViewById(R.id.game_pop_ok_button);
		 ok_button.setOnClickListener(new PopWindowOnClickListener());
		 showThread = new showPopWindowThread();
		 popText = (TextView)v_pop.findViewById(R.id.game_pop_text);
	}
	private class PopWindowOnClickListener implements View.OnClickListener{
		public void onClick(View v) {
			popupWindow.setFocusable(false);
		    popupWindow.dismiss();
		}
	}
	public void showPopWindow(String pid){
		String cn = ga.getInteractiveGameHandler().getCodeNameByPID(pid);
		popText.setText(cn + "為您加油");
		popText.setTextColor(0xFF0000FF);
		popupWindow.setBackgroundDrawable(smile);
        popupWindow.setOutsideTouchable(false);
        bg.post( showThread);
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
