package veszelovszki.soma.rc_car.adapter;

import android.content.Context;
import android.support.annotation.DrawableRes;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import veszelovszki.soma.rc_car.utils.ListItem;
import veszelovszki.soma.rc_car.utils.PreferenceAdaptActivity;
import veszelovszki.soma.rc_car.R;
import veszelovszki.soma.rc_car.utils.IconListItem;

/**
 * Created by Soma Veszelovszki {soma.veszelovszki@gmail.com} on 2017. 04. 02.
 */
public class IconListAdapter<T extends ListItem> extends BaseListAdapter<T> {

    public IconListAdapter(Context context) {
        this(context, R.layout.icon_list_item);
    }

    public IconListAdapter(Context context, Integer resourceId) {
        super(context, resourceId);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View view = convertView;

        if (view == null) {
            view = LayoutInflater.from(getContext()).inflate(mResourceId, null);
        }

        super.getView(position, view, parent);

        IconListItem item = (IconListItem) getItem(position);

        if (item != null) {

            Log.d("asdasd", "appending icon...");

            ImageView iconImageView = (ImageView) view.findViewById(R.id.list_item_icon);

            if (iconImageView != null) {
                iconImageView.setImageDrawable(item.getIcon());
                Log.d("asdasd", "icon is not null...");

            } else {
                Log.d("asdasd", "icon is null...");
            }
        }

        return view;
    }
}
