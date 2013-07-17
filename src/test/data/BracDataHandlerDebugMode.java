package test.data;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import ubicomp.drunk_detection.activities.TestFragment;

public class BracDataHandlerDebugMode extends BracDataHandler {

	public BracDataHandlerDebugMode(String timestamp_string,
			TestFragment fragment) {
		super(timestamp_string, fragment);
		// TODO Auto-generated constructor stub
	}

	@Override
	protected double parseTextFile(File textFile){
		double avg = 0;
        try {
			Scanner s = new Scanner(textFile);
			int index = 0;
			List<String> valueArray_A0 = new ArrayList<String>();
			List<String> valueArray_A1 = new ArrayList<String>();
			while(s.hasNext()){
				index++;
				String word = s.next();
				if(index % 4 == 3)
					valueArray_A0.add(word);
				else if (index %4 == 0)
					valueArray_A1.add(word);
			}
			
			int len = valueArray_A0.size();
			int len2 = valueArray_A1.size();
			if (len2 < len)
				len = len2;
			for(int i = 0; i < len; ++i){
				avg += Double.parseDouble(valueArray_A1.get(i)) - Double.parseDouble(valueArray_A0.get(i));
			}
			if (len==0)
				return ERROR;
			avg /= len;
			
		} catch (FileNotFoundException e1) {
			return ERROR;
		}
        return avg;
	}
	
}
