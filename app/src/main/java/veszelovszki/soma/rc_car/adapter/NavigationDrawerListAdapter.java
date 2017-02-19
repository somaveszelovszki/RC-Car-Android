package veszelovszki.soma.rc_car.adapter;

import android.content.Context;

import java.util.ArrayList;
import java.util.List;

import veszelovszki.soma.rc_car.ProfileActivity;
import veszelovszki.soma.rc_car.R;
import veszelovszki.soma.rc_car.SettingsActivity;
import veszelovszki.soma.rc_car.adapter.BaseListAdapter;
import veszelovszki.soma.rc_car.utils.NavigationDrawerListItem;

/**
 * Created by Created by Soma Veszelovszki {soma.veszelovszki@gmail.com} on 2017. 02. 09.
 */

public class NavigationDrawerListAdapter extends BaseListAdapter<NavigationDrawerListItem> {

    public NavigationDrawerListAdapter(Context context) {
        super(context);

        this.addAll(createItemList());
    }

    public NavigationDrawerListAdapter(Context context, int layoutResourceId) {
        super(context, layoutResourceId);

        this.addAll(createItemList());
    }

    private List<NavigationDrawerListItem> createItemList() {

        List<NavigationDrawerListItem> items = new ArrayList<>();

        items.add(new NavigationDrawerListItem(1, getContext().getString(R.string.my_profile), ProfileActivity.class));
        items.add(new NavigationDrawerListItem(2, getContext().getString(R.string.settings), SettingsActivity.class));

        return items;
    }
}
