package game;

import database.DBHelper;
import android.app.Activity;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class GameDB {
	/*basic functions for Tree Game communicating with the database*/
	private SQLiteOpenHelper gDBHelper = null;
    private SQLiteDatabase gDB = null;
    
    public GameDB(Activity gActivity){
		gDBHelper = new DBHelper(gActivity);
    }
    
    public GameState getLatestGameState(){
    	gDB = gDBHelper.getWritableDatabase();
    	Cursor cursor = gDB.rawQuery("SELECT MAX(_ID) FROM AlcoholTreeGame", null);
    	
    	cursor.moveToFirst();
    	int max_id = cursor.getInt(0);
    	cursor = gDB.rawQuery("SELECT _STAGE,_COIN FROM AlcoholTreeGame WHERE _ID="+String.valueOf(max_id),null);
    	if (cursor.getCount()==0){
    		gDB.close();
    		return new GameState(0,0);
    	}
    	cursor.moveToFirst();
    	int stage = cursor.getInt(0);
    	int coin = cursor.getInt(1);
    	if (stage > GameState.MAX_STAGE || stage < GameState.MIN_STAGE)
    		stage = 0;
    	if (coin > GameState.MAX_COINS || coin < GameState.MIN_COINS)
    		coin = GameState.MIN_COINS;
    	gDB.close();
    	return new GameState(stage,coin);
    }
    
    public GameState updateState(GameState newState){
    	GameState oldState = getLatestGameState();
    	gDB = gDBHelper.getWritableDatabase();
    	int stage = newState.stage;
    	int coin = newState.coin;
    	gDB.execSQL( 
    	"INSERT INTO AlcoholTreeGame (_STAGE,_COIN) VALUES ("+String.valueOf(stage)+", "+String.valueOf(coin)+")");
    	gDB.close();
    	return oldState;
    }
    
    public GameState[] getAllStates(){
    	gDB = gDBHelper.getReadableDatabase();
    	Cursor cursor;
    	
    	cursor = gDB.rawQuery("SELECT _ID,_STAGE,_COIN FROM AlcoholTreeGame ORDER BY _ID ASC",null);
    	if (cursor.getCount()==0){
    		gDB.close();
    		return null;
    	}
    	int list_size = cursor.getCount();
    	
    	GameState[] stateList = new GameState[list_size];
    	
    	cursor.moveToFirst();
    	int stage = cursor.getInt(1);
    	int coin = cursor.getInt(2);
    	stateList[0]=new GameState(stage,coin);
    	int i = 1;
    	while(cursor.moveToNext()){
        	stage = cursor.getInt(1);
        	coin = cursor.getInt(2);
        	stateList[i]=new GameState(stage,coin);
        	++i;
    	}
    	gDB.close();
    	return stateList;
    }
    
}
