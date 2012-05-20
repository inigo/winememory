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
    private static final String LOG_TAG = "MainActivity";

    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        Button photoButton = (Button) findViewById(R.id.photoButton);

        getDataDirectory().mkdirs();

        photoButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                Uri fileUri = getNewFileUri();
                intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri);
                startActivityForResult(intent, CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE);
            }
        });

        ListView list = (ListView) findViewById(R.id.list);
        WineListAdapter wineList = new WineListAdapter(this);
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
                // Called back with data == null
                // Toast.makeText(this, "Image successfully saved to " + data.getData(), Toast.LENGTH_LONG).show();
//                Log.d(LOG_TAG, "Called back on");
//                for (File file : getDataDirectory().listFiles()) {
//                    Log.d(LOG_TAG, "File is " + file.getName());
//                }

                // @todo Show metadata capture activity
            } else if (resultCode == RESULT_CANCELED) {
                Log.d(LOG_TAG, "Capture cancelled");
            } else {
                Toast.makeText(this, "Photo capture failed", Toast.LENGTH_LONG).show();
            }
        }
    }

}
