package w1441879.boggle;

import android.content.Context;
import android.content.res.AssetManager;
import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class Ocr {

    private static final String DATA_PATH = Environment.getExternalStorageDirectory().toString() + "/BoggleSolver/";
    private static final String LANG = "die";
    private static final String TAG = "Ocr.java";
    //private Context mContext;

    public Ocr(){

    }

    public static void init(Context mContext){
        String[] paths = new String[] {DATA_PATH, DATA_PATH + "tessdata/"};

        for(String path : paths){
            File dir = new File(path);
            if(!dir.exists()) {
                if (!dir.mkdirs()) {
                    Log.v(TAG, "ERROR creating directory " + path);
                } else {
                    Log.v(TAG, "Created directory " + path);
                }
            }
        }

        if (!(new File(DATA_PATH + "tessdata/" + LANG + ".traineddata")).exists()) {
            try{
                Log.v(TAG, "Copying data");
                AssetManager assetManager = mContext.getAssets();
                InputStream input = assetManager.open("tessdata/" + LANG + ".traineddata");
                OutputStream output = new FileOutputStream(DATA_PATH + "tessdata/" + LANG + ".trainedata");

                //copy file
                byte[] buffer = new byte[1024];
                int read;
                while((read = input.read(buffer)) != -1){
                    output.write(buffer,0,read);
                }

                input.close();
                output.close();

                Log.v(TAG, "data copied");

            } catch (IOException e){
                Log.e(TAG, "cannot copy" + LANG + ".traineddata " + e.toString());
            }
        }
    }


}
