package game.interaction;

import ioio.examples.hello.GameActivity;
import ioio.examples.hello.R;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;

public class InteractiveGamePopupWindowHandler {

	private GameActivity ga;
	private InteractiveGameHandler igh;
	private Button ok_button;
	private Button no_button;
	private TextView popText;
	private PopupWindow popupWindow;
	private ImageView bg;
	
	
	private String cur_pid;
	
	public InteractiveGamePopupWindowHandler(GameActivity ga,InteractiveGameHandler igh){
		this.ga = ga;
		this.igh=igh;
		this.bg = (ImageView)ga.findViewById(R.id.background);
		initPopWindow();
	}
	private OnClickListener listener;
	private showPopWindowThread showThread;
	
	private void initPopWindow(){
		 Context mContext = ga;   
		 LayoutInflater mLayoutInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		 View v_pop = mLayoutInflater.inflate(R.layout.interactive_game_pop_window, null);
		 popupWindow = new PopupWindow(v_pop,400,400);
		 showThread = new showPopWindowThread();
		 listener = new PopWindowOnClickListener();
		 ok_button = (Button)v_pop.findViewById(R.id.interactive_game_pop_ok_button);
		 ok_button.setOnClickListener(listener);
		 no_button = (Button)v_pop.findViewById(R.id.interactive_game_pop_no_button);
		 no_button.setOnClickListener(listener);
		 popText = (TextView)v_pop.findViewById(R.id.interactive_game_pop_text);
	}
	private class PopWindowOnClickListener implements View.OnClickListener{
		public void onClick(View v) {
			if (v.getId() ==R.id.interactive_game_pop_ok_button ){
				igh.send_cheers(cur_pid);
			}
			popupWindow.setFocusable(false);
		    popupWindow.dismiss();
		}
	}
	public void showPopWindow(String code_name,String pid){
        popupWindow.setOutsideTouchable(false);
        popText.setText("Cheers "+code_name+"?");
        cur_pid = pid;
        bg.post(showThread);
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
