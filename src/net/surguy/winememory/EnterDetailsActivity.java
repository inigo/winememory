package net.surguy.winememory;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RatingBar;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
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
        imageView.setImageBitmap(bitmapFromFile(file));
        imageView.setMaxHeight(400);
        imageView.setMaxWidth(400);
        imageView.setScaleType(ImageView.ScaleType.CENTER_INSIDE);

        Button photoButton = (Button) findViewById(R.id.form_saveFormButton);
        photoButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                String title = ((EditText) findViewById(R.id.form_name)).getText().toString();
                String description = ((EditText) findViewById(R.id.form_description)).getText().toString();
                float rating = ((RatingBar) findViewById(R.id.form_rating)).getRating();

                final Bottle newBottle = new Bottle(title, description, rating, photoUri.getPath());
                final DatabaseHandler db = new DatabaseHandler(context);

//                runOnUiThread(new Runnable() {
//                    public void run() {
                        db.addBottle(newBottle);
//                    }
//                });

                context.finish();
            }
        });
    }

    private Bitmap bitmapFromFile(File file) {
        try {
            FileInputStream inputStream = new FileInputStream(file);
            Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
            inputStream.close();
            return bitmap;
        } catch (FileNotFoundException e) {
            throw new IllegalStateException("File was unexpectedly deleted : " + file + " with " + e, e);
        } catch (IOException e) {
            throw new IllegalStateException("Could not close input stream : " + file + " with " + e, e);
        }
    }

}
