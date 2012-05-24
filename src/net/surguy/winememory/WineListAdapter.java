package net.surguy.winememory;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;

import java.io.File;

/**
 * @author Inigo Surguy
 */
public class WineListAdapter extends BaseAdapter {
    private final LayoutInflater inflater;
    private final Context context;
    private final DatabaseHandler db;

    public WineListAdapter(Context context) {
        this.context = context;
        this.inflater = LayoutInflater.from(context);
        db = new DatabaseHandler(context);
    }

    public int getCount() {
        // @todo Switch to a count method, to be more efficient
        return db.getAllBottles().size();
    }
    public Object getItem(int i) {
        // @todo Efficiency!
        return db.getAllBottles().get(i);
    }

    public long getItemId(int i) { return 0; }

    public View getView(int position, View view, ViewGroup viewGroup) {
        View currentView = (view == null) ? createView(position) : view;
        Line holder = (Line) currentView.getTag();

//        holder.textLine.setText("Item " + holder.file.getName());

        return currentView;
    }

    private View createView(final int position) {
        // Similar to http://www.codemobiles.com/forum/viewtopic.php?t=876
        // Layout based on http://android-developers.blogspot.co.uk/2009/02/android-layout-tricks-1.html

        View view = inflater.inflate(R.layout.wine_list, null);
        TextView textLine = (TextView) view.findViewById(R.id.description);
//        TextView titleLine = (TextView) view.findViewById(R.id.title);
        ImageView iconLine = (ImageView) view.findViewById(R.id.icon);
        RatingBar ratingBar = (RatingBar) view.findViewById(R.id.rating);

        final Bottle bottle = db.getAllBottles().get(position);

        final File file = new File(bottle.getFilePath());
        textLine.setText(bottle.getName());
        iconLine.setAdjustViewBounds(true);
        iconLine.setImageBitmap(Utils.bitmapFromFile(file, 200));
        iconLine.setMaxHeight(200);
        iconLine.setMaxWidth(200);
        iconLine.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
        ratingBar.setRating(bottle.getRating());

        view.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Toast.makeText(context, bottle.getDescription(), Toast.LENGTH_SHORT).show();
            }
        });

        Line holder = new Line(textLine, iconLine, ratingBar, file);
        view.setTag(holder);
        return view;
    }

    private static class Line {
        final TextView textLine;
        final ImageView iconLine;
        final RatingBar ratingBar;
        final File file;

        private Line(TextView textLine, ImageView iconLine, RatingBar ratingBar, File file) {
            this.textLine = textLine;
            this.iconLine = iconLine;
            this.ratingBar = ratingBar;
            this.file = file;
        }
    }

}
