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

public class DevelopNormalView extends StatisticPageView {

	private HistoryDB db;
	
	private TextView brac,voltage,ts;
	private long timestamp;
	
	private double brac_val, voltage_val;
	private boolean result;
	
	public DevelopNormalView(Context context) {
		super(context, R.layout.statistic_developer_view_normal);
		db = new HistoryDB(context);
	}

	@Override
	public void onPreTask() {
		brac = (TextView) view.findViewById(R.id.developer_brac_value);
		voltage = (TextView) view.findViewById(R.id.developer_voltage_value);
		ts = (TextView) view.findViewById(R.id.developer_ts_value);
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
			voltage.setText(String.valueOf(voltage_val));
		}else{
			brac.setText("NULL");
			voltage.setText("NULL");
		}
		ts.setText(String.valueOf(timestamp));
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
			List<Double> valueArray_brac = new ArrayList<Double>();
			List<Double> valueArray_voltage = new ArrayList<Double>();
			while(s.hasNext()){
				index++;
				String word = s.next();
				if(index % 4 == 3)
					valueArray_brac.add(Double.valueOf(word));
				else if (index % 4 == 0)
					valueArray_voltage.add(Double.valueOf(word));
			}
			
			Double[] values_brac = valueArray_brac.toArray(new Double[valueArray_brac.size()]);
			Double[] values_voltage = valueArray_voltage.toArray(new Double[valueArray_voltage.size()]);
			
			int size = values_brac.length;
			if (values_voltage.length < size)
				size = values_voltage.length;
				
			Value[] values = new Value[size];
			for (int i=0;i<values.length;++i){
				values[i]= new Value(values_brac[i],values_voltage[i]);
			}
			
			Arrays.sort(values);
			brac_val = values[(values.length-1)/2].brac;
			voltage_val = values[(values.length-1)/2].voltage;
			s.close();
		} catch (FileNotFoundException e1) {
			return false;
		}
        return true;
	}
	
	
	private class Value implements Comparable<Value>{
		double brac,voltage;
		
		public Value(double brac, double voltage){
			this.brac = brac;
			this.voltage = voltage;
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
