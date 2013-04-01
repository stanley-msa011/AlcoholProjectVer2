package ioio.examples.hello;

import history.GameHistory;
import new_database.HistoryDB;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class HistoryFragment extends Fragment {

	private View  view;
	
	private TextView levelTextView;
	private HistoryDB db;
	
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    	view = inflater.inflate(R.layout.history_fragment, container,false);
    	setViews();
    	db = new HistoryDB(this.getActivity());
    	return view;
    }
	
    private void setViews(){
    	levelTextView = (TextView) view.findViewById(R.id.history_level);
    }
    
	public void onResume(){
		super.onResume();
		GameHistory history = db.getLatestBracGameHistory(); 
		int level = history.level;
		String output = String.valueOf(level);
		levelTextView.setText(output);
	}
}
