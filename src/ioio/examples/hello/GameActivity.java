package ioio.examples.hello;

import ioio.examples.hello.R;
import ioio.examples.hello.R.anim;
import ioio.examples.hello.R.id;
import ioio.examples.hello.R.layout;

import java.util.ArrayList;
import java.util.HashMap;

import database.Reuploader;

import game.BackgroundHandler;
import game.BracDataHandler;
import game.GameDB;
import game.GameMenuHandler;
import game.GamePopupWindowHandler;
import game.GameState;
import game.TreeGame;
import game.interaction.InteractiveGameHandler;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;


public class GameActivity extends Activity{
	
	/*communication with MainACtivity*/
	public static final int REQUEST_TEST = 99;
	
	private static final int TREE_TYPES = GameState.MAX_STATE+1; 
	private static final int MAX_COIN = GameState.MAX_COINS; 
	private ImageView[] tree_image = new ImageView[TREE_TYPES];
	private ImageView[] coin_image = new ImageView[MAX_COIN];
	private ImageView background;
	private ImageView background_anime;
	private TreeGame treeGame=null;
	private GameDB gDB=null;
	private Animation appear_anim;
	private Animation disappear_anim;
	private ImageView setting_image;
	private GamePopupWindowHandler gPopWindow;
	private GameMenuHandler gMenu;
	private InteractiveGameHandler gInteractiveGame;
	private Reuploader reuploader;
	
	
	private Bitmap cur_bg = null;
	private Bitmap bg_now = null;
	ArrayList<HashMap<String,Object>> game_list = new ArrayList<HashMap<String,Object>>();

	public final static int START_DO_NOTHING = 0;
	public final static int START_MAIN = 1;
	
	private static int START_ACTION=START_DO_NOTHING;
	public Context context;

	/*Setting the action when start the activity*/
	static public void setStartAction(int action){
		START_ACTION = action;
	}
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		System.gc();
		setContentView(R.layout.activity_game);
		gDB = new GameDB(this);
		GameState gState = gDB.getLatestGameState();
		treeGame=new TreeGame(gState);
		initBackground();
		initAnim();
		initTreeImage();
		initCoinImage();
		initSettingButton();
		setImage();
		gPopWindow = new GamePopupWindowHandler(this);
		gMenu = new GameMenuHandler(this);
		gInteractiveGame = new InteractiveGameHandler(this);
		context = this;
		reuploader = new Reuploader(this);
		reuploader.reTransmission();
		/*Go to MainActivity if start because of the notice*/
		if (START_ACTION == START_MAIN){
			START_ACTION = START_DO_NOTHING;
			Intent newActivity;
			newActivity = new Intent(context, MainActivity.class);  
			startActivityForResult(newActivity, REQUEST_TEST);  
		}
	}
	
	
	protected void onPause(){
		super.onPause();
		if (gInteractiveGame != null)
			gInteractiveGame.clear();
	}
	
	
	protected void onResume(){
		super.onResume();
		//System.gc();
//		SharedPreferences sp= PreferenceManager.getDefaultSharedPreferences(this);
//        boolean s = sp.getBoolean("enable_gps_check", false);
//        Log.d("Pref in Game","<"+String.valueOf(s)+">");
		gInteractiveGame.update();
	}
	
	
	private void initBackground(){
		background = (ImageView) findViewById(R.id.background);
		background_anime = (ImageView) findViewById(R.id.background_anime);
		background_anime.setVisibility(View.INVISIBLE);
	}
	
	private void initAnim(){
		/*used for initializing animations*/
		appear_anim = new AlphaAnimation(0.f,1.f);
		appear_anim.setDuration(1000);
		appear_anim.setStartOffset(30);
		disappear_anim = new AlphaAnimation(1.f,0.f);
		disappear_anim.setDuration(1000);
		disappear_anim.setStartOffset(30);
	}
	private void initTreeImage(){
		/*used for initializing tree images*/
		tree_image[0] = (ImageView) findViewById(R.id.tree1);
		tree_image[1] = (ImageView) findViewById(R.id.tree2);
		tree_image[2] = (ImageView) findViewById(R.id.tree3);
		tree_image[3] = (ImageView) findViewById(R.id.tree4);
		tree_image[4] = (ImageView) findViewById(R.id.tree5);
		tree_image[5] = (ImageView) findViewById(R.id.tree6);
		tree_image[6] = (ImageView) findViewById(R.id.tree7);
	}
	private void initCoinImage(){
		/*used for initializing coin images*/
		coin_image[0] = (ImageView) findViewById(R.id.coin1);
		coin_image[1] = (ImageView) findViewById(R.id.coin2);
		coin_image[2] = (ImageView) findViewById(R.id.coin3);
		coin_image[3] = (ImageView) findViewById(R.id.coin4);
	}	
	
	
	private void initSettingButton(){
		setting_image= (ImageView) findViewById(R.id.setting_button);
		setting_image.setClickable(true);
		setting_image.setOnClickListener(new SettingButtonOnClickListener());
	}
	
	private void setImage(){
		/*set image visibility*/
		if (cur_bg != null)
			cur_bg.recycle();
		GameState gState=treeGame.getGameState();
		for (int i=0;i<TREE_TYPES;++i)
			tree_image[i].setVisibility(ImageView.INVISIBLE);
		for (int i=0;i<MAX_COIN;++i)
			coin_image[i].setVisibility(ImageView.INVISIBLE);
		tree_image[gState.state].setVisibility(ImageView.VISIBLE);
		for (int i=0;i<gState.coin;++i)
			coin_image[i].setVisibility(ImageView.VISIBLE);
		cur_bg = BitmapFactory.decodeResource(this.getResources(), BackgroundHandler.getBackgroundDrawableId(gState.state, gState.coin));
		background.setImageBitmap(cur_bg);
	}
	
	
	private void setImageChange(GameState oldState){
		GameState gState=treeGame.getGameState();
		if (oldState.coin < gState.coin){
			if (oldState.coin ==GameState.MIN_COINS && gState.coin == GameState.MAX_COINS){
				//get coin because state drop
				for (int i=oldState.coin;i<gState.coin;++i){
					coin_image[i].setAlpha(0.f);
					coin_image[i].setVisibility(View.VISIBLE);
					coin_image[i].startAnimation(appear_anim);
					coin_image[i].setAlpha(1.f);
				}
			}
			else{
				Animation a;
				a = AnimationUtils.loadAnimation(this, R.anim.coin_appear_anim);
				for (int i=oldState.coin;i<gState.coin;++i){
					coin_image[i].setVisibility(View.VISIBLE);
					coin_image[i].clearAnimation();
					coin_image[i].startAnimation(a);
				}
			}
		}
		else if (oldState.coin > gState.coin){
			if (oldState.coin ==GameState.MAX_COINS && gState.coin == GameState.MIN_COINS){
				//lose coin because state increases
				for (int i=gState.coin;i<oldState.coin;++i){
					coin_image[i].startAnimation(disappear_anim);
					coin_image[i].setVisibility(ImageView.INVISIBLE);
				}
			}
			else{
				Animation a;
				a = AnimationUtils.loadAnimation(this, R.anim.coin_disappear_anim_type1);
				for (int i=gState.coin;i<oldState.coin;++i){
					coin_image[i].clearAnimation();
					coin_image[i].startAnimation(a);
					coin_image[i].setVisibility(ImageView.INVISIBLE);
				}
			}
		}
		
		if (oldState.state != gState.state){
			tree_image[oldState.state].startAnimation(disappear_anim);
			tree_image[oldState.state].setVisibility(ImageView.INVISIBLE);
			tree_image[gState.state].setVisibility(ImageView.VISIBLE);
			tree_image[gState.state].startAnimation(appear_anim);
		}
		
		if (oldState.coin != gState.coin || oldState.state != gState.state){
			if (bg_now != null)
				bg_now.recycle();
			bg_now = cur_bg;
			cur_bg = BitmapFactory.decodeResource(this.getResources(), BackgroundHandler.getBackgroundDrawableId(gState.state, gState.coin));
			background_anime.setImageBitmap(bg_now);
			background_anime.setVisibility(View.VISIBLE);
			background.setImageBitmap(cur_bg);
			background_anime.startAnimation(disappear_anim);
			background.setAnimation(appear_anim);
			background_anime.setVisibility(View.INVISIBLE);			
		}
	}	
	
	public void getCoin(){
		GameState oldState = treeGame.getPrevState();
		this.setImageChange(oldState);
	}
	public void loseCoin(){
		GameState oldState = treeGame.getPrevState();
		this.setImageChange(oldState);
	}
	
	/*OnListenerForSettingButton*/
	private class SettingButtonOnClickListener implements View.OnClickListener{
		public void onClick(View v) {
			gMenu.changeMenuVisibility();
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data){
		if (requestCode == REQUEST_TEST && resultCode == RESULT_OK){
			Bundle ts_bundle = data.getExtras();  
            String ts = ts_bundle.getString("testfilename");  
            startBracDataHandler(ts);
		}
	}

	private void startBracDataHandler(String ts){
		BracDataHandler bdh = new BracDataHandler(ts,context,treeGame,gDB);
		int result = bdh.start();
		gPopWindow.showPopWindow(result);
	}

	
}
