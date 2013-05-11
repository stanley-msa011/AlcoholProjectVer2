package main.activities;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;


public class QuestionFragment extends Fragment {

	private View view;
	private LinearLayout qLayout;
	private LayoutInflater inflater;
	
	private View[] questions;
	private EditText[] edits;
	
	private ScrollView scrollView;
	private Button submitButton;
	
	static final private String[] question_ask={
		"我做了什麼? ",
		"因為我感覺到? ",
		"發生了什麼事? ",
		"我那時的想法是? ",
		"如果同樣情景再發生一次,我希望的行為是? ",
		"要有哪種心情,我才能做到新的行為? "
		
		}; 
	
	
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}
	
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    	view = inflater.inflate(R.layout.question_fragment, container,false);
    	this.inflater = inflater;
    	return view;
    }
	
	public void onPause(){
		super.onPause();
	}
	
	public void onResume(){
		super.onResume();
		
		qLayout = (LinearLayout) view.findViewById(R.id.question_layout);
		qLayout.removeAllViewsInLayout();
		questions = new RelativeLayout[6];
		edits = new EditText[6];
		scrollView = (ScrollView) view.findViewById(R.id.question_scroll);
		
		questions[0] = inflater.inflate(R.layout.question_block_type1, null);
		questions[1] = inflater.inflate(R.layout.question_block_type1, null);
		questions[2] = inflater.inflate(R.layout.question_block_type1, null);
		questions[3] = inflater.inflate(R.layout.question_block_type1, null);
		questions[4] = inflater.inflate(R.layout.question_block_type1, null);
		questions[5] = inflater.inflate(R.layout.question_block_type1, null);

		
		
		for (int i=0;i<questions.length;++i){
			TextView title = (TextView) questions[i].findViewById(R.id.question_ask);
			title.setText(question_ask[i]);
			edits[i] = (EditText) questions[i].findViewById(R.id.question_reply);
			Button ok = (Button) questions[i].findViewById(R.id.question_button);
			ok.setOnClickListener(new EditListener(i));
			
		}
		
		for (int i=1;i<questions.length;++i){
			questions[i].setVisibility(View.INVISIBLE);
		}
		
		for (int i=0;i<questions.length;++i){
			qLayout.addView(questions[i]);
		}
		
		submitButton = new Button(this.getActivity());
		submitButton.setText("確定完成");
		submitButton.setVisibility(View.INVISIBLE);
		qLayout.addView(submitButton);
		
		View blankView = new View(this.getActivity());
		qLayout.addView(blankView);
		LayoutParams vParam = blankView.getLayoutParams();
		vParam.height = 100;
		
		
	}
	
	private class EditListener implements OnClickListener{
		
		private int id;
		public EditListener(int id){
			this.id = id;
		}
		
		@Override
		public void onClick(View arg0) {
			if (edits[id].getText().length()==0)
				return;
			if (id < questions.length-1){
				questions[id+1].setVisibility(View.VISIBLE);
				
			}else{
				submitButton.setVisibility(View.VISIBLE);
			}
			scrollView.smoothScrollTo(0, scrollView.getScrollY()+100);
			
		}
		
	}
	
}
