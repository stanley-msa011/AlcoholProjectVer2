package debug.data_generator;


import java.util.Calendar;
import java.util.Random;

import test.data.BracDataHandler;

import data.calculate.WeekNum;
import data.database.DBHelper;
import data.database.HistoryDB;
import data.info.AccumulatedHistoryState;
import data.info.DateBracDetectionState;

import android.app.AlarmManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.preference.PreferenceManager;

public class DataGenerator {

	static final long mDAYMILLIS = AlarmManager.INTERVAL_DAY;
	
	public static void generateData(Context context){
		
		
		SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
		boolean hasDummy = sp.getBoolean("Dummy", false);
		
		SQLiteOpenHelper dbHelper = new DBHelper(context);
		SQLiteDatabase db = dbHelper.getWritableDatabase();
	
		String sql = "DELETE FROM Detection WHERE id >= 0";
		db.execSQL(sql);
		db.close();
		
		if (hasDummy){
			
			SharedPreferences.Editor editor = sp.edit();
			editor.putBoolean("Dummy", false);
			editor.commit();
		}
		else{
			Calendar cal = Calendar.getInstance();
			long to = cal.getTimeInMillis();
		    int mYear = sp.getInt("sYear", cal.get(Calendar.YEAR));
		    int mMonth = sp.getInt("sMonth", cal.get(Calendar.MONTH));
		    int mDay = sp.getInt("sDate", cal.get(Calendar.DATE));
	    	cal.set(mYear, mMonth, mDay, 0, 0, 0);
	    	cal.set(Calendar.MILLISECOND, 0);
			
			long from = cal.getTimeInMillis();
			final int day_num = (int)((to-from)/mDAYMILLIS);
			final int num_of_data = 3 * day_num;
			final long millis = mDAYMILLIS / 3;
			
			HistoryDB hdb = new HistoryDB(context);
			Random rand = new Random();
			
			
			for (int i=0;i<num_of_data;++i){
				
				int miss = rand.nextInt(5);
				if (miss > 2){
					from += millis;
					continue;
				}
				int fail = rand.nextInt(5);
				float brac = 0.F; 
				int emotion = rand.nextInt(5)+1;
				int desire = rand.nextInt(10)+1;
				if (fail > 3)
					brac = (float) (BracDataHandler.THRESHOLD + rand.nextFloat() * 0.2F);
				
				int week = WeekNum.getWeek(context, from);
				DateBracDetectionState history = new DateBracDetectionState(week,from,brac,emotion,desire);
				AccumulatedHistoryState a_state = hdb.getLatestAccumulatedHistoryState();
				
				if (brac >= 0  && brac < BracDataHandler.THRESHOLD)
					a_state.changeAcc(true, week, history.timeblock);
				else
					a_state.changeAcc(false, week, history.timeblock);
				hdb.insertNewState(history,a_state);
				from+=millis;
			}
			
			hdb.updateAllDetectionUploaded();
			SharedPreferences.Editor editor = sp.edit();
			editor.putBoolean("Dummy", true);
			editor.commit();
		}
	}
}
