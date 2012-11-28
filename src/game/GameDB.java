package game;

import database.DBHelper;
import android.app.Activity;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class GameDB {
	/*basic functions for Tree Game communicating with the database*/
	private SQLiteOpenHelper gDBHelper = null;
    private SQLiteDatabase gDB = null;
    
    public GameDB(Activity gActivity){
		gDBHelper = new DBHelper(gActivity);
		Log.e(this.getClass().toString(), "new GameDBHelper");
    }
    
    public GameState getLatestGameState(){
    	Log.e(this.getClass().toString(), "call getLatestGameState");
    	gDB = gDBHelper.getWritableDatabase();
    	Log.e(this.getClass().toString(), "getDBsucess");
    	Cursor cursor = gDB.rawQuery("SELECT MAX(_ID) FROM AlcoholTreeGame", null);
    	Log.e(this.getClass().toString(), "getCursor");
    	
    	cursor.moveToFirst();
    	int max_id = cursor.getInt(0);
    	cursor = gDB.rawQuery("SELECT _STATE,_COIN FROM AlcoholTreeGame WHERE _ID="+String.valueOf(max_id),null);
    	Log.e(this.getClass().toString(), "getCursorData "+cursor.getCount());
    	if (cursor.getCount()==0){
    		gDB.close();
    		return new GameState(3,0);
    	}
    	Log.e(this.getClass().toString(), "getData_xxx");
    	cursor.moveToFirst();
    	int state = cursor.getInt(0);
    	int coin = cursor.getInt(1);
    	Log.e(this.getClass().toString(), "getCursor"+state+" "+coin);
    	if (state > GameState.MAX_STATE || state < GameState.MIN_STATE)
    		state = 3;
    	if (coin > GameState.MAX_COINS || coin < GameState.MIN_COINS)
    		coin = GameState.MIN_COINS;
    	gDB.close();
    	return new GameState(state,coin);
    }
    
    public GameState updateState(GameState newState){
    	GameState oldState = getLatestGameState();
    	gDB = gDBHelper.getWritableDatabase();
    	int state = newState.state;
    	int coin = newState.coin;
    	gDB.execSQL( 
    	"INSERT INTO AlcoholTreeGame (_STATE,_COIN) VALUES ("+String.valueOf(state)+", "+String.valueOf(coin)+")");
    	gDB.close();
    	return oldState;
    }
    
}
