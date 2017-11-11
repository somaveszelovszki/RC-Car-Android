package veszelovszki.soma.rc_car.view;

import android.content.Context;
import android.support.v7.widget.AppCompatSeekBar;
import android.util.AttributeSet;
import android.widget.SeekBar;

/**
 * Created by Created by Soma Veszelovszki {soma.veszelovszki@gmail.com} on 2017. 02. 10.
 */

public abstract class MiddlePositionSeekBar extends AppCompatSeekBar {

    public MiddlePositionSeekBar(Context context) {
        this(context, null);
    }

    public MiddlePositionSeekBar(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public MiddlePositionSeekBar(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

        this.setMax(this.getMax());
        this.setMiddle();
    }

    @Override
    abstract public int getMax();

    public Integer getMiddle() {
        return getMax() / 2;
    }

    public void setMiddle() {
        this.setProgress(this.getMiddle());
    }
}
