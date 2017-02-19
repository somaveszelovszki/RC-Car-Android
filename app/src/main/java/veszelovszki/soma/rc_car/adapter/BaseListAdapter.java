package veszelovszki.soma.rc_car.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import veszelovszki.soma.rc_car.R;
import veszelovszki.soma.rc_car.utils.ListItem;


/**
 * Created by Soma Veszelovszki {soma.veszelovszki@gmail.com} on 2016. 08. 28.
 */
public class BaseListAdapter<T extends ListItem> extends ArrayAdapter<T> {

    private static Integer DEFAULT_RESOURCE_ID = R.layout.list_item;

    protected Integer mResourceId;

    public BaseListAdapter(Context context) {
        this(context, DEFAULT_RESOURCE_ID);
    }

    public BaseListAdapter(Context context, Integer mResourceId) {
        this(context, mResourceId, new ArrayList<T>());
    }

    public BaseListAdapter(Context context, Integer mResourceId, List<T> data) {
        super(context, mResourceId, data);

        this.mResourceId = mResourceId;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View view = convertView;

        if (view == null) {
            view = LayoutInflater.from(getContext()).inflate(mResourceId, null);
        }

        T item = getItem(position);

        if (item != null) {
            TextView titleTextView = (TextView) view.findViewById(R.id.list_item_title);

            if (titleTextView != null) {

                titleTextView.setText(item.getTitle());
            }
        }

        return view;
    }
}

