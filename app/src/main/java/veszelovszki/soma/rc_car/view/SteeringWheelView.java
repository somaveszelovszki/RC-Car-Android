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

    private ImageView mSteeringWheel;
    private RelativeLayout mSteeringWheelContainer;

    private Float mInitialViewRotation;
    private Double mInitialFingerRotation;

    // these two fields help handling finger movements
    private Double mPrevFingerRotation;
    private Boolean mIsFingerPositionOverBorder;

    private static final Double BORDER = 180.0;

    private ObjectAnimator animation;
    private static final Integer ANIMATION_TIME_MS = 500;

    public static Float STEERING_WHEEL_MAX_ROTATION = (float) Math.toRadians(120);  // degrees
    private Float STEERING_WHEEL_JUMP_LIMIT = (float) Math.toRadians(50);  // degrees

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

        mSteeringWheelContainer.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                return onSteeringWheelTouch(motionEvent);
            }
        });
    }

    public Float getWheelRotation() {
        // changing signal so that LEFT is positive
        // returns angle in radians
        return (float) Math.toRadians(-1 * mSteeringWheel.getRotation());
    }

    private void setWheelRotation(Float rotation) {
        mSteeringWheel.setRotation(rotation);
    }

    private Point getPivot() {
        return new Point(
                mSteeringWheelContainer.getWidth() / 2,
                mSteeringWheelContainer.getHeight() / 2
        );
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
                mInitialFingerRotation = Math.toDegrees(Math.atan2(x - pivot.x, pivot.y - y));

                mPrevFingerRotation = mInitialFingerRotation;
                mIsFingerPositionOverBorder = false;

                break;
            case MotionEvent.ACTION_MOVE:

                Double newFingerRotation = Math.toDegrees(Math.atan2(x - pivot.x, pivot.y - y));


                if (mPrevFingerRotation < -1 * BORDER + STEERING_WHEEL_JUMP_LIMIT / 2
                    && newFingerRotation > BORDER - STEERING_WHEEL_JUMP_LIMIT / 2
                        ||
                        newFingerRotation < -1 * BORDER + STEERING_WHEEL_JUMP_LIMIT / 2
                                && mPrevFingerRotation > BORDER - STEERING_WHEEL_JUMP_LIMIT / 2 ) {
                    mIsFingerPositionOverBorder = !mIsFingerPositionOverBorder;
                }

                Double newFingerCountedRotation = mIsFingerPositionOverBorder ?
                        (newFingerRotation > 0 ?
                                -2 * BORDER + newFingerRotation : 2 * BORDER + newFingerRotation)
                        : newFingerRotation;

                Float rotation = (float)(mInitialViewRotation + newFingerCountedRotation - mInitialFingerRotation);
                if (rotation > STEERING_WHEEL_MAX_ROTATION) {
                    rotation = STEERING_WHEEL_MAX_ROTATION;
                } else if (rotation < (-1) * STEERING_WHEEL_MAX_ROTATION) {
                    rotation = (-1) * STEERING_WHEEL_MAX_ROTATION;
                }

                Float prevRotation = getWheelRotation();

                if (Math.abs(rotation - prevRotation) > STEERING_WHEEL_JUMP_LIMIT) {
                    rotation = prevRotation;
                }

                setWheelRotation(rotation);

                mPrevFingerRotation = newFingerRotation;

                break;
            case MotionEvent.ACTION_UP:
                animation = ObjectAnimator.ofFloat(mSteeringWheel, "rotation", getWheelRotation(), 0);
                animation.setDuration(ANIMATION_TIME_MS);
                animation.setRepeatCount(0);
                animation.setInterpolator(new AccelerateDecelerateInterpolator());
                animation.start();

                break;
        }

        return true;
    }
}
