package veszelovszki.soma.rc_car.adapter;

import android.content.Context;
import android.os.Bundle;

import java.util.ArrayList;
import java.util.List;

import veszelovszki.soma.rc_car.ProfileActivity;
import veszelovszki.soma.rc_car.R;
import veszelovszki.soma.rc_car.SettingsActivity;
import veszelovszki.soma.rc_car.adapter.BaseListAdapter;
import veszelovszki.soma.rc_car.utils.IconListItem;
import veszelovszki.soma.rc_car.utils.NavigationDrawerListItem;
import veszelovszki.soma.rc_car.utils.PreferenceAdaptActivity;
import veszelovszki.soma.rc_car.utils.Utils;

/**
 * Created by Created by Soma Veszelovszki {soma.veszelovszki@gmail.com} on 2017. 04. 02.
 */

public class NavigationDrawerListAdapter extends IconListAdapter<NavigationDrawerListItem> {

    public NavigationDrawerListAdapter(Context context) {
        super(context, R.layout.list_item_navigation_drawer);

        //super(context);
        this.addAll(createItemList());
    }

    public NavigationDrawerListAdapter(Context context, int layoutResourceId) {
        super(context, layoutResourceId);

        this.addAll(createItemList());
    }

    private List<NavigationDrawerListItem> createItemList() {

        final PreferenceAdaptActivity activity = (PreferenceAdaptActivity) getContext();

        List<NavigationDrawerListItem> items = new ArrayList<>();

        final Bundle profileExtras = new Bundle();
        items.add(new NavigationDrawerListItem(
                getContext(), 1, getContext().getString(R.string.my_profile), R.drawable.ic_account_circle, new Utils.Callback() {
            @Override
            public void onEvent() {
                activity.startActivity(ProfileActivity.class, profileExtras);
            }
        }));

        final Bundle settingsExtras = new Bundle();
        items.add(new NavigationDrawerListItem(
                getContext(), 2, getContext().getString(R.string.settings), R.drawable.ic_settings, new Utils.Callback() {
            @Override
            public void onEvent() {
                activity.startActivity(SettingsActivity.class, settingsExtras);
            }
        }));

        return items;
    }
}