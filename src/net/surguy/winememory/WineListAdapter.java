package net.surguy.winememory;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;

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
        View view = inflater.inflate(R.layout.winelist, null);
        TextView textLine = (TextView) view.findViewById(R.id.description);
        ImageView iconLine = (ImageView) view.findViewById(R.id.icon);

        final File file = directory.listFiles()[position];
        textLine.setText("Item " + file.getName());

        view.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Toast.makeText(context, "Click-" + position + " for " + file.getName(), Toast.LENGTH_SHORT).show();
            }
        });

        Line holder = new Line(textLine, iconLine, file);
        view.setTag(holder);
        return view;
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
