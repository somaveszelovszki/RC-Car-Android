package veszelovszki.soma.rc_car.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import veszelovszki.soma.rc_car.R;

/**
 * Created by Soma Veszelovszki {soma.veszelovszki@gmail.com} on 2017.10.25.
 */

public class EnvironmentView extends View {

    private Canvas mCanvas;
    Context context;
    private Paint mPaint;

    public EnvironmentView(Context context, AttributeSet attrs) {
        super(context, attrs);

        // and we set a new Paint with the desired attributes
        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setColor(Color.RED);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeJoin(Paint.Join.ROUND);
        mPaint.setStrokeWidth(4f);
    }

    // override onSizeChanged
    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);

        // your Canvas will draw onto the defined Bitmap
        mCanvas = new Canvas(Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888));

        setBackgroundResource(R.drawable.ic_settings);
    }

    // override onDraw
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
    }

    public void clearCanvas() {
        // TODO
        invalidate();
    }

}