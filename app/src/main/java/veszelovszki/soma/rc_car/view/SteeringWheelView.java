package veszelovszki.soma.rc_car.view;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.Point;
import android.renderscript.Double2;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import veszelovszki.soma.rc_car.R;

/**
 * Created by Soma Veszelovszki {soma.veszelovszki@gmail.com} on 2017. 01. 28.
 */
public class SteeringWheelView extends RelativeLayout {

    private static final String TAG = SteeringWheelView.class.getCanonicalName();

    private ImageView mSteeringWheel;
    private RelativeLayout mSteeringWheelContainer;

    private float mInitialViewRotation;
    private float mInitialFingerRotation;

    // these two fields help handling finger movements
    private float mPrevFingerRotation;
    private boolean mIsFingerPositionOverBorder;

    private static final float BORDER = 180.0f;

    private ObjectAnimator animation;
    private static final int ANIMATION_TIME = 500;

    public static float STEERING_WHEEL_MAX_ROTATION = 120.0f;  // degrees
    private float STEERING_WHEEL_JUMP_LIMIT = 50.0f;  // degrees

    public SteeringWheelView(Context context) {
        this(context, null);
    }

    public SteeringWheelView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SteeringWheelView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

        LayoutInflater inflater = LayoutInflater.from(context);
        inflater.inflate(R.layout.steering_wheel_view, this);

        mSteeringWheelContainer = (RelativeLayout) this.findViewById(R.id.steering_wheel_container);
        mSteeringWheel = (ImageView) this.findViewById(R.id.steering_wheel);

//        mSteeringWheelContainer.setOnTouchListener(new View.OnTouchListener() {
//            @Override
//            public boolean onTouch(View view, MotionEvent motionEvent) {
//                return onSteeringWheelTouch(motionEvent);
//            }
//        });
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        return true;
    }

    public float getWheelRotation() {
        // changing signal so that LEFT is positive
        // returns angle in degrees
        return mSteeringWheel.getRotation();
    }

    private void setWheelRotation(float rotation) {
        mSteeringWheel.setRotation(rotation);
    }

    private Point getPivot() {
        return new Point(
                mSteeringWheelContainer.getWidth() / 2,
                mSteeringWheelContainer.getHeight() / 2
        );
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return onSteeringWheelTouch(event);
    }

    private Boolean onSteeringWheelTouch(MotionEvent motionEvent) {

        if (animation != null && animation.isRunning()) {
            animation.cancel();
        }

        final Float x = motionEvent.getX();
        final Float y = motionEvent.getY();

        final Point pivot = getPivot();

        switch (motionEvent.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mInitialViewRotation = getWheelRotation();
                mInitialFingerRotation = (float) Math.toDegrees(Math.atan2(x - pivot.x, pivot.y - y));

                mPrevFingerRotation = mInitialFingerRotation;
                mIsFingerPositionOverBorder = false;

                break;
            case MotionEvent.ACTION_MOVE:
                float newFingerRotation = (float) Math.toDegrees(Math.atan2(x - pivot.x, pivot.y - y));

                if (mPrevFingerRotation < -1 * BORDER + STEERING_WHEEL_JUMP_LIMIT / 2
                        && newFingerRotation > BORDER - STEERING_WHEEL_JUMP_LIMIT / 2
                        ||
                        newFingerRotation < -1 * BORDER + STEERING_WHEEL_JUMP_LIMIT / 2
                                && mPrevFingerRotation > BORDER - STEERING_WHEEL_JUMP_LIMIT / 2 ) {
                    mIsFingerPositionOverBorder = !mIsFingerPositionOverBorder;
                }

                float newFingerCountedRotation = mIsFingerPositionOverBorder ?
                        (newFingerRotation > 0 ?
                                -2 * BORDER + newFingerRotation : 2 * BORDER + newFingerRotation)
                        : newFingerRotation;

                Float rotation = mInitialViewRotation + newFingerCountedRotation - mInitialFingerRotation;
                if (rotation > STEERING_WHEEL_MAX_ROTATION) {
                    rotation = STEERING_WHEEL_MAX_ROTATION;
                } else if (rotation < (-1) * STEERING_WHEEL_MAX_ROTATION) {
                    rotation = (-1) * STEERING_WHEEL_MAX_ROTATION;
                }

                Float prevRotation = getWheelRotation();

                if (Math.abs(rotation - prevRotation) > STEERING_WHEEL_JUMP_LIMIT)
                    rotation = prevRotation;

                setWheelRotation(rotation);

                mPrevFingerRotation = newFingerRotation;

                break;
            case MotionEvent.ACTION_UP:
                animation = ObjectAnimator.ofFloat(mSteeringWheel, "rotation", getWheelRotation(), 0);
                animation.setDuration(ANIMATION_TIME);
                animation.setRepeatCount(0);
                animation.setInterpolator(new AccelerateDecelerateInterpolator());
                animation.start();

                break;
        }

        invalidate();

        return true;
    }
}