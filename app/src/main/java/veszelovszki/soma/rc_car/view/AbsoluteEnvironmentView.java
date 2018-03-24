package veszelovszki.soma.rc_car.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import veszelovszki.soma.rc_car.R;
import veszelovszki.soma.rc_car.utils.Config;

import static java.lang.Math.min;

/**
 * Created by Soma Veszelovszki {soma.veszelovszki@gmail.com} on 2017.10.25.
 */

public class AbsoluteEnvironmentView extends View {

    private static final String TAG = AbsoluteEnvironmentView.class.getCanonicalName();

    private Paint mPaint;

    private Bitmap mCar;

    boolean mIsCarScaled = false;

    private float mCarAngleDeg;
    int mCarSize;
    Matrix mCarRotator;

    private static final int X = Config.ENV_ABS_AXIS_POINTS_NUM, Y = Config.ENV_ABS_AXIS_POINTS_NUM;

    private int[][] mEnvPoints = new int[Y][X];

    float mResX, mResY;

    public AbsoluteEnvironmentView(Context context, AttributeSet attrs) {
        super(context, attrs);

        for (int y = 0; y < Y; ++y) {
            for (int x = 0; x < X; ++x) {
                mEnvPoints[y][x] = 0;
            }
        }

        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setColor(Color.BLACK);
        mPaint.setStyle(Paint.Style.FILL);
    }

    @Override
    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int measureSpec = min(widthMeasureSpec, heightMeasureSpec);
        super.onMeasure(measureSpec, measureSpec);
    }

    // override onDraw
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        int w = getWidth(), h = getHeight();

        if (!mIsCarScaled) {
            Bitmap carRaw = BitmapFactory.decodeResource(getContext().getResources(), R.drawable.ic_car_top);
            int ratio = min(w, h);
            mCarSize = (int) (Config.CAR_LENGTH / (Config.ENV_ABS_AXIS_POINTS_NUM * Config.ENV_ABS_POINTS_DIST) * ratio);
            mCar = Bitmap.createScaledBitmap(carRaw, mCarSize, mCarSize, true);

            mResX = w / (float)X;
            mResY = h / (float)Y;

            mCarAngleDeg = 0.0f;
            mCarRotator = new Matrix();
            mCarRotator.postTranslate((w - mCarSize) / 2, (h - mCarSize) / 2);

            mIsCarScaled = true;
        }

        for (int y = 0; y < Y; ++y) {
            for (int x = 0; x < X; ++x) {

                mPaint.setAlpha((255 * mEnvPoints[y][x]) / Config.ENV_ABS_POINTS_BIT_DEPTH);

                float left = x * mResX,
                        top = y * mResY,
                        right = left + mResX,
                        bottom = top + mResY;
                canvas.drawRect(left, top, right, bottom, mPaint);
            }
        }

        canvas.drawBitmap(mCar, mCarRotator, null);
        canvas.rotate(0.0f);
    }

    public void updatePoint(int x, int y, int point){
        mEnvPoints[y][x] = point;
        invalidate();
    }

    public void updateCar(int x, int y, float angleDeg) {
        float carX = x * mResX - mCarSize / 2;
        float carY = y * mResY - mCarSize / 2;
        mCarAngleDeg = angleDeg;

        mCarRotator = new Matrix();
        mCarRotator.postRotate(mCarAngleDeg);
        mCarRotator.postTranslate(carX, carY);
    }
}