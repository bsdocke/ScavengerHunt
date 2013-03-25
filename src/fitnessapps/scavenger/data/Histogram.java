package fitnessapps.scavenger.data;

import java.lang.reflect.InvocationTargetException;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.util.Log;

public class Histogram {

	private Bitmap bitmap;
	private int redPixels;
	private int greenPixels;
	private int bluePixels;
	private int purplePixels;
	private int orangePixels;
	private int greyPixels;
	private int brownPixels;
	private int pinkPixels;

	public Histogram(Bitmap bmp) throws IllegalArgumentException,
			SecurityException, IllegalAccessException,
			InvocationTargetException, NoSuchMethodException {
		bitmap = bmp;
		generateHistogram(bitmap);
	}

	public int getRedPixels() {
		return redPixels;
	}

	public int getGreenPixels() {
		return greenPixels;
	}

	public int getBluePixels() {
		return bluePixels;
	}

	public int getPurplePixels() {
		return purplePixels;
	}

	public int getOrangePixels() {
		return orangePixels;
	}

	public int getGreyPixels() {
		return greyPixels;
	}

	public int getBrownPixels() {
		return brownPixels;
	}

	public int getPinkPixels() {
		return pinkPixels;
	}

	private void generateHistogram(Bitmap bmp) throws IllegalArgumentException,
			SecurityException, IllegalAccessException,
			InvocationTargetException, NoSuchMethodException {
		int width = bitmap.getWidth();
		int height = bitmap.getHeight();

		for (int i = 0; i < width; i += 2) {
			for (int j = 0; j < height; j += 2) {
				int pixColor = bmp.getPixel(i, j);
				queryColorMap(pixColor);
			}
		}
	}

	private void queryColorMap(int rawPixelColor){
		String colorResult = ColorMap.getInstance().getColorFromInt(
				rawPixelColor);
		try {
			callIncrementMethod(colorResult);
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
			Log.e("Illegal Argument Exception while calling color increment method: ", colorResult);
		} catch (SecurityException e) {
			e.printStackTrace();
			Log.e("Security Exception", "Security Exception invoking color increment method " + colorResult);
		} catch (IllegalAccessException e) {
			Log.e("Illegal Access Exception:", "Illegal access exception invoking color increment method " + colorResult);
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NoSuchMethodException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private void callIncrementMethod(String methodColor)
			throws IllegalArgumentException, IllegalAccessException,
			InvocationTargetException, SecurityException, NoSuchMethodException {
		java.lang.reflect.Method method;
		method = this.getClass()
				.getMethod("increment" + methodColor + "Pixels");
		method.invoke(this);
	}

	private void incrementRedPixels() {
		redPixels++;
	}

	private void incrementGreenPixels() {
		greenPixels++;
	}

	private void incrementBluePixels() {
		bluePixels++;
	}

	private void incrementOrangePixels() {
		orangePixels++;
	}

	private void incrementGreyPixels() {
		greyPixels++;
	}

	private void incrementBrownPixels() {
		brownPixels++;
	}

	private void incrementPurplePixels() {
		purplePixels++;
	}

	private void incrementPinkPixels() {
		pinkPixels++;
	}
}
