package ioio.examples.hello;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import android.app.Activity;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

public class ShowBracActivity extends Activity {
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.show_brac);
        
        //timeStamp 1347886007
        //zoe的機器
        TextView bracValueTextView = (TextView)findViewById(R.id.barc_value);
        ImageView thumbsImageView = (ImageView)findViewById(R.id.imageView1);
        int numberToForsakeHead = 2;
        int numberToForsakeTail = 2;
        double thresholdBracValue = 0.01;
        
        File fileSDCard = null;
        
        //先make sure SDcard is ready
        if(Environment.getExternalStorageState().equals(Environment.MEDIA_REMOVED))
        {
           return;
        }
        else
        {
            fileSDCard = Environment.getExternalStorageDirectory();
            Log.i("tag", "in else");
        }
        
        //the activity to call this view should pass in a key-value pair by
        //--> intent.putExtra("timestamp",1234567890); 
        String timeStamp = this.getIntent().getStringExtra("timestamp");
        File textFile = new File(fileSDCard.getParent() + "/" + fileSDCard.getName() + "/drunk_detection/" + timeStamp +"/" + timeStamp + ".txt");
        //File textFile = new File(fileSDCard.getParent() + "/" + fileSDCard.getName() + "/drunk_detection/1348033520/1348033520.txt");
        
        //second way - Scanner
        try {
			Scanner s = new Scanner(textFile);
			int index = 0;
			List<String> valueArray = new ArrayList<String>();
			
			while(s.hasNext()){
				index++;
				String word = s.next();
				if(index % 2 == 0){
					valueArray.add(word);
					Log.i("nextword added = ",word);

				}
				
			}
			
			//這時候數值都存起來了 要去頭去尾。
			for(int i = 0; i < numberToForsakeHead ;++i ){
				valueArray.remove(0);
			}
			for(int i = 0; i < numberToForsakeTail ;++i){
				valueArray.remove(valueArray.size()-1);
			}
			
			//計算並且顯示 平均值
			double average = 0;
			for(int i = 0; i < valueArray.size(); ++i){
				
				average += Double.parseDouble(valueArray.get(i));
				
			}
			average = average/valueArray.size();
			bracValueTextView.setText(String.valueOf(average));
			
			//set thumbs up/down 的pic
			if(average < thresholdBracValue){
				thumbsImageView.setImageResource(R.drawable.thumbs_up);
			}
			else thumbsImageView.setImageResource(R.drawable.thumbs_down);
			
			
		} catch (FileNotFoundException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
        
        
        /*
        FileInputStream fstream = null;
        String readString = "";  
        
        try 
        {
            fstream = new FileInputStream(textFile);
            StringBuffer buffer = new StringBuffer();
            int readChar;
            
            while ((readChar = fstream.read()) != -1) 
            { 
                buffer.append((char) readChar); 
            } 
            
            fstream.close(); 
            readString = buffer.toString(); 
            Log.i("file content = ",readString);
            
        } 
        catch (Exception e) 
        {
            e.printStackTrace();
        }
        
        */
        
        
    }
}