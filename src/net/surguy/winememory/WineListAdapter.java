package net.surguy.winememory;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

/**
 * @author Inigo Surguy
 */
public class WineListAdapter extends BaseAdapter {
    private final LayoutInflater inflater;
    private final Context context;

    private static String[] data = new String[] { "0", "1", "2", "3", "4" };

    public WineListAdapter(Context context) {
        this.context = context;
        this.inflater = LayoutInflater.from(context);
    }

    public int getCount() { return data.length; }
    public Object getItem(int i) { return data[i]; }
    public long getItemId(int i) { return 0; }

    public View getView(int position, View view, ViewGroup viewGroup) {
        View currentView = (view == null) ? createView(position) : view;
        Line holder = (Line) currentView.getTag();

//        holder.iconLine.setImageBitmap(mIcon1);
        holder.textLine.setText("Item " + position);

        return currentView;
    }

    private View createView(final int position) {
        View view = inflater.inflate(R.layout.winelist, null);
        TextView textLine = (TextView) view.findViewById(R.id.title);
        ImageView iconLine = (ImageView) view.findViewById(R.id.icon);
        Line holder = new Line(textLine, iconLine);

        view.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Toast.makeText(context, "Click-" + String.valueOf(position), Toast.LENGTH_SHORT).show();
            }
        });

        view.setTag(holder);
        return view;
    }

    private static class Line {
        final TextView textLine;
        final ImageView iconLine;

        private Line(TextView textLine, ImageView iconLine) {
            this.textLine = textLine;
            this.iconLine = iconLine;
        }
    }

}
