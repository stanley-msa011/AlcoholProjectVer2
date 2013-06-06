package ubicomp.drunk_detection.activities;

import history.BracGameHistory;

import java.util.Calendar;
import java.util.Random;

import database.DBHelper;
import database.HistoryDB;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.preference.PreferenceManager;
import android.util.Log;

public class DummyData {

	static final long mDAYMILLIS = 24*60*60*1000;
	
	public static void generateDummyData(Context context){
		
		
		SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
		boolean hasDummy = sp.getBoolean("Dummy", false);
		
		SQLiteOpenHelper dbHelper = new DBHelper(context);
		SQLiteDatabase db = dbHelper.getWritableDatabase();
	
		String sql = "DELETE FROM HistoryGame WHERE _ID >= 0";
		db.execSQL(sql);
		db.close();
		
		if (hasDummy){
			
			SharedPreferences.Editor editor = sp.edit();
			editor.putBoolean("Dummy", false);
			editor.commit();
		}
		else{
			Calendar cal = Calendar.getInstance();
		
			
			
			long from = cal.getTimeInMillis() - mDAYMILLIS * 28;
			
			Log.d("CHART","cur cal = "+cal.getTimeInMillis());
			Log.d("CHART","cur cal = "+cal.toString());
			
			Log.d("CHART","- = "+mDAYMILLIS);
			
			cal.setTimeInMillis(from);
			Log.d("CHART","- cal = "+cal.getTimeInMillis());
			Log.d("CHART","- cal = "+cal.toString());

			final int num_of_data = 4 * 28;
			final long millis = mDAYMILLIS / num_of_data * 28;
			
			HistoryDB hdb = new HistoryDB(context);
			Random rand = new Random();
			
			int level = 0;
			
			for (int i=0;i<num_of_data;++i){
				
				int miss = rand.nextInt(5);
				if (miss > 1){
					from += millis;
					continue;
				}
				int fail = rand.nextInt(5);
				float brac = 0.F; 
				int emotion = rand.nextInt(5)+1;
				int desire = rand.nextInt(10)+1;
				if (fail > 3){
					brac = rand.nextFloat() * 0.5F;
				}
				else{
					++level;
				}
				BracGameHistory history = new BracGameHistory(level,from/1000,brac,emotion,desire);
				
				hdb.insertNewState(history);
				from+=millis;
			}
			
			SharedPreferences.Editor editor = sp.edit();
			editor.putBoolean("Dummy", true);
			editor.commit();
		}
	}
}
