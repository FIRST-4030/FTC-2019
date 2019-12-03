package org.firstinspires.ftc.teamcode.vuforia;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Environment;

import org.firstinspires.ftc.robotcore.internal.system.AppUtil;

import java.io.File;
import java.io.FileOutputStream;

public class ImageFTC {
    private static final int RED = 0;
    private static final int GREEN = 1;
    private static final int BLUE = 2;

    public static final String SAVE_DIR_DEFAULT = Environment.DIRECTORY_PICTURES;
    public static final int SAVE_QUALITY_DEFAULT = 100;

    private final Bitmap bitmap;
    private final long timestamp;

    public ImageFTC(Bitmap bitmap) {
        this.bitmap = bitmap;
        this.timestamp = System.currentTimeMillis();
    }

    public int getHeight() {
        return bitmap.getHeight();
    }

    public int getWidth() {
        return bitmap.getWidth();
    }

    public int getPixel(int x, int y) {
        return bitmap.getPixel(x, y);
    }

    public Bitmap getBitmap() {
        return bitmap;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public boolean save(File file, Bitmap.CompressFormat format, int quality) {
        boolean success;
        try {
            FileOutputStream out = new FileOutputStream(file);
            success = bitmap.compress(format, quality, out);
            out.close();
        } catch (Exception e) {
            return false;
        }
        return success;
    }

    public boolean savePNG(String name) {
        File dir = Environment.getExternalStoragePublicDirectory(SAVE_DIR_DEFAULT);
        File dir2 = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
        AppUtil.getInstance().ensureDirectoryExists(dir);
        File file = new File(dir, name);
        return save(file, Bitmap.CompressFormat.PNG, SAVE_QUALITY_DEFAULT);
    }

    public boolean savePNGMyVo(String name){
        File dir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS);
        AppUtil.getInstance().ensureDirectoryExists(dir);
        File file = new File(dir, name);
        return save(file, Bitmap.CompressFormat.PNG, SAVE_QUALITY_DEFAULT);
    }

    /**
     * @param x x coordinate of the pixel to be analyzed
     * @param y y coordinate of the pixel to be analyzed
     * @return Individual R, G, and B values from the pixel
     */
    public int rgb(int x, int y) {
        int[] pixel = {x, y};
        return rgb(pixel, pixel);
    }

    /**
     * @param c1 x,y coordinates of the upper-left corner of the region to be analyzed
     * @param c2 x,y coordinates of he lower-right corner of the region to be analyzed
     * @return Individual sums of the R, G, and B values from the region specified
     */
    public int rgb(int[] c1, int[] c2) {
        // Ensure the rectangle we define exists
        if (c1[0] > c2[0] || c1[1] > c2[1] ||
                c2[0] >= bitmap.getWidth() ||
                c2[1] >= bitmap.getHeight()) {
            throw new IllegalArgumentException("Invalid corners: " +
                    "i(" + bitmap.getWidth() + "," + bitmap.getHeight() + ")" +
                    ", c1(" + c1[0] + "," + c1[1] + ")" +
                    ", c2(" + c2[0] + "," + c2[1] + ")");
        }

        // Sum all of the RGB values in the defined region
        int numPixels = 0;
        double[] rgb = {0, 0, 0};
        for (int y = c1[1]; y <= c2[1]; y++) {
            for (int x = c1[0]; x <= c2[0]; x++) {
                int pixel = bitmap.getPixel(x, y);
                rgb[RED] += Color.red(pixel);
                rgb[GREEN] += Color.green(pixel);
                rgb[BLUE] += Color.blue(pixel);
                numPixels++;
            }
        }

        // Return the average color of the region
        return Color.rgb(
                (int) (rgb[RED] / numPixels),
                (int) (rgb[GREEN] / numPixels),
                (int) (rgb[BLUE] / numPixels)
        );
    }

    public int hsl(int[] c1, int[] c2){
        int temp = this.rgb(c1, c2);
        int red = Color.red(temp);
        int green = Color.green(temp);
        int blue = Color.blue(temp);
        return -2;
    }

    public int getLightness (int r, int g, int b) {
        float lightness = (((float) r + (float) g + (float) b)/3.0f)/255.0f;
        return Math.round(lightness * 100.0f);
    }

    public int getSaturation(int r, int g, int b){
        float r2 = (float) r;
        float g2 = (float) g;
        float b2 = (float) b;
        float saturation = 1 - ((3/(r + b + g))*Math.min(Math.min(r, g), b));
        return Math.round(saturation);
    }
}
