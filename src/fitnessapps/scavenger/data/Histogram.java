package fitnessapps.scavenger.data;

import java.lang.reflect.InvocationTargetException;

import android.graphics.Bitmap;
import android.graphics.Color;

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

	private void queryColorMap(int rawPixelColor)
			throws IllegalArgumentException, SecurityException,
			IllegalAccessException, InvocationTargetException,
			NoSuchMethodException {
		String colorResult = ColorMap.getInstance().getColorFromInt(
				rawPixelColor);
		callIncrementMethod(colorResult);
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
