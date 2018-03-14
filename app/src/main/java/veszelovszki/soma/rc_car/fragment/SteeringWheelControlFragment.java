package veszelovszki.soma.rc_car.fragment;

import android.graphics.Point;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import veszelovszki.soma.rc_car.R;
import veszelovszki.soma.rc_car.communication.Message;
import veszelovszki.soma.rc_car.utils.Pointf;
import veszelovszki.soma.rc_car.utils.Utils;
import veszelovszki.soma.rc_car.view.AbsoluteEnvironmentView;
import veszelovszki.soma.rc_car.view.AccelerationSeekBar;
import veszelovszki.soma.rc_car.view.RelativeEnvironmentView;
import veszelovszki.soma.rc_car.view.SteeringWheelView;

/**
 * Created by Soma Veszelovszki {soma.veszelovszki@gmail.com} on 2016. 11. 13.
 */
public class SteeringWheelControlFragment extends ControlFragment {

    public static final String TAG = SteeringWheelControlFragment.class.getCanonicalName();

    private SteeringWheelView mSteeringWheelView;
    private AccelerationSeekBar mAccelerationSeekBar;
    //private RelativeEnvironmentView mRelativeEnvironmentView;
    private AbsoluteEnvironmentView mAbsoluteEnvironmentView;
    private Button mCarEnvironmentEnableButton;

    private Boolean mIsCarEnvironmentEnabled = false;

    public static SteeringWheelControlFragment newInstance() {
        return new SteeringWheelControlFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_control_steering_wheel, container, false);

        mSteeringWheelView = (SteeringWheelView) view.findViewById(R.id.steering_wheel_view);
        mAccelerationSeekBar = (AccelerationSeekBar) view.findViewById(R.id.accelerator_seek_bar);
        //mRelativeEnvironmentView = (RelativeEnvironmentView) view.findViewById(R.id.environment_view);
        mAbsoluteEnvironmentView = (AbsoluteEnvironmentView) view.findViewById(R.id.environment_view);
        mCarEnvironmentEnableButton = (Button) view.findViewById(R.id.car_environment_enable_button);

        mCarEnvironmentEnableButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setCarEnvironmentEnabled(!isCarEnvironmentEnabled());
                if (mIsCarEnvironmentEnabled)
                    mListener.onCarEnvironmentEnabled();
                else
                    mListener.onCarEnvironmentDisabled();
            }
        });

        return view;
    }

    @Override
    public float getSpeed() {
        return Utils.map((float) mAccelerationSeekBar.getProgress(),
                (float) AccelerationSeekBar.MIN_POS,
                (float) AccelerationSeekBar.MAX_POS,
                (float) Message.CODE.Speed.getMinDataValue(), (float) Message.CODE.Speed.getMaxDataValue());
    }

    @Override
    public float getSteeringAngle() {
        return Utils.map(mSteeringWheelView.getWheelRotation(),
                (-1) * SteeringWheelView.STEERING_WHEEL_MAX_ROTATION,
                SteeringWheelView.STEERING_WHEEL_MAX_ROTATION,
                (float) Message.CODE.SteeringAngle.getMinDataValue(), (float) Message.CODE.SteeringAngle.getMaxDataValue());
    }

    @Override
    public void setCarEnvironmentEnabled(Boolean enabled) {
        mIsCarEnvironmentEnabled = enabled;
        //mRelativeEnvironmentView.setVisibility(enabled ? View.VISIBLE : View.GONE);
        mAbsoluteEnvironmentView.setVisibility(enabled ? View.VISIBLE : View.GONE);
        mCarEnvironmentEnableButton.setText(enabled ? R.string.hide_car_environment : R.string.show_car_environment);
    }

    @Override
    public Boolean isCarEnvironmentEnabled() {
        return mIsCarEnvironmentEnabled;
    }

//    public void updateCarEnvironmentPoint(int idx, Pointf point){
//        mRelativeEnvironmentView.updatePoint(idx, point);
//    }

    public void updateEnvironment_Point(int x, int y, int point){
        mAbsoluteEnvironmentView.updatePoint(x, y, point);
    }

    public void updateEnvironment_Car(int x, int y, float angleDeg) {
        mAbsoluteEnvironmentView.updateCar(x, y, angleDeg);
    }
}

