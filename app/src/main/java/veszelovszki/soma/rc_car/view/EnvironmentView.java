package veszelovszki.soma.rc_car.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

import veszelovszki.soma.rc_car.R;
import veszelovszki.soma.rc_car.utils.Config;
import veszelovszki.soma.rc_car.utils.Pointf;

import static java.lang.Math.min;

/**
 * Created by Soma Veszelovszki {soma.veszelovszki@gmail.com} on 2017.10.25.
 */

public class EnvironmentView extends View {

    private static final String TAG = EnvironmentView.class.getCanonicalName();

    private Path mPath;
    private Paint mPaint, mClearPaint;

    private List<Pointf> mPoints = new ArrayList<>();

    public EnvironmentView(Context context, AttributeSet attrs) {
        super(context, attrs);

        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setColor(Color.GRAY);
        mPaint.setStyle(Paint.Style.FILL_AND_STROKE);

        mClearPaint = new Paint();
        mClearPaint.setColor(Color.WHITE);
        mClearPaint.setStyle(Paint.Style.FILL);

        mPath = new Path();
        mPath.setFillType(Path.FillType.EVEN_ODD);
    }

    // override onSizeChanged
    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
    }

    // override onDraw
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        Integer w = getWidth(), h = getHeight();

        mPath.reset();

        mPath.moveTo(0, 0);
        mPath.lineTo(w, 0);
        mPath.lineTo(w, h);
        mPath.lineTo(0, h);
        mPath.lineTo(0, 0);
        mPath.close();

        Pointf startPoint = mPoints.get(0).toDisplayPoint(w, h);
        mPath.moveTo(startPoint.x, startPoint.y);

        for (int i = 1; i < mPoints.size(); ++i) {
            Pointf p = mPoints.get(i).toDisplayPoint(w, h);
            mPath.lineTo(p.x, p.y);
        }
        mPath.lineTo(startPoint.x, startPoint.y);
        mPath.close();

        canvas.drawPaint(mClearPaint);
        canvas.drawPath(mPath, mPaint);
    }

    public void updatePoint(int idx, Pointf point){
        mPoints.set(idx, point);
        invalidate();
    }

    public void updatePoints(List<Pointf> points) {
        mPoints.clear();
        mPoints.addAll(points);
        invalidate();
    }

}