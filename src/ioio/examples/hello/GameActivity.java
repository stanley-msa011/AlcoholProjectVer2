package ioio.examples.hello;

import java.util.ArrayList;
import java.util.HashMap;

import game.BracDataHandler;
import game.GameDB;
import game.GameMenuHandler;
import game.GamePopupWindowHandler;
import game.GameState;
import game.TreeGame;
import android.os.Bundle;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

@SuppressLint("NewApi")
public class GameActivity extends Activity{
	
	/*communication with MainACtivity*/
	public static final int REQUEST_TEST = 99;
	
	private static final int TREE_TYPES = GameState.MAX_STATE+1; 
	private static final int MAX_COIN = GameState.MAX_COINS; 
	private ImageView[] tree_image = new ImageView[TREE_TYPES];
	private ImageView[] coin_image = new ImageView[MAX_COIN];
	private TreeGame treeGame=null;
	private GameDB gDB=null;
	private Animation appear_anim;
	private Animation disappear_anim;
	private ImageView setting_image;
	private GamePopupWindowHandler gPopWindow;
	private GameMenuHandler gMenu;

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
		setContentView(R.layout.activity_game);
		gDB = new GameDB(this);
		GameState gState = gDB.getLatestGameState();
		treeGame=new TreeGame(gState);
		initAnim();
		initTreeImage();
		initCoinImage();
		initSettingButton();
		setImage();
		gPopWindow = new GamePopupWindowHandler(this);
		gMenu = new GameMenuHandler(this);
		context = this;
		/*Go to MainActivity if start because of the notice*/
		if (START_ACTION == START_MAIN){
			START_ACTION = START_DO_NOTHING;
			Intent newActivity;
			newActivity = new Intent(context, TestActivity.class);  
			startActivityForResult(newActivity, REQUEST_TEST);  
		}
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
		GameState gState=treeGame.getGameState();
		for (int i=0;i<TREE_TYPES;++i){
			tree_image[i].setVisibility(ImageView.INVISIBLE);
		}
		for (int i=0;i<MAX_COIN;++i)
			coin_image[i].setVisibility(ImageView.INVISIBLE);
		tree_image[gState.state].setVisibility(ImageView.VISIBLE);
		for (int i=0;i<gState.coin;++i)
			coin_image[i].setVisibility(ImageView.VISIBLE);
	}
	
	
	private void setImageDelay(GameState oldState){
		/*set images with animation (used in onCreate())*/
		GameState gState=treeGame.getGameState();
		if (oldState.coin < gState.coin){
			if (oldState.coin ==GameState.MIN_COINS && gState.coin == GameState.MAX_COINS){
				//get coin because state drop
				for (int i=oldState.coin;i<gState.coin;++i){
					coin_image[i].setAlpha(0.f);
					coin_image[i].setVisibility(ImageView.VISIBLE);
					coin_image[i].startAnimation(appear_anim);
					coin_image[i].setAlpha(1.f);
				}
			}
			else{
				Animation a;
				a = AnimationUtils.loadAnimation(this, R.anim.coin_appear_anim);
				for (int i=oldState.coin;i<gState.coin;++i){
					coin_image[i].setVisibility(ImageView.VISIBLE);
					coin_image[i].clearAnimation();
					coin_image[i].startAnimation(a);
				}
			}
		}
		else if (oldState.coin > gState.coin){
			if (oldState.coin ==GameState.MAX_COINS && gState.coin == GameState.MIN_COINS){
				//lose coin because state increases
				for (int i=gState.coin;i<oldState.coin;++i){
					coin_image[i].setAlpha(1.f);
					coin_image[i].startAnimation(disappear_anim);
					coin_image[i].setVisibility(ImageView.INVISIBLE);
				}
			}
			else{
				Animation a;
				a = AnimationUtils.loadAnimation(this, R.anim.coin_disappear_anim_type1);
				for (int i=gState.coin;i<oldState.coin;++i){
					coin_image[i].setAlpha(1.f);
					coin_image[i].clearAnimation();
					coin_image[i].startAnimation(a);
					coin_image[i].setVisibility(ImageView.INVISIBLE);
				}
			}
		}
		
		if (oldState.state != gState.state){
			tree_image[oldState.state].setAlpha(1.f);
			tree_image[oldState.state].startAnimation(disappear_anim);
			tree_image[oldState.state].setVisibility(ImageView.INVISIBLE);
			tree_image[gState.state].setAlpha(0.f);
			tree_image[gState.state].setVisibility(ImageView.VISIBLE);
			tree_image[gState.state].startAnimation(appear_anim);
			tree_image[gState.state].setAlpha(1.f);
		}
	}	
	
	/*Similar to getCoin(), but with delayed animation*/
	public void getCoin(){
		GameState oldState = new GameState(treeGame.getGameState());
		treeGame.getCoin();
		gDB.updateState(treeGame.getGameState());
		this.setImageDelay(oldState);
	}
	/*Similar to loseCoin(), but with delayed animation*/
	public void loseCoin(){
		GameState oldState = new GameState(treeGame.getGameState());
		treeGame.loseCoin();
		gDB.updateState(treeGame.getGameState());
		this.setImageDelay(oldState);
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
		BracDataHandler bdh = new BracDataHandler(ts,context);
		int result = bdh.start();
		gPopWindow.showPopWindow(result);
	}

	
}
