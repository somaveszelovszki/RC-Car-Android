package veszelovszki.soma.rc_car.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.view.View;

import veszelovszki.soma.rc_car.R;
import veszelovszki.soma.rc_car.utils.Config;
import veszelovszki.soma.rc_car.utils.Pointf;

import static java.lang.Math.max;
import static java.lang.Math.min;

/**
 * Created by Soma Veszelovszki {soma.veszelovszki@gmail.com} on 2017.10.25.
 */

public class RelativeEnvironmentView extends View {

    private static final String TAG = RelativeEnvironmentView.class.getCanonicalName();

    private Path mPath;
    private Paint mPaint, mClearPaint;

    private Bitmap mBackground;

    private Pointf[] mPoints = new Pointf[Config.ULTRA_NUM_SENSORS];

    public RelativeEnvironmentView(Context context, AttributeSet attrs) {
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

        mBackground = BitmapFactory.decodeResource(getContext().getResources(), R.drawable.ic_car_top);

        // initializes points so that they won't be null
        for (int i = 0; i < mPoints.length; ++i)
            mPoints[i] = Pointf.ORIGO;
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

        int w = getWidth(), h = getHeight();

        mPath.reset();

        mPath.moveTo(0, 0);
        mPath.lineTo(w, 0);
        mPath.lineTo(w, h);
        mPath.lineTo(0, h);
        mPath.lineTo(0, 0);
        mPath.close();

        if (mPoints.length > 0) {
            Pointf startPoint = mPoints[0].toDisplayPoint(w, h);
            mPath.moveTo(startPoint.x, startPoint.y);

            for (int i = 1; i < mPoints.length; ++i) {
                Pointf p = mPoints[i].toDisplayPoint(w, h);
                mPath.lineTo(p.x, p.y);
            }
            mPath.lineTo(startPoint.x, startPoint.y);
            mPath.close();
        }

        __clearCanvas(canvas);
        canvas.drawPath(mPath, mPaint);
    }

    private void __clearCanvas(Canvas canvas) {
        canvas.drawPaint(mClearPaint);
        int ratio = max(getWidth(), getHeight());
        int dstSide = (int) (Config.CAR_LENGTH / (2 * Config.ULTRA_MAX_DIST) * ratio);
        Bitmap bitmap = Bitmap.createScaledBitmap(mBackground, dstSide, dstSide, true);
        canvas.drawBitmap(bitmap,
                (getWidth() - bitmap.getWidth()) / 2,
                (getHeight() - bitmap.getHeight()) / 2, null);
    }

    public void updatePoint(int idx, Pointf point){
        mPoints[idx] = point;
        invalidate();
    }
}