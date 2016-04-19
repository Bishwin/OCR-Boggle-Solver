package w1441879.boggle;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import org.opencv.android.OpenCVLoader;

import java.io.File;

public class MainActivity extends Activity {

    private static final String DATA_PATH = Environment.getExternalStorageDirectory().toString() + "/BoggleSolver";
    protected String imgPath = DATA_PATH + "/boggleImg.jpg";

    private static final int PREVIEW_REQUEST_CODE = 1;
    private static final int SAVE_REQUEST_CODE = 2;
    private File photoFile;
    Ocr ocr;

    Toolbar bottomToolbar, topToolbar;

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
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button textSolver = (Button) findViewById(R.id.new_text_game);
        textSolver.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                startActivity(new Intent(getApplicationContext(),TextSolverActivity.class));
            }
        });

        Button cameraSolver = (Button) findViewById(R.id.new_take_picture);
        cameraSolver.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                startCameraActivity();
            }
        });

        Button pictureSolver = (Button) findViewById(R.id.new_select_picture);
        pictureSolver.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                Intent getIntent = new Intent(Intent.ACTION_GET_CONTENT);
                getIntent.setType("image/*");

                Intent pickIntent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                pickIntent.setType("image/*");

                Intent chooserIntent = Intent.createChooser(getIntent, "Select Image");
                chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, new Intent[] {pickIntent});

                startActivityForResult(chooserIntent, 2);
            }
        });

        Ocr.init(this);

        topToolbar = (Toolbar) findViewById(R.id.toolbar_top);
        setActionBar(topToolbar);


        bottomToolbar = (Toolbar) findViewById(R.id.toolbar_bottom);
        //bottomToolbar.inflateMenu(R.menu.toolbar_bottom_menu);



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

        startActivityForResult(intent, 0);
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data){

        if(requestCode == -1){
            ImgProcessing imgProcessing = new ImgProcessing();
        }

    }







}


