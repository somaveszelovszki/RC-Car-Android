package veszelovszki.soma.rc_car.view;

import android.content.Context;
import android.support.v4.content.res.ResourcesCompat;
import android.util.AttributeSet;
import android.widget.LinearLayout;

import veszelovszki.soma.rc_car.R;
import veszelovszki.soma.rc_car.utils.Utils;

/**
 * Created by Created by Soma Veszelovszki {soma.veszelovszki@gmail.com} on 2017. 02. 10.
 */

public class RecalibrateSteeringSeekBar extends MiddlePositionSeekBar {

    public static final Integer MAX = 100;
    private static final Integer HEIGHT = 20;

    public RecalibrateSteeringSeekBar(Context context) {
        this(context, null);
    }

    public RecalibrateSteeringSeekBar(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public RecalibrateSteeringSeekBar(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

        this.init();
    }

    private void init() {
        Integer size = ((Double)(1.5 * HEIGHT)).intValue();

        this.setThumb(Utils.resizeDrawable(getContext(), ResourcesCompat.getDrawable(getResources(),
                R.drawable.rc_steering_wheel, null), size, size));

        this.setThumbOffset(0);

        this.setProgressDrawable(ResourcesCompat.getDrawable(getResources(),
                R.drawable.accelerator_seekbar_progress, null));

        this.setMinimumHeight(HEIGHT);
        this.setLayoutParams(new LinearLayout.LayoutParams(this.getWidth(), HEIGHT));
    }

    @Override
    public int getMax() {
        return MAX;
    }
}
