package veszelovszki.soma.rc_car.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import veszelovszki.soma.rc_car.R;
import veszelovszki.soma.rc_car.utils.Config;
import veszelovszki.soma.rc_car.utils.Pointf;

import static java.lang.Math.max;

/**
 * Created by Soma Veszelovszki {soma.veszelovszki@gmail.com} on 2017.10.25.
 */

public class AbsoluteEnvironmentView extends View {

    private static final String TAG = AbsoluteEnvironmentView.class.getCanonicalName();

    private Paint mPaint;

    private Bitmap mCar;

    boolean mIsCarScaled = false;

    private int mCarX, mCarY;
    private float mCarRot = 0.0f;

    private static final int X = Config.ENV_ABS_AXIS_POINTS_NUM, Y = Config.ENV_ABS_AXIS_POINTS_NUM;

    private int[][] mEnvPoints = new int[Y][X];

    public AbsoluteEnvironmentView(Context context, AttributeSet attrs) {
        super(context, attrs);

        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setColor(Color.BLACK);
        mPaint.setStyle(Paint.Style.FILL);
    }

    // override onDraw
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        int w = getWidth(), h = getHeight();
        int resolution = 8 / Config.ENV_ABS_POINTS_BIT_DEPTH;

        if (!mIsCarScaled) {
            Bitmap carRaw = BitmapFactory.decodeResource(getContext().getResources(), R.drawable.ic_car_top);
            int ratio = max(w, h);
            int dstSide = (int) (Config.CAR_LENGTH / (Config.ENV_ABS_AXIS_POINTS_NUM * Config.ENV_ABS_POINTS_DIST) * ratio);
            mCar = Bitmap.createScaledBitmap(carRaw, dstSide, dstSide, true);

            mCarX = w / 2;
            mCarY = h / 2;

            mIsCarScaled = true;
        }

        for (int y = 0; y < Y; ++y) {
            for (int x = 0; x < X; ++x) {
                mPaint.setAlpha((255 * mEnvPoints[y][x]) / resolution);

                float left = (x / (float)X) * w,
                        top = (y / (float)Y) * h,
                        right = left + w / (float)X,
                        bottom = top + h / (float)Y;
                canvas.drawRect(left, top, right, bottom, mPaint);
            }
        }

        canvas.rotate(mCarRot);
        canvas.drawBitmap(mCar, mCarX, mCarY, null);
        canvas.rotate(0.0f);
    }

    int cntr = 0;

    public void updatePoint(int x, int y, int point){

        mEnvPoints[y][x] = point;

        if (++cntr % 100 == 0)
            invalidate();
    }

    public void updateCar(int x, int y, float angleDeg) {
        mCarX = x;
        mCarY = y;
        mCarRot = angleDeg;
    }
}