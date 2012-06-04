package net.surguy.winememory;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Inigo Surguy
 */
public class WineListAdapter extends BaseAdapter {
    private static final String LOG_TAG = "WineListAdapter";

    private final LayoutInflater inflater;
    private final Context context;
    private final DatabaseHandler db;

    private final Map<Integer, Bottle> cache = new HashMap<Integer, Bottle>();

    public WineListAdapter(Context context) {
        Log.d(LOG_TAG, "Creating WineListAdapter");
        this.context = context;
        this.inflater = LayoutInflater.from(context);
        db = new DatabaseHandler(context);
    }

    public int getCount() {
        int count = db.countBottles();
        Log.d(LOG_TAG, "Getting count of bottles : "+count);
        return count;
    }
    public Object getItem(int position) {
        Log.d(LOG_TAG, "Getting item " + position + " via getItem");
        return db.getBottle(position);
    }

    public long getItemId(int position) {
        Log.d(LOG_TAG, "Getting item id for " + position);
        return 0;
    }

    public View getView(int position, View view, ViewGroup viewGroup) {
        Log.d(LOG_TAG, "Getting view for " + position + " - view is " + view);
        View currentView = (view!=null) ? view : createView();
        Bottle bottle = getBottle(position);

        TextView textLine = (TextView) currentView.findViewById(R.id.description);
        ImageView iconLine = (ImageView) currentView.findViewById(R.id.icon);
        RatingBar ratingBar = (RatingBar) currentView.findViewById(R.id.rating);

        textLine.setText(bottle.getName());
        iconLine.setImageBitmap(bottle.getIcon());
        ratingBar.setRating(bottle.getRating());
        currentView.setTag(bottle);

        return currentView;
    }

    private Bottle getBottle(int position) {
        if (! cache.containsKey(position)) {
            final Bottle bottle = db.getBottle(position);
            Log.d(LOG_TAG, "Bottle is " + bottle.getId() + " with text " + bottle.getName());
            cache.put(position, bottle);
        }
        Log.d(LOG_TAG, "Retrieving bottle from cache at " + position);
        return cache.get(position);
    }

    @Override
    public void notifyDataSetChanged() {
        super.notifyDataSetChanged();
        Log.d(LOG_TAG, "Dataset changed");
    }

    @Override
    public void notifyDataSetInvalidated() {
        super.notifyDataSetInvalidated();
        Log.d(LOG_TAG, "Dataset invalidated");
    }

    private View createView() {
        View view = inflater.inflate(R.layout.wine_list, null);
        ImageView iconLine = (ImageView) view.findViewById(R.id.icon);
        iconLine.setAdjustViewBounds(true);
        iconLine.setMaxHeight(200);
        iconLine.setMaxWidth(200);
        iconLine.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
        view.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                String description = ((Bottle) v.getTag()).getDescription();
                Toast.makeText(context, description, Toast.LENGTH_SHORT).show();
            }
        });
        return view;
    }

}
