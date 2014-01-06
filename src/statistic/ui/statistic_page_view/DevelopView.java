package statistic.ui.statistic_page_view;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

import data.database.HistoryDB;
import data.info.BracDetectionState;
import ubicomp.drunk_detection.activities.R;
import android.content.Context;
import android.os.Environment;
import android.widget.TextView;

public class DevelopView extends StatisticPageView {

	private HistoryDB db;
	
	private TextView brac,voltage;
	private long timestamp;
	
	private double brac_val;
	private boolean result;
	
	public DevelopView(Context context) {
		super(context, R.layout.statistic_developer_view);
		db = new HistoryDB(context);
	}

	@Override
	public void onPreTask() {
		brac = (TextView) view.findViewById(R.id.developer_brac_value);
		voltage = (TextView) view.findViewById(R.id.developer_voltage_value);
		BracDetectionState history = db.getLatestBracDetection();
		timestamp = history.timestamp/1000L;
	}

	@Override
	public void onInBackground() {
		File mainStorageDir;
		File textFile;
		
        if(Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED))
        	mainStorageDir = new File(Environment.getExternalStorageDirectory(), "drunk_detection");
        else
        	mainStorageDir = new File(context.getFilesDir(),"drunk_detection");
        
        textFile = new File(mainStorageDir.getPath() + File.separator + timestamp + File.separator + timestamp + ".txt");
        result = parseTextFile(textFile);
	}

	@Override
	public void onPostTask() {
		if (result){
			brac.setText(String.valueOf(brac_val));
		}else{
			brac.setText("NULL");
			voltage.setText("NULL");
		}
		voltage.setText(String.valueOf(timestamp));
	}

	@Override
	public void onCancel() {

	}

	@Override
	public void clear() {

	}

	protected boolean parseTextFile(File textFile){
        try {
			Scanner s = new Scanner(textFile);
			int index = 0;
			List<Double> valueArray_A0 = new ArrayList<Double>();
			List<Double> valueArray_A1 = new ArrayList<Double>();
			while(s.hasNext()){
				index++;
				String word = s.next();
				if(index % 5 == 3)
					valueArray_A0.add(Double.valueOf(word));
				else if (index %5 == 4)
					valueArray_A1.add(Double.valueOf(word));
			}
			
			
			int size = valueArray_A0.size();
			int min_size =size, max_size = size;
			if (min_size >valueArray_A1.size() )
				min_size = valueArray_A1.size();
			else
				max_size = valueArray_A1.size();
				
			Double[] values_A0 = valueArray_A0.toArray(new Double[max_size]);
			Double[] values_A1 = valueArray_A1.toArray(new Double[max_size]);
			
			Value[] values = new Value[min_size];
			for (int i=0;i<values.length;++i){
				values[i]= new Value(values_A0[i],values_A1[i]);
			}
			
			Arrays.sort(values);
			brac_val = values[(values.length-1)/2].brac;
			s.close();
		} catch (FileNotFoundException e1) {
			return false;
		}
        return true;
	}
	
	
	private class Value implements Comparable<Value>{
		double brac;
		
		public Value(double A0, double A1){
			this.brac = A1 - A0;
		}

		@Override
		public int compareTo(Value val) {
			if (this.brac > val.brac)
				return 1;
			else if (this.brac < val.brac)
				return -1;
			return 0;
		}
		
	}
	
}
