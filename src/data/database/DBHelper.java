package data.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DBHelper extends SQLiteOpenHelper {

	/*SQLiteOpenHelper. need to migrate with */
	private static final String DATABASE_NAME = "Alcohol Project";
	private static final int DB_VERSION = 12; 
	
	public DBHelper(Context context) {
		super(context, DATABASE_NAME, null, DB_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
        db.execSQL(
                "CREATE TABLE Detection (" +
                        " id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        " year INTEGER NOT NULL," +
        				" month INTEGER NOT NULL," +
        				" day INTEGER NOT NULL," +
        				" week INTEGER NOT NULL,"+
        				" ts INTEGER NOT NULL," +
        				" timeblock INTEGER NOT NULL,"+
                        " brac FLOAT NOT NULL,"+
                        " emotion INTEGER NOT NULL,"+
                        " desire INTEGER NOT NULL," +
                        " upload INTEGER NOT NULL DEFAULT 0"+
                        ")"
        );
        
        db.execSQL(
        		"CREATE TABLE AccDetection (" +
        				" id INTEGER PRIMARY KEY AUTOINCREMENT," +
        				" detection_id INTEGER NOT NULL,"+
        				" w_morning INTEGER NOT NULL," +
        				" w_noon INTEGER NOT NULL," +
        				" w_night INTEGER NOT NULL," +
        				" w_morning_pass INTEGER NOT NULL," +
        				" w_noon_pass INTEGER NOT NULL," +
        				" w_night_pass INTEGER NOT NULL," +
        				" morning INTEGER NOT NULL," +
        				" noon INTEGER NOT NULL," +
        				" night INTEGER NOT NULL," +
        				" morning_pass INTEGER NOT NULL," +
        				" noon_pass INTEGER NOT NULL," +
        				" night_pass INTEGER NOT NULL," +
        				" FOREIGN KEY(detection_id) REFERENCES Detection(id)"+
        				")"
        		);
        
        db.execSQL(
        		"CREATE TABLE UsedDetection (" +
        				" id INTEGER PRIMARY KEY AUTOINCREMENT," +
        				" ts INTEGER NOT NULL,"+
        				" morning INTEGER NOT NULL," +
        				" noon INTEGER NOT NULL," +
        				" night INTEGER NOT NULL," +
        				" morning_pass INTEGER NOT NULL," +
        				" noon_pass INTEGER NOT NULL," +
        				" night_pass INTEGER NOT NULL" +
        				")"
        		);
        
        db.execSQL(
        		"CREATE TABLE Ranking ("+
        				" user_id CHAR[255] PRIMERY KEY," +
        				" score INTEGER NOT NULL" +
        				")"
        );
        
        db.execSQL(
        		"CREATE TABLE RankingToday ("+
        				" user_id CHAR[255] PRIMERY KEY," +
        				" score INTEGER NOT NULL" +
        				")"
        );
        
        db.execSQL(
        		"CREATE TABLE Record ("+
        				" id INTEGER PRIMARY KEY AUTOINCREMENT," +
        				" year INTEGER NOT NULL," +
        				" month INTEGER NOT NULL," +
        				" day INTEGER NOT NULL," +
        				" filename CHAR[255] NOT NULL," +
        				" upload INTEGER NOT NULL DEFAULT 0," +
        				" ts INTEGER NOT NULL," +
        				" acc1 INTEGER NOT NULL DEFAULT 0,"+
        				" acc2 INTEGER NOT NULL DEFAULT 0,"+
        				" acc3 INTEGER NOT NULL DEFAULT 0,"+
        				" used1 INTEGER NOT NULL DEFAULT 0,"+
        				" used2 INTEGER NOT NULL DEFAULT 0,"+
        				" used3 INTEGER NOT NULL DEFAULT 0"+
        				")"
        		);
        
        db.execSQL(
        		"CREATE TABLE Emotion ("+
        				" id INTEGER PRIMARY KEY AUTOINCREMENT," +
        				" ts INTEGER NOT NULL," +
        				" selection INTEGER NOT NULL," +
        				" call CHAR[255]," +															//used for saving recreation
        				" upload INTEGER NOT NULL  DEFAULT 0,"+
        				" acc_tb0 INTEGER NOT NULL,"+
        				" acc_tb1 INTEGER NOT NULL,"+
        				" acc_tb2 INTEGER NOT NULL,"+
        				" used_tb0 INTEGER NOT NULL,"+
        				" used_tb1 INTEGER NOT NULL,"+
        				" used_tb2 INTEGER NOT NULL"+
        				")"
        		);
        
        db.execSQL(
        		"CREATE TABLE EmotionManage ("+
        				" id INTEGER PRIMARY KEY AUTOINCREMENT," +
        				" ts INTEGER NOT NULL," +
        				" emotion INTEGER NOT NULL," +
        				" type INTEGER NOT NULL," +
        				" reason CHAR[255] NOT NULL," +
        				" upload INTEGER NOT NULL  DEFAULT 0,"+
        				" acc_tb0 INTEGER NOT NULL,"+
        				" acc_tb1 INTEGER NOT NULL,"+
        				" acc_tb2 INTEGER NOT NULL,"+
        				" used_tb0 INTEGER NOT NULL,"+
        				" used_tb1 INTEGER NOT NULL,"+
        				" used_tb2 INTEGER NOT NULL"+
        				")"
        		);
        
        db.execSQL(
        		"CREATE TABLE Questionnaire ("+
        				" id INTEGER PRIMARY KEY AUTOINCREMENT," +
        				" ts INTEGER NOT NULL," +
        				" type INTEGER NOT NULL,"+
        				" sequence CHAR[255] NOT NULL," +
        				" upload INTEGER NOT NULL  DEFAULT 0,"+
        				" acc_tb0_0 INTEGER NOT NULL,"+
        				" acc_tb1_0 INTEGER NOT NULL,"+
        				" acc_tb2_0 INTEGER NOT NULL,"+
        				" acc_tb0_1 INTEGER NOT NULL,"+
        				" acc_tb1_1 INTEGER NOT NULL,"+
        				" acc_tb2_1 INTEGER NOT NULL,"+
        				" acc_tb0_2 INTEGER NOT NULL,"+
        				" acc_tb1_2 INTEGER NOT NULL,"+
        				" acc_tb2_2 INTEGER NOT NULL,"+
        				" acc_tb0_3 INTEGER NOT NULL,"+
        				" acc_tb1_3 INTEGER NOT NULL,"+
        				" acc_tb2_3 INTEGER NOT NULL,"+
        				" used_tb0_0 INTEGER NOT NULL,"+
        				" used_tb1_0 INTEGER NOT NULL,"+
        				" used_tb2_0 INTEGER NOT NULL,"+
        				" used_tb0_1 INTEGER NOT NULL,"+
        				" used_tb1_1 INTEGER NOT NULL,"+
        				" used_tb2_1 INTEGER NOT NULL,"+
        				" used_tb0_2 INTEGER NOT NULL,"+
        				" used_tb1_2 INTEGER NOT NULL,"+
        				" used_tb2_2 INTEGER NOT NULL,"+
        				" used_tb0_3 INTEGER NOT NULL,"+
        				" used_tb1_3 INTEGER NOT NULL,"+
        				" used_tb2_3 INTEGER NOT NULL"+
        				")"
        		);
        
        
        db.execSQL(
        		"CREATE TABLE SelfHelpCounterUpdate ("+
        				" id INTEGER PRIMARY KEY AUTOINCREMENT," +
        				" ts INTEGER NOT NULL," +
        				" upload INTEGER NOT NULL  DEFAULT 0"+
        				")"
        		);
        
        db.execSQL(
        		"CREATE TABLE StorytellingUsage (" +
        		" id INTEGER PRIMARY KEY AUTOINCREMENT," +
        		" ts INTEGER NOT NULL," +
        		" daily_usage INTEGER NOT NULL DEFAULT 0," +
        		" acc INTEGER NOT NULL," +
        		" used INTEGER NOT NULL," +
        		" upload INTEGER NOT NULL DEFAULT 0," +
        		" name CHAR[255] NOT NULL DEFAULT 'NONE',"+
        		" minutes INTEGER NOT NULL DEFAULT -1"+
        		")"
        		);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int old_ver, int new_ver){
		
		if (old_ver < 7){
			db.execSQL("DROP TABLE IF EXISTS HistoryGame");
			db.execSQL("DROP TABLE IF EXISTS InteractionGame");
			db.execSQL("DROP TABLE IF EXISTS NotUploadedTS");
			db.execSQL("DROP TABLE IF EXISTS RecDB");
			db.execSQL("DROP TABLE IF EXISTS EmotionDB");
			db.execSQL("DROP TABLE IF EXISTS EmotionManageDB");
			db.execSQL("DROP TABLE IF EXISTS QuestionnaireDB");
			onCreate(db);
		}
		if (old_ver < 8){
			db.execSQL(
	        		"CREATE TABLE SelfHelpCounterUpdate ("+
	        				" id INTEGER PRIMARY KEY AUTOINCREMENT," +
	        				" ts INTEGER NOT NULL," +
	        				" upload INTEGER NOT NULL  DEFAULT 0"+
	        				")"
	        		);
		}
		if (old_ver < 9){
			 db.execSQL(
		        		"CREATE TABLE RankingToday ("+
		        				" user_id CHAR[255] PRIMERY KEY," +
		        				" score INTEGER NOT NULL" +
		        				")"
		        );
		}
		if (old_ver < 10){
			db.execSQL("ALTER TABLE Record ADD acc1 INTEGER NOT NULL DEFAULT 0");
			db.execSQL("ALTER TABLE Record ADD acc2 INTEGER NOT NULL DEFAULT 0");
			db.execSQL("ALTER TABLE Record ADD acc3 INTEGER NOT NULL DEFAULT 0");
			db.execSQL("ALTER TABLE Record ADD used1 INTEGER NOT NULL DEFAULT 0");
			db.execSQL("ALTER TABLE Record ADD used2 INTEGER NOT NULL DEFAULT 0");
			db.execSQL("ALTER TABLE Record ADD used3 INTEGER NOT NULL DEFAULT 0");
		}
		
		if (old_ver < 11){
			db.execSQL(
	        		"CREATE TABLE StorytellingUsage (" +
	        		" id INTEGER PRIMARY KEY AUTOINCREMENT," +
	        		" ts INTEGER NOT NULL," +
	        		" daily_usage INTEGER NOT NULL DEFAULT 0," +
	        		" acc INTEGER NOT NULL," +
	        		" used INTEGER NOT NULL," +
	        		" upload INTEGER NOT NULL DEFAULT 0" +
	        		")"
	        		);
		}
		if (old_ver <12){
			db.execSQL("ALTER TABLE StorytellingUsage ADD name CHAR[255] NOT NULL DEFAULT 'NONE'");
			db.execSQL("ALTER TABLE StorytellingUsage ADD minutes INTEGER NOT NULL DEFAULT -1");
		}
	}
	
	public void onOpen(SQLiteDatabase db){
		super.onOpen(db);
	}
	public synchronized void close(){
		super.close();
	}

}
