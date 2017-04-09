package veszelovszki.soma.rc_car.utils;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.DrawableRes;
import android.support.annotation.NonNull;
import android.support.annotation.StringRes;

/**
 * Created by Created by Soma Veszelovszki {soma.veszelovszki@gmail.com} on 2017. 04. 02.
 */
public class NavigationDrawerListItem extends IconListItem {

    private Utils.Callback mCallback;

    public NavigationDrawerListItem(Context context, Integer id, String title, @DrawableRes Integer iconResId,
                                    Utils.Callback callback) {
        super(context, id, title, iconResId);

        mCallback = callback;
    }

    public Utils.Callback getCallback() {
        return mCallback;
    }
}
