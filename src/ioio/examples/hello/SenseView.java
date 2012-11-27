package ioio.examples.hello;

import ioio.examples.bluetooth.BTService;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

public class SenseView extends SurfaceView implements SurfaceHolder.Callback {
	private static final String TAG = "SenseView";
	private static final boolean D = true;
	
	// Message types sent from the BluetoothChatService Handler
    public static final int MESSAGE_STATE_CHANGE = 1;
    public static final int MESSAGE_READ = 2;
    public static final int MESSAGE_WRITE = 3;
    public static final int MESSAGE_DEVICE_NAME = 4;
    public static final int MESSAGE_TOAST = 5;
	
	// Member object for the chat services
    private BTService mBTService = null;
    
    public float currentPressure;
    public float currentBrac;
	
    private SenseLoop sLoop;
    
	public SenseView(Context ctx, AttributeSet attrs) {
		super(ctx, attrs);
		getHolder().addCallback(this);
		sLoop = new SenseLoop(getHolder(), ctx);
//		mBTService = new BTService(ctx, mHandler);
	}
	
	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
		Log.i(TAG, "Surface changed");
	}

	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		Log.i(TAG, "Surface created");
		sLoop.setRunning(true);
//		sLoop.start();
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		Log.i(TAG, "Surface destroyed");
		sLoop.setRunning(false);
	}
	
	public SenseLoop getThread() {
		return sLoop;
	}
	
	public void startThread() {
		Log.d(TAG, "Starting SenseLoop thread");
		sLoop.start();
	}
	
	public class SenseLoop extends Thread {
		private static final String TAG2 = "SenseLoop";
		
		public static final int STATE_READY = 0x00;
		public static final int STATE_RUN = 0X01;
		
		private Context mContext;
		private boolean mRun = false;
		private int mState;
		
		private int balloonID;
		private Bitmap balloon;
		
	    private boolean decrease = false;
	    private boolean increase = false;
	    
	    private int[] balloons = {
	    		R.drawable.balloon1,
	    		R.drawable.balloon2,
	    		R.drawable.balloon3,
	    		R.drawable.balloon4,
	    		R.drawable.balloon5
	    		};
	    private int bDraw = 0;
		
		private SenseLoop(SurfaceHolder surfaceHolder, Context ctx) {
//			balloonID = R.drawable.balloon_5;
			mContext = ctx;
//			balloon = BitmapFactory.decodeResource(ctx.getResources(), R.drawable.balloon_5);
			mState = STATE_READY;
		}
		
		public void setState(int s) {
			mState = s;
		}
		
		public void setRunning(boolean run) {
			mRun = run;
		}
		
		public void increase() {
			increase = true;
			decrease = false;
		}
		
		public void decrease() {
			increase = false;
			decrease = true;
		}
		
		@Override
		public void run() {
			while(mRun) {
				updateState();
				updateInput();
				updateAI();
				updatePhysics();
				updateAnimation();
				updateSound();
				updateView();
			}
		}
		
		private void updateState() {
			
		}
		
		private void updateInput() {
//			currentPressure = (float) (100.0 * Math.random());
//			if (currentPressure < 50) {
//				decrease = true;
//				increase = false;
//			} else {
//				decrease = false;
//				increase = true;
//			}
		}
		
		private void updateAI() {
			
		}
		
		private void updatePhysics() {
//			if (decrease) {
//				balloon = BitmapFactory.decodeResource(mContext.getResources(), R.drawable.balloon_1a);
//			}
			if (increase) {
				if (bDraw < 5)
					bDraw++;
			} else {
				if (bDraw > 0)
					bDraw--;
			}
			balloon = BitmapFactory.decodeResource(mContext.getResources(), balloons[bDraw]);
		}
		
		private void updateAnimation() {
			
		}
		
		private void updateSound() {
			
		}
		
		private void updateView() {
			Canvas canvas = getHolder().lockCanvas(null);
			try {
				synchronized(getHolder()) {
					if (canvas != null) {
						if (mState == STATE_RUN)
							canvas.drawBitmap(balloon, 0, 0, null);
					}
				}
			} finally {
				if (canvas != null) {
					getHolder().unlockCanvasAndPost(canvas);
				}
			}
		}
	}
}
