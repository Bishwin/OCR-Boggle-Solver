package w1441879.boggle;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Random;


import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.os.Environment;
import android.provider.ContactsContract;
import android.util.Log;

import org.opencv.android.Utils;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;

public class ImgProcess{

    private static final String TAG = "ImgProcess.java";

    public static Bitmap processImage(String imgPath){

        Mat greyMat = new Mat();
        Mat mean = new Mat();

        //Rotates image
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inSampleSize = 4;
        Bitmap bitmap = BitmapFactory.decodeFile(imgPath, options);
        bitmap = rotatePhoto(bitmap, imgPath);

        //GREYSCALE image and save
        Bitmap greyScaledBMP = greyscaleImg(bitmap);
        //SaveImage(greyScaledBMP, "GREYSCALE");

        //CONVERT bitmap to MAT
        Utils.bitmapToMat(greyScaledBMP, greyMat);
        //OPENCV GREYSCALE, To convert it to work with openCV methods
        Imgproc.cvtColor(greyMat,greyMat,Imgproc.COLOR_BGR2GRAY);

        //new bitmap to store greyscale opencv
        Bitmap TMPBMP = Bitmap.createBitmap(greyMat.width(), greyMat.height(),Bitmap.Config.ARGB_8888);

        //threshold image
        Imgproc.threshold(greyMat, mean, 0, 255, Imgproc.THRESH_BINARY +Imgproc.THRESH_OTSU);

        //save to phone
        Utils.matToBitmap(mean, TMPBMP);
        SaveImage(TMPBMP, "threshold-OTSU");

        //erode lines
        Imgproc.erode(mean, mean, Imgproc.getStructuringElement(Imgproc.CV_SHAPE_RECT, new Size(5,5)));
        //save to phone
        Utils.matToBitmap(mean, TMPBMP);
        SaveImage(TMPBMP, "CVerode-7-7");

        //get contours and cropped img
        Mat cropped  = Contours(mean);
        Imgproc.dilate(cropped, cropped, Imgproc.getStructuringElement(Imgproc.CV_SHAPE_CROSS, new Size(5,5)));
        Imgproc.erode(cropped, cropped, Imgproc.getStructuringElement(Imgproc.CV_SHAPE_RECT, new Size(3,3)));

        Bitmap cropBMP = Bitmap.createBitmap(cropped.width(), cropped.height(),Bitmap.Config.ARGB_8888);
        Utils.matToBitmap(cropped, cropBMP);

        //save and analyse
        SaveImage(cropBMP, "Finished Image");
        return cropBMP;
    }

    /**
     * rotates image
     * @param bitmap bitmap to be rotated
     * @param imgPath location of file
     */
    public static Bitmap rotatePhoto(Bitmap bitmap, String imgPath){

        try {
            ExifInterface exif = new ExifInterface(imgPath);
            int rotation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION,ExifInterface.ORIENTATION_NORMAL);
            Log.v(TAG, "Orient: " + rotation);

            int rotate = 0;

            switch (rotation) {
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

                // Getting width & height of the image.
                int w = bitmap.getWidth();
                int h = bitmap.getHeight();

                // Setting pre rotate
                Matrix mtx = new Matrix();
                mtx.preRotate(rotate);

                // Rotating Bitmap
                bitmap = Bitmap.createBitmap(bitmap, 0, 0, w, h, mtx, false);
            }

        } catch (IOException e) {
            Log.e(TAG, "Couldn't correct orientation: " + e.toString());
        }

        return bitmap;
    }

    /**
     * greyscales image
     * @param original image to greyscale
     */
    public static Bitmap greyscaleImg(Bitmap original){

        int alpha, red, green, blue, newPixel;

        /*
        * Convert to ARGB_8888 to get colour data,
        * format is required by tesstwo
        */
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
        return greyscaledBMP;
    }

    /**
     * saves images to phone for debugging
     * @param finalBitmap bitmap to be saved
     * @param filename name of image
     */
    static void SaveImage(Bitmap finalBitmap, String filename) {

        String root = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).toString();
        File myDir = new File(root + "/saved_images");
        myDir.mkdirs();
        //Random generator = new Random();
        //int n = 10000;
        //n = generator.nextInt(n);

        DateFormat df = new SimpleDateFormat("dd_MM_HH_mm_");
        Date dateobj = new Date();

        String fname = df.format(dateobj)+ filename + ".jpg";
        System.out.println(fname);
        File file = new File (myDir, fname);
        if (file.exists ()) file.delete ();
        try {
            FileOutputStream out = new FileOutputStream(file);
            finalBitmap.compress(Bitmap.CompressFormat.JPEG, 90, out);
            out.flush();
            out.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * detects image and crops based on largest contour
     * @param originalMat , image to be edge detected
     */
    static Mat Contours(Mat originalMat){
        Mat cannyEdges = new Mat();
        Mat hierarchy = new Mat();
        Bitmap TMPBMP = Bitmap.createBitmap(originalMat.width(), originalMat.height(),Bitmap.Config.ARGB_8888);

        double highest = 0;
        int index =0;
        //list to store all the contours
        List<MatOfPoint> contourList = new ArrayList<MatOfPoint>();

        //Converting Mat back to Bitmap for testing
        Imgproc.Canny(originalMat, cannyEdges,10, 50);
        Utils.matToBitmap(cannyEdges, TMPBMP);
        SaveImage(TMPBMP, "CANNYEDGES-10-50");

        //finding contours
        Imgproc.findContours(cannyEdges,contourList,hierarchy,Imgproc.RETR_TREE, Imgproc.CHAIN_APPROX_SIMPLE);

        //Drawing contours on a new image
        Mat contours = new Mat();
        contours.create(cannyEdges.rows(),cannyEdges.cols(),CvType.CV_8UC3);

        for(int i = 0; i < contourList.size(); i++){

            double area = contourList.get(i).size().area();
            if(area > highest){
               highest = area;
               index = i;
            }
        }

        Rect boundingbox = Imgproc.boundingRect(contourList.get(index));
        Mat crop = new Mat(originalMat, boundingbox);

        Bitmap cropbmp = Bitmap.createBitmap(crop.width(), crop.height(),Bitmap.Config.ARGB_8888);
        Utils.matToBitmap(crop, cropbmp);
        SaveImage(cropbmp, "CROP");
        return crop;

    }
}
