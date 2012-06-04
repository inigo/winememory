package net.surguy.winememory;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RatingBar;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Enter wine details into a form.
 *
 * @author Inigo Surguy
 */
public class EnterDetailsActivity extends Activity {
    private static final String LOG_TAG = "EnterDetailsActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.enter_details);

        final Activity context = this;

        Intent intent = getIntent();
        final Uri photoUri = (Uri) intent.getExtras().get("PHOTO_URI");
        Log.d(LOG_TAG, "Received URI " + photoUri);

        final File file = new File(photoUri.getPath());
        ImageView imageView = (ImageView) findViewById(R.id.form_icon);
        imageView.setAdjustViewBounds(true);
        final Bitmap bm = Utils.bitmapFromFile(file, 400);
        imageView.setImageBitmap(bm);
        imageView.setMaxHeight(400);
        imageView.setMaxWidth(400);
        imageView.setScaleType(ImageView.ScaleType.CENTER_INSIDE);

        //noinspection unchecked
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... params) {
                final String title = getTitle(file);
                Log.i(LOG_TAG, "Got title of " + title);
                runOnUiThread(new Runnable() {
                    public void run() {
                        Log.i(LOG_TAG, "Updating title to " + title);
                        ((EditText) findViewById(R.id.form_name)).setText(title);
                    }
                });
                return null;
            }
        }.execute();

        Button photoButton = (Button) findViewById(R.id.form_saveFormButton);
        photoButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                String title = ((EditText) findViewById(R.id.form_name)).getText().toString();
                String description = ((EditText) findViewById(R.id.form_description)).getText().toString();
                float rating = ((RatingBar) findViewById(R.id.form_rating)).getRating();

                final Bottle newBottle = new Bottle(title, description, rating, photoUri.getPath());
                final DatabaseHandler db = new DatabaseHandler(context);

                db.addBottle(newBottle);

                context.finish();
            }
        });
    }

    private String getTitle(File file) {
        try {
            // Google Goggles fails if the image sent to it is too small! A 400px image doesn't work. A 600px image does.
            // However, sending files that have too large a file size (not sure of the limit, but original sized photos) also fails
            final Bitmap bitmap = Utils.bitmapFromFile(file, 600);

            ByteArrayOutputStream out = new ByteArrayOutputStream();
            boolean success = bitmap.compress(Bitmap.CompressFormat.JPEG, 90, out);
            if (!success) { Log.i(LOG_TAG, "Could not successfully compress bitmap"); }
            // writeDebuggingFile(out);

            Goggles goggles = new Goggles();
            String response = goggles.sendPhoto(out.toByteArray());
            return goggles.extractText(response);
        } catch (IOException e) {
            Log.i(LOG_TAG, "Error retrieving parsed text " + e, e);
            return "";
        }
    }

    @SuppressWarnings("UnusedDeclaration")
    private void writeDebuggingFile(ByteArrayOutputStream out) throws IOException {
        File directory = new File(Environment.getExternalStorageDirectory().getName() + File.separatorChar + "Android/data/" +
            EnterDetailsActivity.this.getPackageName() + "/files/");
        File f = new File(directory, "compressed.jpg");
        FileOutputStream fileOutputStream = new FileOutputStream(f);
        fileOutputStream.write(out.toByteArray());
        fileOutputStream.close();
        Log.i(LOG_TAG, "Written JPEG to " + f.getAbsolutePath());
    }

}
