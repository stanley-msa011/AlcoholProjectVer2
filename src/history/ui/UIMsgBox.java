package history.ui;

import main.activities.FragmentTabs;
import main.activities.HistoryFragment;
import main.activities.R;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.graphics.Typeface;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.Switch;
import android.widget.TextView;

public class UIMsgBox {

	private HistoryFragment historyFragment;
	private Context context;
	private LayoutInflater inflater;
	private RelativeLayout boxLayout = null;
	
	private TextView help;
	private Button closeButton,recButton,playButton;
	
	private RelativeLayout mainLayout;
	
	private Typeface digitTypeface;
	private Typeface wordTypeface;
	
	private Point screen;
	
	public UIMsgBox(HistoryFragment historyFragment,RelativeLayout mainLayout){
		Log.d("UIMSG","NEW");
		this.historyFragment = historyFragment;
		this.context = historyFragment.getActivity();
		this.inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		this.mainLayout = mainLayout;
		screen = FragmentTabs.getSize();
		setting();
	}
	
	private void setting(){
		
		digitTypeface = Typeface.createFromAsset(context.getAssets(), "fonts/dinproregular.ttf");
		wordTypeface = Typeface.createFromAsset(context.getAssets(), "fonts/dfheistd-w3.otf");
		
		boxLayout = (RelativeLayout) inflater.inflate(R.layout.rec_layout,null);
		boxLayout.setVisibility(View.INVISIBLE);
		help = (TextView) boxLayout.findViewById(R.id.rec_help);
		help.setTypeface(wordTypeface);
		
		mainLayout.addView(boxLayout);
		
		RelativeLayout.LayoutParams param = (LayoutParams) boxLayout.getLayoutParams();
		param.width = screen.x * 2 / 3;
		param.height = screen.x * 2 / 3;
		param.addRule(RelativeLayout.CENTER_IN_PARENT, RelativeLayout.TRUE);
		
		closeButton = (Button) boxLayout.findViewById(R.id.rec_close_button);
		closeButton.setOnClickListener(new EndListener());
		
	}
	
	private class EndListener implements View.OnClickListener{
		@Override
		public void onClick(View v) {
			boxLayout.setVisibility(View.INVISIBLE);
		}
	}
	
	public void showMsgBox(String date){
		help.setText(date);
		boxLayout.setVisibility(View.VISIBLE);
		
	}
	
}
