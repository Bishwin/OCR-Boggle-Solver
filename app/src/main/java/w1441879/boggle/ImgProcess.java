package w1441879.boggle;


import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import android.content.Context;
import android.content.ContextWrapper;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Matrix;
import android.media.ExifInterface;

import android.os.Environment;
import android.util.Log;

import com.googlecode.leptonica.android.AdaptiveMap;
import com.googlecode.leptonica.android.Pix;
import com.googlecode.leptonica.android.ReadFile;
import com.googlecode.leptonica.android.Rotate;
import com.googlecode.leptonica.android.Skew;
import com.googlecode.tesseract.android.TessBaseAPI;
import com.googlecode.leptonica.android.Binarize;


public class ImgProcess{

    private static final String TAG = "ImgProcess.java";
    static final String DATA_PATH = Environment.getExternalStorageDirectory().toString() + "/BoggleSolver/";
    static final String LANG = "die";
    boolean photoTaken;
    String imgPath;

    ImgProcess(String imgPath){
        this.imgPath = imgPath;
    }

    public void onPhotoTaken(){
        photoTaken = true;

        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inSampleSize = 4;

        Bitmap bitmap = BitmapFactory.decodeFile(imgPath, options);

        //bitmap = rotatePhoto(bitmap);

        //bitmap = greyscaleImg(bitmap);

        recogniseText(bitmap);



        // Convert to ARGB_8888, required by tess
        //bitmap = bitmap.copy(Bitmap.Config.ARGB_8888, true);




    }

    private void recogniseText(Bitmap bitmap){

        TessBaseAPI baseAPI = new TessBaseAPI();
        baseAPI.setDebug(true);
        Log.e(TAG, "BEFORE BASE API INIT");
        baseAPI.init(DATA_PATH, LANG);

        baseAPI.setImage(bitmap);

        String recognisedText = baseAPI.getUTF8Text();

        baseAPI.end();

        Log.v(TAG, "OCRED TEXT: " + recognisedText);

    }

    public static Bitmap rotatePhoto(Bitmap bitmap, String imgPath){

        try {
            ExifInterface exif = new ExifInterface(imgPath);
            int exifOrientation = exif.getAttributeInt(
                    ExifInterface.TAG_ORIENTATION,
                    ExifInterface.ORIENTATION_NORMAL);

            Log.v(TAG, "Orient: " + exifOrientation);

            int rotate = 0;

            switch (exifOrientation) {
                case ExifInterface.ORIENTATION_ROTATE_90:
                    rotate = 90;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_180:
                    rotate = 180;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_270:
                    rotate = 270;
                    break;
            }

            Log.v(TAG, "Rotation: " + rotate);

            if (rotate != 0) {

                // Getting width & height of the given image.
                int w = bitmap.getWidth();
                int h = bitmap.getHeight();

                // Setting pre rotate
                Matrix mtx = new Matrix();
                mtx.preRotate(rotate);

                // Rotating Bitmap
                bitmap = Bitmap.createBitmap(bitmap, 0, 0, w, h, mtx, false);
            }

            // Convert to ARGB_8888, required by tess
            //bitmap = bitmap.copy(Bitmap.Config.ARGB_8888, true);

        } catch (IOException e) {
            Log.e(TAG, "Couldn't correct orientation: " + e.toString());
        }

        return bitmap;
    }

    public static Bitmap greyscaleImg(Bitmap original){

        int alpha, red, green, blue;
        int newPixel;

        Bitmap greyscaledBMP = original.copy(Bitmap.Config.ARGB_8888,true);

        for(int i = 0; i < original.getWidth(); i++){
            for(int j = 0; j < original.getHeight(); j++){

                //get pixels in order of argb
                alpha = Color.alpha(original.getPixel(i,j));
                red = Color.red(original.getPixel(i,j));
                green = Color.green(original.getPixel(i,j));
                blue = Color.blue(original.getPixel(i,j));

                int tmpPixel = (int) (0.21 * red + 0.71 * green + 0.07 * blue);
                //return to argb format
                newPixel = Color.argb(alpha, tmpPixel,tmpPixel, tmpPixel);

                greyscaledBMP.setPixel(i,j,newPixel);

            }
        }
        //saveToInternalStorage(greyscaledBMP);
        return greyscaledBMP;
    }


    public void onPhotoTaken1(){
        photoTaken = true;

        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inSampleSize = 4;

        Bitmap bitmap = BitmapFactory.decodeFile(imgPath, options);

        try {
            ExifInterface exif = new ExifInterface(imgPath);
            int exifOrientation = exif.getAttributeInt(
                    ExifInterface.TAG_ORIENTATION,
                    ExifInterface.ORIENTATION_NORMAL);

            Log.v(TAG, "Orient: " + exifOrientation);

            int rotate = 0;

            switch (exifOrientation) {
                case ExifInterface.ORIENTATION_ROTATE_90:
                    rotate = 90;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_180:
                    rotate = 180;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_270:
                    rotate = 270;
                    break;
            }

            Log.v(TAG, "Rotation: " + rotate);

            if (rotate != 0) {

                // Getting width & height of the given image.
                int w = bitmap.getWidth();
                int h = bitmap.getHeight();

                // Setting pre rotate
                Matrix mtx = new Matrix();
                mtx.preRotate(rotate);

                // Rotating Bitmap
                bitmap = Bitmap.createBitmap(bitmap, 0, 0, w, h, mtx, false);
            }

            // Convert to ARGB_8888, required by tess
            bitmap = bitmap.copy(Bitmap.Config.ARGB_8888, true);

        } catch (IOException e) {
            Log.e(TAG, "Couldn't correct orientation: " + e.toString());
        }

        // _image.setImageBitmap( bitmap );

        Log.v(TAG, "Before baseApi");

        //Pix pix;

        //pix = ReadFile.readBitmap(bitmap);
        //float degrees = Skew.findSkew(pix);
        //Log.e(TAG,"degrees = " + degrees);
        //pix = Rotate.rotate(pix, degrees);
        //pix = Scale.scale(pix, 10f);

        //pix = AdaptiveMap.backgroundNormMorph(pix);

        //pix = Edge.pixSobelEdgeFilter(pix, Edge.L_HORIZONTAL_EDGES);
        //pix = GrayQuant.pixThresholdToBinary(pix, 100);



        //pix = Binarize.otsuAdaptiveThreshold(pix);
        //pix = Binarize.sauvolaBinarizeTiled(pix);
        //Pix pix2 = AdaptiveMap.backgroundNormMorph(pix);

        //pix = Enhance.unsharpMasking(pix);


        TessBaseAPI baseApi = new TessBaseAPI();
        baseApi.setDebug(true);
        Log.e(TAG,"BEFORE BASEAPI.INIT");
        baseApi.init(DATA_PATH, LANG);


        baseApi.setImage(bitmap);
        //baseApi.setImage(pix);

        String recognizedText = baseApi.getUTF8Text();
        int[] con = baseApi.wordConfidences();


        baseApi.end();

        // You now have the text in recognizedText var, you can do anything with it.
        // We will display a stripped out trimmed alpha-numeric version of it (if lang is eng)
        // so that garbage doesn't make it to the display.

        Log.v(TAG, "OCRED TEXT: " + recognizedText);


        for(int word : con){
            Log.v(TAG, "confidence : " + word);
        }


        if ( MainActivity.LANG.equalsIgnoreCase("eng") ) {
            recognizedText = recognizedText.replaceAll("[^a-zA-Z0-9]+", " ");
        }

        recognizedText = recognizedText.trim();


        // Cycle done.
    }



    /*private String saveToInternalStorage(Bitmap bitmapImage){
        ContextWrapper cw = new ContextWrapper(mContext.getApplicationContext());
        // path to /data/data/yourapp/app_data/imageDir
        File directory = cw.getDir("imageDir", Context.MODE_PRIVATE);
        // Create imageDir
        File mypath=new File(directory,"profile.jpg");

        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(mypath);
            // Use the compress method on the BitMap object to write image to the OutputStream
            bitmapImage.compress(Bitmap.CompressFormat.PNG, 100, fos);
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return directory.getAbsolutePath();
    }*/





}
