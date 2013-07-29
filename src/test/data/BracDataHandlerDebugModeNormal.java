package test.data;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

import android.content.SharedPreferences;
import android.os.Environment;
import android.preference.PreferenceManager;
import data.calculate.WeekNum;
import data.info.AccumulatedHistoryState;
import data.info.DateBracDetectionState;

import ubicomp.drunk_detection.fragments.TestFragment;

public class BracDataHandlerDebugModeNormal extends BracDataHandler {

	public BracDataHandlerDebugModeNormal(String timestamp_string,
			TestFragment fragment) {
		super(timestamp_string, fragment);
		// TODO Auto-generated constructor stub
	}
	
	@Override
	public int start(){
		
		File mainStorageDir;
		File textFile,  stateFile, questionFile;
		int result = SUCCESS;
		
        if(Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED))
        	mainStorageDir = new File(Environment.getExternalStorageDirectory(), "drunk_detection");
        else
        	mainStorageDir = new File(context.getFilesDir(),"drunk_detection");
        
        textFile = new File(mainStorageDir.getPath() + File.separator + ts + File.separator + ts + ".txt");
        questionFile = new File(mainStorageDir.getPath() + File.separator + ts + File.separator + "question.txt");
        
        avg_result = parseTextFile(textFile);
        
        int q_result = getQuestionResult(questionFile);
        int emotion = q_result/100;
        int desire = q_result%100;
        if (q_result == -1){
        	emotion = -1;
        	desire = -1;
        }
      
        AccumulatedHistoryState a_history = db.getLatestAccumulatedHistoryState();
        
        float brac = (float)avg_result;
        long timestamp = Long.parseLong(ts) * 1000L;
        int week = WeekNum.getWeek(context, timestamp);
        
        DateBracDetectionState detection= new DateBracDetectionState(week,timestamp,brac,emotion,desire);
        
        DateBracDetectionState prevDetection = db.getLatestBracDetection();
        
    
    	if (detection.year != prevDetection.year || detection.month != prevDetection.month || detection.day != prevDetection.day || detection.timeblock != prevDetection.timeblock){
    		if (avg_result >=0 && avg_result < THRESHOLD)
    				a_history.changeAcc(true, week, detection.timeblock);
    		else if (avg_result < 0)
    			result = ERROR;
    		else
    			a_history.changeAcc(false, week, detection.timeblock);
    	}
    	
    	db.insertNewState(detection,a_history);
    	db.updateDetectionUploaded(timestamp);
    	
    	SharedPreferences sp= PreferenceManager.getDefaultSharedPreferences(context);
		SharedPreferences.Editor editor = sp.edit();
    	if (avg_result  < THRESHOLD){
    		if (emotion <=3 || desire >= 5)
    			editor.putInt("latest_result", 1);
    		else
    			editor.putInt("latest_result", 0);
    	}else if (avg_result < THRESHOLD2)
    		editor.putInt("latest_result", 2);
    	else
    		editor.putInt("latest_result", 3);
    	editor.putBoolean("tested", true);
    	editor.putBoolean("hourly_alarm", false);
    	editor.commit();
        
       	stateFile = new File(mainStorageDir.getPath() + File.separator + ts + File.separator + "state.txt");
       	try {
       		BufferedWriter state_writer = new BufferedWriter(new FileWriter(stateFile));
       		String state_str = a_history.toString();
       		String used_str = db.getLatestUsedState().toString();
       		String output_str = state_str + used_str;
       		state_writer.write(output_str);
       		state_writer.flush();
       		state_writer.close();
		} catch (Exception e) {}
       	
		return result;
	}
	

	@Override
	protected double parseTextFile(File textFile){
		double median = 0;
        try {
			Scanner s = new Scanner(textFile);
			int index = 0;
			List<Double> valueArray = new ArrayList<Double>();
			while(s.hasNext()){
				index++;
				String word = s.next();
				if(index % 4 == 3)
					valueArray.add(Double.valueOf(word));
			}
			if (valueArray.size()==0)
				return ERROR;
			Double[] values = valueArray.toArray(new Double[valueArray.size()]);
			Arrays.sort(values);
			median = values[(values.length-1)/2];
			
		} catch (FileNotFoundException e1) {
			return ERROR;
		}
        return median;
	}
	
}
