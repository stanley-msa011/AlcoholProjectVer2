package ioio.examples.hello;

import ioio.examples.hello.R;

import java.util.ArrayList;
import java.util.HashMap;


import com.google.android.gcm.GCMRegistrar;

import database.Reuploader;

import game.BackgroundHandler;
import game.BracDataHandler;
import game.GameDB;
import game.GameMenuHandler;
import game.GamePopupWindowHandler;
import game.GameState;
import game.TreeGame;
import game.interaction.InteractiveGameHandler;
import game.interaction.InteractivePopupWindowHandler;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.view.Display;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.ImageView;


public class GameActivity extends Activity{
	
	/*communication with MainACtivity*/
	private static GameActivity ga = null;
	public static final int REQUEST_TEST = 99;
	private ImageView background;
	private ImageView background_anime;
	private TreeGame treeGame=null;
	private GameDB gDB=null;
	private Animation appear_anim;
	private Animation disappear_anim;
	private ImageView setting_image;
	private GamePopupWindowHandler gPopWindow;
	private InteractivePopupWindowHandler iPopWindow;
	private GameMenuHandler gMenu;
	private InteractiveGameHandler gInteractiveGame;
	private Reuploader reuploader;
	
	
	private Bitmap cur_bg = null;
	private Bitmap bg_now = null;
	
	ArrayList<HashMap<String,Object>> game_list = new ArrayList<HashMap<String,Object>>();

	public Context context;

	private Point screen_size;
	
	AsyncTask<Void, Void, Void> mRegisterTask;
	String regId;
	@SuppressWarnings("deprecation")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		 
		 Display display = getWindowManager().getDefaultDisplay();
		 if (Build.VERSION.SDK_INT<13){
			 int w = display.getWidth();
			 int h = display.getHeight();
			 screen_size = new Point(w,h);
		 }
		 else{
			 screen_size = getSize(display);
		 }
		
		setContentView(R.layout.activity_game);
		gDB = new GameDB(this);
		GameState gState = gDB.getLatestGameState();
		treeGame=new TreeGame(gState);
		initBackground();
		initAnim();
		initSettingButton();
		setImage();
		gPopWindow = new GamePopupWindowHandler(this);
		iPopWindow = new InteractivePopupWindowHandler(this);
		gMenu = new GameMenuHandler(this);
		gInteractiveGame = new InteractiveGameHandler(this);
		context = this;
		reuploader = new Reuploader(this);
		reuploader.reTransmission();
		initRegistration();
		
		Intent intent = this.getIntent();
		if (intent != null){
			boolean notify = intent.getBooleanExtra("notify", false);
			if (notify){
				Intent newActivity;
				if (Build.VERSION.SDK_INT < 11) {
					newActivity = new Intent(context, MainLegacyActivity.class);  
				} else {
					newActivity = new Intent(context, MainActivity.class);
				}
				startActivityForResult(newActivity, REQUEST_TEST);
			}
			String msg = intent.getStringExtra("msgmsg");
			if (msg != null){
				iPopWindow.showPopWindow(msg);
			}
		}
		
      	Intent service_intent = new Intent(this, TimerService.class);
      	startService(service_intent);
	}
	
	@TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
	private Point getSize(Display display){
		Point p = new Point();
		display.getSize(p);
		return p;
	}
	
	public Point getSize(){
		return screen_size;
	}
	
	protected void onPause(){
		super.onPause();
		ga = null;
		if (gInteractiveGame != null)
			gInteractiveGame.clear();
		System.gc();

	}
	
	protected void onDestroy(){
		ga = null;
        if (mRegisterTask != null) {
            mRegisterTask.cancel(true);
        }
        unregisterReceiver(mHandleMessageReceiver);
		if (cur_bg != null){
			cur_bg.recycle();
			cur_bg = null;
		}
		if (bg_now != null){
			bg_now.recycle();
			bg_now = null;
		}
		super.onDestroy();
	}
	
	protected void onResume(){
		super.onResume();
		ga = this;
		gInteractiveGame.update();
	}
	
	protected void onNewIntent(Intent intent){
		super.onNewIntent(intent);
		if (intent != null){
			boolean notify = intent.getBooleanExtra("notify", false);
			if (notify){
				Intent newActivity;
				if (Build.VERSION.SDK_INT < 11) {
					newActivity = new Intent(context, MainLegacyActivity.class);  
				} else {
					newActivity = new Intent(context, MainActivity.class);
				}
				startActivityForResult(newActivity, REQUEST_TEST);
			}
			String msg = intent.getStringExtra("msgmsg");
			if (msg != null){
				iPopWindow.showPopWindow(msg);
			}
		}
	}
	public static void showCheerMessage(String msg){
		if (ga!= null && msg != null)
				if (ga.iPopWindow != null)
					ga.iPopWindow.showPopWindow(msg);
	}
	
	
	private void initBackground(){
		background = (ImageView) findViewById(R.id.background);
		background_anime = (ImageView) findViewById(R.id.background_anime);
		background_anime.setVisibility(View.INVISIBLE);
	}
	
	private void initAnim(){
		/*used for initializing animations*/
		appear_anim = new AlphaAnimation(0.f,1.f);
		appear_anim.setDuration(1500);
		disappear_anim = new AlphaAnimation(0.7f,0.f);
		disappear_anim.setDuration(1500);
	}
	
	private void initRegistration(){
		
		GCMRegistrar.checkDevice(this);
		GCMRegistrar.checkManifest(this);
		registerReceiver(mHandleMessageReceiver,  new IntentFilter("GCM_RECEIVE_ACTION"));
		regId = GCMRegistrar.getRegistrationId(this);
        if (regId.equals(""))
            GCMRegistrar.register(this, CommonUtilities.SENDER_ID);
		else {
            if (GCMRegistrar.isRegisteredOnServer(this));
            else {
                mRegisterTask = new AsyncTask<Void, Void, Void>() {
                    @Override
                    protected Void doInBackground(Void... params) {
                        boolean registered = ServerUtilities.register(context, regId);
                        if (!registered)  GCMRegistrar.unregister(context);
                        return null;
                    }
                    @Override
                    protected void onPostExecute(Void result) {mRegisterTask = null; }
                };
                mRegisterTask.execute(null, null, null);
            }
        }
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

		cur_bg = BitmapFactory.decodeResource(this.getResources(), BackgroundHandler.getBackgroundDrawableId(gState.state, gState.coin));
		background.setImageBitmap(cur_bg);
	}
	
	
	private void setImageChange(GameState oldState){
		GameState gState=treeGame.getGameState();
		
		//if (oldState.coin != gState.coin || oldState.state != gState.state){
		if (oldState.coin != gState.coin){
			if (bg_now != null){
				bg_now.recycle();
				bg_now = null;
			}
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
	
	public void changeView(){
		GameState oldState = treeGame.getPrevState();
		this.setImageChange(oldState);
	}
	
	/*OnListenerForSettingButton*/
	private class SettingButtonOnClickListener implements View.OnClickListener{
		public void onClick(View v) {
			gMenu.changeMenuVisibility();
		}
	}

	public InteractiveGameHandler getInteractiveGameHandler(){
		return gInteractiveGame;
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

    private final BroadcastReceiver mHandleMessageReceiver =
            new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
        }
    };
}
