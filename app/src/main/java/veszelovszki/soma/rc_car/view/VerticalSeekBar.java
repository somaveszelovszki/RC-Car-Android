package veszelovszki.soma.rc_car.view;

import android.content.Context;
import android.graphics.Canvas;
import android.support.v4.content.res.ResourcesCompat;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.SeekBar;

import veszelovszki.soma.rc_car.R;

/**
 * Created by Soma Veszelovszki {soma.veszelovszki@gmail.com} on 2017. 01. 27.
 *
 * @see {http://stackoverflow.com/questions/4892179/how-can-i-get-a-working-vertical-seekbar-in-android/7341546#7341546}
 */
public abstract class VerticalSeekBar extends MiddlePositionSeekBar {

    private OnSeekBarChangeListener onChangeListener;

    public VerticalSeekBar(Context context) {
        super(context);
    }

    public VerticalSeekBar(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public VerticalSeekBar(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(h, w, oldh, oldw);
    }

    @Override
    protected synchronized void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(heightMeasureSpec, widthMeasureSpec);
        setMeasuredDimension(getMeasuredHeight(), getMeasuredWidth());
    }

    protected void onDraw(Canvas c) {
        c.rotate(-90);
        c.translate(-getHeight(), 0);

        super.onDraw(c);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (!isEnabled()) {
            return false;
        }

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                onChangeListener.onStartTrackingTouch(this);
                updateProgress(event);
                break;

            case MotionEvent.ACTION_MOVE:
                updateProgress(event);
                break;

            case MotionEvent.ACTION_UP:
                updateProgress(event);
                onChangeListener.onStopTrackingTouch(this);
                break;

            case MotionEvent.ACTION_CANCEL:
                break;
        }
        return true;
    }

    private void updateProgress(MotionEvent event) {
        this.setProgress(getMax() - (int) (getMax() * event.getY() / getHeight()));
    }

    @Override
    public void setOnSeekBarChangeListener(OnSeekBarChangeListener onChangeListener) {
        this.onChangeListener = onChangeListener;
    }


    @Override
    public void setProgress(int progress) {
        super.setProgress(progress);
        onSizeChanged(getWidth(), getHeight(), 0, 0);
    }
}
