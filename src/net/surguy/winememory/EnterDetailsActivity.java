package net.surguy.winememory;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RatingBar;

import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;

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

        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... params) {
                final String title = getTitle(bm);
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

    private String getTitle(Bitmap bitmap) {
        try {
            ByteBuffer buffer = ByteBuffer.allocate(bitmap.getByteCount());
            bitmap.copyPixelsToBuffer(buffer);
            Goggles goggles = new Goggles();
            String response = goggles.sendPhoto(buffer.array());
            return goggles.extractText(response);
        } catch (IOException e) {
            return "";
        }
    }

}
