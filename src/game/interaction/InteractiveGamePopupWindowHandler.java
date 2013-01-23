package game.interaction;

import ioio.examples.hello.GameActivity;
import ioio.examples.hello.R;
import android.content.Context;
import android.graphics.Point;
import android.provider.Settings.Secure;
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
		 Point p = ga.getSize();
		 int width_max = (int) (p.x * 0.8);
		 int width = 400;
		 if (width > width_max)
			 width = width_max;
		 popupWindow = new PopupWindow(v_pop,width,width,true);
		 popupWindow.getContentView().setOnClickListener(new WindowOnClickListener());
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
				if (cur_pid.equals(Secure.getString(ga.getContentResolver(), Secure.ANDROID_ID)))
					;//Do nothing
				else
					igh.send_cheers(cur_pid);
			}
		    popupWindow.dismiss();
		}
	}
	
	private class WindowOnClickListener implements View.OnClickListener{
		public void onClick(View v) {
				if (cur_pid.equals(Secure.getString(ga.getContentResolver(), Secure.ANDROID_ID)))
					popupWindow.dismiss();
		}
	}
	
	
	public void showPopWindow(String code_name,String pid){
        
        String my_pid = Secure.getString(ga.getContentResolver(), Secure.ANDROID_ID);
        if (pid.equals(my_pid)){
        	 popText.setText("為自己加油!!");
        	 ok_button.setVisibility(View.INVISIBLE);
        	 no_button.setVisibility(View.INVISIBLE);
        }
        else{
        	popText.setText("為"+code_name+"加油?");
       	 	ok_button.setVisibility(View.VISIBLE);
       	 	no_button.setVisibility(View.VISIBLE);
        }
        cur_pid = pid;
        bg.post(showThread);
	}
	
	private class showPopWindowThread implements Runnable{
		@Override
		public void run() {
				popupWindow.showAtLocation( bg, Gravity.CENTER, 0, 0);
		        popupWindow.update();
		}
	}

}
