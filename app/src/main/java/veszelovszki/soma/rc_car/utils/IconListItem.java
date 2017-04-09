package veszelovszki.soma.rc_car.utils;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.annotation.DrawableRes;

import veszelovszki.soma.rc_car.communication.BluetoothCommunicator;

/**
 * Created by Soma Veszelovszki {soma.veszelovszki@gmail.com} on 2016. 08. 29.
 */
public class IconListItem extends ListItem {

    private Context mContext;

    private @DrawableRes
    Integer mIconResId;

    public IconListItem(Context context, Integer id, String title, @DrawableRes Integer iconResId) {
        super(id, title);

        mContext = context;

        mIconResId = iconResId;
    }

    public Drawable getIcon() {
        return mContext.getResources().getDrawable(mIconResId);
    }
}
