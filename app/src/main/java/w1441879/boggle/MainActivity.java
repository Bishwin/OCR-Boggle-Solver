package w1441879.boggle;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import com.googlecode.tesseract.android.TessBaseAPI;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.opencv.android.OpenCVLoader;

public class MainActivity extends AppCompatActivity {

    private final int PHOTO_CAPTURED = 0;
    private final int PHOTO_SELECTED = 1;
    public static final String DATA_PATH = Environment.getExternalStorageDirectory().toString() + "/BoggleSolver/";
    public static final String LANG = "die";
    private String imgPath;
    boolean imgTaken;
    TextSolverFragment textSolver;

    private static final String TAG = "MAINACTIVITY";

    static {
        if(!OpenCVLoader.initDebug()) {
            Log.d(TAG, "OpenCV not loaded");
        } else {
            Log.d(TAG, "OpenCV loaded");
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        initFileStructure();

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        android.support.v7.widget.Toolbar toolbar = (android.support.v7.widget.Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        initFAB();

        textSolver = new TextSolverFragment();
        getSupportFragmentManager().beginTransaction().add(R.id.fragment_container, textSolver).commit();

        imgPath = DATA_PATH + "/ocr.jpg";
        PermissionRequest();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void startCameraActivity(){
        File imgFile = new File(imgPath);
        Uri UriOutput = Uri.fromFile(imgFile);

        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, UriOutput);

        startActivityForResult(intent, PHOTO_CAPTURED);
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data){

        Log.i(TAG, "resultCode: " + resultCode);

        if(resultCode == RESULT_OK){
            if(requestCode == PHOTO_CAPTURED){
                System.out.println("photo captured");
                onPhotoTaken(imgPath);
            }else if(requestCode == PHOTO_SELECTED && data !=null){
                System.out.println("image selected");
                Uri selectedImg = data.getData();
                String[] filePathColumn = { MediaStore.Images.Media.DATA};
                Cursor cursor = getContentResolver().query(selectedImg,filePathColumn, null, null, null);

                    if(cursor != null){
                        cursor.moveToFirst();
                        int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                        String selectedimgPath = cursor.getString(columnIndex);
                        cursor.close();
                        Log.i(TAG,selectedimgPath);
                        onPhotoTaken(selectedimgPath);
                    }
            }
        }
    }

    /**
     * Init floating buttons on GUI
     */
    private void initFAB(){
        FloatingActionButton fab_camera = (FloatingActionButton) findViewById(R.id.fab_camera);
        fab_camera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startCameraActivity();
            }
        });

        FloatingActionButton fab_gallery = (FloatingActionButton) findViewById(R.id.fab_gallery);

        fab_gallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Intent getIntent = new Intent(Intent.ACTION_GET_CONTENT);
                //getIntent.setType("image/*");

                Intent pickIntent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                pickIntent.setType("image/*");

                Intent chooserIntent = Intent.createChooser(pickIntent, "Select Image");
                //chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, new Intent[] {pickIntent});

                startActivityForResult(chooserIntent, PHOTO_SELECTED);
            }
        });
    }

    /**
     * Creates file structure for traineddata
     */
    public void initFileStructure(){
        String[] paths = new String[] { DATA_PATH, DATA_PATH + "tessdata/" };

        for (String path : paths) {
            File dir = new File(path);
            if (!dir.exists()) {
                if (!dir.mkdirs()) {
                    Log.v(TAG, "ERROR: Creation of directory " + path + " on sdcard failed");
                    return;
                } else {
                    Log.v(TAG, "Created directory " + path + " on sdcard");
                }
            }

        }
        //copy training data
        if (!(new File(DATA_PATH + "tessdata/" + LANG + ".traineddata")).exists()) {
            try {

                AssetManager assetManager = getAssets();
                InputStream in = assetManager.open("tessdata/" + LANG + ".traineddata");
                //GZIPInputStream gin = new GZIPInputStream(in);
                OutputStream out = new FileOutputStream(DATA_PATH
                        + "tessdata/" + LANG + ".traineddata");

                // Transfer bytes from in to out
                byte[] buf = new byte[1024];
                int length;
                while ((length = in.read(buf)) > 0) {
                    out.write(buf, 0, length);
                }
                in.close();
                out.close();

                Log.v(TAG, "Copied " + LANG + " traineddata");
            } catch (IOException e) {
                Log.e(TAG, "Was unable to copy " + LANG + " traineddata " + e.toString());
            }
        }
    }

    /**
     * uses ImgProcess to process image
     * @param imgPath path of image to be scanned
     */
    private void onPhotoTaken(String imgPath) {
        imgTaken = true;

        Bitmap processedImg = ImgProcess.processImage(imgPath);

        Log.v(TAG, "Before baseApi");

        TessBaseAPI baseApi = new TessBaseAPI();
        baseApi.setDebug(true);
        Log.e(TAG,"BEFORE BASEAPI.INIT");
        baseApi.init(DATA_PATH, LANG);

        baseApi.setImage(processedImg);

        String recognizedText = baseApi.getUTF8Text();

        baseApi.end();

        Log.v(TAG, "OCRED TEXT: " + recognizedText);

        recognizedText = recognizedText.replaceAll("\\s+","");

        Log.v(TAG, "formatted: " + recognizedText);

        textSolver.setText(recognizedText.toLowerCase());
    }

    /**
     * requests permission for API 23 to use camera
     */
    private void PermissionRequest(){
        // Here, thisActivity is the current activity
        if ((ContextCompat.checkSelfPermission(this,Manifest.permission.CAMERA)!= PackageManager.PERMISSION_GRANTED)
                && (ContextCompat.checkSelfPermission(this,Manifest.permission.WRITE_EXTERNAL_STORAGE)!= PackageManager.PERMISSION_GRANTED)
                && (ContextCompat.checkSelfPermission(this,Manifest.permission.READ_EXTERNAL_STORAGE)!= PackageManager.PERMISSION_GRANTED)) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.CAMERA)
                    && ActivityCompat.shouldShowRequestPermissionRationale(this,Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    && ActivityCompat.shouldShowRequestPermissionRationale(this,Manifest.permission.READ_EXTERNAL_STORAGE)) {

                // Show an expanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.

            } else {

                // No explanation needed, we can request the permission.

                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE},
                        1);

                // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
                // app-defined int constant. The callback method gets the
                // result of the request.
            }
        }
    }

}


