package net.surguy.winememory;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class MainActivity extends Activity {

    private static final int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 100;
    private static final int CREATE_BOTTLE_REQUEST_CODE = 200;
    private static final String LOG_TAG = "MainActivity";

    ListView list;
    WineListAdapter wineList;
    Uri fileUri = null;

    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        Log.i(LOG_TAG, "Created MainActivity");

        Button photoButton = (Button) findViewById(R.id.photoButton);

        getDataDirectory().mkdirs();

        photoButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                fileUri = getNewFileUri();
                // It's possible to use android.hardware.Camera.Parameters.setPictureSize to limit the file size that will be created
                // if you're using the Camera directly - but that doesn't seem possible using the built-in camera app
                intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri);

                // Size limit in bytes - but this is documented as applying to video, so probably does nothing for photo
                intent.putExtra(MediaStore.EXTRA_SIZE_LIMIT, 5120);

                startActivityForResult(intent, CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE);
            }
        });

        list = (ListView) findViewById(R.id.list);
        wineList = new WineListAdapter(this);
        list.setAdapter(wineList);
    }

    Uri getNewFileUri() {
        try {
            String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
            File mediaFile = new File(getDataDirectory(), timeStamp + ".jpg");
            // Creating the file is recommended by http://stackoverflow.com/questions/1910608/android-action-image-capture-intent
            if (!mediaFile.exists()) {
                mediaFile.createNewFile();
                Log.d(LOG_TAG, "Created output file " + mediaFile);
            }
            return Uri.fromFile(mediaFile);
        } catch (IOException e) {
            Log.w(LOG_TAG, "Could not create output file");
            return null;
        }
    }

    private File getDataDirectory() {
        // Data directory needs to be the external storage directory, or the camera can't write to it
        return new File(Environment.getExternalStorageDirectory().getName() + File.separatorChar + "Android/data/" +
                MainActivity.this.getPackageName() + "/files/");
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                Log.d(LOG_TAG, "Intent is " + data);
                Intent intent = new Intent(this, EnterDetailsActivity.class);
                intent.putExtra("PHOTO_URI", fileUri);
                startActivityForResult(intent, CREATE_BOTTLE_REQUEST_CODE);
            } else if (resultCode == RESULT_CANCELED) {
                Log.d(LOG_TAG, "Capture cancelled");
            } else {
                Toast.makeText(this, "Photo capture failed", Toast.LENGTH_LONG).show();
            }
        } else if (requestCode == CREATE_BOTTLE_REQUEST_CODE) {
            Log.i(LOG_TAG, "Bottle creation activity has returned");
            wineList.notifyDataSetChanged();
        }
    }

}
