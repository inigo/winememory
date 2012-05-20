package net.surguy.winememory;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

/**
 * @author Inigo Surguy
 */
public class WineListAdapter extends BaseAdapter {
    private final LayoutInflater inflater;
    private final Context context;
    private final File directory;

    public WineListAdapter(Context context, File directory) {
        this.context = context;
        this.inflater = LayoutInflater.from(context);
        this.directory = directory;
    }

    public int getCount() { return directory.listFiles().length; }
    public Object getItem(int i) { return directory.listFiles()[i]; }
    public long getItemId(int i) { return 0; }

    public View getView(int position, View view, ViewGroup viewGroup) {
        View currentView = (view == null) ? createView(position) : view;
        Line holder = (Line) currentView.getTag();

//        holder.iconLine.setImageBitmap(mIcon1);
        holder.textLine.setText("Item " + holder.file.getName());

        return currentView;
    }

    private View createView(final int position) {
        // Similar to http://www.codemobiles.com/forum/viewtopic.php?t=876
        // Layout based on http://android-developers.blogspot.co.uk/2009/02/android-layout-tricks-1.html

        View view = inflater.inflate(R.layout.wine_list, null);
        TextView textLine = (TextView) view.findViewById(R.id.description);
        TextView titleLine = (TextView) view.findViewById(R.id.title);
        ImageView iconLine = (ImageView) view.findViewById(R.id.icon);

        final File file = directory.listFiles()[position];
        titleLine.setText("Some text");
        textLine.setText("Item " + file.getName());
        iconLine.setAdjustViewBounds(true);
        iconLine.setImageBitmap(bitmapFromFile(file));
        iconLine.setMaxHeight(200);
        iconLine.setMaxWidth(200);
        iconLine.setScaleType(ImageView.ScaleType.CENTER_INSIDE);

        view.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Toast.makeText(context, "Click-" + position + " for " + file.getName(), Toast.LENGTH_SHORT).show();
            }
        });

        Line holder = new Line(textLine, iconLine, file);
        view.setTag(holder);
        return view;
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

    private static class Line {
        final TextView textLine;
        final ImageView iconLine;
        final File file;

        private Line(TextView textLine, ImageView iconLine, File file) {
            this.textLine = textLine;
            this.iconLine = iconLine;
            this.file = file;
        }
    }

}
