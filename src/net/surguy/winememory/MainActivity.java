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
//                intent.setData(fileUri);
                intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri);
//                intent.setFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
//                grantUriPermission();
                startActivityForResult(intent, CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE);
            }
        });
    }

    Uri getNewFileUri() {
        // http://stackoverflow.com/questions/1910608/android-action-image-capture-intent
        try {
            String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
            File mediaFile = new File(getDataDirectory(), timeStamp + ".jpg");
            if(!mediaFile.exists()) {
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
        return new File(Environment.getExternalStorageDirectory().getName() + File.separatorChar + "Android/data/" +
                MainActivity.this.getPackageName() + "/files/");
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//        Log.d(LOG_TAG, "Called back with " + requestCode + " and " + resultCode + " and " + data.getDataString());
        // @todo Not being called back on? - maybe because of some sort of permissions problem with the camera writing to my dataspace
        if (requestCode == CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                // Called back with data == null
                // Toast.makeText(this, "Image successfully saved to " + data.getData(), Toast.LENGTH_LONG).show();
                Log.d(LOG_TAG, "Called back on");
                for (File file : getDataDirectory().listFiles()) {
                    Log.d(LOG_TAG, "File is " + file.getName());
                }

                // @todo Show metadata capture activity
            } else if (resultCode == RESULT_CANCELED) {
                Toast.makeText(this, "Capture cancelled", Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(this, "Capture failed", Toast.LENGTH_LONG).show();
            }
        }
        // Example in the docs doesn't call super, or do anything if the code doesn't match?
        // super.onActivityResult(requestCode, resultCode, data);
    }

}
