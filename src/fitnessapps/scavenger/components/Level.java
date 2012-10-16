package fitnessapps.scavenger.components;

import java.util.Random;

import fitnessapps.scavenger.activity.LevelActivity;
import fitnessapps.scavenger.activity.R;
import fitnessapps.scavenger.activity.StartGameActivity;
import fitnessapps.scavenger.data.GlobalState;
import fitnessapps.scavenger.data.Histogram;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.hardware.Camera;
import android.hardware.Camera.PictureCallback;
import android.os.Bundle;
import android.os.PowerManager;
import android.os.Vibrator;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;
import android.widget.Toast;

public abstract class Level extends Activity {

	private AlertDialog.Builder builder = null;
	private AlertDialog alertDialog = null;
	private MyTimer countdownTimer = null;
	private TextView timerView = null;
	private Histogram histogram = null;
	private SurfaceView preview = null;
	private SurfaceHolder previewHolder = null;
	private Camera mCamera = null;
	private boolean inPreview = false;
	private boolean cameraConfigured = false;
	private int taskNumber = 1;
	private int tasksToComplete;
	private int colorImgDrawable;
	private String currentColor = null;
	private boolean lastLevel = false;
	private boolean taskSuccess = false;
	private Intent startIntent;
	private Intent currIntent;
	private Bitmap bitmapPicture;

	private static final long COUNTDOWN_INTERVAL = 1000;
	private static final int RED = 1;
	private static final int BLUE = 2;
	private static final int GREEN = 3;
	private static final int PURPLE = 4;
	private static final int PINK = 5;
	private static final int ORANGE = 6;
	private static final int GREY = 7;
	private static final int BROWN = 8;
	private int randColorNum = -1;
	private PowerManager pm;
	private PowerManager.WakeLock wakelock;

	private void initIntents() {
		startIntent = new Intent(this, StartGameActivity.class);
		currIntent = new Intent(this, LevelActivity.class);
		/*cameraIntent = new Intent(
				android.provider.MediaStore.ACTION_IMAGE_CAPTURE);*/
	}

	/**************** CAMERA METHODS ***********************************/
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.camerapreview);
		timerView = (TextView) findViewById(R.id.timerView);
		//initActionListeners();
		initIntents();
		//pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
		//wakelock = pm.newWakeLock(
		//        pm.SCREEN_DIM_WAKE_LOCK, "My wakelook");
		//wakelock.acquire();

		preview = (SurfaceView) findViewById(R.id.preview);
		previewHolder = preview.getHolder();
		previewHolder.addCallback(surfaceCallback);
		previewHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
		alertTask();

	}

	@Override
	public void onResume() {
		super.onResume();
		mCamera = Camera.open();
		mCamera.startPreview();
	}

	@Override
	public void onPause() {
		if (inPreview) {
			mCamera.stopPreview();
		}

		mCamera.release();
		mCamera = null;
		inPreview = false;

		super.onPause();

	}
	
	private PictureCallback myPictureCallback_JPG = new PictureCallback(){

		 public void onPictureTaken(byte[] arg0, Camera arg1) {
		  
		  bitmapPicture = getBitmap(arg0);
		 
		  if (validatePicture(bitmapPicture)) {
				taskCompleted();
				checkTaskProgress();
			}
			else {
				tryAgain();
			} 
		  bitmapPicture.recycle();
		  bitmapPicture = null;
		 }};
		 
   private static Bitmap getBitmap(final byte[] array) {
	   final Bitmap bitM = BitmapFactory.decodeByteArray(array, 0, array.length);
	   return bitM;
   }
		 
	public void tryAgain() {
		Toast.makeText(this, "Try Again!", Toast.LENGTH_LONG).show();
	}

	private Camera.Size getBestPreviewSize(int width, int height,
			Camera.Parameters parameters) {
		Camera.Size result = null;

		for (Camera.Size size : parameters.getSupportedPreviewSizes()) {
			if (size.width <= width && size.height <= height) {
				if (result == null) {
					result = size;
				} else {
					int resultArea = result.width * result.height;
					int newArea = size.width * size.height;

					if (newArea > resultArea) {
						result = size;
					}
				}
			}
		}

		return (result);
	}

	private void initPreview(int width, int height) {
		if (mCamera != null && previewHolder.getSurface() != null) {
			try {
				mCamera.setPreviewDisplay(previewHolder);
			} catch (Throwable t) {
				Log.e("PreviewDemo-surfaceCallback",
						"Exception in setPreviewDisplay()", t);
			}

			if (!cameraConfigured) {
				Camera.Parameters parameters = mCamera.getParameters();
				Camera.Size size = getBestPreviewSize(width, height, parameters);

				if (size != null) {
					parameters.setPreviewSize(size.width, size.height);
					mCamera.setParameters(parameters);
					cameraConfigured = true;
				}
			}
		}
	}

	private void startPreview() {
		if (cameraConfigured && mCamera != null) {
			mCamera.startPreview();
			inPreview = true;
		}
	}

	SurfaceHolder.Callback surfaceCallback = new SurfaceHolder.Callback() {
		public void surfaceCreated(SurfaceHolder holder) {
			// The Surface has been created, acquire the camera and tell it
			// where
			// to draw.
			// mCamera = Camera.open();
			// try {
			// mCamera.setPreviewDisplay(holder);
			// } catch (IOException exception) {
			// mCamera.release();
			// mCamera = null;
			// TODO: add more exception handling logic here
			// }
		}

		public void surfaceDestroyed(SurfaceHolder holder) {
			// Surface will be destroyed when we return, so stop the preview.
			// Because the CameraDevice object is not a shared resource, it's
			// very
			// important to release it when the activity is paused.
			// mCamera.stopPreview();
			// mCamera.release();
			// mCamera = null;
		}

		public void surfaceChanged(SurfaceHolder holder, int format, int w,
				int h) {
			// Now that the size is known, set up the camera parameters and
			// begin
			// the preview.
			// Camera.Parameters parameters = mCamera.getParameters();
			// parameters.setPreviewSize(w, h);
			// mCamera.setParameters(parameters);
			initPreview(w, h);
			// mCamera.startPreview();
			startPreview();
		}
	};

	public boolean validatePicture(Bitmap bitMap) {
		boolean goodPic = false;
		histogram = new Histogram(bitMap);
		if( ((getColorPixels(histogram)) / (bitMap.getWidth() * bitMap.getHeight()* 100)) > 20) {
			goodPic = true;
		}
		return goodPic;
	}
	
	public int getColorPixels(Histogram mHist) {
		int colorPix = -1;
		switch (randColorNum) {
		case RED:
			colorPix = mHist.getRedPixels();
			break;
		case BLUE:
			colorPix = mHist.getBluePixels();
			break;
		case GREEN:
			colorPix = mHist.getGreenPixels();
			break;
		case ORANGE:
			colorPix = mHist.getOrangePixels();
			break;
		case PINK:
			colorPix = mHist.getPinkPixels();
			break;
		case GREY:
			colorPix = mHist.getGreyPixels();
			break;
		case PURPLE:
			colorPix = mHist.getPurplePixels();
			break;
		case BROWN:
			colorPix = mHist.getBrownPixels();
			break;
		}
		return colorPix;
	}

	/************* END CAMERA METHODS *********************************/

	public void setViews(TextView timerTextView) {
		timerView = timerTextView;
	}

	public void initGameTimer(long numberOfMiliSec) {
		countdownTimer = new MyTimer(numberOfMiliSec, COUNTDOWN_INTERVAL,
				timerView, this);
	}

	public void stopTimer() {
		countdownTimer.cancel();
	}

	public void alertTask() {
		currentColor = getNewColor();
		Context mContext = this;
		// LayoutInflater inflater = (LayoutInflater) mContext
		// .getSystemService(LAYOUT_INFLATER_SERVICE);
		// View layout = inflater.inflate(R.layout.custom_dialog,
		// (ViewGroup) findViewById(R.id.layout_root));

		final Dialog dialog = new Dialog(mContext);
		dialog.setContentView(R.layout.custom_dialog);
		dialog.setTitle("Level " + getLevelNumber() + " - Task " + taskNumber);
		TextView text = (TextView) dialog.findViewById(R.id.text);
		text.setText("Take a picture of something that is " + currentColor);
		ImageView image = (ImageView) dialog.findViewById(R.id.imageview);

		image.setImageResource(colorImgDrawable);

		Button button = (Button) dialog.findViewById(R.id.button);
		button.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				if (taskNumber == 1) {
					countdownTimer.start();
				}
				//startActivityForResult(cameraIntent, 1);
				dialog.dismiss();
			}
		});
		dialog.show();
		/*
		 * builder = new AlertDialog.Builder(mContext); builder.setView(layout);
		 * builder.setPositiveButton("OK", new DialogInterface.OnClickListener()
		 * { public void onClick(DialogInterface dialogInterface, int arg1) {
		 * dialogInterface.cancel(); } }); alertDialog = builder.create();
		 * alertDialog.setTitle("Task " + taskNumber); alertDialog.show();
		 */

	}

	public String getNewColor() {
		String newColor = "";
		int nColor = randomizeColorSelection();
		randColorNum = nColor;
		switch (nColor) {
		case RED:
			newColor = "Red";
			colorImgDrawable = R.drawable.red;
			break;
		case BLUE:
			newColor = "Blue";
			colorImgDrawable = R.drawable.blue;
			break;
		case GREEN:
			newColor = "Green";
			colorImgDrawable = R.drawable.green;
			break;
		case PURPLE:
			newColor = "Purple";
			colorImgDrawable = R.drawable.purple;
			break;
		case PINK:
			newColor = "Pink";
			colorImgDrawable = R.drawable.pink;
			break;
		case GREY:
			newColor = "Grey";
			colorImgDrawable = R.drawable.grey;
			break;
		case BROWN:
			newColor = "Brown";
			colorImgDrawable = R.drawable.brown;
			break;
		case ORANGE:
			newColor = "Orange";
			colorImgDrawable = R.drawable.orange;
			break;
		}
		return newColor;
	}

	public int randomizeColorSelection() {
		Random gen = new Random();
		int pickedNumber = gen.nextInt(8) + 1;
		return pickedNumber;
	}

	/****************** LEVELS AND TASKS **********************************/
	public int getLevelNumber() {
		return GlobalState.level_number;
	}

	public void setLevelNumber(int levelNum) {
		GlobalState.level_number = levelNum;
	}

	public void checkTaskProgress() {
		if (taskNumber <= tasksToComplete) {
			alertTask();
		} else {
			levelCompleted();
		}
	}

	public void taskCompleted() {
		taskNumber += 1;
	}

	public void setTasksToComplete(int numOfTasks) {
		tasksToComplete = numOfTasks;
	}

	public void levelCompleted() {
		this.onStop();
		showEndOfLevelAlert(true);
	}

	public void levelFailed() {
		this.onStop();
		showEndOfLevelAlert(false);
	}
	
	public boolean randomOutputPicture() {
		Random rnd = new Random();
		int genNum = rnd.nextInt(2) + 1;
		if (genNum == 1) {
			return true;
		}
		else {
			return false;
		}
	}

	public void onTakePicture(View view) {
		//if (validatePicture(photo)) {
		/*taskSuccess = randomOutputPicture();
		if (taskSuccess) {
			taskCompleted();
			checkTaskProgress();
		}
		else {
			vibrate();
			Toast.makeText(this, "Try Again!", Toast.LENGTH_SHORT).show();
		} */
		mCamera.takePicture(null, null, myPictureCallback_JPG);
	}
	
	private void vibrate() {
		Vibrator v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
		v.vibrate(200);
	}

	public void showEndOfLevelAlert(boolean completed) {
		int currLevel = GlobalState.level_number;
		AlertDialog.Builder alertBox = new AlertDialog.Builder(this);
		if (completed && currLevel < 10) {
			alertBox.setMessage("Great job on completing level " + currLevel
					+ "! Let's keep hunting!");
			GlobalState.level_number += 1;
		} else if (completed && currLevel == 10) {
			alertBox.setMessage("Congratulations! You completed the scavenger hunt!");
			lastLevel = true;
		} else {
			alertBox.setMessage("You ran out of time! Try again!");
		}
		alertBox.setPositiveButton("OK", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialogInterface, int arg1) {
				if (lastLevel) {
					startActivity(startIntent);
				} else {
					startActivity(currIntent);
				}
				dialogInterface.cancel();
			}
		});
		alertBox.show();
	}

	@Override
	public void onBackPressed() {
		if (countdownTimer.getSecondsRemaining() >= 0) {
			this.onStop();
			startActivity(currIntent);
		} else {
			// GlobalState.score -= pointsForLevel;
			if (getLevelNumber() > 1) {
				setLevelNumber(getLevelNumber() - 1);
				startActivity(currIntent);
			} else {
				startActivity(startIntent);
			}
		}
		
	}

	@Override
	public void onStop() {
		stopTimer();
		//wakelock.release();

		super.onStop();
	}

}
