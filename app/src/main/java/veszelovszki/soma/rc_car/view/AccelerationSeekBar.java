package veszelovszki.soma.rc_car.view;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.support.v4.content.res.ResourcesCompat;
import android.util.AttributeSet;
import android.util.Log;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SeekBar;

import veszelovszki.soma.rc_car.R;
import veszelovszki.soma.rc_car.utils.Utils;

/**
 * Created by Soma Veszelovszki {soma.veszelovszki@gmail.com} on 2017. 01. 28.
 */
public class AccelerationSeekBar extends VerticalSeekBar
        implements SeekBar.OnSeekBarChangeListener {

    ObjectAnimator animation;
    private static final Integer ANIMATION_TIME_MS = 500;

    public static final Integer MIN_POS = 0;
    public static final Integer MAX_POS = 100;
    private static final Integer HEIGHT = 20;

    public AccelerationSeekBar(Context context) {
        this(context, null);
    }

    public AccelerationSeekBar(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public AccelerationSeekBar(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

        this.init();
    }

    private void init() {

        this.setThumb(Utils.resizeDrawable(getContext(), ResourcesCompat.getDrawable(getResources(),
                R.drawable.pedal_rectangle, null),
                HEIGHT, ((Double)(HEIGHT * 1.5)).intValue()));

        this.setThumbOffset(0);

        this.setProgressDrawable(ResourcesCompat.getDrawable(getResources(),
                R.drawable.accelerator_seekbar_progress, null));


        //this.setLayoutParams(new LinearLayout.LayoutParams(this.getWidth(), HEIGHT));

        this.setOnSeekBarChangeListener(this);
    }

    @Override
    public int getMax() {
        return MAX_POS;
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {
        if (animation != null && animation.isRunning()) {
            animation.cancel();
        }
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        animation = ObjectAnimator.ofInt(seekBar, "progress", seekBar.getProgress(), ((VerticalSeekBar)seekBar).getMiddle());
        animation.setDuration(ANIMATION_TIME_MS);
        animation.setRepeatCount(0);
        animation.setInterpolator(new AccelerateDecelerateInterpolator());
        animation.start();
    }
}
