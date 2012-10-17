package fitnessapps.scavenger.activity;

import java.util.List;
import java.util.Random;

import fitnessapps.scavenger.activity.LevelActivity;
import fitnessapps.scavenger.activity.R;
import fitnessapps.scavenger.components.MyTimer;
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
import android.hardware.Camera.Parameters;
import android.hardware.Camera.PictureCallback;
import android.media.AudioManager;
import android.media.SoundPool;
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
import android.view.WindowManager.LayoutParams;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;
import android.widget.Toast;

public class LevelActivity extends Activity {

	private AlertDialog.Builder builder = null;
	private AlertDialog alertDialog = null;
	private MyTimer countdownTimer = null;
	private TextView timerView = null;
	private ImageView imageView = null;
	private TextView levelTaskView = null;
	private Histogram histogram = null;
	private SurfaceView preview = null;
	private SurfaceHolder previewHolder = null;
	private Camera mCamera = null;
	private boolean inPreview = false;
	private boolean cameraConfigured = false;
	private int taskNumber = 1;
	private boolean isFirstTask = true;
	private int colorImgDrawable;
	private String currentColor = null;
	private boolean lastLevel = false;
	private Intent startIntent;
	private Intent currIntent;
	private Bitmap bitmapPicture;
	private SoundPool pool;
	private int crowdCheer;
	private Button snapButton;

	private static final long COUNTDOWN_INTERVAL = 1000;
	private static final int RED = 1;
	private static final int BLUE = 2;
	private static final int GREEN = 3;
	private static final int PURPLE = 4;
	private static final int PINK = 5;
	private static final int ORANGE = 6;
	private int randColorNum = -1;

	private void initIntents() {
		startIntent = new Intent(this, StartGameActivity.class);
		currIntent = new Intent(this, LevelActivity.class);
	}
	
	private void initSounds() {
		pool = new SoundPool(10, AudioManager.STREAM_MUSIC, 0);
		crowdCheer = pool.load(this, R.raw.crowd_cheer, 1);
	}

	/**************** CAMERA METHODS ***********************************/
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.camerapreview);
		getWindow().addFlags(LayoutParams.FLAG_KEEP_SCREEN_ON);
		initSounds();
		timerView = (TextView) findViewById(R.id.timerView);
		levelTaskView = (TextView) findViewById(R.id.currentLevelTaskText);
		imageView = (ImageView) findViewById(R.id.currentColorImage);
		snapButton = (Button) findViewById(R.id.buttonSnap);
		// must be called after timerView is instantiated
		initGameTimer(GlobalState.levelDurationMili);

		initIntents();

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
		Parameters params = mCamera.getParameters();
		List<Camera.Size> sizes = params.getSupportedPictureSizes();

		params.setPictureSize(sizes.get(0).width, sizes.get(0).height);
		mCamera.setParameters(params);
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

	private PictureCallback myPictureCallback_JPG = new PictureCallback() {

		public void onPictureTaken(byte[] arg0, Camera arg1) {

			bitmapPicture = getBitmap(arg0);

			if (validatePicture(bitmapPicture)) {
				taskCompleted();
				mCamera.startPreview();
				checkTaskProgress();
			} else {
				taskFailed();
				mCamera.startPreview();
			}
			
			bitmapPicture.recycle();
			bitmapPicture = null;
		}
	};

	private static Bitmap getBitmap(final byte[] array) {
		final Bitmap bitM = BitmapFactory.decodeByteArray(array, 0,
				array.length);
		return bitM;
	}

	private void taskFailed() {
		vibrate();
		Toast.makeText(this,
				"Sorry that wasn't " + currentColor + " enough! Try another color!",
				Toast.LENGTH_LONG).show();
		alertTask();
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
			// Empty
		}

		public void surfaceDestroyed(SurfaceHolder holder) {
			// Empty
		}

		public void surfaceChanged(SurfaceHolder holder, int format, int w,
				int h) {
			initPreview(w, h);

			startPreview();
		}
	};

	public boolean validatePicture(Bitmap bitMap) {
		boolean goodPic = false;
		histogram = new Histogram(bitMap);
		int width = bitMap.getWidth();
		int height = bitMap.getHeight();
		int grey = histogram.getGreyPixels();
		int brown = histogram.getBrownPixels();
		int imageSize = width * height;
		int colorPixels = getColorPixels(histogram);
		int adjustedSize = imageSize - brown - grey;
		if ((colorPixels * 100) / (adjustedSize / 4) > 20) {
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
		case PURPLE:
			colorPix = mHist.getPurplePixels();
			break;
		}
		return colorPix;
	}

	/************* END CAMERA METHODS *********************************/

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
				if (isFirstTask) {
					countdownTimer.start();
					isFirstTask = false;
				}
				if (imageView != null && levelTaskView != null) {
					imageView.setBackgroundResource(colorImgDrawable);
					levelTaskView.setText("Level: " + getLevelNumber()
							+ " Task: " + taskNumber);
				}
				snapButton.setClickable(true);
				dialog.dismiss();
			}
		});
		dialog.show();
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
		case ORANGE:
			newColor = "Orange";
			colorImgDrawable = R.drawable.orange;
			break;
		}
		return newColor;
	}

	public int randomizeColorSelection() {
		Random gen = new Random();
		int pickedNumber = gen.nextInt(6) + 1;
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
		if (taskNumber <= GlobalState.tasksToComplete) {
			alertTask();
		} else {
			levelCompleted();
		}
	}

	public void taskCompleted() {
		taskNumber += 1;
		pool.play(crowdCheer, 1, 1, 1, 0, 1);
	}

	public void incrementTasksToComplete() {
		GlobalState.tasksToComplete += 2;
	}

	public void decrementTasksToComplete() {
		GlobalState.tasksToComplete -= 2;
	}

	public void incrementLevelDuration() {
		GlobalState.levelDurationMili += 45000;
	}

	public void decrementLevelDuration() {
		GlobalState.levelDurationMili -= 45000;
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
		} else {
			return false;
		}
	}

	public void onTakePicture(View view) {
		
		snapButton.setClickable(false);
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
			incrementLevelDuration();
			incrementTasksToComplete();
		} else if (completed && currLevel == 10) {
			alertBox.setMessage("Congratulations! You completed the scavenger hunt!");
			lastLevel = true;
		} else {
			alertBox.setMessage("You ran out of time! Try level " + currLevel
					+ " again!");
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
			decrementTasksToComplete();
			decrementLevelDuration();
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

		super.onStop();
	}

}
